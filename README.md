<b>GPS Based Asset Tracking Portal</b><br><br>
  In this project, we have created a portal which helps us to track our assets better. It gives us details of the assets such as the current location, where the asset has been in the last 24 hours etc. We can also specify geofence and georoutes for each of the assets and in case the asset is showing some unexpected behaviour, then a notification is triggered to all the admins of the company.<br><br>
  In addition to this, we have added several filters like id, type of asset and time in which the asset was active.<br><br>
  For authorization we have used role based login such that only the admins of the company are authorized to view the data. In case the a new admin has to be created, only the existing admins will be able to do so. For security purpose, the session login time has been restricted to 1 hour <br><br>
  To run the project follow the steps mentioned below:<br>
  1. Clone the project in your local<br>
  2. Run the main class JumbogpsBackendApplication.java (Path: backend/src/main/java/com/crio/jumbogps)<br>
  3. Execute the command <b>cd frontend</b> and then <b>ng serve</b> to start the server. Server will get started on port 4200. <br>
  4. Follow this link <b>http://localhost:4200/login</b> to view the login page.<br>
  5. Credentials: username: dummy_user password:Test@123<br>
  6. To recieve notifications, allow the notifications in browser.
  <b>For details on the endpoints, please refer to :
  https://sarveksana.ml/swagger-ui/ </b>
