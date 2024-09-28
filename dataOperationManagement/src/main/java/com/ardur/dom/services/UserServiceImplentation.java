package com.ardur.dom.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ardur.dom.config.JwtTokenProvider;
import com.ardur.dom.exceptions.UserException;
import com.ardur.dom.model.User;
//import com.ardur.dom.model.User;
import com.ardur.dom.repository.UserRepository;

@Service
public class UserServiceImplentation implements  UserDetailsService  {

	private UserRepository userRepository;
	private JwtTokenProvider jwtTokenProvider;

	public UserServiceImplentation(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
		super();
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);

		if (user == null) {

			throw new UsernameNotFoundException("user not found with id - " + username);
		}
		
		List<GrantedAuthority> authorities=new ArrayList<>();
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);
	}







	

}
