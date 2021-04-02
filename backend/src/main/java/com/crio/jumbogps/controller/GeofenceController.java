package com.crio.jumbogps.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.json.JSONException;
import com.crio.jumbogps.Notification.NotificationSender;
import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.model.AssetHistory;
import com.crio.jumbogps.model.LatLang;
import com.crio.jumbogps.repository.AssetDetailRepository;
import com.crio.jumbogps.repository.AssetHistoryRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class GeofenceController {
	
	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired
	private AssetDetailRepository assetDetailRepository;
	
	@Autowired
	private NotificationSender notificationSender;

	private double PI = 22/7;
	
	@PostMapping(value = "/geofencing/coordinates")
	public void addGeoFenceCoordinates(@RequestBody AssetDetail assetDetails) throws JSONException {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetDetails.getPkAssetId());
		if(assetDetailOptional.isPresent()) {
			AssetDetail assetDetail = assetDetailOptional.get();
			assetDetail.setGeoFencingCoordinates(assetDetails.getGeoFencingCoordinates());
			assetDetailRepository.save(assetDetail);
		}
	}
	
	
	@GetMapping(value = "/asset/geofencing")
	public void checkAssetInGeofence(@RequestParam("id") Integer assetId) {
		AssetHistory assetHistory = assetHistoryRepository.getCurrentLocationOfAsset(assetId);
		Double latitude = assetHistory.getLatitude();
		Double longitude = assetHistory.getLongitude();
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if(assetDetailOptional.isPresent()) {
			AssetDetail assetDetail = assetDetailOptional.get();
			List<LatLang> polygon = notificationSender.decodeCoordinates(assetDetail.getGeoFencingCoordinates());
			Boolean assetInsidePolygon = containsLocation(latitude, longitude, polygon);
			if(!assetInsidePolygon) {
				notificationSender.sendNotification();
			}
		}
		
	}
	
	private Boolean containsLocation(Double latitude,Double longitude,List<LatLang> polygon) {
		final int size = polygon.size();
        if (size == 0) {
            return false;
        }
        double lat3 = Math.toRadians(latitude);
        double lng3 = Math.toRadians(longitude);
        LatLang prev = polygon.get(size - 1);
        double lat1 = Math.toRadians(prev.getLatitude());
        double lng1 = Math.toRadians(prev.getLongitude());
        int nIntersect = 0;
        for (LatLang point2 : polygon) {
            double dLng3 = wrap(lng3 - lng1, -PI, PI);
            if (lat3 == lat1 && dLng3 == 0) {
                return true;
            }
            double lat2 = Math.toRadians(point2.getLatitude());
            double lng2 = Math.toRadians(point2.getLongitude());
            if (intersects(lat1, lat2, wrap(lng2 - lng1, -PI, PI), lat3, dLng3, true)) {
                ++nIntersect;
            }
            lat1 = lat2;
            lng1 = lng2;
        }
        return (nIntersect & 1) != 0;
	}

	private boolean intersects(double lat1, double lat2, double lng2,double lat3, double lng3, boolean geodesic) {
		if ((lng3 >= 0 && lng3 >= lng2) || (lng3 < 0 && lng3 < lng2)) {
            return false;
        }
        if (lat3 <= -PI / 2) {
            return false;
        }
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
        if (lat3 >= PI / 2) {
            return true;
        }
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

}
