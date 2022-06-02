/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

import java.time.LocalDateTime;

/**
 * @author allad
 *
 */
public class AuditPayload {

	private Long userId;
	private String desc;
	private LocalDateTime debut;
	private LocalDateTime fin;
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the debut
	 */
	public LocalDateTime getDebut() {
		return debut;
	}
	/**
	 * @param debut the debut to set
	 */
	public void setDebut(LocalDateTime debut) {
		this.debut = debut;
	}
	/**
	 * @return the fin
	 */
	public LocalDateTime getFin() {
		return fin;
	}
	/**
	 * @param fin the fin to set
	 */
	public void setFin(LocalDateTime fin) {
		this.fin = fin;
	}
	
	
}
