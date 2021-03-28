package com.crio.jumbogps.controller;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.model.LuUser;
import com.crio.jumbogps.repository.UserRepository;;

@RestController
public class UserController {
	
	@Autowired  
	private UserRepository userRepository;
	
	
	@GetMapping(value = "/user/loginUser")
	public Boolean loginUser(@RequestParam("username") String username, @RequestParam("password") String password) {
		return userFound(username,password);
	}
	
	private Boolean userFound(String username, String password) {
		List<LuUser> userList = userRepository.findByUsernameAndPassword(username);
		if(userList.size()>0) {
			LuUser user = userList.get(0);
			if(validatePassword(password,user.getPassword())) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean validatePassword(String plainPassword, String hashedPassword) {
		if (BCrypt.checkpw(plainPassword, hashedPassword)) {
			return true;
		}
		return false;
	}
	
	@PostMapping("/user/signup")
	public HttpStatus saveUser(@RequestBody LuUser newUser) {
        List<LuUser> existingUsers = userRepository.findAll();
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

}
