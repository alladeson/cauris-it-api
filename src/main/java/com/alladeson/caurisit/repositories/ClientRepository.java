/**
 * 
 */
package com.alladeson.caurisit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Client;

/**
 * @author William ALLADE
 *
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

}
