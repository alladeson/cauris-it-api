/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.Facture;

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

}
