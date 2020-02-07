package com.pkpm.bpm.identity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.AbstractEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntity;

public class Department extends SSOGroup implements GroupEntity, Serializable {

	  private static final long serialVersionUID = 1L;

	  protected String name;
	  protected String type;

	  public Department() {
	  }

	}
