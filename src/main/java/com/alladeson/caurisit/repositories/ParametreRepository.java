/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alladeson.caurisit.models.entities.Parametre;

/**
 * @author allad
 *
 */
public interface ParametreRepository extends JpaRepository<Parametre, Long> {

	@Query(value = "select * from parametre limit 1", nativeQuery = true)
	Optional<Parametre> findOneParams();

}
