/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.security.entities.Account;

/**
 * @author William
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByLastnameContaining(String search);

	Optional<User> findByAccount(Account account);

}
