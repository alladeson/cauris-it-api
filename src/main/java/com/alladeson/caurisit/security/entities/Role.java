package com.alladeson.caurisit.security.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Role extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5915425407965425916L;
	//
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_user_role")
	@SequenceGenerator(name = "gen_user_role", sequenceName = "_seq_user_role", allocationSize = 1)
	private Long id;
	//
	private String name;

	public Role() {
		super();
	}
	public Role(String name) {
		super();
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
