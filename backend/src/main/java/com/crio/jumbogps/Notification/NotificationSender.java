package com.crio.jumbogps.Notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crio.jumbogps.model.LatLang;

@Service
public class NotificationSender {
	
	public List<LatLang> decodeCoordinates(String encodedPath) {
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
	
	public void sendNotification() {
		System.out.println("Not inside");
	}


}
