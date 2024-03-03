/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Feature;

/**
 * @author William ALLADE
 *
 */
public interface FeatureRepository extends JpaRepository<Feature, Long> {

	Optional<Feature> findByCode(Integer code);

	List<Feature> findAllByCodeNotIn(Collection<Integer> codes);

}
