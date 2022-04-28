/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class InvoiceRecapData {

	// Le group de la taxe : Groupe - Libelle (valeur%)
	private String taxe_group;
	// Le montant total pour ce groupe de taxe
	private Long total;
	// Le montant ht de ce groupe de taxe
	private Long imposable;
	// Le montant tva
	private Long impot;
	/**
	 * @return the taxe_group
	 */
	public String getTaxe_group() {
		return taxe_group;
	}
	/**
	 * @param taxe_group the taxe_group to set
	 */
	public void setTaxe_group(String taxe_group) {
		this.taxe_group = taxe_group;
	}
	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}
	/**
	 * @return the imposable
	 */
	public Long getImposable() {
		return imposable;
	}
	/**
	 * @param imposable the imposable to set
	 */
	public void setImposable(Long imposable) {
		this.imposable = imposable;
	}
	/**
	 * @return the impot
	 */
	public Long getImpot() {
		return impot;
	}
	/**
	 * @param impot the impot to set
	 */
	public void setImpot(Long impot) {
		this.impot = impot;
	}
	
}
