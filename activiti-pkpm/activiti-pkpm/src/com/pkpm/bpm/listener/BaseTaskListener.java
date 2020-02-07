package com.pkpm.bpm.listener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

public abstract class BaseTaskListener implements org.activiti.engine.delegate.TaskListener{
	
	protected Logger logger=LoggerFactory.getLogger(BaseTaskListener.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		TaskEntity taskEnt=(TaskEntity)delegateTask;
		String nodeId=taskEnt.getExecution().getActivityId();
		String actDefId=taskEnt.getProcessDefinitionId();
		
		//获取脚本类型
		int beforeScriptType=getBeforeScriptType();
		//执行事件脚本
		exeEventScript(delegateTask,beforeScriptType,actDefId,nodeId);
		
		logger.debug("enter the baseTaskListener notify method...");
		
		//执行子类业务逻辑
		execute(delegateTask,actDefId,nodeId);
		//获取脚本类型
		int scriptType=getScriptType();
		//执行事件脚本
		exeEventScript(delegateTask,scriptType,actDefId,nodeId);
		
	}
	
	/**
	 * 执行子类业务逻辑
	 * @param delegateTask
	 * @param actDefId
	 * @param nodeId
	 */
	protected abstract void execute(DelegateTask delegateTask,String actDefId,String nodeId);
	
	/**
	 * 获取脚本类型
	 * @return
	 */
	protected abstract int getScriptType();
	
	/**
	 * 执行前置脚本
	 * @return
	 */
	protected abstract int getBeforeScriptType();

	/**
	 * 执行事件脚本
	 * @param delegateTask
	 * @param scriptType
	 * @param actDefId
	 * @param nodeId
	 */
	private void exeEventScript(DelegateTask delegateTask,int scriptType,String actDefId,String nodeId ){
		logger.debug("enter the baseTaskListener exeEventScript method...");
		/*
		BpmNodeScriptService bpmNodeScriptService=(BpmNodeScriptService)AppUtil.getBean("bpmNodeScriptService");
	
		BpmNodeScript model=bpmNodeScriptService.getScriptByType(nodeId, actDefId,scriptType);
		if(model==null) return;
		
		String script=model.getScript();
		if(StringUtil.isEmpty(script)) return;
		
		String instId=delegateTask.getProcessInstanceId();
		//设置临时变量
		TaskThreadService.setTempLocal(instId);
		
		GroovyScriptEngine scriptEngine=(GroovyScriptEngine)AppUtil.getBean("scriptEngine");
		Map<String, Object> vars=delegateTask.getVariables();
		
		vars.put("task", delegateTask);
		scriptEngine.execute(script, vars);
		//恢复线程变量
		TaskThreadService.resetTempLocal(instId);
		*/
	}
	
	

	
	/**
	 * 获取执行人。
	 * @param list
	 * @return
	 */
	protected Set<TaskExecutor> getByTaskExecutors(List<TaskExecutor> list){
		Set<TaskExecutor> exSet=new LinkedHashSet<TaskExecutor>();
		for(TaskExecutor ex:list){
			List<TaskExecutor> tmp= getByTaskExecutor(ex);
			
			exSet.addAll(tmp);
		}
		return exSet;
	}
	
	
	
	/**
	 * 根据组执行人取得任务执行人。
	 * @param taskExecutor
	 * @return
	 */
	protected List<TaskExecutor> getByTaskExecutor(TaskExecutor taskExecutor){
		List<TaskExecutor> list=new ArrayList<TaskExecutor>();
		/*
		if(taskExecutor.getExactType()==TaskExecutor.EXACT_NOEXACT){
			list.add(taskExecutor);
		}
		else{
			List<SysUser> userList=BpmNodeUserUtil.getUserListByExecutor(taskExecutor);
			for(SysUser sysUser:userList){
				list.add(TaskExecutor.getTaskUser(sysUser.getUserId().toString(), sysUser.getUsername()));
			}
		}*/
		return list;
	}
	
	protected String getNotAssignMessage(TaskExecutor taskExecutor){
		String message="{0}:【{1}】没有配置人员!";
		return message;
		/*String type="";
		if(TaskExecutor.USER_TYPE_ORG.equals(taskExecutor.getType())){
			type="部门";
		}
		else if(TaskExecutor.USER_TYPE_POS .equals(taskExecutor.getType())){
			type="岗位";
		}
		else if(TaskExecutor.USER_TYPE_ROLE .equals(taskExecutor.getType())){
			type="角色";
		}
		return StringUtil.formatParamMsg(message, type,taskExecutor.getExecutor()).toString();*/
	}

	
	
}