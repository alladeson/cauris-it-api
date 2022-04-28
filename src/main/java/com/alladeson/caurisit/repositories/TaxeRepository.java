/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Taxe;

/**
 * @author William
 *
 */
public interface TaxeRepository extends JpaRepository<Taxe, Long> {

}
