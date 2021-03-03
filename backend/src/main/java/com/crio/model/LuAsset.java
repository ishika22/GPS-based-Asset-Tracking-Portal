package com.crio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lu_asset") 
public class LuAsset {
	
	private Integer pkAssetTypeId;
	private String assetType;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pk_asset_type_id", unique = true, nullable = false)
	public Integer getPkAssetTypeId() {
		return pkAssetTypeId;
	}
	public void setPkAssetTypeId(Integer pkAssetTypeId) {
		this.pkAssetTypeId = pkAssetTypeId;
	}
	
	@Column(name = "asset_type")
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

}
