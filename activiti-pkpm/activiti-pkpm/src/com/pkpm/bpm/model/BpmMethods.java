package com.pkpm.bpm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author dell1
 *
 */
@Entity
@Table(name = "BPM_METHODS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BpmMethods implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotNull
    @Id
    @Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
    @Lob	  
    @Column(name = "METHODS", length = 65535) 
	private String methods;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMethods() {
		return methods;
	}

	public void setMethods(String methods) {
		this.methods = methods;
	}
    
}
