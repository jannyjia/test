package org.activiti.engine.impl.persistence.entity.data.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.activiti.app.conf.ApplicationContextRegister;
import org.activiti.app.service.SsoService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.data.AbstractDataManager;
import org.activiti.engine.impl.persistence.entity.data.UserDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class SSOUserDataManager extends AbstractDataManager<UserEntity> implements UserDataManager {

	public SSOUserDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
		super(processEngineConfiguration);
	}

	@Override
	public UserEntity create() {
		// TODO Auto-generated method stub
		return new com.pkpm.bpm.identity.User();
	}

	@Override
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		SsoService ssoService = ApplicationContextRegister.getApplicationContext().getBean(SsoService.class);
		return ssoService.findUserByQueryCriteria(query, page);
	}

	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		SsoService ssoService = ApplicationContextRegister.getApplicationContext().getBean(SsoService.class);
		return ssoService.findUserCountByQueryCriteria(query);
	}

	@Override
	public List<Group> findGroupsByUser(String userId) {
		SsoService ssoService = ApplicationContextRegister.getApplicationContext().getBean(SsoService.class);
		return ssoService.findGroupsByUser(userId);
	}



	@Override
	public Class<? extends UserEntity> getManagedEntityClass() {
		// TODO Auto-generated method stub
		return com.pkpm.bpm.identity.User.class;
	}

	@Override
	public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		// TODO Auto-generated method stub
		return 0;
	}

}
