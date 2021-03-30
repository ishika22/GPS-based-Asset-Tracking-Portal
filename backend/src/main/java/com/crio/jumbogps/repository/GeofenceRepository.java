package com.crio.jumbogps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crio.jumbogps.model.GeofenceLocation;

public interface GeofenceRepository extends JpaRepository<GeofenceLocation, Integer> {
	
	@Query("SELECT geofence FROM GeofenceLocation geofence WHERE  assetDetail.pkAssetId = :pkAssetId")
	List<GeofenceLocation> findByAssetId(@Param("pkAssetId") Integer pkAssetId);

}
