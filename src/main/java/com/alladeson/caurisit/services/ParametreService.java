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
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.models.entities.TypeSystem;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.entities.FrontendLayoutSettings;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.repositories.FrontendLayoutSettingsRepository;
import com.alladeson.caurisit.repositories.ParametreRepository;
import com.alladeson.caurisit.repositories.TaxeRepository;
import com.alladeson.caurisit.repositories.TypeFactureRepository;
import com.alladeson.caurisit.repositories.TypePaiementRepository;

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
	private UserService userService;

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
		// Mise à jour du token du param
		parametre.setToken(parametre.getTokenTmp());
		// Vérification des données de l'emcef
		InfoResponseDto emcefStatus = new InfoResponseDto();
		try {
			emcefStatus = this.emcefStatus(parametre);
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

		if (emcefStatus.isStatus() != null && emcefStatus.isStatus()) {
			if (this.getAllParametre().isEmpty())
				return paramRepos.save(parametre);
			else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
						"Votre système est déjà configuré. Veuillez contactez votre fournisseur du SFE pour plus d'assistance. Merci !");
		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Vos informations ne correspondent à aucune machine emcef de la DGI. Veuillez revoir vos informations et reprenez svp. Merci !");
	}

	public Parametre getParametre(Long parametreId) {
		return paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));
	}

	public Parametre getOneParametre() {
		return paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Votre système n'est pas encore paramètré."));
	}

	public List<Parametre> getAllParametre() {
		return paramRepos.findAll();
	}

	public Parametre updateParametre(Parametre parametre, Long parametreId) {
		var params = paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		parametre.setId(parametreId);
		parametre.setToken(params.getToken());

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
		fileService.store(file, filename);
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
		return taxeRepos.findAllByType(TypeData.IMPOT);
	}

	public List<Taxe> getAllTaxeAib() {
		return taxeRepos.findAllByType(TypeData.AIB);
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

	public List<TypeFacture> getAllTypeFactureVente() {
		return tfRepos.findAllByGroupe(TypeData.FV);
	}

	public List<TypeFacture> getAllTypeFactureAvoir() {
		return tfRepos.findAllByGroupe(TypeData.FA);
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

	// Frontend layout settings
	public User createLayout(FrontendLayoutSettings layout) {
		// Récupération de l'utilisateur connecté
		User user = userService.getAuthenticated();
		// Mise à de l'utilisateur du layout et sauvegarde
		// layout.setUser(user);
		layout = layoutRepos.save(layout);
		// Mise à jour du layout de l'utilisateur
		user.setLayout(layout);
		// Sauvegarde et retour de l'utilisateur, utile pour la mise à jour de la vue
		return userService.save(user);
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
