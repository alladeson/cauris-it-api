/**
 * 
 */
package com.alladeson.caurisit.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//import com.alladeson.caurisit.repositories.ApprovisionnementRepository;
import com.alladeson.caurisit.repositories.ArticleRepository;
import com.alladeson.caurisit.repositories.CategorieRepository;
import com.alladeson.caurisit.repositories.ClientRepository;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.CategorieArticle;
import com.alladeson.caurisit.models.entities.Client;

/**
 * @author allad
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
	
	// Gestion du catégorie des articles
	public CategorieArticle createCategorie(CategorieArticle categorie){
        return categorieRepos.save(categorie);
    }

    public CategorieArticle getCategorie(Long categorieId){
        return categorieRepos.findById(categorieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));
    }

    public List<CategorieArticle> getAllCategorie(){
        return categorieRepos.findAll();
    }

    public CategorieArticle updateCategorie(CategorieArticle categorie, Long categorieId){
        categorieRepos.findById(categorieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));
        
        categorie.setId(categorieId);

        return categorieRepos.save(categorie);
    }

    public boolean deleteCategorie(Long categorieId){
        CategorieArticle categorie = categorieRepos.findById(categorieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégoire non trouvée"));
        categorieRepos.delete(categorie);
        return true;
    }
    
    // Gestion des articles
 	public Article createArticle(Long categorieId, Long taxeId, Article article){
 		article.setCategorie(getCategorie(categorieId));
 		article.setTaxe(paramService.getTaxe(taxeId));
         return articleRepos.save(article);
     }

     public Article getArticle(Long articleId){
         return articleRepos.findById(articleId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
     }

     public List<Article> getAllArticle(){
         return articleRepos.findAll();
     }

     public Article updateArticle(Long categorieId, Long articleId, Long taxeId, Article article){
         articleRepos.findById(articleId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
         
         article.setId(articleId);
         
         article.setCategorie(getCategorie(categorieId));
         article.setTaxe(paramService.getTaxe(taxeId));

         return articleRepos.save(article);
     }

     public boolean deleteArticle(Long articleId){
         Article article = articleRepos.findById(articleId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
         articleRepos.delete(article);
         return true;
     }
     
  // Gestion du client
  	public Client createClient(Client client){
          return clientRepos.save(client);
      }

      public Client getClient(Long clientId){
          return clientRepos.findById(clientId)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
      }

      public List<Client> getAllClient(){
          return clientRepos.findAll();
      }

      public Client updateClient(Client client, Long clientId){
          clientRepos.findById(clientId)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
          
          client.setId(clientId);

          return clientRepos.save(client);
      }

      public boolean deleteClient(Long clientId){
          Client client = clientRepos.findById(clientId)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
          clientRepos.delete(client);
          return true;
      }
}
