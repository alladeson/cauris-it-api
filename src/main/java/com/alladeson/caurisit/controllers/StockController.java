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
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.CategorieArticle;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.services.StockService;

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
}
