package com.crio.jumbogps.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.model.AssetHistory;
import com.crio.jumbogps.repository.AssetDetailRepository;
import com.crio.jumbogps.repository.AssetHistoryRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AssetController {
	
	@Autowired  
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired  
	private AssetDetailRepository assetDetailRepository;
	
	@GetMapping("/location/list")
	public List<AssetHistory> getAllAssets() {
// 		getCurrentLocationOfAsset();
		return assetHistoryRepository.findDistinctByFkAssetIdIn();
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
	public List<Map<String,Object>> getAAssetsHistoryById(@RequestParam("id") int assetId) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		List<Map<String,Object>> assetHistoryListMap = new ArrayList<Map<String,Object>>();
		
		if(assetDetailOptional.isPresent()) {
			try {
				List<AssetHistory> assetHistoryList = assetHistoryRepository.getAssetDetailByIdSinceLastDay(assetDetailOptional.get().getPkAssetId());
				for(AssetHistory assetHistory : assetHistoryList) {
					Map<String,Object> assetHistoryMap = new HashMap<String,Object>();
					assetHistoryMap.put("pkAssetHistoryDetailId", assetHistory.getPkAssetHistoryDetailId());
					assetHistoryMap.put("timeOfTracking", assetHistory.getTimeOfTracking());
					assetHistoryMap.put("latitude", assetHistory.getLatitude());
					assetHistoryMap.put("longitude", assetHistory.getLongitude());
					assetHistoryListMap.add(assetHistoryMap);
				}
				}catch(Exception e) {
					e.printStackTrace();
				}
			return assetHistoryListMap;
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not found");
		}	
	}
	
	@GetMapping("/location/time")
	public List<AssetHistory> getAssetsHistoryByTime(
		 @RequestParam("startTime")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime ,
		 @RequestParam("endTime")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
		
		try {
			// LocalDateTime start = LocalDateTime.of(date, time)
			// LocalDateTime end = LocalDateTime.parse(endTime);
			return assetHistoryRepository.getAssetDetailsByTime(startTime,endTime);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	@GetMapping("/location/time/id")
	public List<AssetHistory> getAssetsHistoryByIdAndTime(
		 @RequestParam("id") int assetId,
		 @RequestParam("startTime")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime ,
		 @RequestParam("endTime")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
		
		try {
			return assetHistoryRepository.getAssetDetailsByIdAndTime(assetId,startTime,endTime);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
}
