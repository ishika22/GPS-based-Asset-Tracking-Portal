package com.crio.jumbogps.repository;

import com.crio.jumbogps.model.LuSecurityRole;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityRoleRepository extends JpaRepository<LuSecurityRole, Integer> {
    
}
