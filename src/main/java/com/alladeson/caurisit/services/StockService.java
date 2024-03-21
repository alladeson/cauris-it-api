/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.repositories.ApprovisionnementRepository;
import com.alladeson.caurisit.repositories.ArticleRepository;
import com.alladeson.caurisit.repositories.CategorieRepository;
import com.alladeson.caurisit.repositories.ClientRepository;
import com.alladeson.caurisit.repositories.CmdFournisseurRepository;
import com.alladeson.caurisit.repositories.DetailCmdFournisseurRepository;
import com.alladeson.caurisit.repositories.FournisseurRepository;
import com.alladeson.caurisit.repositories.MouvementArticleRepository;
import com.alladeson.caurisit.repositories.RemiseRepository;
import com.alladeson.caurisit.repositories.TaxeRepository;
import com.alladeson.caurisit.utils.Tool;

import net.sf.jasperreports.engine.JRException;

import com.alladeson.caurisit.models.entities.Approvisionnement;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.CategorieArticle;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.CommandeFournisseur;
import com.alladeson.caurisit.models.entities.DetailCmdFournisseur;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Fournisseur;
import com.alladeson.caurisit.models.entities.MouvementArticle;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.Remise;
import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeData;

/**
 * @author William ALLADE
 *
 */
@Service
public class StockService {

	@Autowired
	private CategorieRepository categorieRepos;

	@Autowired
	private ArticleRepository articleRepos;

	@Autowired
	private ClientRepository clientRepos;

	@Autowired
	private ParametreService paramService;

	@Autowired
	private AccessService accessService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private Tool tool;

	@Autowired
	private ApprovisionnementRepository approvRepos;

	@Autowired
	private FournisseurRepository fournisseurRepos;

	@Autowired
	private CmdFournisseurRepository cmdFournisseurRepos;

	@Autowired
	private DetailCmdFournisseurRepository detailCmdFournissieurRepos;

	@Autowired
	private MouvementArticleRepository mvtArticleRepos;

	@Autowired
	private TaxeRepository taxeRepos;

	@Autowired
	private RemiseRepository remiseRepos;

	/* Gestion du catégorie des articles */
	/**
	 * 
	 * @param categorie
	 * @return
	 */
	public CategorieArticle createCategorie(CategorieArticle categorie) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCategorie))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Gestion audit : valeurAvant
		String valAvant = null;

		categorie = saveCategorie(categorie, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(categorie);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CATEGORIE_CREATE, valAvant, valApres);
		// Renvoie de la catégorie de l'article
		return categorie;
	}

	/**
	 * @param categorie
	 * @return
	 */
	private CategorieArticle saveCategorie(CategorieArticle categorie, boolean delete) {

		try {
			if (delete)
				categorieRepos.delete(categorie);
			else
				categorie = categorieRepos.save(categorie);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Cette catégorie est déjà associée à d'autres données, un article par exemple");
				else if (exception.getMessage().contains("UniqueLibelle"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Une autre catégorie a déjà le même libellé");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le libellé ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return categorie;
	}

	public CategorieArticle getCategorie(Long categorieId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCategorie))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return categorieRepos.findById(categorieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));
	}

	public List<CategorieArticle> getAllCategorie() {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCategorie))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return categorieRepos.findAll();
	}

	public CategorieArticle updateCategorie(CategorieArticle categorie, Long categorieId) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCategorie))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		CategorieArticle categorie1 = categorieRepos.findById(categorieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(categorie1);

		categorie1.setLibelle(categorie.getLibelle());
		// Permet de gerer la mise à jour automatique de la date de mise à jour par le
		// système
		categorie1.setUpdatedAt(null);

		categorie1 = saveCategorie(categorie1, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(categorie1);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CATEGORIE_UPDATE, valAvant, valApres);

		// Renvoie de la catégorie
		return categorie1;

	}

	public boolean deleteCategorie(Long categorieId) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockCategorie))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		CategorieArticle categorie = categorieRepos.findById(categorieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(categorie);

		// Suppression de la catégorie
		categorie = saveCategorie(categorie, true);

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CATEGORIE_DELETE, valAvant, null);
		//
		return true;
	}

	/* Gestion des articles */
	/**
	 * 
	 * @param categorieId
	 * @param taxeId
	 * @param article
	 * @return
	 */
	public Article createArticle(Long categorieId, Long taxeId, Article article) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockArticle))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		CategorieArticle categorie = categorieRepos.findById(categorieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Mise à jour des champ de l'article
		article.setCategorie(categorie);
		article.setTaxe(paramService.getTaxe(taxeId));
		// Sauvegarde dans la base
		article = saveArticle(article, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(article);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.ARTICLE_CREATE, valAvant, valApres);

		// Renvoie de l'article
		return article;
	}

	/**
	 * @param article
	 * @return
	 */
	private Article saveArticle(Article article, boolean delete) {
		try {
			if (delete)
				articleRepos.delete(article);
			else
				article = articleRepos.save(article);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Cet article est déjà associé à d'autres données, une facture par exemple");
				else if (exception.getMessage().contains("UniqueDesignation"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre article porte déjà la même désignation");
				else if (exception.getMessage().contains("'designation' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"La désignation de l'article ne peut être vide");
				else if (exception.getMessage().contains("UniqueReference"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre article porte déjà la même référence");
				else if (exception.getMessage().contains("'reference' ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"La référence de l'article ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return article;
	}

	public Article getArticle(Long articleId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockArticle))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
	}

	public List<Article> getAllArticle() {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockArticle))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return articleRepos.findAll();
	}

	public Article updateArticle(Long categorieId, Long articleId, Long taxeId, Article article) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockArticle))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		CategorieArticle categorie = categorieRepos.findById(categorieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));

		Article article1 = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(article1);
		// Mise à jour des champs de l'article
		article1.setCategorie(categorie);
		article1.setTaxe(paramService.getTaxe(taxeId));
		article1.setReference(article.getReference());
		article1.setDesignation(article.getDesignation());
		article1.setPrix(article.getPrix());
		article1.setStock(article.getStock());
		article1.setStockSecurite(article.getStockSecurite());
		article1.setTaxeSpecifique(article.getTaxeSpecifique());
		article1.setTsName(article.getTsName());
		// Permet de gerer la mise à jour automatique de la date de mise à jour par le
		// système
		article1.setUpdatedAt(null);
		// Sauvegarde dans la base
		article = saveArticle(article1, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(article);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.ARTICLE_UPDATE, valAvant, valApres);

		// Renvoie de l'article
		return article;
	}

	public boolean deleteArticle(Long articleId) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockArticle))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Article article = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(article);

		// Suppression de l'article
		article = saveArticle(article, true);

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.ARTICLE_DELETE, valAvant, null);
		//
		return true;
	}

	/* Gestion du client */
	/**
	 * 
	 * @param client
	 * @return
	 */
	public Client createClient(Client client) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationClient))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Gestion audit : valeurAvant
		String valAvant = null;

		client = saveClient(client, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(client);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CLIENT_CREATE, valAvant, valApres);

		// Renvoie du client
		return client;
	}

	/**
	 * @param article
	 * @return
	 */
	private Client saveClient(Client client, boolean delete) {

		try {
			if (delete)
				clientRepos.delete(client);
			else
				client = clientRepos.save(client);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Ce client est déjà associé à d'autres données, une facture par exemple");
				else if (exception.getMessage().contains("UniqueIfu"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre client est déjà enregistré avec cet ifu");
				else if (exception.getMessage().contains("UniqueRcm"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre client est déjà enregistré avec ce registre de commerce (RCCM)");
				else if (exception.getMessage().contains("UniquePhone"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre client est déjà enregistré avec ce numero de téléphone");
				else if (exception.getMessage().contains("UniqueEmail"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre client est déjà enregistré avec cette adresse e-mail");
				else if (exception.getMessage().contains("UniqueName"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre client est déjà enregistré avec ce nom");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom du client ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return client;
	}

	public Client getClient(Long clientId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationClient))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return clientRepos.findById(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
	}

	public List<Client> getAllClient() {
		// Check permission
		if (!accessService.canReadable(Feature.facturationClient))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return clientRepos.findAll();
	}

	public Client updateClient(Client client, Long clientId) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationClient))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Client client1 = clientRepos.findById(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(client1);

		// Mise à jour des champs du client
		client1.setIfu(client.getIfu());
		client1.setName(client.getName());
		client1.setTelephone(client.getTelephone());
		client1.setEmail(client.getEmail());
		client1.setAddress(client.getAddress());
		client1.setRaisonSociale(client.getRaisonSociale());
		client1.setVille(client.getVille());
		client1.setPays(client.getPays());
		client1.setRcm(client.getRcm());
		// Permet de gerer la mise à jour automatique de la date de mise à jour par le
		// système
		client1.setUpdatedAt(null);

		// Sauvegarde dans la base
		client = saveClient(client1, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(client);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CLIENT_UPDATE, valAvant, valApres);

		// Renvoie du client
		return client;
	}

	public boolean deleteClient(Long clientId) {
		// Check permission
		if (!accessService.canDeletable(Feature.facturationClient))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Client client = clientRepos.findById(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(client);

		// Suppression du client
		client = saveClient(client, true);

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.CLIENT_DELETE, valAvant, null);
		//
		return true;
	}

	/* Gestion du Fournisseur */
	/**
	 * 
	 * @param fournisseur
	 * @return
	 */
	public Fournisseur createFournisseur(Fournisseur fournisseur) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Gestion audit : valeurAvant
		String valAvant = null;

		fournisseur = saveFournisseur(fournisseur, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(fournisseur);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.FOURNISSEUR_CREATE, valAvant, valApres);

		// Renvoie du fournisseur
		return fournisseur;
	}

	/**
	 * @param fournisseur
	 * @return
	 */
	private Fournisseur saveFournisseur(Fournisseur fournisseur, boolean delete) {

		try {
			if (delete)
				fournisseurRepos.delete(fournisseur);
			else
				fournisseur = fournisseurRepos.save(fournisseur);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Ce fournisseur est déjà associé à d'autres données, un ordre d'achat par exemple");
				else if (exception.getMessage().contains("UniqueIfu"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre fournisseur est déjà enregistré avec cet ifu");
				else if (exception.getMessage().contains("UniqueRcm"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre fournisseur est déjà enregistré avec ce registre de commerce (RCCM)");
				else if (exception.getMessage().contains("UniquePhone"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre fournisseur est déjà enregistré avec ce numero de téléphone");
				else if (exception.getMessage().contains("UniqueEmail"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre fournisseur est déjà enregistré avec cette adresse e-mail");
				else if (exception.getMessage().contains("UniqueName"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre fournisseur est déjà enregistré avec ce nom");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom du fournisseur ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return fournisseur;
	}

	public Fournisseur getFournisseur(Long fournisseurId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur non trouvé"));
	}

	public List<Fournisseur> getAllFournisseur() {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return fournisseurRepos.findAll();
	}

	public Fournisseur updateFournisseur(Fournisseur fournisseur, Long fournisseurId) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Fournisseur fournisseur1 = fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(fournisseur);

		// Mise à jour des champs du client
		fournisseur1.setIfu(fournisseur.getIfu());
		fournisseur1.setName(fournisseur.getName());
		fournisseur1.setTelephone(fournisseur.getTelephone());
		fournisseur1.setEmail(fournisseur.getEmail());
		fournisseur1.setAddress(fournisseur.getAddress());
		fournisseur1.setRaisonSociale(fournisseur.getRaisonSociale());
		fournisseur1.setVille(fournisseur.getVille());
		fournisseur1.setPays(fournisseur.getPays());
		fournisseur1.setRcm(fournisseur.getRcm());
		// Permet de gerer la mise à jour automatique de la date de mise à jour par le
		// système
		fournisseur1.setUpdatedAt(null);

		// Sauvegarde dans la base
		fournisseur = saveFournisseur(fournisseur1, false);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(fournisseur);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.FOURNISSEUR_UPDATE, valAvant, valApres);

		// Renvoie du fournisseur
		return fournisseur;
	}

	public boolean deleteFournisseur(Long fournisseurId) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Fournisseur fournisseur = fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(fournisseur);

		// Suppression du client
		fournisseur = saveFournisseur(fournisseur, true);

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.FOURNISSEUR_DELETE, valAvant, null);
		//
		return true;
	}

	/**** Gestion de l'ordre d'achat : CommandeFournisseur *****/

	/*** Gestion des récupérations sur l'ordre d'achat ***/

	/**
	 * Récupère la liste des 100 dernières commandes
	 * 
	 * @return {@link List<CommandeFournisseur>}
	 */
	public List<CommandeFournisseur> getAllCmdFournisseur() {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		
		Page<CommandeFournisseur> page = cmdFournisseurRepos.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id")));

		return page.getContent();

	}
	
	/**
	 * Récupère une commande dont l'identifiant est renseigné
	 * 
	 * @param id L'identifiant de la commande
	 * @return {@link Commande}
	 */
	public CommandeFournisseur getCmdFoournisseur(Long id) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (id.equals((long) 0)) {
			return new CommandeFournisseur();
		}

		Optional<CommandeFournisseur> optional = cmdFournisseurRepos.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé");
		return optional.get();
	}

	/**
	 * Récupérer une commande par son numéro
	 * 
	 * @param numero La référence de la commande
	 * @return {@link CommandeFournisseur}
	 */
	public CommandeFournisseur getCmdFournisseurByNumero(String numero) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<CommandeFournisseur> optional = cmdFournisseurRepos.findByNumero(numero);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé");
		return optional.get();
	}

	/**
	 * Récupère une commande non validée d'un fournisseur
	 * 
	 * @param fournisseurId L'identifiant du client
	 * @return {@link CommandeFournisseur}
	 */
	public CommandeFournisseur getCmdValidFalseByFournisseur(Long fournisseurId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		// Renvoie une commande vide si l'id du fournisseur est égale à 0
		if (fournisseurId.equals(0l)) {
			return new CommandeFournisseur();
		}
		// Récupération du fournisseur
		Fournisseur fournisseur = fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur non trouvé"));
		// Récupération de la commande non validée du fournisseur
		CommandeFournisseur commande = cmdFournisseurRepos.findByFournisseurAndValidFalse(fournisseur);
		if (commande == null)
			return new CommandeFournisseur();
		// Renvoie de la commande
		return commande;
	}

	/**
	 * Récupère la liste des commandes validées d'un fournisseur
	 * 
	 * @param fournisseurId L'identifiant du fournisseur
	 * @return {@link List<CommandeFournisseur>}
	 */
	public List<CommandeFournisseur> getCmdByFournisseur(Long fournisseurId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		// Récupération du fournisseur
		Fournisseur fournisseur = fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur non trouvé"));

		// Recupération de la liste des commandes validées pour le fournisseur
		return cmdFournisseurRepos.findAllByFournisseur(fournisseur);

	}

	/**
	 * Récupération du detail de la commande par son identifiant
	 * 
	 * @param commandeId
	 * @param detailId
	 * @return {@link DetailCmdFournisseur}
	 */
	public DetailCmdFournisseur getDetailCmdFournisseur(Long commandeId, Long detailId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (detailId.equals((long) 0) || commandeId.equals((long) 0)) {
			return new DetailCmdFournisseur();
		}

		Optional<DetailCmdFournisseur> optional = detailCmdFournissieurRepos.findByCommandeIdAndId(commandeId,
				detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne Commande non trouvée");
		return optional.get();
	}

	/**
	 * Recupération du detail de la commande en fonction de l'article
	 * 
	 * @param commandeId L'id de l'ordre d'achat
	 * @param articleId  L'id de l'article
	 * @return {@link DetailCmdFournisseur}
	 */
	public DetailCmdFournisseur getDetailCmdFournisseurByArticle(Long commandeId, Long articleId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (commandeId.equals((long) 0) || articleId.equals((long) 0)) {
			return new DetailCmdFournisseur();
		}

		// Tentative de récupération du detail pour cet article
		Optional<DetailCmdFournisseur> optional = detailCmdFournissieurRepos.findByCommandeIdAndArticleId(commandeId,
				articleId);
		if (optional.isEmpty())
			return null; // Utile pour gérer le tableau de liste dans la vue
		// Renvoie de la commande
		return optional.get();
	}

	/**
	 * Ajout et mise à jour d'une ligne de la commande
	 * 
	 * @param fournisseurId
	 * @param articleId
	 * @param detailPayload
	 * @return {@link CommandeFournisseur}
	 */
	public CommandeFournisseur ajouterDetailCommande(Long fournisseurId, Long articleId,
			DetailCmdFournisseur detailPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du fournisseur
		Fournisseur fournisseur = fournisseurRepos.findById(fournisseurId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fournisseur non trouvé"));
		// Récupération de l'article
		Article article = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Récupération de la commande non validée du fournisseur
		CommandeFournisseur commande = cmdFournisseurRepos.findByFournisseurAndValidFalse(fournisseur);
		// Si la commande n'existe pas, la créer et mettre à jour la date de création et
		// le fournisseur
		if (commande == null) {
			valAvant = null;
			commande = new CommandeFournisseur();
			commande.setDateCreation(new Date());
			commande.setFournisseur(fournisseur);
			commande = cmdFournisseurRepos.save(commande);
			/*
			 * Mise à jour du numéro de la commande Voici le format : PO000001 ("PO"
			 * concatené avec 6 chiffre) Le numéro des chiffre représente l'ID de la
			 * commande C'est pourquoi l'ID des commandes commence par le numéro 1
			 */
			String numeroTmp = "000000" + commande.getId().toString();
			String numeroCmd = "PO" + numeroTmp.substring(numeroTmp.length() - 6);
			commande.setNumero(numeroCmd);
			commande = cmdFournisseurRepos.save(commande);
		} else {
			valAvant = tool.toJson(commande);
		}

		// Instanciation d'un detailCmdFournisseur
		DetailCmdFournisseur detail = new DetailCmdFournisseur();

		// Tentative de récupération du detail pour cet article
		Optional<DetailCmdFournisseur> optionalDf = detailCmdFournissieurRepos
				.findByCommandeIdAndArticleId(commande.getId(), articleId);
		// Si le detail de la commande existe, on le récupère
		if (optionalDf.isPresent()) {
			detail = optionalDf.get();
		}
		// Si le détail de la commande n'existais pas, on met à jour le nouveau détail
		else {
			detail.setCommande(commande);
			detail.setArticle(article);
			detail = detailCmdFournissieurRepos.save(detail);
			commande.getDetails().add(detail);
		}

		// Mise à jour des champs du detail commande
		detail = setDetailCmdFournisseur(detailPayload, article, detail);

		// Enregistrement du detail de la commande
		detail = detailCmdFournissieurRepos.save(detail);
		// Calcule et mise à jour des montants de la commande
		commande = calculer(commande);
		// Gestion audit : valeurApres
		String valApres = tool.toJson(commande);
		// Enregisterment de l'audit
		auditService.traceChange(
				valAvant == null ? Operation.COMMANDE_FOURNISSEUR_CREATE : Operation.COMMANDE_FOURNISSEUR_UPDATE,
				valAvant, valApres);
		// Renvoie de la commande
		return commande;
	}

	/**
	 * @param detailPayload
	 * @param article
	 * @param detail
	 * @return {@link DetailCmdFournisseur}
	 */
	private DetailCmdFournisseur setDetailCmdFournisseur(DetailCmdFournisseur detailPayload, Article article,
			DetailCmdFournisseur detail) {
		// Récupération et mise à jour de la taxe
		if (detailPayload.getTaxeId() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Taxe non précisée");
		Taxe taxe = taxeRepos.findById(detailPayload.getTaxeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
		detail.setTaxe(taxe);
		// Mise à jour des autres champs du detail
		// Désignation ou description du detail
		if (article != null) {
			// Si c'est l'ajout d'un article
			detail.setName(article.getDesignation());
			detail.setReference(article.getReference());
		} else {
			// Sinon c'est l'ajout des données d'expédition
			detail.setName(detailPayload.getName());
		}
		// Le prix unitaire H
		detail.setPrixUht(detailPayload.getPrixUht());
		// Le prix unitaire TTC = (prix unitaire HT * (10+taux))/100
		detail.setPrixUnitaire((detail.getPrixUht() * (100 + detail.getTaxe().getValeur())) / 100);
		detail.setQuantite(detailPayload.getQuantite());
		detail.setMontantHt(detail.getQuantite() * detail.getPrixUht());
		detail.setMontantTva((detail.getMontantHt() * detail.getTaxe().getValeur()) / 100);
		detail.setMontantTtc(detail.getPrixUnitaire() * detail.getQuantite());
		// Gestion des remise
		detail.setRemise(detailPayload.isRemise());
		// Récupération d'une probable ancienne remise
		var remise = detail.getDiscount();
		if (detail.isRemise()) {
			// Si l'ancienne remise est nulle, on instancie une nouvelle
			if (remise == null)
				remise = new Remise();
			remise.setTaux(detailPayload.getTaux());
			remise.setOriginalPrice(detailPayload.getOriginalPrice());
			remise.setPriceModification(detailPayload.getPriceModification());
			remise.setMontant(
					(detailPayload.getTaux() * detailPayload.getOriginalPrice() * detail.getQuantite()) / 100);
			detail.setDiscount(remiseRepos.save(remise));
		} else {
			detail.setDiscount(null);
			// Suppression de la probable ancienne remise si elle n'est pas null
			if (remise != null)
				remiseRepos.delete(remise);
		}

		// Renvoie du detailCmdFournissseur
		return detail;
	}

	/**
	 * Calcul des montants de la commande
	 * 
	 * @param commande
	 * @return {@link CommandeFournisseur}
	 */
	private CommandeFournisseur calculer(CommandeFournisseur commande) {
		// Mise à jour de la quantité total d'article : somme des quantités des lignes
		// de la commande
		commande.setQteTotal(cmdFournisseurRepos.calcQuantiteTotal(commande));
		// Mise à jour montantRemise : somme des montant de la remise des lignes de la
		// commande
		commande.setMontantRemise(cmdFournisseurRepos.calcMontantRemise(commande));
		// Mise à jour du montantHT : somme des montantHT des lignes de la commande
		commande.setMontantHt(cmdFournisseurRepos.calcMontantHt(commande));
		// Mise à jour du montantTva : somme des montantTva des lignes de la commande
		commande.setMontantTva(cmdFournisseurRepos.calcMontantTva(commande));
		// Mise à jour du montantTTC : somme des montantTtc des lignes de la commande
		commande.setMontantTtc(cmdFournisseurRepos.calcMontantTtc(commande));
		// Enregistrement dans la base de données
		commande = cmdFournisseurRepos.save(commande);
		// Renvoie de la commande
		return commande;
	}

	public CommandeFournisseur ajouterExpeditionCmdFournisseur(Long commandeId, DetailCmdFournisseur detailPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du fournisseur
		CommandeFournisseur commande = cmdFournisseurRepos.findById(commandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé"));
		if (commande.isValid()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Ordre d'achat déjà validé");
		}
		if (commande.getDetails().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Veuillez ajouter un article à l'ordre d'achat d'abord. Merci !");
		}

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(commande);

		// Instanciation d'un detailCmdFournisseur
		DetailCmdFournisseur detail = new DetailCmdFournisseur();

		// Tentative de récupération du detail pour cet article
		Optional<DetailCmdFournisseur> optionalDf = detailCmdFournissieurRepos
				.findByCommandeIdAndExpeditionTrue(commande.getId());
		// Si le detail de la commande existe, on le récupère
		if (optionalDf.isPresent()) {
			detail = optionalDf.get();
		}
		// Si le détail de la commande n'existais pas, on met à jour le nouveau détail
		else {
			detail.setCommande(commande);
			detail.setExpedition(true);
			detail = detailCmdFournissieurRepos.save(detail);
			commande.getDetails().add(detail);
		}

		// Mise à jour des champs du detail commande
		detail = setDetailCmdFournisseur(detailPayload, null, detail);

		// Enregistrement du detail de la commande
		detail = detailCmdFournissieurRepos.save(detail);
		// Calcule et mise à jour des montants de la commande
		commande = calculer(commande);
		// Gestion audit : valeurApres
		String valApres = tool.toJson(commande);
		// Enregisterment de l'audit
		auditService.traceChange(
				valAvant == null ? Operation.COMMANDE_FOURNISSEUR_CREATE : Operation.COMMANDE_FOURNISSEUR_UPDATE,
				valAvant, valApres);
		// Renvoie de la commande
		return commande;
	}

	/**
	 * Valide un DetailCmdFournisseur
	 * 
	 * @param id L'identifiant du DetailCmdFournisseur
	 * @return {@link CommandeFournisseur} La commande du DetailCmdFournisseur
	 */
	public CommandeFournisseur validerDetailCmdFournisseur(Long commandeId, Long detailId) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du DetailCmdFournisseur à valider
		Optional<DetailCmdFournisseur> optional = detailCmdFournissieurRepos.findByCommandeIdAndId(commandeId,
				detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lingne de la commande non trouvée");
		DetailCmdFournisseur detail = optional.get();
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(detail);

		// Validation du detail
		detail.setValid(true);
		// Mise à jour du personnel qui met à jour (qui valide le detail)
		// df.setUpdatedBy(getAuthPersonnel());
		// Enregistrement et renvoie du detail
		detail = detailCmdFournissieurRepos.save(detail);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(detail);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.COMMANDE_FOURNISSEUR_DETAIL_VALIDATE, valAvant, valApres);

		// Renvoie de la commande
		return detail.getCommande();
	}

	/**
	 * Supprimer une ligne de la commande et mise à jour de cette dernière
	 * 
	 * @param detailId L'identifiant du detailCmdFournisseur à supprimer
	 * @return {@link CommandeFournisseur}
	 */
	public CommandeFournisseur deleteDetailCmdFournisseur(Long commandeId, Long detailId) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du detail à supprimer
		Optional<DetailCmdFournisseur> optional = detailCmdFournissieurRepos.findByCommandeIdAndId(commandeId,
				detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de la commande non trouvée");
		DetailCmdFournisseur detail = optional.get();
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(detail);
		// Récupération de la commande
		CommandeFournisseur commande = detail.getCommande();
		// Vérifier si le detail n'est pas encore validé
		if (detail.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ligne de la commande déjà validée");
		// Vérification si la commande n'est pas encore valider
		if (commande.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Commande déjà validée");
		// Suppression du detail
		detailCmdFournissieurRepos.delete(detail);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.COMMANDE_FOURNISSEUR_DETAIL_DELETE, valAvant, null);

		// Mise à jour des montants de la commande et renvoie de cette dernière
		return this.resetCommande(commande.getId());
	}

	/**
	 * Remettre à null les montant de la commande si cette dernière est vide, c'est
	 * à dire ne contient aucune ligne
	 * 
	 * @param commandeId L'identifiant de la commande
	 * @return {@link CommandeFournisseur} La commande mise à jour
	 */
	private CommandeFournisseur resetCommande(Long commandeId) {
		// Récupération de la commande à mettre à jour
		CommandeFournisseur commande = cmdFournisseurRepos.findByIdAndValidFalse(commandeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé"));
		// Quand la commande n'est pas vide, mettre à jour les montants
		if (!commande.getDetails().isEmpty())
			return this.calculer(commande);
		else { // Sinon, remettre à null tous les montant de la commande
			commande.setMontantHt(null);
			commande.setMontantTtc(null);
			commande.setMontantTva(null);
			commande.setQteTotal(null);
			commande.setMontantRemise(null);
			return cmdFournisseurRepos.save(commande);
		}
	}

	/**
	 * Valide une commande non vide
	 * 
	 * @param id L'identifiant de la commande
	 * @return {@link CommandeFournisseur} La commande validée
	 * @throws JRException
	 * @throws IOException
	 */
	public CommandeFournisseur validerCmdFournisseur(Long id, CommandeFournisseur payload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la commande à valider
		Optional<CommandeFournisseur> optional = cmdFournisseurRepos.findByIdAndValidFalse(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé ou déjà validé");
		CommandeFournisseur commande = optional.get();
		// Vérification si la commande n'est pas vide
		if (commande.getDetails().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordre d'achat vide");

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(commande);

		// Mise à jour de la commande
		// Mise à jour de la référence de la commande
		if (payload.getReferenceFactureFournisseur() == null) // Si la référence n'est pas renseignée
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Veuillez renseigner la référence de la facture du fournisseur");
		// la référence a été renseignée, le système l'enregistre
		commande.setReferenceFactureFournisseur(payload.getReferenceFactureFournisseur());
		// Mise à jour Date de livraison (= date de validation de la commande)
		commande.setDateLivraison(payload.getDateLivraison());
		// Validatation de la commande
		commande.setValid(true);
		// Enregistrement de la commande
		commande = cmdFournisseurRepos.save(commande);

		// Validation des lignes de la commande
		for (DetailCmdFournisseur detail : commande.getDetails()) {
			detail.setValid(true);
			detail = detailCmdFournissieurRepos.save(detail);
			// Si le detail n'est les données d'une expédition,
			// C'est donc lié à un article
			if (!detail.isExpedition()) {
				// Générer approvisisonnnment
				Approvisionnement approvisionnement = generateApprovisionnement(commande, detail);
				// Générer le mouvement article
				String description = "Entrée d'article suite à la validation d'un ordre d'achat";
				generateMvtEntreeArticle(approvisionnement, description);
			}
		}

		// Si la commande est validé, enregistrer les traces de changement
		if (commande.isValid()) {
			// Gestion audit : valeurApres
			String valApres = tool.toJson(commande);

			// Enregistrement de l'audit
			auditService.traceChange(Operation.COMMANDE_FOURNISSEUR_VALIDATE, valAvant, valApres);
		}

		// Renvoie de la commande
		return commande;
	}

	/**
	 * @param approvisionnement
	 */
	private void generateMvtEntreeArticle(Approvisionnement approvisionnement, String description) {
		/*** Début mise à jour de l'article ***/
		// Mise à jour de la quantité de l'article
		Article article = approvisionnement.getArticle();
		article.setStock(article.getStock() + approvisionnement.getQuantite());
		article = articleRepos.save(article);
		/*** Fin mise à jour de l'article ***/
		// Générer le mouvement article
		MouvementArticle mvt = new MouvementArticle();
		// Mise à jour des champs du mouvement article
		mvt.setApprovisionnement(approvisionnement);
		mvt.setArticle(approvisionnement.getArticle());
		mvt.setDate(new Date());
		mvt.setType(TypeData.ENTREE);
		mvt.setDescription(description);
		// Enregistrement du mouvement de l'article
		mvtArticleRepos.save(mvt);
	}

	/**
	 * @param commande
	 * @param detail
	 * @return
	 */
	private Approvisionnement generateApprovisionnement(CommandeFournisseur commande, DetailCmdFournisseur detail) {
		// Générer l'approvisionnement pour le detail de la commande
		Approvisionnement approvisionnement = new Approvisionnement();
		// Mise à jour des champs de l'approvisionnement
		approvisionnement.setDate(new Date());
		approvisionnement.setDiscount(detail.getDiscount());
		approvisionnement.setMontantHt(detail.getMontantHt());
		approvisionnement.setMontantTtc(detail.getMontantTtc());
		approvisionnement.setMontantTva(detail.getMontantTva());
		approvisionnement.setPrixUht(detail.getPrixUht());
		approvisionnement.setPrixUttc(detail.getPrixUnitaire());
		approvisionnement.setTaxe(detail.getTaxe());
		approvisionnement.setRemise(detail.isRemise());
		approvisionnement.setValid(detail.isValid());
		approvisionnement.setQuantite(detail.getQuantite());
		approvisionnement.setReferenceFacture(commande.getReferenceFactureFournisseur());
		// Mise à jour de la quantité de l'approvisionnement
		approvisionnement.setArticle(detail.getArticle());
		// Enregistrement de l'approvisionnement
		approvisionnement = approvRepos.save(approvisionnement);
		// Renvoie de l'approvisionnement
		return approvisionnement;
	}

	/**
	 * Supprime une commande
	 * 
	 * @param id L'identifiant de la commande
	 * @return {@link Boolean} Le resultat de la suppression
	 */
	public boolean deleteCmdFournisseur(Long id) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la commande à valider
		Optional<CommandeFournisseur> optional = cmdFournisseurRepos.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé");
		CommandeFournisseur commande = optional.get();
		// Vérification si la commande n'est pas encore valider
		if (commande.isValid())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordre d'achat déjà validé");
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(commande);
		// Suppression de la commande
		cmdFournisseurRepos.delete(commande);
		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.COMMANDE_FOURNISSEUR_DELETE, valAvant, null);
		// Si tout s'est bien passé, on renvoie true
		return true;
	}

	/**
	 * 
	 * @param commandeId
	 * @param cmdPayload
	 * @return
	 */
	public CommandeFournisseur additionalInfosCmdFournisseur(Long commandeId, CommandeFournisseur cmdPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockCmdFournisseur))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la commande à valider
		Optional<CommandeFournisseur> optional = cmdFournisseurRepos.findByIdAndValidFalse(commandeId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordre d'achat non trouvé");
		CommandeFournisseur commande = optional.get();
		// Vérification si la commande n'est pas encore valider
		if (commande.isValid())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordre d'achat déjà validé");

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(commande);

		// Mise à jour des champs additionnels de la commande
		if (cmdPayload.getDateCreation() != null)
			commande.setDateCreation(cmdPayload.getDateCreation());
		if (cmdPayload.getNotes() != null)
			commande.setNotes(cmdPayload.getNotes());
		if (cmdPayload.getReferenceExterne() != null)
			commande.setReferenceExterne(cmdPayload.getReferenceExterne());
		// Enregistrement de la commande
		commande = cmdFournisseurRepos.save(commande);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(commande);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.COMMANDE_FOURNISSEUR_UPDATE, valAvant, valApres);

		// Renvoie de la commande
		return commande;
	}

	/* Gestion de l'approvisionnement manually */

	/**
	 * Pour créer et enregistrer un approvisionnement dans la bdd
	 * 
	 * @param articleId     L'ID de l'article à approvisionner
	 * @param approvPayload Le payload de l'approvisionnement
	 * @return {@link Approvisionnement}
	 */
	public Approvisionnement createApprovisionnement(Long articleId, Approvisionnement approvPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de l'article
		Article article = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Instanciation de l'approvisionement
		Approvisionnement approvisionnement = new Approvisionnement();

		// Mise à jour des champs du detail commande
		approvisionnement = setApprovisionnement(approvPayload, article, approvisionnement);
		// Enregistrement de l'approvisionnement
		approvisionnement = approvRepos.save(approvisionnement);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(approvisionnement);
		// Enregisterment de l'audit
		auditService.traceChange(Operation.APPROVISIONNEMENT_CREATE, valAvant, valApres);
		// Renvoie de la commande
		return approvisionnement;
	}

	/**
	 * Pour mettre à jour les champs de l'approvisionnement
	 * 
	 * @param approvPayload     Le payload de l'approvisionnement
	 * @param article           L'article à approvisionner
	 * @param approvisionnement L'approvisionnement à mettre à jour
	 * @return {@link Approvisionnement}
	 */
	private Approvisionnement setApprovisionnement(Approvisionnement approvPayload, Article article,
			Approvisionnement approvisionnement) {
		// Récupération et mise à jour de la taxe
		if (approvPayload.getTaxeId() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Taxe non précisée");
		Taxe taxe = taxeRepos.findById(approvPayload.getTaxeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
		approvisionnement.setTaxe(taxe);
		// Mise à jour des autres champs de l'approvisionnement
		// Article de l'approvisionnement
		approvisionnement.setArticle(article);
		// Le prix unitaire H
		approvisionnement.setPrixUht(approvPayload.getPrixUht());
		// Le prix unitaire TTC = (prix unitaire HT * (10+taux))/100
		approvisionnement
				.setPrixUttc((approvisionnement.getPrixUht() * (100 + approvisionnement.getTaxe().getValeur())) / 100);
		approvisionnement.setQuantite(approvPayload.getQuantite());
		approvisionnement.setMontantHt(approvisionnement.getQuantite() * approvisionnement.getPrixUht());
		approvisionnement
				.setMontantTva((approvisionnement.getMontantHt() * approvisionnement.getTaxe().getValeur()) / 100);
		approvisionnement.setMontantTtc(approvisionnement.getPrixUttc() * approvisionnement.getQuantite());
		// Gestion des remise
		approvisionnement.setRemise(approvPayload.isRemise());
		// Récupération d'une probable ancienne remise
		var remise = approvisionnement.getDiscount();
		if (approvisionnement.isRemise()) {
			// Si l'ancienne remise est nulle, on instancie une nouvelle
			if (remise == null)
				remise = new Remise();
			remise.setTaux(approvPayload.getTaux());
			remise.setOriginalPrice(approvPayload.getOriginalPrice());
			remise.setPriceModification(approvPayload.getPriceModification());
			remise.setMontant(
					(approvPayload.getTaux() * approvPayload.getOriginalPrice() * approvisionnement.getQuantite())
							/ 100);
			approvisionnement.setDiscount(remiseRepos.save(remise));
		} else {
			approvisionnement.setDiscount(null);
			// Suppression de la probable ancienne remise si elle n'est pas null
			if (remise != null)
				remiseRepos.delete(remise);
		}

		// Mise à jour de la date
		approvisionnement.setDate(new Date());

		// Renvoie du detailCmdFournissseur
		return approvisionnement;
	}

	/**
	 * Pour récupérer un approvisionnement
	 * 
	 * @param approvisionnementId L'ID de l'approvisionnement à récupérer
	 * @return {@link Approvisionnement}
	 */
	public Approvisionnement getApprovisionnement(Long approvisionnementId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return approvRepos.findById(approvisionnementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approvisionnement non trouvé"));
	}

	/**
	 * Pour récupérer la liste des approvisionnements
	 * 
	 * @return {@link List<Approvisionnement>}
	 */
	public List<Approvisionnement> getAllApprovisionnement() {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Page<Approvisionnement> page = approvRepos.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id")));

		return page.getContent();
	}

	/**
	 * Pour récupérer la liste des approvisionnements pour un article
	 * 
	 * @param articleId L'article dont on veut récupérer les approvisionnements
	 * @return {@link List<Approvisionnement>}
	 */
	public List<Approvisionnement> getAllApprovisionnementByArticle(Long articleId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de l'article
		articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Récupération des approvisionnements
		return approvRepos.findByArticleIdOrderByIdDesc(articleId);
	}

	/**
	 * Pour la mise à jour d'un approvisionnement
	 * 
	 * @param approvisionnementId L'id de l'approvisionnement à mettre à jour
	 * @param articleId           L'artile à approvisionner
	 * @param approvPayload       Le payload de l'approvisionnement
	 * @return {@link Approvisionnement}
	 */
	public Approvisionnement updateApprovisionnement(Long approvisionnementId, Long articleId,
			Approvisionnement approvPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de l'article
		Article article = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
		// Récupération de l'approvisionnement
		Approvisionnement approvisionnement = approvRepos.findById(approvisionnementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approvisionnement non trouvé"));
		// Test de la validation de l'approvisionnement
		if (approvisionnement.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Approvisionnement déjà validé");

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(approvisionnement);
		// Mise à jour des champs de l'approvisionnement
		approvisionnement = setApprovisionnement(approvPayload, article, approvisionnement);
		// Enregistrement de l'approvisionnement
		approvisionnement = approvRepos.save(approvisionnement);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(approvisionnement);
		// Enregistrement des trace de changement
		auditService.traceChange(Operation.APPROVISIONNEMENT_UPDATE, valAvant, valApres);

		// Renvoie du fournisseur
		return approvisionnement;
	}

	/**
	 * Valide un approvisionnement
	 * 
	 * @param id L'identifiant de l'approvisionnement
	 * @return {@link Approvisionnement} L'approvisionnement validé
	 */
	public Approvisionnement validerApprovisionnement(Long approvisionnementId, Approvisionnement approvPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de l'approvisionnement
		Approvisionnement approvisionnement = approvRepos.findById(approvisionnementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approvisionnement non trouvé"));
		// Test de la validation de l'approvisionnement
		if (approvisionnement.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Approvisionnement déjà validé");
		// Test de la présence de la référence de la facture
		if (approvPayload.getReferenceFacture() == null)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Veuillez renseigner la référence de la facture svp !");
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(approvisionnement);

		// Mise à jour de la référence facture
		approvisionnement.setReferenceFacture(approvPayload.getReferenceFacture());
		// Validation de l'approvisionnement
		approvisionnement.setValid(true);
		// Enregistrement et renvoie de l'approvisionnement
		approvisionnement = approvRepos.save(approvisionnement);

		// Générer le mouvement article
		String description = "Entrée d'article suite à la validation d'un approvisionnement";
		generateMvtEntreeArticle(approvisionnement, description);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(approvisionnement);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.APPROVISIONNEMENT_VALIDATE, valAvant, valApres);

		// Renvoie de l'approvisionnement mis à jour
		return approvisionnement;
	}

	/**
	 * Suppression d'un approvisionnement
	 * 
	 * @param approvisionnementId L'id de l'approvisionnement
	 * @return {@link Boolean}
	 */
	public boolean deleteApprovisionnement(Long approvisionnementId) {
		// Check permission
		if (!accessService.canDeletable(Feature.gestStockApprovisionnement))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		// Récupération de l'approvisionnement
		Approvisionnement approvisionnement = approvRepos.findById(approvisionnementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approvisionnement non trouvé"));
		// Test de la validation de l'approvisionnement
		if (approvisionnement.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Approvisionnement déjà validé");

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(approvisionnement);

		// Suppression de l'approvisionnement
		try {
			approvRepos.delete(approvisionnement);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Cet approvisionnement est déjà associé à d'autres données, le mouvement de l'article par exemple");
			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}

		// Enregistrement des trace de changement
		auditService.traceChange(Operation.APPROVISIONNEMENT_DELETE, valAvant, null);
		//
		return true;
	}

	/*** Mouvment article ***/

	public MouvementArticle getOneMvtArticle(Long mvtId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStock))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return mvtArticleRepos.findById(mvtId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mouvement article non trouvé"));
	}

	public List<MouvementArticle> getListMvtByArticle(Long articleId) {
		// Check permission
		if (!accessService.canReadable(Feature.gestStock))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return mvtArticleRepos.findAllByArticleIdOrderByIdDesc(articleId);
	}
}
