package com.pkpm.bpm.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
@Component
public class TaskCompleteListener extends BaseTaskListener {

	//@Resource
	//private CalendarAssignService calendarAssignService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;

	@Override
	protected void execute(DelegateTask delegateTask, String actDefId, String nodeId) {

		//为了解决在任务自由跳转或回退时，若流程实例有多个相同Key的任务，会把相同的任务删除。
		//ProcessCmd processCmd = TaskThreadService.getProcessCmd();
		//if (processCmd != null && (processCmd.isBack() > 0 || StringUtils.isNotEmpty(processCmd.getDestTask()))) {
		//	taskDao.updateNewTaskDefKeyByInstIdNodeId(delegateTask.getTaskDefinitionKey() + "_1", delegateTask.getTaskDefinitionKey(), delegateTask.getProcessInstanceId());
		//}
		//更新执行堆栈里的执行人员及完成时间等
		updateExecutionStack(delegateTask.getProcessInstanceId(), delegateTask.getTaskDefinitionKey(), "");
		//更新任务意见。
		updOpinion(delegateTask);
		//更新流程节点状态。
		updNodeStatus(nodeId, delegateTask);
		//更新历史节点
		setActHisAssignee(delegateTask);
	}

	/**
	 * 更新执行堆栈里的执行人员及完成时间等
	 * 
	 * @param instanceId
	 *            流程实例ID
	 * @param nodeId
	 *            节点IDeas
	 * @param token
	 *            　令牌
	 */
	private void updateExecutionStack(String instanceId, String nodeId, String token) {
		/*
		ExecutionStack executionStack = executionStackDao.getLastestStack(instanceId, nodeId, token);
		if (executionStack != null) {
			SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
			String userId = "";
			if (curUser != null) {
				userId = curUser.getUserId().toString();
			} else {
				userId = SystemConst.SYSTEMUSERID.toString();
			}
			executionStack.setAssignees(userId);
			executionStack.setEndTime(new Date());
			executionStackDao.update(executionStack);
		}*/

	}

	/**
	 * 根据流程节点的状态。
	 * 
	 * @param nodeId
	 * @param delegateTask
	 */
	private void updNodeStatus(String nodeId, DelegateTask delegateTask) {
		/*
		boolean isMuliti = BpmUtil.isMultiTask(delegateTask);
		//非会签节点,更新节点的状态。
		if (!isMuliti) {
			Map<String, Object> map = runtimeService.getVariables(delegateTask.getProcessInstanceId());
			String actInstanceId = delegateTask.getProcessInstanceId();
			//更新节点状态。
			Short approvalStatus = (Short) map.get(BpmConst.NODE_APPROVAL_STATUS + "_" + delegateTask.getTaskDefinitionKey());
			bpmProStatusDao.updStatus(new Long(actInstanceId), nodeId, approvalStatus);
		}*/
	}

	/**
	 * 修改当前任务意见。
	 * 
	 * @param delegateTask
	 */
	private Long updOpinion(DelegateTask delegateTask) {
			

		return 0L;
	}



	@Override
	protected int getScriptType() {
		return 0;
	}

	private void setActHisAssignee(DelegateTask delegateTask) {
		/*
		ExecutionExtDao executionExtDao = (ExecutionExtDao) AppUtil.getBean(ExecutionExtDao.class);
		DelegateExecution delegateExecution = delegateTask.getExecution();
		String parentId = delegateExecution.getParentId();

		//		String executionId=delegateTask.getExecutionId();
		String nodeId = delegateTask.getTaskDefinitionKey();

		//			List<HistoricActivityInstanceEntity> hisList = historyActivityDao.getByExecutionId(executionId, nodeId);
		//			hisList = historyActivityDao.getByExecutionId(parentId, nodeId);
		List<HistoricActivityInstanceEntity> hisList = null;
		DelegateExecution execution = delegateExecution;
		while (execution != null) {
			hisList = historyActivityDao.getByExecutionId(execution.getId(), nodeId);
			if (BeanUtils.isNotEmpty(hisList)) {
				break;
			}
			parentId = execution.getParentId();
			if (StringUtil.isEmpty(parentId)) {
				execution = null;
			} else {
				execution = executionExtDao.getById(parentId);
			}

		}
		if (BeanUtils.isEmpty(hisList)) {
			return;
		}

		SysUser curUser = (SysUser) ContextUtil.getCurrentUser();
		if (curUser == null) {
			return;
		}
		String assignee = curUser.getUserId().toString();
		for (HistoricActivityInstanceEntity hisActInst : hisList) {
			//流转任务正常流转，即无别人干预
			if (TaskOpinion.STATUS_COMMON_TRANSTO.toString().equals(delegateTask.getDescription())) {
				taskService.setAssignee(delegateTask.getId(), delegateTask.getAssignee());
				hisActInst.setAssignee(delegateTask.getAssignee());
			} else {
				//任务执行人为空或者 执行人和当前人不一致，设置当前人为任务执行人。如果任务已结束（endTime不为空），那么就不需要更新记录了
				if ((StringUtil.isEmpty(hisActInst.getAssignee()) || !hisActInst.getAssignee().equals(assignee)) && hisActInst.getEndTime() == null) {
					taskService.setAssignee(delegateTask.getId(), assignee);
					hisActInst.setAssignee(assignee);
				}
			}
			historyActivityDao.update(hisActInst);
		}*/
	}

	@Override
	protected int getBeforeScriptType() {
		return 0;
	}
}
