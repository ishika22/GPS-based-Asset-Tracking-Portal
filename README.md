<b>GPS Based Asset Tracking Portal</b><br><br>
  In this project, we have created a portal which helps us to track our assets better. It gives us details of the assets such as the current location, where the asset has been in the last 24 hours etc. We can also specify geofence and georoutes for each of the assets and in case the asset is showing some unexpected behaviour, then a notification is triggered to all the admins of the company.<br><br>
  In addition to this, we have added several filters like id, type of asset and time in which the asset was active.<br><br>
  For authorization we have used role based login such that only the admins of the company are authorized to view the data. In case the a new admin has to be created, only the existing admins will be able to do so. For security purpose, the session login time has been restricted to 1 hour <br><br>
  To run the project follow the steps mentioned below:<br>
  1. Clone the project in your local<br>
  2. To setup database in local, make changes in <b>application.properties</b> (Path: backend/src/main/resources). Below are the properties which needs to be changed: <br>
      1. spring.datasource.url = <your database/schema><br>
      2. spring.datasource.username = <database_username><br>
      3. spring.datasource.password = <database_password><br>
  3. Run the main class JumbogpsBackendApplication.java (Path: backend/src/main/java/com/crio/jumbogps)<br>
  4. Execute the command <b>cd frontend</b> and then <b>ng serve</b> to start the server. Server will get started on port 4200. <br>
  5. When running for the first time, create a dummy user for yourself using the query given below:<br>
      INSERT INTO `lu_user` (`first_name`, `last_name`, `email`, `username`, `password`, 
      `fk_security_role_id`, `is_active`) VALUES ('Dummy', 'User', 'dummy_user', 'dummy_user', 
      '$2a$10$EZYghUaylPejXbRfQvDg2uxEzgphpK0.fFtDcWdZF5a5QKKVDeM4y', '1', '1'); <br>
  6. Follow this link <b>http://localhost:4200/login</b> to view the login page.<br>
  7. Credentials: username: dummy_user password:Test@123<br>
  8. To recieve notifications, allow the notifications in browser.<br><br>
  
      
   The app can also be accessed publicly from <b> https://jumbogps.web.app/login </b> Please login using the same credentials mentioned above<br><br>
  <b><a href = "https://sarveksana.ml/swagger-ui/">Swagger for details</a></b>
