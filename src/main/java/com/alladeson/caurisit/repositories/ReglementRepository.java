/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.ReglementFacture;

/**
 * @author William ALLADE
 *
 */
public interface ReglementRepository extends JpaRepository<ReglementFacture, Long> {

}
