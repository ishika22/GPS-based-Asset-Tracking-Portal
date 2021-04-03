package com.crio.jumbogps.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crio.jumbogps.model.AssetHistory;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Integer> {
	
	@Query(value = "SELECT * FROM asset_history assets WHERE time_of_tracking IN(SELECT MAX(time_of_tracking) FROM asset_history GROUP BY fk_asset_id) LIMIT :limit",nativeQuery = true)
	List<AssetHistory> findDistinctByFkAssetIdIn(@Param("limit") Integer numberOfAsset);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE  TIMEDIFF(NOW(), timeOfTracking) < '24:00:00' GROUP BY fkAssetId")
	List<AssetHistory> getAssetDetailSinceLastDay();
	
	@Query(value ="SELECT assets FROM AssetHistory assets WHERE  fkAssetId.pkAssetId = :assetId AND TIMEDIFF(NOW(), timeOfTracking) < '24:00:00'")
	List<AssetHistory> getAssetDetailByIdSinceLastDay(@Param("assetId") Integer assetId);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId) AND fkAssetId.fkAssetType.pkAssetTypeId = :assetType")
	List<AssetHistory> getAssetDetailByType(@Param("assetType") Integer assetType);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking >= :startDate and timeOfTracking <= :endDate AND timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId)")
	List<AssetHistory> getAssetDetailsByTime(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking >= :startTime and timeOfTracking <= :endTime AND fkAssetId.pkAssetId = :assetId")
	List<AssetHistory> getAssetDetailsByIdAndTime(int assetId, LocalDateTime startTime, LocalDateTime endTime);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE fkAssetId.pkAssetId = :assetId and timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory where fkAssetId.pkAssetId = :assetId) ")
	AssetHistory getCurrentLocationOfAsset(@Param("assetId") Integer pkAssetId);
	
}