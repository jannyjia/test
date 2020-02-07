package com.pkpm.bpm.identity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.AbstractEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntity;

public class SSOGroup extends AbstractEntity implements GroupEntity, Serializable {
	
	  private static final long serialVersionUID = 1L;

	  protected String name;
	  protected String type;

	  public SSOGroup() {
	  }

	  public Object getPersistentState() {
	    Map<String, Object> persistentState = new HashMap<String, Object>();
	    persistentState.put("name", name);
	    persistentState.put("type", type);
	    return persistentState;
	  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public String getType() {
	    return type;
	  }

	  public void setType(String type) {
	    this.type = type;
	  }

}
