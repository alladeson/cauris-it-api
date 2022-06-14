/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alladeson.caurisit.models.entities.SerialKey;

/**
 * @author allad
 *
 */
public interface SerialKeyRepository extends JpaRepository<SerialKey, Long> {

	Optional<SerialKey> findBySerialKey(String serialKey);

	@Query("SELECT sk.serialKey AS value FROM SerialKey sk WHERE sk.status=true AND sk NOT IN (SELECT serialKey FROM Demande)"
			+ " AND sk.serialKey LIKE :search%")
	List<serialKeyValue> getSerialKeysAutocomplete(@Param("search") String search);
	
	@Query("SELECT sk FROM SerialKey sk WHERE sk.status=true AND sk NOT IN (SELECT serialKey FROM Demande) AND sk.serialKey IN (SELECT p.serialKey FROM Parametre p)")
	List<SerialKey> getSerialKeysForDemande();
	
	static interface serialKeyValue { 
	    public String getValue();
	}
}
