/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.UserGroup;
import com.alladeson.caurisit.security.entities.TypeRole;

/**
 * @author allad
 *
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

	List<UserGroup> findByRoleNot(TypeRole superAdmin);

	Optional<UserGroup> findByName(String name);

}
