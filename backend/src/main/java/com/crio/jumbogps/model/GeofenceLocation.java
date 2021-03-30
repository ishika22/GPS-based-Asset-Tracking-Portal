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
@Table(name = "geofence_location")
public class GeofenceLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5344373395632688792L;
	private Integer pkGeofenceLocation;
	private Double latitude;
	private Double longitude;
	private AssetDetail assetDetail;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pk_geofence_location", unique = true, nullable = false)
	public Integer getPkGeofenceLocation() {
		return pkGeofenceLocation;
	}
	public void setPkGeofenceLocation(Integer pkGeofenceLocation) {
		this.pkGeofenceLocation = pkGeofenceLocation;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "asset_detail", nullable = false, referencedColumnName = "pk_asset_id")
	public AssetDetail getAssetDetail() {
		return assetDetail;
	}
	public void setAssetDetail(AssetDetail assetDetail) {
		this.assetDetail = assetDetail;
	}
	
	@Column(name = "latitude",nullable = false)
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "longitude",nullable = false)
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
