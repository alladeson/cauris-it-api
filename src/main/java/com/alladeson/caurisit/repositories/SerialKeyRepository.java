/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.SerialKey;

/**
 * @author allad
 *
 */
public interface SerialKeyRepository extends JpaRepository<SerialKey, Long> {

	Optional<SerialKey> findBySerialKey(String serialKey);

}
