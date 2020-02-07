package com.pkpm.bpm.listener.assign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;

import com.pkpm.bpm.model.BpmNodeAssign;

/**
 * 发起人
 * @author wangjia
 *
 */
@Component("QueryAssignStarter")
public class QueryAssignStarter extends BaseQueryAssign {

	@Override
	public List<String> queryCandidateUsers(BpmNodeAssign nodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		List<String> userids = new ArrayList<String>();
		if(processInstance != null) {
			userids.add(processInstance.getStartUserId());
		}else {
			userids.add(variables.get("initiator").toString());
		}		
		
		return userids;
	}

	@Override
	public List<String> queryCandidateGroups(BpmNodeAssign nodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		return new ArrayList<String>();
	}



}
