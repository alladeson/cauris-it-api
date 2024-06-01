/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypePaiement;
import com.alladeson.caurisit.models.entities.TypeSystem;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.reports.ConfigReportData;
import com.alladeson.caurisit.models.reports.ConfigTableData;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author William ALLADE
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

	@Autowired
	private ReportService reportService;

	private static final String CONFIG_REPORT_TEMPLATE = "report/rapport-de-configuration.jrxml";
	private static final String CONFIG_REPORT_MAIL_TEMPLATE = "rapport-de-configuration";

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

		// try {
		// // INFO
		infoResponseDto = apiInfoInstance.apiInfoStatusGet();
		System.out.println(infoResponseDto);

		// } catch (ApiException e) {
		// System.err.println("Exception when calling SfeInvoiceApi");
		// e.printStackTrace();
		// }

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
		// Récupération du status de l'emcef du serveur de la DGI
		InfoResponseDto emcefInfo = infoResponseDto(parametre);
		// Si l'emcef est actif, alors enregistrement du paramètre
		if (emcefInfo.isStatus() != null && emcefInfo.isStatus()) {
			// Mise à jour de l'IFU et du NIM
			parametre.setIfu(emcefInfo.getIfu() /* parametre.getIfu() */);
			parametre.setNim(emcefInfo.getNim() /* parametre.getNim() */);
			// Une seule ligne de données requise pour l'installation du client
			if ((paramRepos.findAll()).isEmpty()) {
				try {
					// Mise à jour de la date d'expiration de l'emcef
					String expirationDate = emcefInfo.getTokenValid().toString().substring(0, 16);
					Date date = tool.stringToDate(expirationDate, "yyyy-MM-dd'T'HH:mm");
					parametre.setExpiration(date);
					// Mise à jour de la date d'activation
					parametre.setActivationDate(new Date());
					// Vérification et Validation de la clé d'activation
					if (accessService.checkSecrialKey(parametre.getSerialKey())) {
						// Récupération du fichier du rapport de configuration et mise à jour du
						// paramètre
						String configReport = this.getConfigReportPrintName(parametre);
						parametre.setConfigReport(configReport);
						// Sauvegarde
						var params = saveParametre(parametre, false);
						// Gestion audit : valeurApres
						String valApres = tool.toJson(params);
						// Enregistrement de la trace de changement
						auditService.traceChange(Operation.SYSTEM_CREATE, valAvant, valApres);

						// Envoi du mail
						accessService.sendMail(params, CONFIG_REPORT_MAIL_TEMPLATE);
						// Envoie des données de paramètre au serveur distant
						accessService.sendParametreData(params, false);
						// Renvoie du paramètre
						return params;
					} else {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clé d'activation non valable");
					}
				}
				// catch (IOException | URISyntaxException | WebClientResponseException |
				// ParseException
				// | JRException e) {
				catch (Exception e) {
					e.printStackTrace();
					if (e instanceof WebClientResponseException) {
						if (e.getLocalizedMessage().contains("Bad Request"))
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
									"La clé d'activation n'est pas valable");
						else
							throw new ResponseStatusException(HttpStatus.NOT_FOUND,
									"La clé d'activation n'est pas valable");
					} else {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
					}
				}

			} else
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Votre système est déjà configuré. Veuillez contactez votre fournisseur du SFE pour plus d'assistance. Merci !");
		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Vos informations ne correspondent à aucune machine emcef de la DGI. Veuillez revoir vos informations et reprenez svp. Merci !");
	}

	private InfoResponseDto infoResponseDto(Parametre parametre) {
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
		return emcefInfo;
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
				else if (exception.getMessage().contains("UniqueSerialKey"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doublon de la clé d'activation");
				else if (exception.getMessage().contains("'ifu'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'IFU de la société ne peut être vide");
				else if (exception.getMessage().contains("'name'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom de la société ne peut être vide");
				else if (exception.getMessage().contains("'nim'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le NIM de la société ne peut être vide");
				else if (exception.getMessage().contains("'token'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le token de sécurité ne peut être vide");
				else if (exception.getMessage().contains("'serial_key'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La clé d'activation ne peut être vide");
				else if (exception.getMessage().contains("'expiration'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"La date d'expiration de l'e-mecef ne peut être vide");
				else if (exception.getMessage().contains("'format_facture'"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Le format de la facture ne peut être vide");
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
		if (!accessService.canWritable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var params = paramRepos.findById(parametreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Récupération de l'utilisateur connecté
		User user = this.getAuthenticated();

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(params);
		// Initiation des données de l'emcef DGI
		InfoResponseDto emcefInfo = null;
		// Si le token est fourni dans les données du paramètre
		if (parametre.getTokenTmp() != null) {
			// Récupération du status de l'emcef du serveur de la DGI
			emcefInfo = infoResponseDto(parametre);
		}

		// Initiation d'un flag pour la mise à jour des données de paramètre sur le
		// serveur distant en cas de changement du token
		boolean sendParamData = false;

		// Mise à jour des champs de paramètre
		params.setName(parametre.getName());
		params.setTelephone(parametre.getTelephone());
		params.setEmail(parametre.getEmail());
		params.setAddress(parametre.getAddress());
		params.setRaisonSociale(parametre.getRaisonSociale());
		params.setVille(parametre.getVille());
		params.setPays(parametre.getPays());
		params.setRcm(parametre.getRcm());
		// En cas de mise à jour du token, vérifier si l'emcef est actif
		if (emcefInfo != null && emcefInfo.isStatus()) {
			// Si oui, mise à jour des données sensibles par le super_admin uniquement
			if (user.isSA()) {
				// Mise à jour du flag
				sendParamData = true;
				//
				params.setIfu(emcefInfo.getIfu() /* parametre.getIfu() */);
				params.setNim(emcefInfo.getNim() /* parametre.getNim() */);
				// if (parametre.getTokenTmp() != null) // Déjà vérifié lors de la récupération
				// du status de l'emcef
				params.setToken(parametre.getTokenTmp());
				//
				if (parametre.getTypeSystem() != null)
					params.setTypeSystem(parametre.getTypeSystem());
				// if (parametre.getExpiration() != null)
				// params.setExpiration(parametre.getExpiration());
				// if (parametre.getSerialKey() != null)
				// params.setSerialKey(parametre.getSerialKey());
			}
		} else if (emcefInfo != null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Vos informations ne correspondent à aucune machine emcef de la DGI. Veuillez revoir vos informations et reprenez svp. Merci !");
		}
		
		// Mise à null de la data de mise à jour pour permettre à l'ORM de le gérer pour
		// nous
		params.setUpdatedAt(null);
		// Si le token à été changé
		if (sendParamData) {
			try {
				// Mise à jour de la date d'expiration de l'emcef
				String expirationDate = emcefInfo.getTokenValid().toString().substring(0, 16);
				Date date = tool.stringToDate(expirationDate, "yyyy-MM-dd'T'HH:mm");
				params.setExpiration(date);
				// Envoie des données de paramètre au serveur distant
				// Mise à jour du champ tokenTmp
				params.setTokenTmp(parametre.getTokenTmp());
				accessService.sendParametreData(params, true);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
			}
		}

		// Gestion audit : valeurApres
		String valApres = tool.toJson(parametre);
		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.SYSTEM_UPDATE, valAvant, valApres);

		// Sauvegarde
		parametre = saveParametre(params, false);
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
		if (!accessService.canWritable(Feature.parametreSysteme))
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

		// Envoie du logo au serveur distant
		try {
			accessService.sendParametreLogo(param);
			// } catch (SSLException | URISyntaxException e) {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Renvoie du paramètre
		return param;
	}

	/**
	 * Mettre à jour le format de la facture pour l'impression
	 * 
	 * @param paramId
	 * @param format
	 * @return
	 */
	public Parametre setFormatFacture(Long paramId, TypeData format) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Parametre param = paramRepos.findById(paramId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(param);
		// Mise à jour du logo du parametre
		param.setFormatFacture(format);
		// Enregistrement et renvoie du parametre
		param = paramRepos.save(param);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(param);

		// Enregistrement des traces de changement
		auditService.traceChange(Operation.SYSTEM_FORMAT_FACTURE_UPDATE, valAvant, valApres);

		// Renvoie du paramètre
		return param;
	}
	
	
	/**
	 * Mise à jour des propriétés de la gestion de stock
	 * 
	 * @param paramId
	 * @param format
	 * @return
	 */
	public Parametre setGestionStockProperties(Long paramId, boolean gestionStock, boolean stockEtFacture) {
		// Check permission
		if (!accessService.canWritable(Feature.parametreSysteme))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Parametre param = paramRepos.findById(paramId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parametre non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(param);
		// Mise à jour du propriété de la gestion de stock
		param.setGestionStock(gestionStock);
		// Mise à jour du propriété de la gestion de stock et facuturation
		param.setStockEtFacture(stockEtFacture);
		// Enregistrement et renvoie du parametre
		param = paramRepos.save(param);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(param);

		// Enregistrement des traces de changement
		auditService.traceChange(Operation.SYSTEM_GESTION_STOCK_PROPERTIES_UPDATE, valAvant, valApres);

		// Renvoie du paramètre
		return param;
	}

	/**
	 * Générer la facture normalisée
	 * 
	 * @param id
	 * @return {@link ResponseEntity}
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> genererRapportConfig(Long id, boolean sendMail) throws IOException, JRException {
		// Récupération de la facture
		Optional<Parametre> optional = paramRepos.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les données du système ne sont pas disponibles");
		Parametre param = optional.get();

		// Nom du fichier du rapport
		var reportName = "rapport_configuration_" + param.getSerialKey().substring(0, 10) + ".pdf";
		// Générer le rapport
		ResponseEntity<byte[]> bytes = printConfigReport(param, reportName);

		// Mise à jour du paramètre
		param.setConfigReport(reportName);
		param = paramRepos.save(param);

		// Envoie de mail
		if (sendMail)
			accessService.sendMail(param, CONFIG_REPORT_MAIL_TEMPLATE);

		// Renvoie du fichier
		return bytes;
	}

	/**
	 * Imprimer la facture et l'envoiyer telle quelle
	 * 
	 * @param facture La facture confirmée par l'emcef
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private String getConfigReportPrintName(Parametre param) throws IOException, JRException {
		// Nomdu fichier du rapport
		var reportName = "rapport_configuration_" + param.getSerialKey().substring(0, 10) + ".pdf";
		printConfigReport(param, reportName);
		// Renvoie du nom du fichier généré
		return reportName;
	}

	/**
	 * @param param
	 * @param reportName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private ResponseEntity<byte[]> printConfigReport(Parametre param, String reportName)
			throws IOException, JRException {
		// Récupération du type de la facture
		var template = CONFIG_REPORT_TEMPLATE;

		// Les données de la facture
		ConfigReportData configReportDate = reportService.setConfigReportData(param);

		// Setting Invoice Report Params
		HashMap<String, Object> map = setConfigReportParams(param);
		// Ajout des données de l'entête
		// map.put("entete", new
		// JRBeanCollectionDataSource(Collections.singleton(invoice)));

		// Générer le rapport
		return reportService.generateConfigReport(configReportDate, map, template, reportName);
	}

	/**
	 * Dénfinir les données de paramètre pour l'impression de la facture
	 * 
	 * @param facture
	 * @param param
	 * @return
	 */
	private HashMap<String, Object> setConfigReportParams(Parametre param) {

		// Les données de la société
		List<ConfigTableData> companyData = reportService.setConfigCompanyData(param);
		// Les données de l'e-mecef
		List<ConfigTableData> emecefData = reportService.setConfigEmecefData(param);

		// Instanciation de la liste des paramètres
		HashMap<String, Object> map = new HashMap<>();
		// Ajout des paramètres
		map.put("societe_data", new JRBeanCollectionDataSource(companyData));
		map.put("emecef_data", new JRBeanCollectionDataSource(emecefData));
		return map;
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
