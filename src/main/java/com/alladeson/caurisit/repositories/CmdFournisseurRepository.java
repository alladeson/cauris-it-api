/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.CommandeFournisseur;

/**
 * @author William ALLADE
 *
 */
public interface CmdFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

}
