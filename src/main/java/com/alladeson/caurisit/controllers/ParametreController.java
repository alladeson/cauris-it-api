/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.services.ParametreService;

/**
 * @author allad
 *
 */
@RestController
public class ParametreController {
	@Autowired
	private ParametreService paramService;

	/**
	 * @param parametre
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createParametre(com.alladeson.caurisit.models.entities.Parametre)
	 */
	@PostMapping("parametre/params")
	public Parametre createParametre(@RequestBody Parametre parametre) {
		// Mise à jour du token du param
		parametre.setToken(parametre.getTokenTmp());
		return paramService.createParametre(parametre);
	}

	/**
	 * @param parametreId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getParametre(java.lang.Long)
	 */
	@GetMapping("parametre/params/{id}")
	public Parametre getParametre(@PathVariable(value = "id") Long parametreId) {
		return paramService.getParametre(parametreId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllParametre()
	 */
	@GetMapping("parametre/params")
	public List<Parametre> getAllParametre() {
		return paramService.getAllParametre();
	}

	/**
	 * @param parametre
	 * @param parametreId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#updateParametre(com.alladeson.caurisit.models.entities.Parametre,
	 *      java.lang.Long)
	 */
	@PutMapping("parametre/params/{id}")
	public Parametre updateParametre(@RequestBody Parametre parametre, @PathVariable(value = "id") Long parametreId) {
		// Mise à jour du token du param
		parametre.setToken(parametre.getTokenTmp());
		return paramService.updateParametre(parametre, parametreId);
	}

	/**
	 * @param parametreId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#deleteParametre(java.lang.Long)
	 */
	@DeleteMapping("parametre/params/{id}")
	public boolean deleteParametre(@PathVariable(value = "id") Long parametreId) {
		return paramService.deleteParametre(parametreId);
	}

	/**
	 * @param paramId
	 * @param file
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#setParamLogo(java.lang.Long,
	 *      org.springframework.web.multipart.MultipartFile)
	 */
	@PutMapping("parametre/params/{id}/logo")
	public Parametre setParamLogo(@PathVariable(value = "id") Long paramId, @RequestParam("file") MultipartFile file) {
		return paramService.setParamLogo(paramId, file);
	}

	/**
	 * @param taxe
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createTaxe(com.alladeson.caurisit.models.entities.Taxe)
	 */
	@PostMapping("parametre/taxe")
	public Taxe createTaxe(@RequestBody Taxe taxe) {
		return paramService.createTaxe(taxe);
	}

	/**
	 * @param taxeId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getTaxe(java.lang.Long)
	 */
	@GetMapping("parametre/taxe/{id}")
	public Taxe getTaxe(@PathVariable(value = "id") Long taxeId) {
		return paramService.getTaxe(taxeId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTaxe()
	 */
	@GetMapping("parametre/taxe")
	public List<Taxe> getAllTaxe() {
		return paramService.getAllTaxe();
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTaxeAib()
	 */
	@GetMapping("parametre/taxe-aib")
	public List<Taxe> getAllTaxeAib() {
		return paramService.getAllTaxeAib();
	}

	/**
	 * @param taxe
	 * @param taxeId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#updateTaxe(com.alladeson.caurisit.models.entities.Taxe,
	 *      java.lang.Long)
	 */
	@PutMapping("parametre/taxe/{id}")
	public Taxe updateTaxe(@RequestBody Taxe taxe, @PathVariable(value = "id") Long taxeId) {
		return paramService.updateTaxe(taxe, taxeId);
	}

	/**
	 * @param taxeId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#deleteTaxe(java.lang.Long)
	 */
	@DeleteMapping("parametre/taxe/{id}")
	public boolean deleteTaxe(@PathVariable(value = "id") Long taxeId) {
		return paramService.deleteTaxe(taxeId);
	}

	/**
	 * @param typeFacture
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createTypeFacture(com.alladeson.caurisit.models.entities.TypeFacture)
	 */
	@PostMapping("parametre/type-facture")
	public TypeFacture createTypeFacture(TypeFacture typeFacture) {
		return paramService.createTypeFacture(typeFacture);
	}

	/**
	 * @param typeFactureId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getTypeFacture(java.lang.Long)
	 */
	@GetMapping("parametre/type-facture/{id}")
	public TypeFacture getTypeFacture(@PathVariable(value = "id") Long typeFactureId) {
		return paramService.getTypeFacture(typeFactureId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTypeFacture()
	 */
	@GetMapping("parametre/type-facture")
	public List<TypeFacture> getAllTypeFacture() {
		return paramService.getAllTypeFacture();
	}

	/**
	 * @param typeFacture
	 * @param typeFactureId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#updateTypeFacture(com.alladeson.caurisit.models.entities.TypeFacture,
	 *      java.lang.Long)
	 */
	@PutMapping("parametre/type-facture/{id}")
	public TypeFacture updateTypeFacture(@RequestBody TypeFacture typeFacture,
			@PathVariable(value = "id") Long typeFactureId) {
		return paramService.updateTypeFacture(typeFacture, typeFactureId);
	}

	/**
	 * @param typeFactureId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#deleteTypeFacture(java.lang.Long)
	 */
	@DeleteMapping("parametre/type-facture/{id}")
	public boolean deleteTypeFacture(@PathVariable(value = "id") Long typeFactureId) {
		return paramService.deleteTypeFacture(typeFactureId);
	}

	/**
	 * @param typePaiement
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createTypePaiement(com.alladeson.caurisit.models.entities.TypePaiement)
	 */
	@PostMapping("parametre/type-paiement")
	public TypePaiement createTypePaiement(@RequestBody TypePaiement typePaiement) {
		return paramService.createTypePaiement(typePaiement);
	}

	/**
	 * @param typePaiementId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getTypePaiement(java.lang.Long)
	 */
	@GetMapping("parametre/type-paiement/{id}")
	public TypePaiement getTypePaiement(@PathVariable(value = "id") Long typePaiementId) {
		return paramService.getTypePaiement(typePaiementId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTypePaiement()
	 */
	@GetMapping("parametre/type-paiement")
	public List<TypePaiement> getAllTypePaiement() {
		return paramService.getAllTypePaiement();
	}

	/**
	 * @param typePaiement
	 * @param typePaiementId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#updateTypePaiement(com.alladeson.caurisit.models.entities.TypePaiement,
	 *      java.lang.Long)
	 */
	@PutMapping("parametre/type-paiement/{id}")
	public TypePaiement updateTypePaiement(@RequestBody TypePaiement typePaiement,
			@PathVariable(value = "id") Long typePaiementId) {
		return paramService.updateTypePaiement(typePaiement, typePaiementId);
	}

	/**
	 * @param typePaiementId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#deleteTypePaiement(java.lang.Long)
	 */
	@DeleteMapping("parametre/type-paiement/{id}")
	public boolean deleteTypePaiement(@PathVariable(value = "id") Long typePaiementId) {
		return paramService.deleteTypePaiement(typePaiementId);
	}
}
