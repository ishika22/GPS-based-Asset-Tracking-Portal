package com.crio.jumbogps.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.crio.jumbogps.model.LuUser;
import com.crio.jumbogps.repository.UserRepository;

@Service
public class JwtUserDetailService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LuUser luUser = userRepository.findByUsernameAndPassword(username);
		if(luUser!=null) {
			return new User(luUser.getUsername(),luUser.getPassword(),new ArrayList<>());
		}
		 return null;
	}
	
}
