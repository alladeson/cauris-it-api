/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Approvisionnement;

/**
 * @author William ALLADE
 *
 */
public interface ApprovisionnementRepository extends JpaRepository<Approvisionnement, Long> {

	List<Approvisionnement> findByArticleIdOrderByIdDesc(Long articleId);
}
