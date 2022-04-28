/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * @author TechDigita
 *
 */
//@Entity
public class UserGroup {

	public static final String SA = "SA";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String name;
	private String description;
	@JsonIgnore
	@OneToMany(mappedBy = "group")
	private List<User> users;
//	@OneToMany(mappedBy = "group")
//	private List<Access> access;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the libelle
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the libelle to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		if(this.users == null)
			return new ArrayList<User>();
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}	
}
