package com.pkpm.db.mybatis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.core.JdbcTemplate;

import com.pkpm.bpm.api.PageList;
import com.pkpm.bpm.api.PagerInfo;

public abstract class BaseDao <E, PK extends Serializable>
extends BaseMyBatisDao
{
	@Resource
	protected JdbcTemplate jdbcTemplate;
	
	@Resource
	Properties configproperties;
	
	public abstract Class getEntityClass();
	
    public String getIbatisMapperNamespace()
    {
    	return getEntityClass().getName();
    }
	static enum SortBy
	{
		ASC,  DESC;
	}
	
	protected String getDbType()
	{
	  return this.configproperties.getProperty("jdbc.dbType");
	}
	public List<E> getList(String statementName)
	  {	    
	    List<E> list = getSqlSessionTemplate().selectList( getIbatisMapperNamespace()+"."+statementName);	    
	    return list;
	  }
	
	public List<E> getList(String statementName, Object params, PagerInfo pageBean)
	  {
	    Map filters = new HashMap();
	    RowBounds rowBounds = new RowBounds(0, pageBean.getPage().getPageSize());	    
	    List<E> list = getSqlSessionTemplate().selectList(statementName, filters, rowBounds);
	    
	    PageList<E> pageList = new PageList();
	    pageList.addAll(list);
	    pageList.setPageBean(pageBean);
	    
	    return pageList;
	  }
	
	
}
