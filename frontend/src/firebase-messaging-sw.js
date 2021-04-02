 importScripts('https://www.gstatic.com/firebasejs/8.3.1/firebase-app.js');
 importScripts('https://www.gstatic.com/firebasejs/8.3.1/firebase-messaging.js');

 // Initialize the Firebase app in the service worker by passing in
 // your app's Firebase config object.
 // https://firebase.google.com/docs/web/setup#config-object
 firebase.initializeApp({
  apiKey: "AIzaSyA4ZRH4ysUDKFL1iDHtFs2BmmXT2R5NL3E",
  authDomain: "notification-test-8be5b.firebaseapp.com",
  projectId: "notification-test-8be5b",
  storageBucket: "notification-test-8be5b.appspot.com",
  messagingSenderId: "1006560389889",
  appId: "1:1006560389889:web:438f634c2dd162f0430900",
  measurementId: "G-CWRZM2YTQX"
 });

 // Retrieve an instance of Firebase Messaging so that it can handle background
 // messages.
 const messaging = firebase.messaging();
 


// If you would like to customize notifications that are received in the
// background (Web app is closed or not in browser focus) then you should
// implement this optional method.
// Keep in mind that FCM will still show notification messages automatically 
// and you should use data messages for custom notifications.
// For more info see: 
// https://firebase.google.com/docs/cloud-messaging/concept-options
messaging.onBackgroundMessage(function(payload) {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);
  // Customize notification here
  const notificationTitle = 'Background Message Title';
  const notificationOptions = {
    body: 'Background Message body.',
    icon: '/firebase-logo.png'
  };

  self.registration.showNotification(notificationTitle,
    notificationOptions);
});
