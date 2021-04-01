package com.crio.jumbogps.controller;

import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.crio.jumbogps.model.AuthenticationResponse;
import com.crio.jumbogps.model.LuUser;
import com.crio.jumbogps.repository.UserRepository;
import com.crio.jumbogps.security.JwtUtil;
import com.crio.jumbogps.service.JwtUserDetailService;;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
	
	@Autowired  
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUserDetailService userDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@PostMapping(value = "/user/loginUser")
	public ResponseEntity<?> loginUser(@RequestBody LuUser luUser) throws Exception{
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(luUser.getUsername(), luUser.getPassword()));
		}catch(BadCredentialsException e) {
			throw new Exception("Incorrect Username or Password",e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(luUser.getUsername());
		final String jwtToken = jwtUtil.generateToken(userDetails);
	
		return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
	}
	
	@PostMapping("/user/signup")
	public HttpStatus saveUser(@RequestBody LuUser newUser) {
        LuUser user = userRepository.findByUsername(newUser.getUsername());
        if(user!=null) {
            return HttpStatus.CONFLICT;
        }
        
        newUser.setPassword(encryptPassword(newUser.getPassword()));
        newUser = userRepository.save(newUser);
        if(newUser!=null) {
        	return HttpStatus.OK;
        }else {
        	return HttpStatus.NOT_FOUND;
        }
        
		
	}
	
	private String encryptPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}
	
	@GetMapping("/user/deactiveUser")
	public HttpStatus deactivateUser(HttpServletRequest request) {
		final String authorizationHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;
		
		if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
			jwtToken = authorizationHeader.substring(7);
			username = jwtUtil.getUsernameFromToken(jwtToken);
		}
        LuUser user = userRepository.findByUsername(username);
        if(user == null) {
        	return HttpStatus.NOT_FOUND;
        }else {
        	userRepository.deactiveUser(username);
        	return HttpStatus.OK;
        }

	}

}
