package org.activiti.rest.service.api.runtime.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * 操作任务task跳转
 * @author dell1
 *
 */
@RestController
@Api(tags = { "Tasks" }, description = "Manage Tasks", authorizations = { @Authorization(value = "basicAuth") })
public class TaskJumpResource{
	
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	TaskService taskService;
	@Autowired
	ManagementService managementService;
	@Autowired
	RuntimeService runtimeService;
	@Autowired
	HistoryService historyService;
	@Autowired
	RestResponseFactory restResponseFactory;
	
	@ApiOperation(value = "退回至上一节点", tags = { "Tasks" }, nickname = "jumpback")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "跳转成功，返回当前任务Task") })
	@RequestMapping(value = "/runtime/tasks/{taskId}/jumpback", method = RequestMethod.POST, produces = "application/json")
	public void jumpback(@ApiParam(name = "taskId", value = "当前任务TaskID") @PathVariable("taskId") String taskId,	HttpServletRequest request) {
		Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        org.activiti.bpmn.model.Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        String processInstanceId = currentTask.getProcessInstanceId();
                
        FlowNode currentNode = (FlowNode)process.getFlowElement(currentTask.getTaskDefinitionKey());
        String targetNodeID = backtoLastFlowNode(currentNode, processInstanceId);
        if(targetNodeID == null) {
        	throw new IllegalStateException("找不到上一节点，或当前节点是第一个节点，taskid" + taskId);
        }
        FlowNode targetNode = (FlowNode) process.getFlowElement(targetNodeID);
        backtoTask(currentTask, targetNode); 
	}
	
	@ApiOperation(value = "退回到发起人/第一个节点", tags = { "Tasks" }, nickname = "jumpBacktoFirst")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "跳转成功，返回当前任务Task") })
	@RequestMapping(value = "/runtime/tasks/{taskId}/jumpBacktoFirst", method = RequestMethod.POST, produces = "application/json")
	public void jumpBacktoFirst(@ApiParam(name = "taskId", value = "当前任务TaskID") @PathVariable("taskId") String taskId,HttpServletRequest request) {
		Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        org.activiti.bpmn.model.Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        FlowNode startActivity = (FlowNode)process.getInitialFlowElement();
        if (startActivity.getOutgoingFlows().size() != 1) {
            throw new IllegalStateException(
                    "start activity outgoing transitions cannot more than 1, now is : "
                            + startActivity.getOutgoingFlows().size());
        }
 
        SequenceFlow sequenceFlow = startActivity.getOutgoingFlows()
                .get(0);
        FlowNode targetNode = (FlowNode) sequenceFlow.getTargetFlowElement();
 
        if (!(targetNode instanceof UserTask)) {
        	throw new ActivitiException("第一个节点不是用户任务节点，不能退回！");
        }
        backtoTask(currentTask, targetNode); 
	}
	
	
	@ApiOperation(value = "跳转任务到指定节点", tags = { "Tasks" }, nickname = "jumptoNode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "跳转成功，返回当前任务Task") })
	@RequestMapping(value = "/runtime/tasks/{taskId}/jumpto/{toNode}", method = RequestMethod.POST, produces = "application/json")
	public void jumpto(@ApiParam(name = "taskId", value = "当前任务TaskID") @PathVariable("taskId") String taskId,
			@ApiParam(name = "toNode", value = "跳转目标节点ID") @PathVariable("toNode") String toNode,
			HttpServletRequest request) {
		Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		org.activiti.bpmn.model.Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId())
				.getMainProcess();
		// 跳转节点
		FlowNode targetNode = (FlowNode) process.getFlowElement(toNode);
		jumptoTask(currentTask, targetNode);
	}
	
	/**
	 * 照理说应该原路返回，待完善
	 * @param currentNode
	 * @return
	 
	private FlowNode lastFlowNode1(FlowNode currentNode){   
        if (currentNode.getIncomingFlows().size() != 1) {
            throw new IllegalStateException(currentNode.getName() 
                    		+ "的上一个节点不唯一, 数量为 : "
                            + currentNode.getOutgoingFlows().size());
        }
		SequenceFlow sequenceFlow = currentNode.getIncomingFlows().get(0);
        FlowNode targetNode = (FlowNode) sequenceFlow.getSourceFlowElement();
        if (!(targetNode instanceof UserTask)) {
            return lastFlowNode1(targetNode);
        }
        return targetNode;
    }  */
	
	/**
	 * 沿原路径返回
	 * @param currentNode
	 * @param processInstanceId
	 * @return
	 */
	private String backtoLastFlowNode(FlowNode currentNode, String processInstanceId) {
		List<HistoricTaskInstance> tasks  =	historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().list();
		for(HistoricTaskInstance task : tasks) {
        	if(!task.getTaskDefinitionKey().equals(currentNode.getId()) &&(task.getDeleteReason() == null || !task.getDeleteReason().equals(TaskJumpReasonEnum.BACK.toString()))) {
        		return task.getTaskDefinitionKey();
        	}
        }
        //之前没有则返回当前节点
        return null;
	}

	/**
	 * 跳转到节点
	 * 
	 * @param currentTask
	 * @param jumpNode
	 */
	private void jumptoTask(Task currentTask, FlowNode jumpNode) {
		// 删除当前运行任务
		String executionEntityId = managementService.executeCommand(new DeleteTaskCmdJump(currentTask.getId()));
		// 流程执行到来源节点
		managementService.executeCommand(new SetFLowNodeAndGoCmd(jumpNode, executionEntityId));

		System.out.println("任务" + currentTask.getId() + "跳转到" + jumpNode.getId() + jumpNode.getName());
	}

	// 删除当前运行时任务命令，并返回当前任务的执行对象id
	// 这里继承了NeedsActiveTaskCmd，主要时很多跳转业务场景下，要求不能时挂起任务。可以直接继承Command即可
	public class DeleteTaskCmdJump extends NeedsActiveTaskCmd<String> {
		private static final long serialVersionUID = 1L;

		public DeleteTaskCmdJump(String taskId) {
			super(taskId);
		}

		public String execute(CommandContext commandContext, TaskEntity currentTask) {
			// 获取所需服务
			TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl) commandContext.getTaskEntityManager();
			// 获取当前任务的来源任务及来源节点信息
			ExecutionEntity executionEntity = currentTask.getExecution();
			// 删除当前任务,来源任务
			taskEntityManager.deleteTask(currentTask, TaskJumpReasonEnum.JUMP.toString(), false, false);
			return executionEntity.getId();
		}

		public String getSuspendedTaskException() {
			return "挂起的任务不能跳转";
		}
	}
	
	/**
	 * 退回到节点
	 * @param currentTask
	 * @param jumpNode
	 */
	private void backtoTask(Task currentTask, FlowNode jumpNode) {
		// 删除当前运行任务
		String executionEntityId = managementService.executeCommand(new DeleteTaskCmdBack(currentTask.getId()));
		// 流程执行到来源节点
		managementService.executeCommand(new SetFLowNodeAndGoCmd(jumpNode, executionEntityId));

		System.out.println("任务" + currentTask.getId() + "跳转到" + jumpNode.getId() + jumpNode.getName());
	}
	
	public class DeleteTaskCmdBack extends NeedsActiveTaskCmd<String> {
		private static final long serialVersionUID = 1L;

		public DeleteTaskCmdBack(String taskId) {
			super(taskId);
		}

		public String execute(CommandContext commandContext, TaskEntity currentTask) {
			// 获取所需服务
			TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl) commandContext.getTaskEntityManager();
			// 获取当前任务的来源任务及来源节点信息
			ExecutionEntity executionEntity = currentTask.getExecution();
			// 删除当前任务,来源任务
			taskEntityManager.deleteTask(currentTask, TaskJumpReasonEnum.BACK.toString(), false, false);
			return executionEntity.getId();
		}

		public String getSuspendedTaskException() {
			return "挂起的任务不能跳转";
		}
	}

	// 根据提供节点和执行对象id，进行跳转命令
	public class SetFLowNodeAndGoCmd implements Command<Void> {
		private FlowNode flowElement;
		private String executionId;

		public SetFLowNodeAndGoCmd(FlowNode flowElement, String executionId) {
			this.flowElement = flowElement;
			this.executionId = executionId;
		}

		public Void execute(CommandContext commandContext) {
			// 获取目标节点的来源连线
			List<SequenceFlow> flows = flowElement.getIncomingFlows();
			if (flows == null || flows.size() < 1) {
				throw new ActivitiException("回退错误，目标节点没有来源连线");
			}
			// 随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
			ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
			executionEntity.setCurrentFlowElement(flows.get(0));
			commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
			return null;
		}
	}
	
	
	
}
