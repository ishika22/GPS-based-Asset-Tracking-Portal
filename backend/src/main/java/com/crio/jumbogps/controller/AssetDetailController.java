package com.crio.jumbogps.controller;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.model.AssetHistory;
import com.crio.jumbogps.model.GeofenceLocation;
import com.crio.jumbogps.repository.AssetDetailRepository;
import com.crio.jumbogps.repository.AssetHistoryRepository;
import com.crio.jumbogps.repository.GeofenceRepository;

@RestController
public class AssetDetailController {
	
	@Autowired  
	private AssetDetailRepository assetDetailRepository;
	
	@Autowired  
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired
	private GeofenceRepository geofenceRepository;
	
    Double PI = (double) (22/7);
	
	@PostMapping(value = "/asset/newAsset",consumes = {MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
	public Integer addNewAsset(@RequestBody AssetDetail assetDetail) {
		
		try {
			return assetDetailRepository.save(assetDetail).getPkAssetId();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/asset/geofenceCheck")
	private void checkCurrentLocationOfAsset(@RequestParam("id")Integer pkAssetId) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(pkAssetId);
		
		if(assetDetailOptional.isPresent()) {
			AssetHistory currentLocation = assetHistoryRepository.getCurrentLocationOfAsset(pkAssetId);
			Double latitude = currentLocation.getLatitude();
			Double longitude = currentLocation.getLongitude();
			List<GeofenceLocation> polygon = geofenceRepository.findByAssetId(pkAssetId);
			
			Boolean locationContained =  containsLocation(latitude,longitude,polygon);
			if(!locationContained)
				sendNotification();
		}
		
	}

	private Boolean containsLocation(Double latitude,Double longitude,List<GeofenceLocation> polygon) {
		final int size = polygon.size();
        if (size == 0) {
            return false;
        }
        double lat3 = Math.toRadians(latitude);
        double lng3 = Math.toRadians(longitude);
        GeofenceLocation prev = polygon.get(size - 1);
        double lat1 = Math.toRadians(prev.getLatitude());
        double lng1 = Math.toRadians(prev.getLongitude());
        int nIntersect = 0;
        for (GeofenceLocation point2 : polygon) {
            double dLng3 = wrap(lng3 - lng1, -PI, PI);
            // Special case: point equal to vertex is inside.
            if (lat3 == lat1 && dLng3 == 0) {
                return true;
            }
            double lat2 = Math.toRadians(point2.getLatitude());
            double lng2 = Math.toRadians(point2.getLongitude());
            // Offset longitudes by -lng1.
            if (intersects(lat1, lat2, wrap(lng2 - lng1, -PI, PI), lat3, dLng3, true)) {
                ++nIntersect;
            }
            lat1 = lat2;
            lng1 = lng2;
        }
        return (nIntersect & 1) != 0;
	}

	private boolean intersects(double lat1, double lat2, double lng2,
            double lat3, double lng3, boolean geodesic) {
		if ((lng3 >= 0 && lng3 >= lng2) || (lng3 < 0 && lng3 < lng2)) {
            return false;
        }
        if (lat3 <= -PI / 2) {
            return false;
        }
        // Any segment end is a pole.
        if (lat1 <= -PI / 2 || lat2 <= -PI / 2 || lat1 >= PI / 2 || lat2 >= PI / 2) {
            return false;
        }
        if (lng2 <= -PI) {
            return false;
        }
        double linearLat = (lat1 * (lng2 - lng3) + lat2 * lng3) / lng2;
        if (lat1 >= 0 && lat2 >= 0 && lat3 < linearLat) {
            return false;
        }
        if (lat1 <= 0 && lat2 <= 0 && lat3 >= linearLat) {
            return true;
        }
        // North Pole.
        if (lat3 >= PI / 2) {
            return true;
        }
        // Compare lat3 with latitude on the GC/Rhumb segment corresponding to lng3.
        // Compare through a strictly-increasing function (tan() or mercator()) as convenient.
        return geodesic ?
                Math.tan(lat3) >= tanLatGC(lat1, lat2, lng2, lng3) :
                (lat3) >= mercatorLatRhumb(lat1, lat2, lng2, lng3);
	}
	
	private static double tanLatGC(double lat1, double lat2, double lng2, double lng3) {
        return (Math.tan(lat1) * Math.sin(lng2 - lng3) + Math.tan(lat2) * Math.sin(lng3)) / Math.sin(lng2);
    }
	
	private static double mercatorLatRhumb(double lat1, double lat2, double lng2, double lng3) {
        return ((lat1) * (lng2 - lng3) + (lat2) * lng3) / lng2;
    }

	private double wrap(double n, double min, Double max) {
		return (n>=min && n<max)? n : (((n-min)%(max-min))+min);
	}

	private void sendNotification() {
		System.out.println("Not inside");
	}

}
