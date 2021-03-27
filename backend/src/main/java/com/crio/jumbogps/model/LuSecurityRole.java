package com.crio.jumbogps.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lu_security_role")
public class LuSecurityRole implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5078712183413799071L;
	private Integer pkSecurityRoleId;
	private String roleName;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pk_security_role_id", unique = true, nullable = false)
	public Integer getPkSecurityRoleId() {
		return pkSecurityRoleId;
	}
	public void setPkSecurityRoleId(Integer pkSecurityRoleId) {
		this.pkSecurityRoleId = pkSecurityRoleId;
	}
	
	@Column(name = "role_name",nullable = false)
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
