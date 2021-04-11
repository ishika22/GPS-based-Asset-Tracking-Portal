package com.crio.jumbogps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.crio.jumbogps.model.LuUser;

public interface UserRepository extends JpaRepository<LuUser, Integer>  {
	
	@Query("Select user from LuUser user where user.username = :username and user.fkSecurityRoleId.pkSecurityRoleId = (select pkSecurityRoleId from LuSecurityRole where roleName = 'Administrator')")
	LuUser findByUsernameAndPassword(@Param("username")String username);

	LuUser findByUsername(String username);
	
	@Transactional
	@Modifying
	@Query("UPDATE LuUser SET isActive = 0  where username = :username ")
	void deactiveUser(@Param("username") String username);

	@Query(value = "SELECT notification_token FROM lu_user WHERE fk_security_role_id = (SELECT pk_security_role_id FROM lu_security_role WHERE role_name = 'Administrator') AND is_active =1 AND notification_token IS NOT NULL group by notification_token",nativeQuery = true)
	String[] getAllTokens();

}
