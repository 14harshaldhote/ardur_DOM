package com.ardur.dom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ardur.dom.config.JwtTokenProvider;
import com.ardur.dom.exceptions.UserException;
import com.ardur.dom.model.User;
import com.ardur.dom.repository.UserRepository;
import com.ardur.dom.request.LoginRequest;
import com.ardur.dom.response.AuthResponse;
import com.ardur.dom.services.UserServiceImplentation;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserRepository userRepository;
	private JwtTokenProvider jwtTokenProvider;
	private PasswordEncoder passwordEncoder;
	private UserServiceImplentation userServiceImplentation;
	
	
	
	

	public AuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,UserServiceImplentation userServiceImplentation) {
		super();
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder=passwordEncoder;
		this.userServiceImplentation=userServiceImplentation;
	}





	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user)throws UserException{
		String email=user.getEmail();
		String password=user.getPassword();
		String firstName=user.getFirstName();
		String lastName=user.getLastName();
		
		
		User isEmailExist=userRepository.findByEmail(email);
		
		if(isEmailExist!=null) {
			throw new UserException("Email is Already Registered...");
		}
		
		User createdUser=new User();
				createdUser.setEmail(email);
		createdUser.setPassword(passwordEncoder.encode(password));
		createdUser.setFirstName(firstName);
		createdUser.setLastName(lastName);
		
		User savedUser=userRepository.save(createdUser);
		
		Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token=jwtTokenProvider.generateToken(authentication);
		
		AuthResponse authResponse=new AuthResponse(token, "Signup done");
		
		
		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
		
	}
	

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> LoginUserHandler(@RequestBody LoginRequest loginRequest){
		
		String username=loginRequest.getEmail();
		String password=loginRequest.getPassword();
		
		Authentication authentication=authenticate(username, password);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		

		String token=jwtTokenProvider.generateToken(authentication);
		
		AuthResponse authResponse=new AuthResponse(token, "SignIn Successful");
		
		
		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);
		
		
		
	}
	
	private Authentication authenticate(String username, String password) {
		UserDetails userDetails=userServiceImplentation.loadUserByUsername(username);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid User Details..");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Need Correct PASSWORD to access");
		}
		
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
	
	
	
	

	
	

}
