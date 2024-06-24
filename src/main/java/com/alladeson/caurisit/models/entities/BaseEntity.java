package com.alladeson.caurisit.models.entities;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.ManyToOne;
//import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
//import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
//import org.springframework.beans.factory.annotation.Autowired;

//import com.alladeson.caurisit.services.UserService;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author William ALLADE
 *
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
	
//	@Autowired
//	private UserService userService;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4001601006457552757L;

	@CreationTimestamp
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

	@JsonIgnore
	@ManyToOne
	private User createdBy;

	@JsonIgnore
	@ManyToOne
	private User updatedBy;

	/**
	 * @return the createdAt
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
//		if(userService.getAuthenticated() != null)
//			this.setCreatedBy(userService.getAuthenticated());
	}

	/**
	 * @return the updatedAt
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
//		if(userService.getAuthenticated() != null)
//			this.setUpdatedBy(userService.getAuthenticated());
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the updatedBy
	 */
	public User getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
//
//	/**
//	 * @return the serialversionuid
//	 */
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
}
