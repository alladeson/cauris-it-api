/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.Facture;
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

	// Récupération si aucune facture d'avoir pour l'autocompletion de la facture,
	// utile pour la création de
	// la facture d'avoir
	/**
	 * 
	 * @param <T>      La list de retour
	 * @param typefvId L'identifiant du type de facture de vente
	 * @param search   Le debut de reference faisant l'objet de recherche
	 * @return {@link List}
	 */
	@Query(value = "select * " + "from facture fv " + "where fv.type_id=:typefvId "
			+ "and fv.confirm is true and fv.origine_ref IS NULL "
			+ "and fv.reference LIKE :search%", nativeQuery = true)
	<T> List<T> getFactureForAutocomplete(@Param("typefvId") Long typefvId, @Param("search") String search);

	// Récupération si au moins une facture d'avoir existe, pour l'autocompletion de
	// la facture, utile pour la création de
	// la facture d'avoir
	/**
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
	<T> List<T> getFactureFaExistForAutocomplete(@Param("typefvId") Long typefvId, @Param("typefaId") Long typefaId,
			@Param("search") String search);

	/**
	 * Récupérer une facture d'avoir par son type
	 * 
	 * @param typefaId L'identifiant du type de facture d'avoir
	 * @return {@link Facture}
	 */
	@Query(value = "select * from facture fa "
			+ "where fa.type_id=:typefaId and fa.origine_ref IS NOT NULL LIMIT 1", nativeQuery = true)
	Optional<Facture> getOneFactureAvoirByType(@Param("typefaId") Long typefaId);

}
