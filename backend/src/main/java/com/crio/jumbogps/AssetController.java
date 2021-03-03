package com.crio.jumbogps;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crio.model.AssetDetail;
import com.crio.model.AssetHistory;

@RestController
public class AssetController {
	
	@Autowired  
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired  
	private AssetDetailRepository assetDetailRepository;
	
	@GetMapping("/location/all")
	public List<AssetHistory> getAllAssets() {
		getCurrentLocationOfAsset();
		return assetHistoryRepository.findDistinctByFkAssetIdIn();
	}
	
	@PostMapping("/location/current")
	private void getCurrentLocationOfAsset() {
		List<AssetDetail> assetDetailList = assetDetailRepository.findAll();
		for(AssetDetail assetDetail : assetDetailList) {
		AssetHistory assetHistory = new AssetHistory();
		double longitude = Math.random() * Math.PI * 2;
		double latitude = Math.acos(Math.random() * 2 - 1);
		
		assetHistory.setFkAssetId(assetDetail);
		assetHistory.setLatitude(latitude);
		assetHistory.setLongitude(longitude);
		assetHistory.setTimeOfTracking(new Date());
		assetHistoryRepository.save(assetHistory);
		}
	}
	
	@GetMapping("/location/{id}")
	public List<AssetHistory> getAAssetsHistoryById(@PathVariable("id") int assetId) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if(assetDetailOptional.isPresent()) {
			return assetHistoryRepository.getAssetDetailByIdSinceLastDay(assetDetailOptional.get().getPkAssetId());
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not found");
		}	
	}
	
	@GetMapping("/location/activeToday")
	public List<AssetHistory> getAAssetsHistoryActiveToday() {
		return assetHistoryRepository.getAssetDetailSinceLastDay();
		
	}
}