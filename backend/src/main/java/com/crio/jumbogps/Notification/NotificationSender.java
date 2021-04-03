package com.crio.jumbogps.Notification;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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
	
	public void sendNotification(String topic,String message) {
		String token = "dLYg7gQqJnJ4doeUUpyj7P:APA91bHNDwMGq-wzTkF3e-2zfbWYjEMsNB7zOFKBKUEi02kLIGdC_D210JdvM_431O2v3VaHEzLi5_AuMzXSQPyLrPvYFDxJ1O8u-xS94cG6wr9tSKRPhw4ziXNTOSOG3owU_bxpIszc";
		AndroidConfig androidConfig = getAndroidConfig(topic);
        ApnsConfig apnsConfig = getApnsConfig(topic);
        Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                        new Notification(topic, message)).setToken(token).build();

		System.out.println("Not inside");

	}

	private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setTag(topic).build()).build();
    }
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }


}
