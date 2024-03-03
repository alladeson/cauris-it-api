/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeData;

/**
 * @author William ALLADE
 *
 */
public interface TaxeRepository extends JpaRepository<Taxe, Long> {

	List<Taxe> findAllByType(TypeData type);

}
