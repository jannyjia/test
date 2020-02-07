package com.pkpm.bpm.model;

import java.io.Serializable;

public class BpmNodeButton implements Serializable{

	private static final long serialVersionUID = 1L;

	private String buttonType;
	
	private String buttonText;

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}	
	
}
