package com.crio.jumbogps.controller;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.repository.AssetDetailRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AssetDetailController {
	
	@Autowired  
	private AssetDetailRepository assetDetailRepository;
	
	@PostMapping(value = "/asset/newAsset",consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
	public Integer addNewAsset(@RequestBody AssetDetail assetDetail) {
		
		try {
			return assetDetailRepository.save(assetDetail).getPkAssetId();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
