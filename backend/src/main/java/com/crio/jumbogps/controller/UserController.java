package com.crio.jumbogps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crio.jumbogps.model.LuUser;
import com.crio.jumbogps.repository.UserRepository;

@RestController
public class UserController {
	
	@Autowired  
	private UserRepository userRepository;
	
	@GetMapping(value = "/user/loginUser")
	public Boolean loginUser(@RequestParam("username") String username, @RequestParam("password") String password) {
		return userFound(username,password);
	}
	
	private Boolean userFound(String username, String password) {
		List<LuUser> userList = userRepository.findByUsernameAndPassword(username,password);
		if(userList.size()>0)
			return true;
		else 
			return false;
	}

}
