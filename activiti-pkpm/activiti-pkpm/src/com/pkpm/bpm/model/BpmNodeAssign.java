package com.pkpm.bpm.model;

import java.io.Serializable;

import com.pkpm.bpm.enums.NodeAssignType;

/**
 * 节点指派人
 * @author wangjia
 *
 */
public class BpmNodeAssign implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private NodeAssignType assignType;
	private String assignText;
	private String assignValue;
	public NodeAssignType getAssignType() {
		return assignType;
	}
	public void setAssignType(NodeAssignType assignType) {
		this.assignType = assignType;
	}
	public String getAssignText() {
		return assignText;
	}
	public void setAssignText(String assignText) {
		this.assignText = assignText;
	}
	public String getAssignValue() {
		return assignValue;
	}
	public void setAssignValue(String assignValue) {
		this.assignValue = assignValue;
	}
	
}
