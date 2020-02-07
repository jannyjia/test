package com.pkpm.bpm.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
@Component
public class TaskAssignListener extends BaseTaskListener {
	
	@Override
	protected void execute(DelegateTask delegateTask, String actDefId,
			String nodeId) {
		String userId=delegateTask.getAssignee();
		logger.debug("任务ID:" + delegateTask.getId());
		
		delegateTask.setOwner(userId);

		
	}

	@Override
	protected int getScriptType() {
		 
		//return BpmConst.AssignScript;
		return 0;
	}

	@Override
	protected int getBeforeScriptType() {
		// TODO Auto-generated method stub
		return 0;
	}

}