package com.pkpm.bpm.identity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.db.HasRevision;
import org.activiti.engine.impl.persistence.entity.AbstractEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntity;

/**
 * 角色id
 * @author dell1
 *
 */
public class Role extends SSOGroup implements GroupEntity, Serializable {

	  private static final long serialVersionUID = 1L;
	  public Role() {
	  }

	}

