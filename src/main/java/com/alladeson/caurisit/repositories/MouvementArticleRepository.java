/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.MouvementArticle;

/**
 * @author William ALLADE
 *
 */
public interface MouvementArticleRepository extends JpaRepository<MouvementArticle, Long> {

}
