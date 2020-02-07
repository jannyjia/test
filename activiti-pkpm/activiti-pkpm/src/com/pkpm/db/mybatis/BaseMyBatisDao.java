package com.pkpm.db.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public abstract class BaseMyBatisDao extends DaoSupport
{
	  protected final Log log = LogFactory.getLog(getClass());
	  @Autowired
	  private SqlSessionFactory sqlSessionFactory;
	  @Autowired
	  private SqlSessionTemplate sqlSessionTemplate;
	  
	  protected void checkDaoConfig()
	    throws IllegalArgumentException
	  {
	    Assert.notNull(this.sqlSessionFactory, "sqlSessionFactory must be not null");
	  }
	  
	  public SqlSessionFactory getSqlSessionFactory()
	  {
	    return this.sqlSessionFactory;
	  }
	  
	  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory)
	  {
	    this.sqlSessionFactory = sqlSessionFactory;
	    this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
	  }
	  
	  public SqlSessionTemplate getSqlSessionTemplate()
	  {
	    return this.sqlSessionTemplate;
	  }
	  
	}
