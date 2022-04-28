/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.DetailFacture;

/**
 * @author allad
 *
 */
public interface DetailFactureRepository extends JpaRepository<DetailFacture, Long> {

	Optional<DetailFacture> findByFactureIdAndId(Long factureId, Long detailId);

	Optional<DetailFacture> findByFactureId(Long id);

	Optional<DetailFacture> findByFactureIdAndArticleId(Long id, Long id2);

}
