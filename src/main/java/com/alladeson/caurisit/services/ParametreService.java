/**
 * 
 */
package com.alladeson.caurisit.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.models.entities.TypeSystem;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.FrontendLayoutSettings;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.repositories.FrontendLayoutSettingsRepository;
import com.alladeson.caurisit.repositories.ParametreRepository;
import com.alladeson.caurisit.repositories.TaxeRepository;
import com.alladeson.caurisit.repositories.TypeFactureRepository;
import com.alladeson.caurisit.repositories.TypePaiementRepository;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.utils.Tool;

import bj.impots.dgi.ApiClient;
import bj.impots.dgi.ApiException;
import bj.impots.dgi.Configuration;
import bj.impots.dgi.auth.ApiKeyAuth;
import bj.impots.dgi.emcf.InfoResponseDto;
import bj.impots.dgi.emcf.SfeInfoApi;

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
	private FrontendLayoutSettingsRepository layoutRepos;

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

	// E-mcef
	/**
	 * Récupération des informations de l'e-mcef depuis le serveur de la dgi
	 * 
	 * @return {@link InfoResponseDto}
	 */
	public InfoResponseDto getStatusInfoMcef() {
		// Initialisation des données
		// Récupération du parametre system
		Parametre param = this.getOneParametre();

		try {
			return emcefStatus(param);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new InfoResponseDto();
	}

	/**
	 * @param param
	 * @return
	 * @throws ApiException
	 */
	private InfoResponseDto emcefStatus(Parametre param) throws ApiException {
		// Les données de connection au serveur de la dgi
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		// Configure API key authorization: Bearer
		ApiKeyAuth Bearer = (ApiKeyAuth) defaultClient.getAuthentication("Bearer");
		// YOUR API TOKEN HERE
		Bearer.setApiKey(param.getToken());

		// By default, SDK is configured with SyGMEF-test
		// Enable following line in order to configure SDK for SyGMEF-production
		if (param.getTypeSystem() == TypeSystem.Production)
			defaultClient.setBasePath("https://sygmef.impots.bj/emcf");

		// Instanciation pour la gestion des informations utiles
		SfeInfoApi apiInfoInstance = new SfeInfoApi();

		// Instanciation de status info
		InfoResponseDto infoResponseDto = new InfoResponseDto();

		System.out.println(Bearer);

//		try {
//			// INFO
		infoResponseDto = apiInfoInstance.apiInfoStatusGet();
		System.out.println(infoResponseDto);

//		} catch (ApiException e) {
//			System.err.println("Exception when calling SfeInvoiceApi");
//			e.printStackTrace();
//		}

		return infoResponseDto;
	}

	// Gestion du parametre
	public Parametre createParametre(Parametre parametre) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreDonneSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Mise à jour du token du param
		parametre.setToken(parametre.getTokenTmp());
		// Vérification des données de l'emcef
		InfoResponseDto emcefInfo = new InfoResponseDto();
		try {
			emcefInfo = this.emcefStatus(parametre);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			if (e.getCode() == HttpStatus.UNAUTHORIZED.value())
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
						"Le token que vous avez fourni n'est pas valide.");
			if (e.getCode() == 0)
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
						"Nous ne parvenons pas à vérifier le status de l'emcef. Vérifier la connexion internet de votre machine svp !");
		}

		if (emcefInfo.isStatus() != null && emcefInfo.isStatus()) {
			if ((paramRepos.findAll()).isEmpty()) {
				var params = saveParametre(parametre, false);
				// Gestion audit : valeurApres
				String valApres = tool.toJson(params);
				// Enregistrement de la trace de changement
				auditService.traceChange(Operation.SYSTEM_CREATE, valAvant, valApres);
				// Renvoie du paramètre
				return params;
			} else
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Votre système est déjà configuré. Veuillez contactez votre fournisseur du SFE pour plus d'assistance. Merci !");
		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Vos informations ne correspondent à aucune machine emcef de la DGI. Veuillez revoir vos informations et reprenez svp. Merci !");
	}

	/**
	 * @param article
	 * @return
	 */
	private Parametre saveParametre(Parametre params, boolean delete) {

		try {
			if (delete)
				paramRepos.delete(params);
			else
				params = paramRepos.save(params);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Ce paramètre est déjà associé à d'autres données");
				else if (exception.getMessage().contains("UniqueIfu"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec cet ifu");
				else if (exception.getMessage().contains("UniqueName"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec ce nom");
				else if (exception.getMessage().contains("UniqueRcm"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec ce registre de commerce (RCCM)");
				else if (exception.getMessage().contains("UniquePhone"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec ce numero de téléphone");
				else if (exception.getMessage().contains("UniqueEmail"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec cette adresse e-mail");
				else if (exception.getMessage().contains("UniqueNim"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec ce NIM");
				else if (exception.getMessage().contains("UniqueToken"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre société est déjà enregistrée avec ce token de sécurité");
				else if (exception.getMessage().contains("Le champ 'ifu' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'IFU de la société ne peut être vide");
				else if (exception.getMessage().contains("Le champ 'name' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom de la société ne peut être vide");
				else if (exception.getMessage().contains("Le champ 'nim' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le NIM de la société ne peut être vide");
				else if (exception.getMessage().contains("Le champ 'token' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le token de sécurité ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return params;
	}

	public Parametre getParametre(Long parametreId) {
		// Check permission
		if (!accessService.canReadable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));
	}

	public Parametre getOneParametre() {
		// Check permission
		if (!accessService.canReadable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Votre système n'est pas encore paramètré."));
	}

	public List<Parametre> getAllParametre() {
		// Check permission
		if (!accessService.canReadable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return paramRepos.findAll();
	}

	public Parametre updateParametre(Parametre parametre, Long parametreId) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreDonneSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var params = paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Récupération de l'utilisateur connecté
		User user = this.getAuthenticated();

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(params);
		// Mise à jour des champs de paramètre
		params.setName(parametre.getName());
		params.setTelephone(parametre.getTelephone());
		params.setEmail(parametre.getEmail());
		params.setAddress(parametre.getAddress());
		params.setRaisonSociale(parametre.getRaisonSociale());
		params.setVille(parametre.getVille());
		params.setPays(parametre.getPays());
		params.setRcm(parametre.getRcm());
		if (user.isSA()) {
			params.setIfu(parametre.getIfu());
			params.setNim(parametre.getNim());
			if (parametre.getTokenTmp() != null)
				params.setToken(parametre.getTokenTmp());
			if (parametre.getTypeSystem() != null)
				params.setTypeSystem(parametre.getTypeSystem());
		}
		params.setUpdatedAt(null);

		parametre = saveParametre(params, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(parametre);
		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.SYSTEM_UPDATE, valAvant, valApres);
		// Renvoie du paramètre
		return params;
	}

	public boolean deleteParametre(Long parametreId) {
		// Check permission
		if (!accessService.canDeletable(Feature.parametreDonneSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Parametre parametre = paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(parametre);

		parametre = saveParametre(parametre, true);

		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.SYSTEM_DELETE, valAvant, null);

		return true;
	}

	public Parametre setParamLogo(Long paramId, MultipartFile file) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreDonneSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Parametre param = paramRepos.findById(paramId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(param);

		var filename = "logo_" + paramId + "." + FilenameUtils.getExtension(file.getOriginalFilename());
		fileService.store(file, filename);
		// Mise à jour du logo du parametre
		param.setLogo(filename);
		// Enregistrement et renvoie du parametre
		param = paramRepos.save(param);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(param);

		// Enregistrement des traces de changement
		auditService.traceChange(Operation.SYSTEM_LOGO_UPDATE, valAvant, valApres);
		// Renvoie du paramètre
		return param;
	}

	// Gestion du taxe
	public Taxe createTaxe(Taxe taxe) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreTaxe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return taxeRepos.save(taxe);
	}

	public Taxe saveTaxe(Taxe taxe) {
		return taxeRepos.save(taxe);
	}

	public Taxe getTaxe(Long taxeId) {

		return taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
	}

	public List<Taxe> getAllTaxe() {
		return taxeRepos.findAll();
	}

	public List<Taxe> getAllTaxeImpot() {
		return taxeRepos.findAllByType(TypeData.IMPOT);
	}

	public List<Taxe> getAllTaxeAib() {
		return taxeRepos.findAllByType(TypeData.AIB);
	}

	public Taxe updateTaxe(Taxe taxe, Long taxeId) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreTaxe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));

		taxe.setId(taxeId);

		return taxeRepos.save(taxe);
	}

	public boolean deleteTaxe(Long taxeId) {
		// Check permission
		if (!accessService.canDeletable(Feature.parametreTaxe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Taxe taxe = taxeRepos.findById(taxeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
		taxeRepos.delete(taxe);
		return true;
	}

	// Gestion du typeFacture
	public TypeFacture createTypeFacture(TypeFacture typeFacture) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreTypeFacture))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return tfRepos.save(typeFacture);
	}

	public TypeFacture saveTypeFacture(TypeFacture typeFacture) {
		return tfRepos.save(typeFacture);
	}

	public TypeFacture getTypeFacture(Long typeFactureId) {
		return tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
	}

	public List<TypeFacture> getAllTypeFacture() {
		return tfRepos.findAll();
	}

	public List<TypeFacture> getAllTypeFactureVente() {
		return tfRepos.findAllByGroupe(TypeData.FV);
	}

	public List<TypeFacture> getAllTypeFactureAvoir() {
		return tfRepos.findAllByGroupe(TypeData.FA);
	}

	public TypeFacture updateTypeFacture(TypeFacture typeFacture, Long typeFactureId) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreTypeFacture))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));

		typeFacture.setId(typeFactureId);

		return tfRepos.save(typeFacture);
	}

	public boolean deleteTypeFacture(Long typeFactureId) {
		// Check permission
		if (!accessService.canDeletable(Feature.parametreTypeFacture))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		TypeFacture typeFacture = tfRepos.findById(typeFactureId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
		tfRepos.delete(typeFacture);
		return true;
	}

	// Gestion du type de paiement
	public TypePaiement createTypePaiement(TypePaiement typePaiement) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreTypePaiement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return tpRepos.save(typePaiement);
	}

	public TypePaiement saveTypePaiement(TypePaiement typePaiement) {
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
		// Check permission
		if (!accessService.canWritable(Feature.parametreTypePaiement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		tpRepos.findById(typePaiementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de paiement non trouvé"));

		typePaiement.setId(typePaiementId);

		return tpRepos.save(typePaiement);
	}

	public boolean deleteTypePaiement(Long typePaiementId) {
		// Check permission
		if (!accessService.canDeletable(Feature.parametreTypePaiement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		TypePaiement typePaiement = tpRepos.findById(typePaiementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de paiement non trouvé"));
		tpRepos.delete(typePaiement);
		return true;
	}

	// Frontend layout settings
	public User createLayout(FrontendLayoutSettings layout) {
		// Récupération de l'utilisateur connecté
		User user = this.getAuthenticated();
		// Mise à de l'utilisateur du layout et sauvegarde
		// layout.setUser(user);
		layout = layoutRepos.save(layout);
		// Mise à jour du layout de l'utilisateur
		user.setLayout(layout);
		// Sauvegarde et retour de l'utilisateur, utile pour la mise à jour de la vue
		return userRepository.save(user);
	}

	public FrontendLayoutSettings getLayout(Long layoutId) {
		return layoutRepos.findById(layoutId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paramètre du layout non trouvé"));
	}

	public List<FrontendLayoutSettings> getAllLayout() {
		return layoutRepos.findAll();
	}

	public FrontendLayoutSettings updateLayout(FrontendLayoutSettings layout, Long layoutId) {
		layoutRepos.findById(layoutId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paramètre du layout non trouvé"));

		layout.setId(layoutId);

		return layoutRepos.save(layout);
	}

	public boolean deleteLayout(Long layoutId) {
		FrontendLayoutSettings layout = layoutRepos.findById(layoutId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paramètre du layout non trouvé"));
		layoutRepos.delete(layout);
		return true;
	}
}
