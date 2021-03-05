package com.crio.jumbogps;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crio.model.AssetHistory;
//Performs all CRUD operations
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Integer> {
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId)")
	List<AssetHistory> findDistinctByFkAssetIdIn();
	
	@Query("SELECT assets FROM AssetHistory assets WHERE  fkAssetId.pkAssetId = :assetId AND TIMEDIFF(NOW(), timeOfTracking) < '24:00:00'")
	List<AssetHistory> getAssetDetailByIdSinceLastDay(@Param("assetId") Integer assetId);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE  TIMEDIFF(NOW(), timeOfTracking) < '24:00:00' GROUP BY fkAssetId")
	List<AssetHistory> getAssetDetailSinceLastDay();
	
}