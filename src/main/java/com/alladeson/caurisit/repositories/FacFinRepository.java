/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.FactureFinalisationDgi;

/**
 * @author allad
 *
 */
public interface FacFinRepository extends JpaRepository<FactureFinalisationDgi, Long> {

	FactureFinalisationDgi findByFactureId(Long factureId);

}
