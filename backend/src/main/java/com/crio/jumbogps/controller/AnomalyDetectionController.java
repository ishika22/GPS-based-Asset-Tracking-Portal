package com.crio.jumbogps.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.Notification.NotificationSender;
import com.crio.jumbogps.model.AssetDetail;
import com.crio.jumbogps.model.AssetHistory;
import com.crio.jumbogps.model.LatLang;
import com.crio.jumbogps.repository.AssetDetailRepository;
import com.crio.jumbogps.repository.AssetHistoryRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AnomalyDetectionController {

	@Autowired
	private AssetHistoryRepository assetHistoryRepository;

	@Autowired
	private AssetDetailRepository assetDetailRepository;
	
	@Autowired
	private NotificationSender notificationSender;

	public static final double DEFAULT_TOLERANCE = 100;
	static final double EARTH_RADIUS = 6371009;

	static final double PI = 22 / 7;

	@PostMapping(value = "/anomaly/coordinates")
	public void addGeoFenceCoordinates(@RequestParam("id") Integer assetId,
			@RequestParam("coordinate") String coordinates) {
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if (assetDetailOptional.isPresent()) {
			AssetDetail assetDetail = assetDetailOptional.get();
			assetDetail.setAnomalyDetectionCoordinates(coordinates);
			assetDetailRepository.save(assetDetail);
		}
	}

	@GetMapping(value = "/asset/anomaly")
	public void isLocationOnPath(@RequestParam("id") Integer assetId) {
		AssetHistory assetHistory = assetHistoryRepository.getCurrentLocationOfAsset(assetId);
		Double latitude = assetHistory.getLatitude();
		Double longitude = assetHistory.getLongitude();
		Optional<AssetDetail> assetDetailOptional = assetDetailRepository.findById(assetId);
		if (assetDetailOptional.isPresent()) {
			AssetDetail assetDetail = assetDetailOptional.get();
			List<LatLang> polygon = notificationSender.decodeCoordinates(assetDetail.getAnomalyDetectionCoordinates());
			Boolean assetOnPath = isLocationOnEdgeOrPath(latitude, longitude, polygon, false, true, DEFAULT_TOLERANCE);
			if (!assetOnPath) {
				notificationSender.sendNotification("Asset not on expected route","Asset "+assetDetail.getAssetName()+" is not on the expected route");
			}
		}
	}

	
	private Boolean isLocationOnEdgeOrPath(Double latitude, Double longitude, List<LatLang> polyline, boolean closed,
			boolean geodesic, double defaultTolerance) {
		int idx = locationIndexOnEdgeOrPath(latitude, longitude, polyline, closed, geodesic, defaultTolerance);
		return (idx >= 0);
	}

	private int locationIndexOnEdgeOrPath(Double latitude, Double longitude, List<LatLang> poly, boolean closed,
			boolean geodesic, double toleranceEarth) {
		int size = poly.size();
		if (size == 0) {
			return -1;
		}
		double tolerance = toleranceEarth / EARTH_RADIUS;
		double havTolerance = hav(tolerance);
		double lat3 = Math.toRadians(latitude);
		double lng3 = Math.toRadians(longitude);
		LatLang prev = poly.get(closed ? size - 1 : 0);
		double lat1 = Math.toRadians(prev.getLatitude());
		double lng1 = Math.toRadians(prev.getLongitude());
		int idx = 0;
		if (geodesic) {
			for (LatLang point2 : poly) {
				double lat2 = Math.toRadians(point2.getLatitude());
				double lng2 = Math.toRadians(point2.getLongitude());
				if (isOnSegmentGC(lat1, lng1, lat2, lng2, lat3, lng3, havTolerance)) {
					return Math.max(0, idx - 1);
				}
				lat1 = lat2;
				lng1 = lng2;
				idx++;
			}
		} else {
			double minAcceptable = lat3 - tolerance;
			double maxAcceptable = lat3 + tolerance;
			double y1 = mercator(lat1);
			double y3 = mercator(lat3);
			double[] xTry = new double[3];
			for (LatLang point2 : poly) {
				double lat2 = Math.toRadians(point2.getLatitude());
				double y2 = mercator(lat2);
				double lng2 = Math.toRadians(point2.getLongitude());
				if (Math.max(lat1, lat2) >= minAcceptable && Math.min(lat1, lat2) <= maxAcceptable) {
					double x2 = wrap(lng2 - lng1, -PI, PI);
					double x3Base = wrap(lng3 - lng1, -PI, PI);
					xTry[0] = x3Base;
					xTry[1] = x3Base + 2 * PI;
					xTry[2] = x3Base - 2 * PI;
					for (double x3 : xTry) {
						double dy = y2 - y1;
						double len2 = x2 * x2 + dy * dy;
						double t = len2 <= 0 ? 0 : clamp((x3 * x2 + (y3 - y1) * dy) / len2, 0, 1);
						double xClosest = t * x2;
						double yClosest = y1 + t * dy;
						double latClosest = inverseMercator(yClosest);
						double havDist = havDistance(lat3, latClosest, x3 - xClosest);
						if (havDist < havTolerance) {
							return Math.max(0, idx - 1);
						}
					}
				}
				lat1 = lat2;
				lng1 = lng2;
				y1 = y2;
				idx++;
			}
		}
		return -1;
	}

	private static boolean isOnSegmentGC(Double lat1, Double lng1, Double lat2, Double lng2, Double lat3, Double lng3,
			Double havTolerance) {
		havTolerance = havTolerance.doubleValue() + 1e-6;
		Double havDist13 = havDistance(lat1, lat3, lng1 - lng3).doubleValue() + 1e-6;
		if (havDist13 <= havTolerance) {
			return true;
		}
		Double havDist23 = havDistance(lat2, lat3, lng2 - lng3).doubleValue() + 1e-6;
		if (havDist23 <= havTolerance) {
			return true;
		}
		Double sinBearing = sinDeltaBearing(lat1, lng1, lat2, lng2, lat3, lng3).doubleValue() + 1e-6;
		Double sinDist13 = sinFromHav(havDist13).doubleValue() + 1e-6;
		Double havCrossTrack = havFromSin(sinDist13 * sinBearing).doubleValue() + 1e-6;
		if (havCrossTrack > havTolerance) {
			return false;
		}
		Double havDist12 = havDistance(lat1, lat2, lng1 - lng2).doubleValue() + 1e-6;
		Double term = (havDist12 + havCrossTrack * (1 - 2 * havDist12));
		term = term.doubleValue() + 1e-6;
		if (havDist13 > term || havDist23 > term) {
			return false;
		}
		if (havDist12 < 0.74) {
			return true;
		}
		double cosCrossTrack = 1 - 2 * havCrossTrack;
		double havAlongTrack13 = (havDist13 - havCrossTrack) / cosCrossTrack;
		double havAlongTrack23 = (havDist23 - havCrossTrack) / cosCrossTrack;
		double sinSumAlongTrack = sinSumFromHav(havAlongTrack13, havAlongTrack23);
		return sinSumAlongTrack > 0;
	}

	static Double sinFromHav(double h) {
		return 2 * Math.sqrt(h * (1 - h));
	}
	
	static double sinSumFromHav(double x, double y) {
        double a = Math.sqrt(x * (1 - x));
        double b = Math.sqrt(y * (1 - y));
        return 2 * (a + b - 2 * (a * y + b * x));
    }
	
	static Double havFromSin(double x) {
        double x2 = x * x;
        return x2 / (1 + Math.sqrt(1 - x2)) * .5;
    }

	private static Double sinDeltaBearing(double lat1, double lng1, double lat2, double lng2, double lat3,
			double lng3) {
		double sinLat1 = Math.sin(lat1);
		double cosLat2 = Math.cos(lat2);
		double cosLat3 = Math.cos(lat3);
		double lat31 = lat3 - lat1;
		double lng31 = lng3 - lng1;
		double lat21 = lat2 - lat1;
		double lng21 = lng2 - lng1;
		double a = Math.sin(lng31) * cosLat3;
		double c = Math.sin(lng21) * cosLat2;
		double b = Math.sin(lat31) + 2 * sinLat1 * cosLat3 * hav(lng31);
		double d = Math.sin(lat21) + 2 * sinLat1 * cosLat2 * hav(lng21);
		double denom = (a * a + b * b) * (c * c + d * d);
		return denom <= 0 ? 1 : (a * d - b * c) / Math.sqrt(denom);
	}

	private double wrap(double n, double min, Double max) {
		return (n >= min && n < max) ? n : (((n - min) % (max - min)) + min);
	}

	static double hav(double x) {
		double sinHalf = Math.sin(x * 0.5);
		return sinHalf * sinHalf;
	}

	static double mercator(double lat) {
		return Math.log(Math.tan(lat * 0.5 + PI / 4));
	}

	static double clamp(double x, double low, double high) {
		return x < low ? low : (x > high ? high : x);
	}

	static double inverseMercator(double y) {
		return 2 * Math.atan(Math.exp(y)) - PI / 2;
	}

	static Double havDistance(double lat1, double lat2, double dLng) {
		return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2);
	}
}
