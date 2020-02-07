package com.pkpm.bpm.listener.assign;


import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;

import com.pkpm.bpm.model.BpmNodeAssign;

public abstract class BaseQueryAssign {
	public abstract List<String> queryCandidateUsers(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance, Map<String, Object> variables);
	
	public abstract List<String> queryCandidateGroups(BpmNodeAssign bpmNodeAssign, HistoricProcessInstance processInstance, Map<String, Object> variables);
}
