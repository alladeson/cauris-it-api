/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

import java.time.Instant;
import java.util.Date;

/**
 * @author William
 *
 */
public class StatsPayload {

	private Date debut;
	private Date fin;

	private Instant debutAt;

	private Instant finAt;

	private String keyword;

	/**
	 * @return the debut
	 */
	public Date getDebut() {
		return debut;
	}

	/**
	 * @param debut the debut to set
	 */
	public void setDebut(Date debut) {
		this.debut = debut;
	}

	/**
	 * @return the fin
	 */
	public Date getFin() {
		return fin;
	}

	/**
	 * @param fin the fin to set
	 */
	public void setFin(Date fin) {
		this.fin = fin;
	}

	/**
	 * @return the debutAt
	 */
	public Instant getDebutAt() {
		return debutAt;
	}

	/**
	 * @param debutAt the debutAt to set
	 */
	public void setDebutAt(Instant debutAt) {
		this.debutAt = debutAt;
	}

	/**
	 * @return the finAt
	 */
	public Instant getFinAt() {
		return finAt;
	}

	/**
	 * @param finAt the finAt to set
	 */
	public void setFinAt(Instant finAt) {
		this.finAt = finAt;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
