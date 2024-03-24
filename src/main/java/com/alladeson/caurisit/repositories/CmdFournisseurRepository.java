/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.CommandeFournisseur;
import com.alladeson.caurisit.models.entities.Fournisseur;

/**
 * @author William ALLADE
 *
 */
public interface CmdFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

	CommandeFournisseur findByFournisseurAndValidFalse(Fournisseur fournisseur);
	List<CommandeFournisseur> findByFournisseurAndValidTrue(Fournisseur fournisseur);
	
	@Query("SELECT SUM(dcf.montantHt) FROM DetailCmdFournisseur dcf WHERE dcf.commande=:commande")
	Double calcMontantHt(@Param("commande") CommandeFournisseur commande);

	@Query("SELECT SUM(dcf.montantTva) FROM DetailCmdFournisseur dcf WHERE dcf.commande=:commande")
	Double calcMontantTva(@Param("commande") CommandeFournisseur commande);

	@Query("SELECT SUM(dcf.montantTtc) FROM DetailCmdFournisseur dcf WHERE dcf.commande=:commande")
	Double calcMontantTtc(@Param("commande") CommandeFournisseur commande);
	
	@Query("SELECT SUM(dcf.quantite) FROM DetailCmdFournisseur dcf WHERE dcf.commande=:commande")
	Double calcQuantiteTotal(@Param("commande") CommandeFournisseur commande);
	
	@Query("SELECT SUM(d.montant) FROM DetailCmdFournisseur dcf JOIN dcf.discount d WHERE dcf.remise=true AND dcf.commande=:commande")
	Double calcMontantRemise(@Param("commande") CommandeFournisseur commande);

	Optional<CommandeFournisseur> findByIdAndValidFalse(Long id);

	Optional<CommandeFournisseur> findByNumero(String numero);
	
	List<CommandeFournisseur> findAllByFournisseur(Fournisseur fournisseur);
	
	Optional<CommandeFournisseur> findByIdAndValidTrue(Long cmdFournisseurId);
}
