package com.pkpm.bpm.api;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;


public class BpmResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private int code = 0;//0=失败，1=成功
	private String data;
	
	public BpmResponse(int code, String data) {
		this.code = code;
		this.data = data;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
}
