package com.crio.jumbogps.repository;

import java.util.List;

import com.crio.jumbogps.model.LuSecurityRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SecurityRoleRepository extends JpaRepository<LuSecurityRole, Integer> {
    @Query("Select role from LuSecurityRole role group by roleName")
    List<LuSecurityRole> findDistinctRoleName();
    
}
