package com.crio.jumbogps.model;

public class LatLang {
	
	private Double latitude;
	private Double longitude;
	public LatLang(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
