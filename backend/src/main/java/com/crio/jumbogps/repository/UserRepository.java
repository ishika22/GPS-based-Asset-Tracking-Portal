package com.crio.jumbogps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crio.jumbogps.model.LuUser;

public interface UserRepository extends JpaRepository<LuUser, Integer>  {
	
	@Query("Select user from LuUser user where user.username = :username and user.password = :password and user.fkSecurityRoleId.pkSecurityRoleId = (select pkSecurityRoleId from LuSecurityRole where roleName = 'Administrator')")
	List<LuUser> findByUsernameAndPassword(@Param("username")String username,@Param("password") String password);

}
