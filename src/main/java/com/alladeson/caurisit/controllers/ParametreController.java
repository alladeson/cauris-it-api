/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alladeson.caurisit.models.entities.FrontendLayoutSettings;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.services.ParametreService;

import bj.impots.dgi.emcf.InfoResponseDto;
import net.sf.jasperreports.engine.JRException;

/**
 * @author allad
 *
 */
@RestController
public class ParametreController {
	@Autowired
	private ParametreService paramService;
	
	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getStatusInfoMcef()
	 */
	@GetMapping("/parametre/emcef/api/info/status")
	public InfoResponseDto getStatusInfoMcef() {
		return paramService.getStatusInfoMcef();
	}

	/**
	 * @param parametre
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createParametre(com.alladeson.caurisit.models.entities.Parametre)
	 */
	@PostMapping("parametre/params")
	public Parametre createParametre(@RequestBody Parametre parametre) {
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
	 * @see com.alladeson.caurisit.services.ParametreService#getOneParametre()
	 */
	@GetMapping("parametre/params/one")
	public Parametre getOneParametre() {
		return paramService.getOneParametre();
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
	 * @param paramId
	 * @param format
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#setFormatFacture(java.lang.Long, com.alladeson.caurisit.models.entities.TypeData)
	 */
	@PutMapping("parametre/params/{id}/format-facture/{format}")
	public Parametre setFormatFacture(@PathVariable(value = "id") Long paramId, @PathVariable(value = "format") TypeData format) {
		return paramService.setFormatFacture(paramId, format);
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
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTaxeImpot()
	 */
	@GetMapping("parametre/taxe-impots")
	public List<Taxe> getAllTaxeImpot() {
		return paramService.getAllTaxeImpot();
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
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTypeFactureVente()
	 */
	@GetMapping("parametre/type-facture/vente")
	public List<TypeFacture> getAllTypeFactureVente() {
		return paramService.getAllTypeFactureVente();
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllTypeFactureAvoir()
	 */
	@GetMapping("parametre/type-facture/avoir")
	public List<TypeFacture> getAllTypeFactureAvoir() {
		return paramService.getAllTypeFactureAvoir();
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

	/**
	 * @param layout
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#createLayout(com.alladeson.caurisit.models.entities.FrontendLayoutSettings)
	 */
	@PostMapping("parametre/layout-settings")
	public User createLayout(@RequestBody FrontendLayoutSettings layout) {
		return paramService.createLayout(layout);
	}

	/**
	 * @param layoutId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getLayout(java.lang.Long)
	 */
	@GetMapping("parametre/layout-settings/{id}")
	public FrontendLayoutSettings getLayout(@PathVariable(value = "id") Long layoutId) {
		return paramService.getLayout(layoutId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#getAllLayout()
	 */
	@GetMapping("parametre/layout-settings")
	public List<FrontendLayoutSettings> getAllLayout() {
		return paramService.getAllLayout();
	}

	/**
	 * @param layout
	 * @param layoutId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#updateLayout(com.alladeson.caurisit.models.entities.FrontendLayoutSettings, java.lang.Long)
	 */
	@PutMapping("parametre/layout-settings/{id}")
	public FrontendLayoutSettings updateLayout(@RequestBody FrontendLayoutSettings layout, @PathVariable(value = "id") Long layoutId) {
		return paramService.updateLayout(layout, layoutId);
	}

	/**
	 * @param layoutId
	 * @return
	 * @see com.alladeson.caurisit.services.ParametreService#deleteLayout(java.lang.Long)
	 */
	@DeleteMapping("parametre/layout-settings/{id}")
	public boolean deleteLayout(@PathVariable(value = "id") Long layoutId) {
		return paramService.deleteLayout(layoutId);
	}


	/** Rapport de configuration **/
	
	/**
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @see com.alladeson.caurisit.services.ParametreService#genererRapportConfig(java.lang.Long)
	 */
	@GetMapping("parametre/config-report/param/{id}/sendMail/{status}")
	public ResponseEntity<byte[]> genererRapportConfig(@PathVariable(value = "id") Long id, @PathVariable(value = "status") boolean sendMail) throws IOException, JRException {
		return paramService.genererRapportConfig(id, sendMail);
	}
	
}
