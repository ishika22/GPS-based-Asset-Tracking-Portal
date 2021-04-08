package com.crio.jumbogps.Notification;

import java.util.ArrayList;
import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.crio.jumbogps.model.LatLang;
import com.crio.jumbogps.repository.UserRepository;

@Service
public class NotificationSender {

	@Autowired
	UserRepository userRepository;
	
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
	
	public void sendNotification(String topic,String body) {
		
		String[] tokens = userRepository.getAllTokens();
		try{
			for(String token : tokens){
				GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
				FirebaseOptions firebaseOptions = FirebaseOptions
				.builder()
				.setCredentials(googleCredentials)
				.build();
				if(FirebaseApp.getApps().isEmpty())
					FirebaseApp.initializeApp(firebaseOptions,"JumboTail");
				FirebaseApp app = FirebaseApp.getInstance("JumboTail");
				Message message = Message
                .builder().setToken(token)
                .setNotification(new Notification(topic, body))
                .build();
			
				FirebaseMessaging.getInstance(app).sendAsync(message).get();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
