package com.pkpm.bpm.service;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.data.AbstractDataManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkpm.bpm.identity.Department;
import com.pkpm.db.mybatis.BaseDao;

@Service
public class BpmIdentityDepartmentService extends BaseDao<Department, Long> {

	
	@Override
	public Class<?> getEntityClass()
	{
		return Department.class;
	}
	
	
	

}
