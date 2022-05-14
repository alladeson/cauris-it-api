/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.paylaods.FactureAutocomplete;

/**
 * @author William
 *
 */
public interface FactureRepository extends JpaRepository<Facture, Long> {

	Optional<Facture> findByClientIdAndValidFalse(Long clientId);

	List<Facture> findByClientIdAndValidTrue(Long clientId);

	List<Facture> findByClientNameContaining(String search);

	Optional<Facture> findByIdAndValidFalse(Long id);

	Facture findByClientAndValidFalse(Client client);

	@Query("SELECT SUM(df.montantHt) FROM DetailFacture df WHERE df.facture=:facture")
	Double calcMontantHt(@Param("facture") Facture facture);

	@Query("SELECT SUM(df.montantTva) FROM DetailFacture df WHERE df.facture=:facture")
	Double calcMontantTva(@Param("facture") Facture facture);

	@Query("SELECT SUM(df.montantTtc) FROM DetailFacture df WHERE df.facture=:facture")
	Double calcMontantTtc(@Param("facture") Facture facture);

	@Query("SELECT SUM(df.taxeSpecifique) FROM DetailFacture df WHERE df.facture=:facture")
	Double calctsHt(@Param("facture") Facture facture);

	@Query("SELECT SUM(df.tsTtc) FROM DetailFacture df WHERE df.facture=:facture")
	Double calctsTtc(@Param("facture") Facture facture);

	Optional<Facture> findByIdAndConfirmTrue(Long factureId);

	Optional<Facture> findByOrigineRef(String reference);

	Optional<Facture> findByReferenceAndConfirmTrue(String reference);

	/**
	 * Récupération pour l'autocompletion de la facture, utile pour la création dela
	 * facture d'avoir
	 * 
	 * @param <T>      La list de retour
	 * @param typefvId L'identifiant du type de facture de vente
	 * @param typefaId L'identifiant du type de facture d'avoir
	 * @param search   Le debut de reference faisant l'objet de recherche
	 * @return {@link List}
	 */
	@Query(value = "select fv.reference AS value from facture fv "
			+ "where fv.type_id=:typefvId and fv.reference NOT IN (select fa.origine_ref from facture fa where fa.type_id=:typefaId) "
			+ "and fv.confirm is true and fv.reference LIKE :search%", nativeQuery = true)
	<T> List<T> getFactureForAutocomplete(@Param("typefvId") Long typefvId, @Param("typefaId") Long typefaId,
			@Param("search") String search);

	/**
	 * Récupération la facture non validée d'un client en fonction du type de
	 * facture
	 * 
	 * @param client L'objet Client
	 * @param type   L'objet TypeFacture
	 * @return
	 */
	Facture findByClientAndTypeAndValidFalse(Client client, TypeFacture type);

	Optional<Facture> findByIdAndTypeIdAndConfirmTrue(Long factureId, Long typeId);

	List<Facture> findAllByType(TypeFacture type);

	List<Facture> findAllByCreatedAtBetween(Instant debutAt, Instant finAt);

	List<Facture> findAllByDateNotNullAndDateBetween(Date debut, Date fin);

	List<Facture> findAllByTypeAndCreatedAtBetween(TypeFacture type, Instant debutAt, Instant finAt);

	List<Facture> findAllByTypeAndDateNotNullAndDateBetween(TypeFacture type, Date debut, Date fin);
}
