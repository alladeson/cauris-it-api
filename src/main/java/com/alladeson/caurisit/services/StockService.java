/**
 * 
 */
package com.alladeson.caurisit.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//import com.alladeson.caurisit.repositories.ApprovisionnementRepository;
import com.alladeson.caurisit.repositories.ArticleRepository;
import com.alladeson.caurisit.repositories.CategorieRepository;
import com.alladeson.caurisit.repositories.ClientRepository;
import com.alladeson.caurisit.utils.Tool;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.CategorieArticle;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Operation;

/**
 * @author William ALLADE
 *
 */
@Service
public class StockService {

//	@Autowired
//	private ApprovisionnementRepository approvRepos;

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
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Une autre catégorie a déjà le même libellé");
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
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre article porte déjà la même désignation");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La désignation ne peut être vide");
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
		article1.setDesignation(article.getDesignation());
		article1.setPrix(article.getPrix());
		article1.setStock(article.getStock());
		article1.setTaxeSpecifique(article.getStockSecurite());
		article1.setTaxeSpecifique(article.getTaxeSpecifique());
		article1.setTsName(article.getTsName());
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
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre client est déjà enregistré avec cet ifu");
				else if (exception.getMessage().contains("UniqueRcm"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre client est déjà enregistré avec ce registre de commerce (RCCM)");
				else if (exception.getMessage().contains("UniquePhone"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre client est déjà enregistré avec ce numero de téléphone");
				else if (exception.getMessage().contains("UniqueEmail"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre client est déjà enregistré avec cette adresse e-mail");
				else if (exception.getMessage().contains("UniqueName"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre client est déjà enregistré avec ce nom");
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
}
