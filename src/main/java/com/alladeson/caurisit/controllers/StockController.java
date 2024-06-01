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
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.models.entities.Approvisionnement;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.CategorieArticle;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.CommandeFournisseur;
import com.alladeson.caurisit.models.entities.DetailCmdFournisseur;
import com.alladeson.caurisit.models.entities.Fournisseur;
import com.alladeson.caurisit.models.entities.MouvementArticle;
import com.alladeson.caurisit.services.StockService;

import net.sf.jasperreports.engine.JRException;

/**
 * @author allad
 *
 */
@RestController
public class StockController {

	@Autowired
	private StockService stockService;

	/**
	 * @param categorie
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#createCategorie(com.alladeson.caurisit.models.entities.CategorieArticle)
	 */
	@PostMapping("/stock/categorie-article")
	public CategorieArticle createCategorie(@RequestBody CategorieArticle categorie) {
		return stockService.createCategorie(categorie);
	}

	/**
	 * @param categorieId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getCategorie(java.lang.Long)
	 */
	@GetMapping("/stock/categorie-article/{id}")
	public CategorieArticle getCategorie(@PathVariable(value = "id") Long categorieId) {
		return stockService.getCategorie(categorieId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllCategorie()
	 */
	@GetMapping("/stock/categorie-article")
	public List<CategorieArticle> getAllCategorie() {
		return stockService.getAllCategorie();
	}

	/**
	 * @param categorie
	 * @param categorieId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#updateCategorie(com.alladeson.caurisit.models.entities.CategorieArticle,
	 *      java.lang.Long)
	 */
	@PutMapping("/stock/categorie-article/{id}")
	public CategorieArticle updateCategorie(@RequestBody CategorieArticle categorie,
			@PathVariable(value = "id") Long categorieId) {
		return stockService.updateCategorie(categorie, categorieId);
	}

	/**
	 * @param categorieId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteCategorie(java.lang.Long)
	 */
	@DeleteMapping("/stock/categorie-article/{id}")
	public boolean deleteCategorie(@PathVariable(value = "id") Long categorieId) {
		return stockService.deleteCategorie(categorieId);
	}

	/**
	 * @param categorieId
	 * @param taxeId
	 * @param article
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#createArticle(java.lang.Long,
	 *      com.alladeson.caurisit.models.entities.Article)
	 */
	@PostMapping("/stock/article/categorie-article/{id}/taxe/{tId}")
	public Article createArticle(@PathVariable(value = "id") Long categorieId, @PathVariable(value = "tId") Long taxeId, @RequestBody Article article) {
		return stockService.createArticle(categorieId, taxeId, article);
	}

	/**
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getArticle(java.lang.Long)
	 */
	@GetMapping("/stock/article/{id}")
	public Article getArticle(@PathVariable(value = "id") Long articleId) {
		return stockService.getArticle(articleId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllArticle()
	 */
	@GetMapping("/stock/article")
	public List<Article> getAllArticle() {
		return stockService.getAllArticle();
	}

	/**
	 * @param categorieId
	 * @param articleId
	 * @param taxeId
	 * @param article
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#updateArticle(java.lang.Long,
	 *      java.lang.Long, com.alladeson.caurisit.models.entities.Article)
	 */
	@PutMapping("/stock/article/{id}/categorie-article/{cId}/taxe/{tId}")
	public Article updateArticle(@PathVariable(value = "cId") Long categorieId,
			@PathVariable(value = "id") Long articleId, @PathVariable(value = "tId") Long taxeId, @RequestBody Article article) {
		return stockService.updateArticle(categorieId, articleId, taxeId, article);
	}

	/**
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteArticle(java.lang.Long)
	 */
	@DeleteMapping("/stock/article/{id}")
	public boolean deleteArticle(@PathVariable(value = "id") Long articleId) {
		return stockService.deleteArticle(articleId);
	}

	/**
	 * @param client
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#createClient(com.alladeson.caurisit.models.entities.Client)
	 */
	@PostMapping("/stock/client")
	public Client createClient(@RequestBody Client client) {
		return stockService.createClient(client);
	}

	/**
	 * @param clientId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getClient(java.lang.Long)
	 */
	@GetMapping("/stock/client/{id}")
	public Client getClient(@PathVariable(value = "id") Long clientId) {
		return stockService.getClient(clientId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllClient()
	 */
	@GetMapping("/stock/client")
	public List<Client> getAllClient() {
		return stockService.getAllClient();
	}

	/**
	 * @param client
	 * @param clientId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#updateClient(com.alladeson.caurisit.models.entities.Client,
	 *      java.lang.Long)
	 */
	@PutMapping("/stock/client/{id}")
	public Client updateClient(@RequestBody Client client, @PathVariable(value = "id") Long clientId) {
		return stockService.updateClient(client, clientId);
	}

	/**
	 * @param clientId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteClient(java.lang.Long)
	 */
	@DeleteMapping("/stock/client/{id}")
	public boolean deleteClient(@PathVariable(value = "id") Long clientId) {
		return stockService.deleteClient(clientId);
	}

	/**
	 * @param fournisseur
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#createFournisseur(com.alladeson.caurisit.models.entities.Fournisseur)
	 */
	@PostMapping("/stock/fournisseur")
	public Fournisseur createFournisseur(@RequestBody Fournisseur fournisseur) {
		return stockService.createFournisseur(fournisseur);
	}

	/**
	 * @param fournisseurId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getFournisseur(java.lang.Long)
	 */
	@GetMapping("/stock/fournisseur/{id}")
	public Fournisseur getFournisseur(@PathVariable(value = "id") Long fournisseurId) {
		return stockService.getFournisseur(fournisseurId);
	}

	/**
	 * @return List<Fournisseur>
	 * @see com.alladeson.caurisit.services.StockService#getAllFournisseur()
	 */
	@GetMapping("/stock/fournisseur")
	public List<Fournisseur> getAllFournisseur() {
		return stockService.getAllFournisseur();
	}

	/**
	 * @param fournisseur
	 * @param fournisseurId
	 * @return Fournisseur
	 * @see com.alladeson.caurisit.services.StockService#updateFournisseur(com.alladeson.caurisit.models.entities.Fournisseur, java.lang.Long)
	 */
	@PutMapping("/stock/fournisseur/{id}")
	public Fournisseur updateFournisseur(@RequestBody Fournisseur fournisseur, @PathVariable(value = "id") Long fournisseurId) {
		return stockService.updateFournisseur(fournisseur, fournisseurId);
	}

	/**
	 * @param fournisseurId
	 * @return boolean
	 * @see com.alladeson.caurisit.services.StockService#deleteFournisseur(java.lang.Long)
	 */
	@DeleteMapping("/stock/fournisseur/{id}")
	public boolean deleteFournisseur(@PathVariable(value = "id") Long fournisseurId) {
		return stockService.deleteFournisseur(fournisseurId);
	}

	/** Gestion des CommandeFournisseur **/
	
	/**
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllCmdFournisseur()
	 */
	@GetMapping("/stock/commande-fournisseur")
	public List<CommandeFournisseur> getAllCmdFournisseur() {
		return stockService.getAllCmdFournisseur();
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getCmdFoournisseur(java.lang.Long)
	 */
	@GetMapping("/stock/commande-fournisseur/{id}")
	public CommandeFournisseur getCmdFoournisseur(@PathVariable(value = "id") Long id) {
		return stockService.getCmdFoournisseur(id);
	}

	/**
	 * @param numero
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getCmdFournisseurByNumero(java.lang.String)
	 */
	@GetMapping("/stock/commande-fournisseur/numero/{numero}")
	public CommandeFournisseur getCmdFournisseurByNumero(@PathVariable(value = "numero") String numero) {
		return stockService.getCmdFournisseurByNumero(numero);
	}

	/**
	 * @param fournisseurId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getCmdValidFalseByFournisseur(java.lang.Long)
	 */
	@GetMapping("/stock/commande-fournisseur/fournisseur/{fournisseurId}")
	public CommandeFournisseur getCmdValidFalseByFournisseur(@PathVariable(value = "fournisseurId") Long fournisseurId) {
		return stockService.getCmdValidFalseByFournisseur(fournisseurId);
	}

	/**
	 * @param fournisseurId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getCmdValidTrueByFournisseur(java.lang.Long)
	 */
	@GetMapping("/stock/commande-fournisseur/fournisseur/{fournisseurId}/list")
	public List<CommandeFournisseur> getCmdByFournisseur(@PathVariable(value = "fournisseurId") Long fournisseurId) {
		return stockService.getCmdByFournisseur(fournisseurId);
	}

	/**
	 * @param commandeId
	 * @param detailId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getDetailCmdFournisseur(java.lang.Long, java.lang.Long)
	 */
	@GetMapping("/stock/commande-fournisseur/{id}/detail/{dtId}")
	public DetailCmdFournisseur getDetailCmdFournisseur(@PathVariable(value = "id") Long commandeId, @PathVariable(value = "dtId") Long detailId) {
		return stockService.getDetailCmdFournisseur(commandeId, detailId);
	}

	/**
	 * @param commandeId
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getDetailCmdFournisseurByArticle(java.lang.Long, java.lang.Long)
	 */
	@GetMapping("/stock/commande-fournisseur/{id}/article/{artId}")
	public DetailCmdFournisseur getDetailCmdFournisseurByArticle(@PathVariable(value = "id") Long commandeId, @PathVariable(value = "artId") Long articleId) {
		return stockService.getDetailCmdFournisseurByArticle(commandeId, articleId);
	}

	/**
	 * @param fournisseurId
	 * @param articleId
	 * @param detailPayload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#ajouterDetailCommande(java.lang.Long, java.lang.Long, com.alladeson.caurisit.models.entities.DetailCmdFournisseur)
	 */
	@PostMapping("/stock/commande-fournisseur/fournisseur/{fournisseurId}/article/{articleId}")
	public CommandeFournisseur ajouterDetailCommande(@PathVariable(value = "fournisseurId") Long fournisseurId, @PathVariable(value = "articleId") Long articleId,
			@RequestBody DetailCmdFournisseur detailPayload) {
		return stockService.ajouterDetailCommande(fournisseurId, articleId, detailPayload);
	}

	/**
	 * @param commandeId
	 * @param detailId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#validerDetailCmdFournisseur(java.lang.Long, java.lang.Long)
	 */
	@PutMapping("/stock/commande-fournisseur/{cmdId}/detail/{dtId}/valider")
	public CommandeFournisseur validerDetailCmdFournisseur(@PathVariable(value = "cmdId") Long commandeId, @PathVariable(value = "dtId") Long detailId) {
		return stockService.validerDetailCmdFournisseur(commandeId, detailId);
	}

	/**
	 * @param commandeId
	 * @param detailId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteDetailCmdFournisseur(java.lang.Long, java.lang.Long)
	 */
	@DeleteMapping("/stock/commande-fournisseur/{cmdId}/detail/{dtId}")
	public CommandeFournisseur deleteDetailCmdFournisseur(@PathVariable(value = "cmdId") Long commandeId, @PathVariable(value = "dtId") Long detailId) {
		return stockService.deleteDetailCmdFournisseur(commandeId, detailId);
	}

	/**
	 * @param id
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#validerCmdFournisseur(java.lang.Long, com.alladeson.caurisit.models.paylaods.ReglementPayload)
	 */
	@PutMapping("/stock/commande-fournisseur/{id}/valider")
	public CommandeFournisseur validerCmdFournisseur(@PathVariable(value = "id") Long id, @RequestBody CommandeFournisseur payload) {
		return stockService.validerCmdFournisseur(id, payload);
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteCmdFournisseur(java.lang.Long)
	 */
	@DeleteMapping("/stock/commande-fournisseur/{id}")
	public boolean deleteCmdFournisseur(@PathVariable(value = "id") Long id) {
		return stockService.deleteCmdFournisseur(id);
	}

	/**
	 * @param commandeId
	 * @param cmdPayload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#additionalInfosCmdFournisseur(java.lang.Long, com.alladeson.caurisit.models.entities.CommandeFournisseur)
	 */
	@PutMapping("/stock/commande-fournisseur/{id}/infos-additionnelles")
	public CommandeFournisseur additionalInfosCmdFournisseur(@PathVariable(value = "id") Long commandeId, @RequestBody CommandeFournisseur cmdPayload) {
		return stockService.additionalInfosCmdFournisseur(commandeId, cmdPayload);
	}

	/**
	 * @param fournisseurId
	 * @param detailPayload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#ajouterExpeditionCmdFournisseur(java.lang.Long, com.alladeson.caurisit.models.entities.DetailCmdFournisseur)
	 */
	@PutMapping("/stock/commande-fournisseur/{id}/expedition-data")
	public CommandeFournisseur ajouterExpeditionCmdFournisseur(@PathVariable(value = "id") Long commandeId, @RequestBody DetailCmdFournisseur detailPayload) {
		return stockService.ajouterExpeditionCmdFournisseur(commandeId, detailPayload);
	}
	
	/**
	 * @param cmdFournisseurId
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @see com.alladeson.caurisit.services.StockService#genererCmdFournisseur(java.lang.Long)
	 */
	@GetMapping("/public/commande-fournisseur/{id}/imprimer")
	public ResponseEntity<byte[]> genererCmdFournisseur(@PathVariable(value = "id") Long cmdFournisseurId) throws IOException, JRException {
		return stockService.genererCmdFournisseur(cmdFournisseurId);
	}

	/*** Gestion des approvisionnements ***/

	/**
	 * @param articleId
	 * @param approvPayload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#createApprovisionnement(java.lang.Long, com.alladeson.caurisit.models.entities.Approvisionnement)
	 */
	@PostMapping("/stock/approvisionnement/article/{artId}")
	public Approvisionnement createApprovisionnement(@PathVariable(value = "artId") Long articleId, @RequestBody Approvisionnement approvPayload) {
		return stockService.createApprovisionnement(articleId, approvPayload);
	}

	/**
	 * @param approvisionnementId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getApprovisionnement(java.lang.Long)
	 */
	@GetMapping("/stock/approvisionnement/{id}")
	public Approvisionnement getApprovisionnement(@PathVariable(value = "id") Long approvisionnementId) {
		return stockService.getApprovisionnement(approvisionnementId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllApprovisionnement()
	 */
	@GetMapping("/stock/approvisionnement")
	public List<Approvisionnement> getAllApprovisionnement() {
		return stockService.getAllApprovisionnement();
	}

	/**
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getAllApprovisionnementByArticle(java.lang.Long)
	 */
	@GetMapping("/stock/approvisionnement/article/{artId}")
	public List<Approvisionnement> getAllApprovisionnementByArticle(@PathVariable(value = "artId") Long articleId) {
		return stockService.getAllApprovisionnementByArticle(articleId);
	}

	/**
	 * @param approvisionnementId
	 * @param articleId
	 * @param approvPayload
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#updateApprovisionnement(java.lang.Long, java.lang.Long, com.alladeson.caurisit.models.entities.Approvisionnement)
	 */
	@PutMapping("/stock/approvisionnement/{id}/article/{artId}")
	public Approvisionnement updateApprovisionnement(@PathVariable(value = "id") Long approvisionnementId, @PathVariable(value = "artId") Long articleId,
			@RequestBody Approvisionnement approvPayload) {
		return stockService.updateApprovisionnement(approvisionnementId, articleId, approvPayload);
	}

	/**
	 * @param approvisionnementId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#validerApprovisionnement(java.lang.Long)
	 */
	@PutMapping("/stock/approvisionnement/{id}/valider")
	public Approvisionnement validerApprovisionnement(@PathVariable(value = "id") Long approvisionnementId, @RequestBody Approvisionnement approvPayload) {
		return stockService.validerApprovisionnement(approvisionnementId, approvPayload);
	}

	/**
	 * @param approvisionnementId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#deleteApprovisionnement(java.lang.Long)
	 */
	@DeleteMapping("/stock/approvisionnement/{id}")
	public boolean deleteApprovisionnement(@PathVariable(value = "id") Long approvisionnementId) {
		return stockService.deleteApprovisionnement(approvisionnementId);
	}

	/*** Mouvement Article ***/
	
	/**
	 * @param mvtId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getOneMvtArticle(java.lang.Long)
	 */
	@GetMapping("/stock/mouvement-article/{id}")
	public MouvementArticle getOneMvtArticle(@PathVariable(value = "id") Long mvtId) {
		return stockService.getOneMvtArticle(mvtId);
	}

	/**
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.StockService#getListMvtByArticle(java.lang.Long)
	 */
	@GetMapping("/stock/mouvement-article/article/{artId}")
	public List<MouvementArticle> getListMvtByArticle(@PathVariable(value = "artId") Long articleId) {
		return stockService.getListMvtByArticle(articleId);
	}
	
	/** Inventaire de stock **/
	
	/**
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @see com.alladeson.caurisit.services.StockService#getInventairePrint()
	 */
	@GetMapping("/public/inventaire-stock/imprimer")
	public ResponseEntity<byte[]> getInventairePrint() throws IOException, JRException {
		return stockService.getInventairePrint();
	}
	
	
	
}
