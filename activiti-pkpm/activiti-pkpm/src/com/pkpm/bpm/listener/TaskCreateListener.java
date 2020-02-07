package com.pkpm.bpm.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.app.conf.ApplicationContextRegister;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pkpm.bpm.listener.assign.BaseQueryAssign;
import com.pkpm.bpm.model.BpmNode;
import com.pkpm.bpm.model.BpmNodeAssign;
import com.pkpm.bpm.respository.BpmNodeRepository;

@Component
public class TaskCreateListener extends BaseTaskListener {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(DelegateTask delegateTask,String actDefId,String nodeId)  {
		if(nodeId != null) {
			BpmNodeRepository  bpmNodeRepository = ApplicationContextRegister.getApplicationContext().getBean(BpmNodeRepository.class);
			
			List<BpmNode> findNodes = bpmNodeRepository. findByNodeKey(nodeId);
			ProcessInstance processInstance= ProcessEngines.getDefaultProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
			HistoricProcessInstance processInstanceHistoric= ProcessEngines.getDefaultProcessEngine().
		    		  getHistoryService().createHistoricProcessInstanceQuery().
		    		  processInstanceId(delegateTask.getProcessInstanceId())
		    		  .unfinished().singleResult();
			Map<String, Object> variables = delegateTask.getVariables();
			for(BpmNode bpmNode : findNodes) {
				BpmNodeAssign bpmNodeAssign = JSONObject.parseObject(bpmNode.getAssignSettings(), BpmNodeAssign.class);
				BaseQueryAssign  queryAssign = (BaseQueryAssign)ApplicationContextRegister.getApplicationContext().getBean(bpmNodeAssign.getAssignType().getBeanName());
				delegateTask.addCandidateUsers(queryAssign.queryCandidateUsers(bpmNodeAssign, processInstanceHistoric, variables));
				delegateTask.addCandidateGroups(queryAssign.queryCandidateGroups(bpmNodeAssign, processInstanceHistoric, variables));
			}
		    return;
		}
		//delegateTask.get
		
		//设置任务状态。
		//delegateTask.setDescription(TaskOpinion.STATUS_CHECKING.toString());

		//生成任务签批意见
		addOpinion(delegateTask);
		
		Long actInstanceId=new Long ( delegateTask.getProcessInstanceId());
		
		//启动流程时添加或保存状态数据。
		//bpmProStatusService.addOrUpd(actDefId, actInstanceId,nodeId);
		
		Map<String,List<TaskExecutor>> nodeUserMap=new HashMap<String,List<TaskExecutor>>(); //taskUserAssignService.getNodeUserMap();
		//处理任务分发。
		boolean isHandForkTask=handlerForkTask(actDefId,nodeId,nodeUserMap,delegateTask);
		if(isHandForkTask) return;
		
		//boolean isSubProcess=handSubProcessUser(delegateTask);
		//if(isSubProcess) return;
		//处理外部子流程用户。
		//boolean isHandExtUser= handExtSubProcessUser(delegateTask);
		//if(isHandExtUser) return;
		
		//在上一步中指定了该任务的执行人员
		if(nodeUserMap!=null && nodeUserMap.get(nodeId)!=null){
			List<TaskExecutor> executorIds=nodeUserMap.get(nodeId);
			assignUser(delegateTask,executorIds);
			return;
		}
		
		//List<TaskExecutor> executorUsers = taskUserAssignService.getExecutors();
		//当前执行人。
		//if(BeanUtils.isNotEmpty(executorUsers)){
		//	assignUser(delegateTask,executorUsers);
		//	return;
		//}
		//处理从数据库加载用户，并进行分配。
		handAssignUserFromDb(actDefId,nodeId,delegateTask);
	}
	
	
	/**
	 * 添加流程任务意见。
	 * @param token
	 * @param delegateTask
	 */
	private void addOpinion(DelegateTask delegateTask){
		/*
		TaskOpinion taskOpinion=new TaskOpinion(delegateTask);
		taskOpinion.setOpinionId(UniqueIdUtil.genId());
		taskOpinion.setTaskToken(token);
		taskOpinionService.add(taskOpinion);*/
	}
	
	/**
	 * 从数据库加载人员并分配用户。
	 * @param actDefId
	 * @param nodeId
	 * @param delegateTask
	 */
	private void handAssignUserFromDb(String actDefId,String nodeId,DelegateTask delegateTask){
		/*
		BpmNodeUserService userService=(BpmNodeUserService) AppUtil.getBean(BpmNodeUserService.class);
		
		String actInstId=delegateTask.getProcessInstanceId();
		
		ProcessInstance processInstance=bpmService.getProcessInstance(actInstId);
		List<TaskExecutor> users=null; 
		//获取流程变量。
		Map<String,Object> vars=delegateTask.getVariables();
		
		vars.put(BpmConst.EXECUTION_ID_, delegateTask.getExecutionId());
		//执行任务的情况
		if(processInstance!=null){
			//获取上个任务的执行人，这个执行人在上一个流程任务的完成事件中进行设置。
			//代码请参考TaskCompleteListener。
			String startUserId=(String)vars.get(BpmConst.StartUser);
			
			String preStepUserId=ContextUtil.getCurrentUserId().toString();
			Long preStepOrgId=ContextUtil.getCurrentOrgId();
			vars.put(BpmConst.PRE_ORG_ID, preStepOrgId);
			
			if(StringUtil.isEmpty(startUserId) && vars.containsKey(BpmConst.PROCESS_INNER_VARNAME)){
				Map<String,Object> localVars=(Map<String,Object>)vars.get(BpmConst.PROCESS_INNER_VARNAME);
				startUserId=(String)localVars.get(BpmConst.StartUser);
			}
			
			users=userService.getExeUserIds(actDefId, actInstId, nodeId, startUserId,preStepUserId,vars);
		}
		//启动流程
		else{
			//startUser
			//上个节点的任务执行人
			String startUserId=(String)vars.get(BpmConst.StartUser);
			//内部子流程启动
			if(StringUtil.isEmpty(startUserId) && vars.containsKey(BpmConst.PROCESS_INNER_VARNAME)){
				Map<String,Object> localVars=(Map<String,Object>)vars.get(BpmConst.PROCESS_INNER_VARNAME);
				startUserId=(String)localVars.get(BpmConst.StartUser);
			}
			users=userService.getExeUserIds(actDefId, actInstId, nodeId, startUserId, startUserId,vars);
		}
		assignUser(delegateTask,users);*/
	}
	
	/**
	 * 处理任务分发。
	 * <pre>
	 * 	1.根据指定的用户产生新的任务，并指定了相应的excution，任务历史数据。
	 * 		支持用户独立的往下执行，不像会签的方式需要等待其他的任务完成才往下执行。
	 *  2.产生分发记录。
	 *   
	 * </pre>
	 * @param actDefId			流程定义ID
	 * @param nodeId			流程节点ID
	 * @param nodeUserMap		上下文指定的分发用户。
	 * @param delegateTask		任务对象。
	 * @return
	 */
	private boolean handlerForkTask(String actDefId,String nodeId,Map<String,List<TaskExecutor>> nodeUserMap,DelegateTask delegateTask){
		return false;
		/*
		//若任务进行回退至分发任务节点上，则不再进行任务分发
		ProcessCmd processCmd=TaskThreadService.getProcessCmd();
		if(processCmd!=null && BpmConst.TASK_BACK.equals(processCmd.isBack())) return false;
		BpmNodeSet bpmNodeSet=bpmNodeSetDao.getByActDefIdNodeId(actDefId, nodeId);
		//当前任务为分发任务,即根据当前分发要求进行生成分发任务
		if(bpmNodeSet!=null && BpmNodeSet.NODE_TYPE_FORK.equals(bpmNodeSet.getNodeType())){
			List<TaskExecutor> taskExecutors=taskUserAssignService.getExecutors();
			//若当前的线程里包含了该任务对应的执行人员列表，则任务的分发用户来自于此
			if(BeanUtils.isEmpty(taskExecutors)){
				//若当前的线程里包含了该任务对应的执行人员列表，则任务的分发用户来自于此
				if(nodeUserMap!=null && nodeUserMap.get(nodeId)!=null){
					taskExecutors=nodeUserMap.get(nodeId);
				}
				//否则，从数据库获取人员设置
				else{
					BpmNodeUserService userService=(BpmNodeUserService) AppUtil.getBean(BpmNodeUserService.class);
					ProcessInstance processInstance=bpmService.getProcessInstance(delegateTask.getProcessInstanceId());
					if(processInstance!=null){
						Map<String,Object> vars=delegateTask.getVariables();
						vars.put("executionId", delegateTask.getExecutionId());
						String preTaskUser=ContextUtil.getCurrentUserId().toString();
						String actInstId=delegateTask.getProcessInstanceId();
						String startUserId=(String)delegateTask.getVariable(BpmConst.StartUser);
						taskExecutors = userService.getExeUserIds(actDefId, actInstId, nodeId, startUserId, preTaskUser, vars);
					}
				}
			}
			if(BeanUtils.isNotEmpty(taskExecutors)){
				bpmService.newForkTasks((TaskEntity)delegateTask, taskExecutors);
				taskForkService.newTaskFork(delegateTask,bpmNodeSet.getJoinTaskName(), bpmNodeSet.getJoinTaskKey(), taskExecutors.size());
			}
			else{
				ProcessRun processRun= processRunService.getByActInstanceId(new Long( delegateTask.getProcessInstanceId()));
			    String msg=processRun.getSubject() + "请设置分发人员";
			    MessageUtil.addMsg(msg);
				throw new RuntimeException(msg);
				
			}
			
			return true;	
		}
		return false;*/
	}
	

	
	/**
	 * 分配用户执行人或候选人组。
	 * @param delegateTask
	 * @param taskExecutor
	 */
	private void assignUser(DelegateTask delegateTask, TaskExecutor taskExecutor){
		/*
		if(TaskExecutor.USER_TYPE_USER.equals(taskExecutor.getType())){
			delegateTask.setOwner(taskExecutor.getExecuteId());
			
			Long sysUserId = Long.valueOf(taskExecutor.getExecuteId());
			SysUser sysUser =null;
			
			//取代理用户
			if(isAllowAgent()){
				sysUser = agentSettingService.getAgent(delegateTask,sysUserId);
			}
			
			if(sysUser!=null){
				delegateTask.setAssignee(sysUser.getUserId().toString());
				delegateTask.setDescription(TaskOpinion.STATUS_AGENT.toString());
				delegateTask.setOwner(taskExecutor.getExecuteId());
			}else{
				delegateTask.setAssignee(taskExecutor.getExecuteId());
			}
			TaskOpinion taskOpinion= taskOpinionService.getByTaskId(new Long(delegateTask.getId()));
			SysUser exeUser= sysUserDao.getById(sysUserId);
			taskOpinion.setExeUserId(exeUser.getUserId());
			taskOpinion.setExeFullname(exeUser.getFullname());
			taskOpinionService.update(taskOpinion);
		}
		else{
			delegateTask.setAssignee(BpmConst.EMPTY_USER);
			delegateTask.setOwner(BpmConst.EMPTY_USER);
			List<TaskExecutor> userList= getByTaskExecutor(taskExecutor);
			for(TaskExecutor ex:userList){
				if(ex.getType().equals(TaskExecutor.USER_TYPE_USER)){
					delegateTask.addCandidateUser(ex.getExecuteId());
				}
				else{
					delegateTask.addGroupIdentityLink(ex.getExecuteId(),ex.getType());
				}
			}
		}*/
	}
	
	/**
	 * 分配任务执行人。
	 * @param delegateTask
	 * @param users
	 */
	private void assignUser(DelegateTask delegateTask, List<TaskExecutor> executors){
		/*
		if(BeanUtils.isEmpty(executors)){
			String msg="节点:" + delegateTask.getName() +",没有设置执行人";
			MessageUtil.addMsg(msg);
			throw new RuntimeException(msg);
		} 
			
		//只有一个人的情况。
		if(executors.size()==1){
			TaskExecutor taskExecutor=executors.get(0);
			
			if(TaskExecutor.USER_TYPE_USER.equals(taskExecutor.getType())){
				
				//是否是流程启动，并跳过第一个节点
				Long sysUserId = Long.valueOf(taskExecutor.getExecuteId());
				SysUser sysUser =null;
				//取代理用户
				if(isAllowAgent()){
					sysUser = agentSettingService.getAgent(delegateTask,sysUserId);
				}
				//有代理人员的情况
				if(sysUser!=null){
					delegateTask.setAssignee(sysUser.getUserId().toString());
					delegateTask.setDescription(TaskOpinion.STATUS_AGENT.toString());
					delegateTask.setOwner(taskExecutor.getExecuteId());
				}else{
					delegateTask.setAssignee(taskExecutor.getExecuteId());
				}
				
				TaskOpinion taskOpinion= taskOpinionService.getByTaskId(new Long(delegateTask.getId()));
				sysUser= sysUserDao.getById(sysUserId);
				taskOpinion.setExeUserId(sysUser.getUserId());
				taskOpinion.setExeFullname(sysUser.getFullname());
				
				taskOpinionService.update(taskOpinion);
			}
			else{
				delegateTask.setAssignee(BpmConst.EMPTY_USER);
				delegateTask.setOwner(BpmConst.EMPTY_USER);
				List<TaskExecutor> list=getByTaskExecutor(taskExecutor);
				if(BeanUtils.isEmpty(list)){
					String msg=getNotAssignMessage(taskExecutor);
					MessageUtil.addMsg(msg);
				}
				for(TaskExecutor ex:list){
					if(ex.getType().equals(TaskExecutor.USER_TYPE_USER)){
						delegateTask.addCandidateUser(ex.getExecuteId());
					}
					else{
						delegateTask.addGroupIdentityLink(ex.getExecuteId(), ex.getType());
					}
				}
			}
		}
		else{
			delegateTask.setAssignee(BpmConst.EMPTY_USER);
			delegateTask.setOwner(BpmConst.EMPTY_USER);
			
			Set<TaskExecutor> set=getByTaskExecutors(executors);
			if(BeanUtils.isEmpty(set)){
				String msg="没有设置人员,请检查人员配置!";
				MessageUtil.addMsg(msg);
				throw new RuntimeException(msg);
			}
			for(Iterator<TaskExecutor> it=set.iterator();it.hasNext();){
				TaskExecutor ex=it.next();
				if(ex.getType().equals(TaskExecutor.USER_TYPE_USER)){
					delegateTask.addCandidateUser(ex.getExecuteId());
				}
				else{
					delegateTask.addGroupIdentityLink(ex.getExecuteId(), ex.getType());
				}
			}
		}*/
	}
	
	
	

	@Override
	protected int getScriptType() {
		return 0;
	}

	@Override
	protected int getBeforeScriptType() {
		return 0;
	}
	

	
	
}
