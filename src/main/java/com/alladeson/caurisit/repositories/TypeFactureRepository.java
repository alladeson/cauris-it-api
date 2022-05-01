/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;

/**
 * @author allad
 *
 */
public interface TypeFactureRepository extends JpaRepository<TypeFacture, Long> {

	List<TypeFacture> findAllByGroup(TypeData group);

}
