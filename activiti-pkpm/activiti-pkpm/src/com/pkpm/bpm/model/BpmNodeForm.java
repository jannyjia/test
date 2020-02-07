package com.pkpm.bpm.model;

import java.io.Serializable;

public class BpmNodeForm implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int useDefault;
	private boolean opinionPop;
	private boolean opinionRequire;
	public int getUseDefault() {
		return useDefault;
	}
	public void setUseDefault(int useDefault) {
		this.useDefault = useDefault;
	}
	public boolean isOpinionPop() {
		return opinionPop;
	}
	public void setOpinionPop(boolean opinionPop) {
		this.opinionPop = opinionPop;
	}
	public boolean isOpinionRequire() {
		return opinionRequire;
	}
	public void setOpinionRequire(boolean opinionRequire) {
		this.opinionRequire = opinionRequire;
	}
	
}
