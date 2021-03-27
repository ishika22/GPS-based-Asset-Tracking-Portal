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

@Entity
@Table(name = "asset_detail") 
public class AssetDetail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5949984005434830805L;
	private Integer pkAssetId;
	private LuAsset fkAssetType;
	private String assetName;
	private String assetContactDetail;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pk_asset_id", unique = true, nullable = false)
	public Integer getPkAssetId() {
		return pkAssetId;
	}
	public void setPkAssetId(Integer pkAssetId) {
		this.pkAssetId = pkAssetId;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_asset_type", nullable = false, referencedColumnName = "pk_asset_type_id")
	public LuAsset getFkAssetType() {
		return fkAssetType;
	}
	public void setFkAssetType(LuAsset fkAssetType) {
		this.fkAssetType = fkAssetType;
	}
	
	@Column(name = "asset_name",nullable = false)
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	
	@Column(name = "asset_contact_detail")
	public String getAssetContactDetail() {
		return assetContactDetail;
	}
	public void setAssetContactDetail(String assetContactDetail) {
		this.assetContactDetail = assetContactDetail;
	}
	

}
