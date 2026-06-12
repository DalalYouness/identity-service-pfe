package com.dalal.identityservicepfe.repositories;

import com.dalal.identityservicepfe.entities.Role;
import com.dalal.identityservicepfe.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleName name);
}
