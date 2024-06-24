/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.TaxeGroups;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;

/**
 * @author William ALLADE
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
	
	@Query("SELECT (COALESCE(SUM(df.montantHt), 0) + COALESCE(SUM(df.taxeSpecifique), 0)) FROM DetailFacture df WHERE df.facture=:facture AND df.taxe.groupe NOT IN :groupe")
	Double calcMontantHtForAib(@Param("facture") Facture facture, @Param("groupe") Collection<TaxeGroups> groupe);

	@Query("SELECT COALESCE(SUM(df.taxeSpecifique), 0) FROM DetailFacture df WHERE (df.facture=:facture AND df.taxe.groupe IN :groupe2)")
	Double calcMontantTsHtForAib(@Param("facture") Facture facture, @Param("groupe2") Collection<TaxeGroups> groupe2);

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

	List<Facture> findAllByTypeGroupeAndDateNotNullAndDateBetween(TypeData typeFactureGroupe, Date debut, Date fin);
	
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
	@Query(value = "select * from facture fv join type_facture tf on fv.type_id = tf.id "
			+ "where tf.groupe=:groupeFv and fv.reference NOT IN (select fa.origine_ref from facture fa join type_facture tfa on fa.type_id = tfa.id where tfa.groupe=:groupeFa) "
			+ "and fv.confirm is true and (fv.date between :debut and :fin)", nativeQuery = true)
	List<Facture> findAllRecapByDateNotNullAndDateBetween(@Param("groupeFv") Integer tfGroupeF, @Param("groupeFa") Integer tfGroupeFa, @Param("debut") Date debut, @Param("fin") Date fin);

	@Query("SELECT COALESCE(SUM(frdgi.ta), 0) AS ta, "
			+ "COALESCE(SUM(frdgi.tb), 0) AS tb, "
			+ "COALESCE(SUM(frdgi.tc), 0) AS tc, "
			+ "COALESCE(SUM(frdgi.td), 0) AS td, "
			+ "COALESCE(SUM(frdgi.taa), 0) AS taa, "
			+ "COALESCE(SUM(frdgi.tab), 0) AS tab, "
			+ "COALESCE(SUM(frdgi.tac), 0) AS tac, "
			+ "COALESCE(SUM(frdgi.tad), 0) AS tad, "
			+ "COALESCE(SUM(frdgi.tae), 0) AS tae, "
			+ "COALESCE(SUM(frdgi.taf), 0) AS taf, "
			+ "COALESCE(SUM(frdgi.hab), 0) AS hab, "
			+ "COALESCE(SUM(frdgi.had), 0) AS had, "
			+ "COALESCE(SUM(frdgi.vab), 0) AS vab, "
			+ "COALESCE(SUM(frdgi.vad), 0) AS vad, "
			+ "COALESCE(SUM(frdgi.aib), 0) AS aib, "
			+ "COALESCE(SUM(frdgi.ts), 0) AS ts, "
			+ "COALESCE(SUM(frdgi.total), 0) AS total "
			+ "FROM FactureResponseDgi frdgi WHERE frdgi.facture.type.groupe=:groupe AND (frdgi.facture.date BETWEEN :debut AND :fin)")
	BilanRecapMontant bilanMontantByConfirmedDate(@Param("groupe") TypeData typeFactureGroupe, @Param("debut") Date debut, @Param("fin") Date fin);
	
	@Query("SELECT COALESCE(SUM(frdgi.ta), 0) as ta, "
			+ "COALESCE(SUM(frdgi.tb), 0) as tb, "
			+ "COALESCE(SUM(frdgi.tc), 0) as tc, "
			+ "COALESCE(SUM(frdgi.td), 0) as td, "
			+ "COALESCE(SUM(frdgi.taa), 0) as taa, "
			+ "COALESCE(SUM(frdgi.tab), 0) as tab, "
			+ "COALESCE(SUM(frdgi.tac), 0) as tac, "
			+ "COALESCE(SUM(frdgi.tad), 0) as tad, "
			+ "COALESCE(SUM(frdgi.tae), 0) as tae, "
			+ "COALESCE(SUM(frdgi.taf), 0) as taf, "
			+ "COALESCE(SUM(frdgi.hab), 0) as hab, "
			+ "COALESCE(SUM(frdgi.had), 0) as had, "
			+ "COALESCE(SUM(frdgi.vab), 0) as vab, "
			+ "COALESCE(SUM(frdgi.vad), 0) as vad, "
			+ "COALESCE(SUM(frdgi.aib), 0) as aib, "
			+ "COALESCE(SUM(frdgi.ts), 0) as ts, "
			+ "COALESCE(SUM(frdgi.total), 0) as total "
			+ "FROM FactureResponseDgi frdgi WHERE frdgi.facture.type.groupe=:groupefv AND (frdgi.facture.date BETWEEN :debut AND :fin) "
			+ "AND frdgi.facture.reference NOT IN (SELECT fa.origineRef FROM Facture fa WHERE fa.type.groupe=:groupefa AND (fa.date BETWEEN :debut AND :fin))")
	BilanRecapMontant bilanMontantRecapByConfirmedDate(@Param("groupefv") TypeData typeFactureGroupeFv, @Param("groupefa") TypeData typeFactureGroupeFa, @Param("debut") Date debut, @Param("fin") Date fin);

	static interface BilanRecapMontant { 
	    public Long getTa();
	    public Long getTb();
	    public Long getTc();
	    public Long getTd();
	    public Long getTaa();
	    public Long getTab();
	    public Long getTac();
	    public Long getTad();
	    public Long getTae();
	    public Long getTaf();
	    public Long getHab();
	    public Long getHad();
	    public Long getVad();
	    public Long getVab();
	    public Long getAib();
	    public Long getTs();
	    public Long getTotal();
	}
}
