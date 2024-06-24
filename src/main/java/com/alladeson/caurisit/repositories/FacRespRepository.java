/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.FactureResponseDgi;

/**
 * @author William ALLADE
 *
 */
public interface FacRespRepository extends JpaRepository<FactureResponseDgi, Long> {

	FactureResponseDgi findByFactureId(Long factureId);

}
