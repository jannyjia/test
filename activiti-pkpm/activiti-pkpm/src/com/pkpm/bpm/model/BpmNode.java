package com.pkpm.bpm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BPM_NODE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BpmNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Id
	@Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// @NotNull
	// @Column(name = "DEFINITIONID")
	private int definitionId;
	//@NotFound(action=NotFoundAction.IGNORE)
	//@JSONField(serialize = false)
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "DEFINITIONID")
	//private BpmDefinition bpmDefinition;

	@Size(min = 0, max = 50)
	@Column(name = "NODEKEY")
	private String nodeKey;

	@Size(min = 0, max = 200)
	@Column(name = "NODENAME")
	private String nodeName;

	@Size(min = 0, max = 4000)
	@Column(name = "ASSIGNSETTINGS")
	private String assignSettings = "{}";

	@Size(min = 0, max = 4000)
	@Column(name = "FORMSETTINGS")
	private String formSettings = "{}";

	@Size(min = 0, max = 4000)
	@Column(name = "BUTTONSETTINGS")
	private String buttonSettings = "[]";

    
    @Transient  
	private List<BpmVariables> bpmVariables;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(int definitionId) {
		this.definitionId = definitionId;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getAssignSettings() {
		return assignSettings;
	}

	public void setAssignSettings(String assignSettings) {
		this.assignSettings = assignSettings;
	}

	public String getFormSettings() {
		return formSettings;
	}

	public void setFormSettings(String formSettings) {
		this.formSettings = formSettings;
	}

	public String getButtonSettings() {
		return buttonSettings;
	}

	public void setButtonSettings(String buttonSettings) {
		this.buttonSettings = buttonSettings;
	}
	
	public List<BpmVariables> getBpmVariables() {
		return bpmVariables;
	}

	public void setBpmVariables(List<BpmVariables> bpmVariables) {
		this.bpmVariables = bpmVariables;
	}

}
