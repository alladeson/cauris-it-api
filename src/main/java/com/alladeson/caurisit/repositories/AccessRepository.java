/**
 * 
 */
package com.alladeson.caurisit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.models.entities.Access;
import com.alladeson.caurisit.security.entities.TypeRole;

/**
 * @author William ALLADE
 *
 */
public interface AccessRepository extends JpaRepository<Access, Long> {

	Access findByGroupIdAndFeatureId(Long groupId, Long featureId);

	List<Access> findByGroupId(Long groupId);

	List<Access> findByGroupNameOrFeatureNameContaining(String search, String search2);

	List<Access> findAllByGroupRoleNot(TypeRole role);

}
