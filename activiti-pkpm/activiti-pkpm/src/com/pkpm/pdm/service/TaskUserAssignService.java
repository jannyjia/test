package com.pkpm.pdm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.pkpm.pdm.enums.BpmConst;

@Service
public class TaskUserAssignService   {
	
	/**
	 * 目标节点人员授权绑定 里面存的值为nodeId,List userIds。
	 */
	private static ThreadLocal<Map<String,List<TaskExecutor>>> nodeUserMapLocal=new ThreadLocal<Map<String,List<TaskExecutor>>>();
	/**
	 * 任务执行人。
	 */
	private static ThreadLocal<List<TaskExecutor>> taskExecutors=new ThreadLocal<List<TaskExecutor>>();
	
	public List<TaskExecutor> getSignUser(ExecutionEntity execution) throws Exception {
		String nodeId=execution.getCurrentActivityId();
		String nodeName=(String)execution.getName();
		String multiInstance="sequential";//(String)execution.;
		
		List<TaskExecutor> userIds=null;
		
		//String varName=nodeId +"_" +BpmConst.SIGN_USERIDS;
		//串行会签人员首先从流程变量中获取。
		if("sequential".equals(multiInstance)){
			//userIds=(List<TaskExecutor>)execution.getVariable(varName);
			//if(userIds!=null) {
			//	return userIds;
			//}
		}
		
		Map<String,List<TaskExecutor>> nodeUserMap=nodeUserMapLocal.get();
		
		//会签任务用户来自前台的用户选择
		if(nodeUserMap!=null && nodeUserMap.containsKey(nodeId) && nodeUserMap.get(nodeId) != null){
			userIds=nodeUserMap.get(nodeId);
			saveExecutorVar(execution,userIds);
			return userIds;
		}
		
		userIds=getExecutors();
		
		if(userIds!=null && userIds.size()>0){
			saveExecutorVar(execution,userIds);
			addNodeUser(nodeId, userIds);
			return userIds;
		}
		//从数据库配置中获取。
		ExecutionEntity ent=(ExecutionEntity)execution;
		String actDefId=ent.getProcessDefinitionId();
		//获取发起用户。
		String actInstId=execution.getProcessInstanceId();
		//Map<String,Object> variables= execution.getVariables();
		//String startUserId=variables.get(BpmConst.StartUser).toString();
		String preTaskUser="admin";//ContextUtil.getCurrentUserId().toString();
		List<TaskExecutor> list=new ArrayList<TaskExecutor>();//bpmNodeUserService.getExeUserIds(actDefId, actInstId, nodeId, startUserId, preTaskUser, variables);
		
		if(list== null || list.size()==0){
			//throw new Exception("请设置会签节点:[" + nodeName  +"]的人员!");
		}
		if(list== null || list.size()==0){
			//saveExecutorVar(execution,list);
		}
		//addNodeUser(nodeId, list);
		
		return list;
			
	}
	
	/**
	 * 添加节点人员。
	 * @param nodeId
	 * @param userIds
	 */
	public void addNodeUser(String nodeId,List<TaskExecutor> executors){
		if(executors == null || executors.size()==0) return;
	
		Map<String,List<TaskExecutor>> nodeUserMap=nodeUserMapLocal.get();
		if(nodeUserMap==null) nodeUserMap=new HashMap<String,List<TaskExecutor>>();
		nodeUserMap.remove(nodeId);
		nodeUserMap.put(nodeId, executors);
		nodeUserMapLocal.set(nodeUserMap);
	}
	
	/**
	 * 
	 * @param execution
	 * @param userIds
	 */
	private void saveExecutorVar(ExecutionEntity execution,List<TaskExecutor> userIds){
		String multiInstance="sequential";//(String)execution.getActivity().getProperty("multiInstance");
		if("sequential".equals(multiInstance)){
			String nodeId=execution.getCurrentActivityId();
			String varName=nodeId +"_" +BpmConst.SIGN_USERIDS;
			execution.setVariable(varName, userIds);
		}
		
	}
	
	/**
	 * 获取任务执行人。
	 * @return
	 */
	public List<TaskExecutor> getExecutors() {
		return taskExecutors.get(); 
	}
}
