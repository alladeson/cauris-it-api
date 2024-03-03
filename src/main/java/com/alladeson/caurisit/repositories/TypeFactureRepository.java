/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;

/**
 * @author William ALLADE
 *
 */
public interface TypeFactureRepository extends JpaRepository<TypeFacture, Long> {

	List<TypeFacture> findAllByGroupe(TypeData group);

	Optional<TypeFacture> findByIdAndGroupe(Long typeId, TypeData group);

}
