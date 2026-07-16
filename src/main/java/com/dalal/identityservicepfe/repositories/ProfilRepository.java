package com.dalal.identityservicepfe.repositories;

import com.dalal.identityservicepfe.entities.PrestataireProfil;
import com.dalal.identityservicepfe.entities.Profil;
import com.dalal.identityservicepfe.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfilRepository extends JpaRepository<Profil, Long> {

    @Query("SELECT p FROM PrestataireProfil p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PrestataireProfil> searchByFullName(@Param("query") String query, Pageable pageable);
    Page<PrestataireProfil> findByCityIgnoreCase(String city, Pageable pageable);
    Optional<Profil> findByUserEmail(String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE profils SET type_profil = 'PRESTATAIRE', intervention_area = :interventionArea, updated_at = NOW() WHERE id = :id", nativeQuery = true)
    void convertToPrestataireNative(@Param("id") Long id, @Param("interventionArea") String interventionArea);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE profils SET type_profil = 'CLIENT', updated_at = NOW() WHERE id = :id", nativeQuery = true)
    void convertToClientNative(@Param("id") Long id);

    @Query("SELECT p FROM Profil p JOIN p.user u JOIN u.roles r WHERE r.roleName = :roleName")
    List<Profil> findAllByRoleName(@Param("roleName") RoleName roleName);

}
