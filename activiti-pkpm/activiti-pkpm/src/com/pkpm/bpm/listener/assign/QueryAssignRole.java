package com.pkpm.bpm.listener.assign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;

import com.pkpm.bpm.model.BpmNodeAssign;

/**
 * 指定角色
 * @author wangjia
 *
 */
@Component("QueryAssignRole")
public class QueryAssignRole extends BaseQueryAssign{

	@Override
	public List<String> queryCandidateUsers(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> queryCandidateGroups(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		List<String> newgroupids = new ArrayList<String>();
		newgroupids.add(bpmNodeAssign.getAssignValue());
		return newgroupids;
	}

}
