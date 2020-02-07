package com.pkpm.bpm.identity;

import java.io.Serializable;


import org.activiti.engine.identity.Picture;
import org.activiti.engine.impl.db.HasRevision;
import org.activiti.engine.impl.persistence.entity.AbstractEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayRef;
import org.activiti.engine.impl.persistence.entity.UserEntity;

public class User extends AbstractEntity implements UserEntity, Serializable, HasRevision{

	private static final long serialVersionUID = 1L;
	 private String persistentState;
	 
	 private String email;
	 
	 private String firstName;
	 
	 private String lastName;
	 
	 private String password;
	 
	 private String picture;
	 
	@Override
	public Object getPersistentState() {
		return persistentState;
	}
	@Override
	public String getEmail() {
		return email;
	}
	@Override
	public String getFirstName() {
		return firstName;
	}
	@Override
	public String getLastName() {
		return lastName;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public Picture getPicture() {
		return null;
	}
	@Override
	public ByteArrayRef getPictureByteArrayRef() {
		return null;
	}
	@Override
	public boolean isPictureSet() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setEmail(String email) {
		this.email  = email;
	}
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
		
	}
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPictureByteArrayRef(ByteArrayRef pictureByteArrayRef) {
	}
	@Override
	public void setPicture(Picture arg0) {
		// TODO Auto-generated method stub
		
	}
	 
}
