/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.DetailCmdFournisseur;

/**
 * @author William ALLADE
 *
 */
public interface DetailCmdFournisseurRepository extends JpaRepository<DetailCmdFournisseur, Long> {

	Optional<DetailCmdFournisseur> findByCommandeIdAndArticleId(Long id, Long articleId);

	Optional<DetailCmdFournisseur> findByCommandeIdAndId(Long commandeId, Long detailId);

	Optional<DetailCmdFournisseur> findByCommandeIdAndExpeditionTrue(Long commandeId);

}
