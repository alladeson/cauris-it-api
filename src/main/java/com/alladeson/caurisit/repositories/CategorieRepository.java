/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.CategorieArticle;

/**
 * @author William ALLADE
 *
 */
public interface CategorieRepository extends JpaRepository<CategorieArticle, Long> {

}
