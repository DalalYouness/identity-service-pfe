package com.dalal.identityservicepfe.repositories;

import com.dalal.identityservicepfe.entities.PrestataireProfil;
import com.dalal.identityservicepfe.entities.Profil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfilRepository extends JpaRepository<Profil, Long> {

    @Query("SELECT p FROM PrestataireProfil p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PrestataireProfil> searchByFullName(@Param("query") String query, Pageable pageable);
    Page<PrestataireProfil> findByCityIgnoreCase(String city, Pageable pageable);
    Optional<Profil> findByUserEmail(String email);

}
