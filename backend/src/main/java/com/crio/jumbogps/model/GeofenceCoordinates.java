package com.crio.jumbogps.model;

import java.io.Serializable;

public class GeofenceCoordinates implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7834197741091659611L;
	private Integer pkAssetId;
	private String[] coordinates;
	public Integer getPkAssetId() {
		return pkAssetId;
	}
	public void setPkAssetId(Integer pkAssetId) {
		this.pkAssetId = pkAssetId;
	}
	public String[] getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(String[] coordinates) {
		this.coordinates = coordinates;
	}

}
