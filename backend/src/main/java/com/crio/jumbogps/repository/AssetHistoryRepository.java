package com.crio.jumbogps.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crio.jumbogps.model.AssetHistory;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Integer> {
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId)")
	List<AssetHistory> findDistinctByFkAssetIdIn();
	
	@Query("SELECT assets FROM AssetHistory assets WHERE  TIMEDIFF(NOW(), timeOfTracking) < '24:00:00' GROUP BY fkAssetId")
	List<AssetHistory> getAssetDetailSinceLastDay();
	
	@Query(value ="SELECT assets FROM AssetHistory assets WHERE  fkAssetId.pkAssetId = :assetId AND TIMEDIFF(NOW(), timeOfTracking) < '24:00:00'")
	List<AssetHistory> getAssetDetailByIdSinceLastDay(@Param("assetId") Integer assetId);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId) AND fkAssetId.fkAssetType.pkAssetTypeId = :assetType")
	List<AssetHistory> getAssetDetailByType(@Param("assetType") Integer assetType);
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking >= :startDate and timeOfTracking <= :endDate AND timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY fkAssetId)")
	List<AssetHistory> getAssetDetailsByTime(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
	
}