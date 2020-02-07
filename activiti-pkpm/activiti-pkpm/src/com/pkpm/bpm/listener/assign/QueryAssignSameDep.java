package com.pkpm.bpm.listener.assign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.stereotype.Component;

import com.pkpm.bpm.model.BpmNodeAssign;

/**
 * 发起人或指定人相同公司
 * @author wangjia
 *
 */
@Component("QueryAssignSameDep")
public class QueryAssignSameDep extends BaseQueryAssign{

	@Override
	public List<String> queryCandidateUsers(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> queryCandidateGroups(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		String userid = "";
		if(bpmNodeAssign.getAssignValue().equals("starter")) {
			userid = processInstance != null ? processInstance.getStartUserId() : variables.get("initiator").toString();
		}else {
			userid = bpmNodeAssign.getAssignValue();
		}
		List<String> groupIds = new ArrayList<String>();
		if(userid != null && !userid.equals("")) {
			List<Group> groups = ProcessEngines.getDefaultProcessEngine().getIdentityService().createGroupQuery().groupMember(userid).list();
			if(groups != null) {
				for(Group g : groups) {
					if(g.getId().startsWith("dep_")) {
						groupIds.add(g.getId());
					}
				}
			}
			
		}		
		
		return groupIds;
	}

}
