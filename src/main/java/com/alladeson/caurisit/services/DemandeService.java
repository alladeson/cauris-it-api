/**
 * 
 */
package com.alladeson.caurisit.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.SerialKey;
import com.alladeson.caurisit.models.entities.Demande;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.repositories.DemandeRepository;
import com.alladeson.caurisit.repositories.SerialKeyRepository;
import com.alladeson.caurisit.repositories.SerialKeyRepository.serialKeyValue;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.utils.Tool;

/**
 * @author allad
 *
 */
@Service
public class DemandeService {
	@Autowired
	private DemandeRepository demandeRepos;
	@Autowired
	private SerialKeyRepository skRepos;
	@Autowired
	private FileService fileService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccessService accessService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private Tool tool;
	//@Autowired
	//private ReportService reportService;

	// private static final String FORMULAIRE_DEMANDE_REPORT_TEMPLATE = "report/formulaire-de-demande.jrxml";

	/**
	 * Récupération de l'utilisateur connecté
	 * 
	 * @return {@link User}
	 */
	public User getAuthenticated() {
		Account account = accountService.getAuthenticated();
		return userRepository.findByAccount(account)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
	}

	// Gestion des demande
	public Demande createDemande(Demande demande) {
		// Check permission
		if (!accessService.canWritable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Gestion audit : valeurAvant
		String valAvant = null;

		demande = saveDemande(demande);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(demande);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.DEMANANDE_CREATE, valAvant, valApres);
		// Renvoie de la catégorie de l'article
		return demande;
	}

	public Demande saveDemande(Demande demande) {
		return demandeRepos.save(demande);
	}

	public Demande getDemande(Long demandeId) {
		// Check permission
		if (!accessService.canReadable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return demandeRepos.findById(demandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));
	}

	public List<Demande> getAllDemande() {
		// Check permission
		if (!accessService.canReadable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return demandeRepos.findAll();
	}

	public Demande updateSerialKey(Long demandeId, Long serialKeyId) {
		// Check permission
		if (!accessService.canWritable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Demande demande = demandeRepos.findById(demandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));

		SerialKey sk = skRepos.findById(serialKeyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clé d'activation non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(demande);
		// Mise à jour de la clé d'activation
		demande.setSerialKey(sk);
		// Sauvegarde de la demande
		demande = saveDemande(demande);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(demande);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.DEMANANDE_UPDATE, valAvant, valApres);

		// Renvoie de la catégorie
		return demande;
	}

	/**
	 * Sauvegarge des fichiers
	 * 
	 * @param demandeId
	 * @param fileName
	 * @param file
	 * @return
	 */
	public Demande setDemandeFiles(Long demandeId, String fileName, MultipartFile file) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreDonneSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Demande demande = demandeRepos.findById(demandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(demande);
		
		// Sauvegarde du fichier
		fileService.store(file, fileName);
		
		// Mise à jour du fichier
		var baseName = "demande_" + demandeId; 
		if (fileName.startsWith(baseName + "_formulaireDgi"))
			demande.setFormulaireDgi(fileName);
		if (fileName.startsWith(baseName + "_formulaire"))
			demande.setFormulaire(fileName);
		if (fileName.startsWith(baseName + "_configReport"))
			demande.setConfigReport(fileName);
		
		// Enregistrement de la demande
		demande = this.saveDemande(demande);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(demande);

		// Enregistrement des traces de changement
		auditService.traceChange(Operation.DEMANANDE_UPDATE, valAvant, valApres);

//		// Envoie du logo au serveur distant
//		try {
//			accessService.sendParametreLogo(param);
////		} catch (SSLException | URISyntaxException e) {
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// Renvoie du paramètre
		return demande;
	}

	public Demande updateStatus(Long demandeId, String statusName) {
		// Check permission
		if (!accessService.canWritable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Demande demande = demandeRepos.findById(demandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(demande);
		
		// Mise à jour des status
		if (statusName.startsWith("treated"))
			demande.setTreated(true);
		if (statusName.startsWith("valid")) {			
			if(!demande.isTreated())
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Demande non traitée");			
			demande.setValid(true);			
		}
		
		// Sauvegarde de la demande
		demande = saveDemande(demande);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(demande);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.DEMANANDE_UPDATE, valAvant, valApres);

		// Renvoie de la catégorie
		return demande;
	}

	public boolean deleteDemande(Long demandeId) {
		// Check permission
		if (!accessService.canDeletable(Feature.demandesList))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Demande demande = demandeRepos.findById(demandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));
		
		if(demande.isTreated())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette demande est déjà traitée");
		
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(demande);
		
		// Suppression de la catégorie
		demandeRepos.delete(demande);

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CATEGORIE_DELETE, valAvant, null);
		//
		return true;
	}
	
	/**
	 * Réchercher et renvoi les clés d'activation non associées à aucune demande
	 * @param search Le terme de réchercher (obligatoire)
	 * @return
	 */
	public List<serialKeyValue> getSerialKeyAutocomplete(String search){
		return skRepos.getSerialKeysAutocomplete(search);
	}
	
	/**
	 * Réchercher et renvoi les clés d'activation non associées à aucune demande
	 * @return {@link List<SerialKey>}
	 */
	public List<SerialKey> getSerialKeysForDemande(){
		return skRepos.getSerialKeysForDemande();
	}
}
