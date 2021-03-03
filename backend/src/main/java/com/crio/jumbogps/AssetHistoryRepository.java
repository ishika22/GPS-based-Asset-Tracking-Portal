package com.crio.jumbogps;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.crio.model.AssetDetail;
import com.crio.model.AssetHistory;
//Performs all CRUD operations
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Integer> {
	
	@Query("SELECT assets FROM AssetHistory assets WHERE timeOfTracking IN(SELECT MAX(timeOfTracking) FROM AssetHistory GROUP BY 'fkAssetId')")
	List<AssetHistory> findDistinctByFkAssetIdIn();
	
}