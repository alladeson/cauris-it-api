/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Demande;

/**
 * @author allad
 *
 */
public interface DemandeRepository extends JpaRepository<Demande, Long> {

}
