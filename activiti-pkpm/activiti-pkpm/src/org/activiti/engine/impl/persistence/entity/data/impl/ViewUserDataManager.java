package org.activiti.engine.impl.persistence.entity.data.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.app.conf.ApplicationContextRegister;
import org.activiti.engine.identity.Group;
import com.pkpm.bpm.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.data.AbstractDataManager;
import org.activiti.engine.impl.persistence.entity.data.UserDataManager;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewUserDataManager extends AbstractDataManager<UserEntity> implements UserDataManager{
	public ViewUserDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {		 
		super(processEngineConfiguration);
	}

	@Override
	public UserEntity create() {
		return new User();
	}

	  @SuppressWarnings("unchecked")
	  public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
	    return getDbSqlSession().selectList("selectViewUserByQueryCriteria", query, page);
	  }
	
	  public long findUserCountByQueryCriteria(UserQueryImpl query) {
	    return (Long) getDbSqlSession().selectOne("selectViewUserCountByQueryCriteria", query);
	  }
	
	  @SuppressWarnings("unchecked")
	  public List<Group> findGroupsByUser(String userId) {
	    return getDbSqlSession().selectList("selectGroupsByUserId", userId);
	  }
	
	  @SuppressWarnings("unchecked")
	  public List<org.activiti.engine.identity.User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
	    return getDbSqlSession().selectListWithRawParameter("selectViewUserByNativeQuery", parameterMap, firstResult, maxResults);
	  }
	
	  public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
	    return (Long) getDbSqlSession().selectOne("selectViewUserCountByNativeQuery", parameterMap);
	  }
	  
	@Override
	public Class<? extends UserEntity> getManagedEntityClass() {
		return User.class;
	}

}
