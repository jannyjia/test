package com.pkpm.bpm.listener.assign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pkpm.bpm.model.BpmNodeAssign;

/**
 * 指定省市州
 * @author wangjia
 *
 */
@Component("QueryAssignArea")
public class QueryAssignArea extends BaseQueryAssign{

	@Override
	public List<String> queryCandidateUsers(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> queryCandidateGroups(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance,
			Map<String, Object> variables) {
		List<String> groupids = JSONObject.parseArray(bpmNodeAssign.getAssignValue(), String.class);
		List<String> newgroupids = new ArrayList<String>();
		for(String groupid : groupids) {
			newgroupids.add("area_" + groupid);
		}
		return newgroupids;
	}

}
