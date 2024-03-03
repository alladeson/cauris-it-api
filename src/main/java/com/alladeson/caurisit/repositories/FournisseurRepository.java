/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Fournisseur;

/**
 * @author William ALLADE
 *
 */
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

}
