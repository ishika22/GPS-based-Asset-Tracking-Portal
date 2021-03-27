package com.crio.jumbogps.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "lu_user")
@Where(clause ="is_active <> 0")
public class LuUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4952425561889424296L;
	private Integer pkUserId;
	private String firstName;
	private String lastName;
	private String email;
	private LuSecurityRole fkSecurityRoleId;
	private String username;
	private String password;
	private Boolean isActive = true;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pk_user_id", unique = true, nullable = false)
	public Integer getPkUserId() {
		return pkUserId;
	}
	public void setPkUserId(Integer pkUserId) {
		this.pkUserId = pkUserId;
	}
	
	@Column(name = "first_name",nullable = false)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name = "last_name")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_security_role_id", nullable = false, referencedColumnName = "pk_security_role_id")
	public LuSecurityRole getFkSecurityRoleId() {
		return fkSecurityRoleId;
	}
	public void setFkSecurityRoleId(LuSecurityRole fkSecurityRoleId) {
		this.fkSecurityRoleId = fkSecurityRoleId;
	}
	
	@Column(name = "username",nullable = false)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name = "password",nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "is_active", nullable = false, columnDefinition = "tinyint default 1")
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
