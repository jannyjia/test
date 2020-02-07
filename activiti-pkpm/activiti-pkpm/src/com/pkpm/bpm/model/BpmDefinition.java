package com.pkpm.bpm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "BPM_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BpmDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Id
	@Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 0, max = 50)
	@Column(name = "CATEGORYID")
	private String categoryId;

	@Size(min = 0, max = 100)
	@Column(name = "DEFNAME")
	private String defName;

	@Size(min = 0, max = 100)
	@Column(name = "DEFKEY")
	private String defKey;

	@Size(min = 0, max = 300)
	@Column(name = "TITLE")
	private String title;

	@Size(min = 0, max = 500)
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "STATUS")
	private int status;

	@Lob
	@Column(name = "BPMNXML", length = 65535)
	private String bpmnXml;

	@Column(name = "CREATEDATE")
	private Date createDate;

	@Column(name = "UPDATEDATE")
	private Date updateDate;

	@Size(min = 0, max = 50)
	@Column(name = "CREATEBY")
	private String createBy;

	@Size(min = 0, max = 50)
	@Column(name = "UPDATEBY")
	private String updateBy;

	@Column(name = "VERSION")
	private int version;

	@Size(min = 0, max = 255)
	@Column(name = "HELP")
	private String help;

	@Size(min = 0, max = 4000)
	@Column(name = "OTHERSETTINGS")
	private String otherSettings;

	@Size(min = 0, max = 50)
	@Column(name = "DEMODELID")
	private String deModelId;
	
	@Size(min = 0, max = 500)
	@Column(name = "FORMURL")
	private String formUrl;
	//@NotFound(action=NotFoundAction.IGNORE)
	//@OneToMany(mappedBy = "bpmDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    @Transient  
	private List<BpmNode> bpmNodes;

	public String getDeModelId() {
		return deModelId;
	}

	public void setDeModelId(String deModelId) {
		this.deModelId = deModelId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getDefName() {
		return defName;
	}

	public void setDefName(String defName) {
		this.defName = defName;
	}

	public String getDefKey() {
		return defKey;
	}

	public void setDefKey(String defKey) {
		this.defKey = defKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBpmnXml() {
		return bpmnXml;
	}

	public void setBpmnXml(String bpmnXml) {
		this.bpmnXml = bpmnXml;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public String getOtherSettings() {
		return otherSettings;
	}

	public void setOtherSettings(String otherSettings) {
		this.otherSettings = otherSettings;
	}

	
	
	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public List<BpmNode> getBpmNodes() {
		return bpmNodes;
	}

	public void setBpmNodes(List<BpmNode> bpmNodes) {
		this.bpmNodes = bpmNodes;
	}
	

}
