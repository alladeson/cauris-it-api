/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.TaxeSpecifique;

/**
 * @author William ALLADE
 *
 */
public interface TSRepository extends JpaRepository<TaxeSpecifique, Long> {

}
