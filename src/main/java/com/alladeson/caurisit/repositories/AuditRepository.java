package com.alladeson.caurisit.repositories;

import com.alladeson.caurisit.models.entities.Audit;
import com.alladeson.caurisit.security.entities.TypeRole;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author William ALLADE
 *
 */
public interface AuditRepository extends JpaRepository<Audit, Long> {

    List<Audit> findByUserIdOrderByDateHeureDesc(Long userId);

    List<Audit> findByUserIdAndDescriptionAndDateHeureBetweenOrderByDateHeureDesc(Long userId, String desc, LocalDateTime debut, LocalDateTime fin);

    List<Audit> findByDescriptionAndDateHeureBetweenOrderByDateHeureDesc(String desc, LocalDateTime debut, LocalDateTime fin);

    List<Audit> findByDescriptionAndDateHeureBeforeOrderByDateHeureDesc(String desc, LocalDateTime fin);

    List<Audit> findByDescriptionAndDateHeureAfterOrderByDateHeureDesc(String desc, LocalDateTime debut);

    List<Audit> findByDescriptionOrderByDateHeureDesc(String desc);

    List<Audit> findByUserIdAndDateHeureBetweenOrderByDateHeureDesc(Long userId, LocalDateTime debut, LocalDateTime fin);

    List<Audit> findByUserIdAndDateHeureAfterOrderByDateHeureDesc(Long userId, LocalDateTime debut);

    List<Audit> findByUserIdAndDateHeureBeforeOrderByDateHeureDesc(Long userId, LocalDateTime fin);

    List<Audit> findByDateHeureBetweenOrderByDateHeureDesc(LocalDateTime debut, LocalDateTime fin);

    List<Audit> findByDateHeureBeforeOrderByDateHeureDesc(LocalDateTime fin);

    List<Audit> findByDateHeureAfterOrderByDateHeureDesc(LocalDateTime debut);

	List<Audit> findByUserIdAndDescriptionAndDateHeureAfterOrderByDateHeureDesc(Long userId, String desc,
			LocalDateTime debut);

	List<Audit> findByUserIdAndDescriptionAndDateHeureBeforeOrderByDateHeureDesc(Long userId, String desc,
			LocalDateTime fin);

	List<Audit> findByUserIdAndDescriptionOrderByDateHeureDesc(Long userId, String desc);

	List<Audit> findAllByUserRoleNot(TypeRole role);

    //List<Audit> findOrderByDateHeureDesc(); // NOT OK -> No property desc found for type LocalDateTime! Traversed path: Audit.dateHeure.

}
