package com.pkpm.bpm.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BPM_VARIABLES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BpmVariables {
private static final long serialVersionUID = 1L;
	
	@NotNull
    @Id
    @Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotNull	
    @Column(name = "NODEID")
	private int nodeId;
	
	@Size(min = 0, max = 50)
    @Column(name = "VARNAME")
	private String varName;
	
	@Size(min = 0, max = 50)
    @Column(name = "VARKEY")
	private String varKey;
	
	@Size(min = 0, max = 50)
    @Column(name = "VARDATATYPE")
	private String varDatatype;
	
	@Size(min = 0, max = 200)
    @Column(name = "DEFAULTVALUE")
	private String defaultValue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarKey() {
		return varKey;
	}

	public void setVarKey(String varKey) {
		this.varKey = varKey;
	}

	public String getVarDatatype() {
		return varDatatype;
	}

	public void setVarDatatype(String varDatatype) {
		this.varDatatype = varDatatype;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	

}
