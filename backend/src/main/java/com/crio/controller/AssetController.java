package com.crio.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crio.model.AssetDetail;
import com.crio.model.AssetHistory;
import com.crio.repository.AssetDetailRepository;
import com.crio.repository.AssetHistoryRepository;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
public class AssetController {
	
	@Autowired  
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired  
	private AssetDetailRepository assetDetailRepository;
	
	@GetMapping("/location/list")
	public List<AssetHistory> getAllAssets() {
		getCurrentLocationOfAsset();
		return assetHistoryRepository.findDistinctByFkAssetIdIn();
	}
	
	@PostMapping("/location/currentList")
	private void getCurrentLocationOfAsset() {
		List<AssetDetail> assetDetailList = assetDetailRepository.findAll();
		for(AssetDetail assetDetail : assetDetailList) {
		AssetHistory assetHistory = new AssetHistory();
		double longitude = Math.random() * Math.PI * 2;
		double latitude = Math.acos(Math.random() * 2 - 1);
		
		assetHistory.setFkAssetId(assetDetail);
		assetHistory.setLatitude(latitude);
		assetHistory.setLongitude(longitude);
		assetHistory.setTimeOfTracking(LocalDateTime.now());
		assetHistoryRepository.save(assetHistory);
		}
	}
	
	@GetMapping("/location/type")
	public List<AssetHistory> getAAssetsHistoryByType(@RequestParam("type") int assetType) {
		return assetHistoryRepository.getAssetDetailByType(assetType);
	}
	
	@GetMapping("/location/activeTodayList")
	public List<AssetHistory> getAAssetsHistoryActiveToday() {
		return assetHistoryRepository.getAssetDetailSinceLastDay();	
	}
	
	
	@GetMapping("/location/id")
	public List<AssetHistory> getAAssetsHistoryById(@RequestParam("id") int assetId) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if(assetDetailOptional.isPresent()) {
			return assetHistoryRepository.getAssetDetailByIdSinceLastDay(assetDetailOptional.get().getPkAssetId());
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not found");
		}	
	}
	
	@GetMapping("/location/time")
	public List<AssetHistory> getAAssetsHistoryByTime(@RequestParam("startTime") String startTime , @RequestParam("endTime") String endTime) {
		
		try {
		LocalDateTime start = LocalDateTime.parse(startTime);
		LocalDateTime end = LocalDateTime.parse(endTime);
		return assetHistoryRepository.getAssetDetailsByTime(start,end);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}