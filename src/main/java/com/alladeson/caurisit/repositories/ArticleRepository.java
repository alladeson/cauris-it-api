/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Article;

/**
 * @author William ALLADE
 *
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

}
