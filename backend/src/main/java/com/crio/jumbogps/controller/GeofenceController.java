package com.crio.jumbogps.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.model.AssetHistory;
import com.crio.jumbogps.model.LatLang;
import com.crio.jumbogps.repository.AssetDetailRepository;
import com.crio.jumbogps.repository.AssetHistoryRepository;

@RestController
public class GeofenceController {
	
	@Autowired
	private AssetHistoryRepository assetHistoryRepository;
	
	@Autowired
	private AssetDetailRepository assetDetailRepository;

	double PI = 22/7;
	
	@PostMapping(value = "/geofencing/coordinates")
	public void addGeoFenceCoordinates(@RequestParam("id") Integer assetId,@RequestParam("coordinate") String coordinates) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if(assetDetailOptional.isPresent()) {
			AssetDetail assetDetail = assetDetailOptional.get();
			assetDetail.setGeoFencingCoordinates(coordinates);
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
			List<LatLang> polygon = decode(assetDetail.getGeoFencingCoordinates());
			Boolean assetInsidePolygon = containsLocation(latitude, longitude, polygon);
			if(!assetInsidePolygon) {
				sendNotification();
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

	private void sendNotification() {
		System.out.println("Not inside");
	}
	
	public List<LatLang> decode(final String encodedPath) {
        int len = encodedPath.length();
        final List<LatLang> path = new ArrayList<LatLang>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLang(lat * 1e-5, lng * 1e-5));
        }

        return path;    
      }
}
