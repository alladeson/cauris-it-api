/**
 * 
 */
package com.alladeson.caurisit.services;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.repositories.ParametreRepository;
import com.alladeson.caurisit.repositories.TaxeRepository;
import com.alladeson.caurisit.repositories.TypeFactureRepository;
import com.alladeson.caurisit.repositories.TypePaiementRepository;

/**
 * @author allad
 *
 */
@Service
public class ParametreService {

	@Autowired
	private ParametreRepository paramRepos;
	@Autowired
	private TaxeRepository taxeRepos;
	@Autowired
	private TypeFactureRepository tfRepos;
	@Autowired
	private TypePaiementRepository tpRepos;

	@Autowired
	private FileService fileService;

	// Gestion du parametre
	public Parametre createParametre(Parametre parametre) {
		return paramRepos.save(parametre);
	}

	public Parametre getParametre(Long parametreId) {
		return paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));
	}

	public List<Parametre> getAllParametre() {
		return paramRepos.findAll();
	}

	public Parametre updateParametre(Parametre parametre, Long parametreId) {
		paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		parametre.setId(parametreId);

		return paramRepos.save(parametre);
	}

	public boolean deleteParametre(Long parametreId) {
		Parametre parametre = paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));
		paramRepos.delete(parametre);
		return true;
	}

	public Parametre setParamLogo(Long paramId, MultipartFile file) {
		Parametre param = this.getParametre(paramId);
		var filename = "logo_" + paramId + "." + FilenameUtils.getExtension(file.getOriginalFilename());
		var filepath = fileService.store(file, filename);
		// Mise à jour du logo du parametre
		param.setLogo(filename);
		// Enregistrement et renvoie du parametre
		return paramRepos.save(param);
	}

	// Gestion du taxe
	public Taxe createTaxe(Taxe taxe) {
		return taxeRepos.save(taxe);
	}

	public Taxe getTaxe(Long taxeId) {
		return taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
	}

	public List<Taxe> getAllTaxe() {
		return taxeRepos.findAll();
	}

	public Taxe updateTaxe(Taxe taxe, Long taxeId) {
		taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));

		taxe.setId(taxeId);

		return taxeRepos.save(taxe);
	}

	public boolean deleteTaxe(Long taxeId) {
		Taxe taxe = taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
		taxeRepos.delete(taxe);
		return true;
	}

	// Gestion du typeFacture
	public TypeFacture createTypeFacture(TypeFacture typeFacture) {
		return tfRepos.save(typeFacture);
	}

	public TypeFacture getTypeFacture(Long typeFactureId) {
		return tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
	}

	public List<TypeFacture> getAllTypeFacture() {
		return tfRepos.findAll();
	}

	public TypeFacture updateTypeFacture(TypeFacture typeFacture, Long typeFactureId) {
		tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));

		typeFacture.setId(typeFactureId);

		return tfRepos.save(typeFacture);
	}

	public boolean deleteTypeFacture(Long typeFactureId) {
		TypeFacture typeFacture = tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
		tfRepos.delete(typeFacture);
		return true;
	}

	// Gestion du type de paiement
	public TypePaiement createTypePaiement(TypePaiement typePaiement) {
		return tpRepos.save(typePaiement);
	}

	public TypePaiement getTypePaiement(Long typePaiementId) {
		return tpRepos.findById(typePaiementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de paiement non trouvé"));
	}

	public List<TypePaiement> getAllTypePaiement() {
		return tpRepos.findAll();
	}

	public TypePaiement updateTypePaiement(TypePaiement typePaiement, Long typePaiementId) {
		tpRepos.findById(typePaiementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de paiement non trouvé"));

		typePaiement.setId(typePaiementId);

		return tpRepos.save(typePaiement);
	}

	public boolean deleteTypePaiement(Long typePaiementId) {
		TypePaiement typePaiement = tpRepos.findById(typePaiementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de paiement non trouvé"));
		tpRepos.delete(typePaiement);
		return true;
	}
}
