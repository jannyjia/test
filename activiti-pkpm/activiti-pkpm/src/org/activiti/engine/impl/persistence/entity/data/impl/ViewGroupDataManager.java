package org.activiti.engine.impl.persistence.entity.data.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityImpl;
import org.activiti.engine.impl.persistence.entity.data.AbstractDataManager;
import org.activiti.engine.impl.persistence.entity.data.GroupDataManager;

public class ViewGroupDataManager extends AbstractDataManager<GroupEntity> implements GroupDataManager {

	  public ViewGroupDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
	    super(processEngineConfiguration);
	  }

	  @Override
	  public Class<? extends GroupEntity> getManagedEntityClass() {
	    return GroupEntityImpl.class;
	  }
	  
	  @Override
	  public GroupEntity create() {
	    return new GroupEntityImpl();
	  }
	  
	  @SuppressWarnings("unchecked")
	  public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
	    return getDbSqlSession().selectList("selectViewGroupByQueryCriteria", query, page);
	  }

	  public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
	    return (Long) getDbSqlSession().selectOne("selectViewGroupCountByQueryCriteria", query);
	  }

	  @SuppressWarnings("unchecked")
	  public List<Group> findGroupsByUser(String userId) {
	    return getDbSqlSession().selectList("selectViewGroupsByUserId", userId);
	  }

	  @SuppressWarnings("unchecked")
	  public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
	    return getDbSqlSession().selectListWithRawParameter("selectViewGroupByNativeQuery", parameterMap, firstResult, maxResults);
	  }

	  public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
	    return (Long) getDbSqlSession().selectOne("selectGroupCountByNativeQuery", parameterMap);
	  }
	  
	}

