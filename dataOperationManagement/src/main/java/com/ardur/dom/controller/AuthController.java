package com.ardur.dom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.ardur.dom.services.UserServiceImplmentation;
import com.ardur.dom.user.domain.Role;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;
    private UserServiceImplmentation userServiceImplmentation;

    public AuthController(UserRepository userRepository,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder,
                          UserServiceImplmentation userServiceImplmentation) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userServiceImplmentation = userServiceImplmentation;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {
        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {
            throw new UserException("User is Already Registered...");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setFirstName(firstName);
        createdUser.setLastName(lastName);
        
        // Set the role to ADMIN
        createdUser.setRole(Role.ADMIN); // Assign the ADMIN role to the new user
        
        // Set created timestamp
        createdUser.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(createdUser);

        // Create a UsernamePasswordAuthenticationToken with role
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            savedUser.getEmail(), 
            savedUser.getPassword(), 
            Collections.singleton(savedUser.getRole()) // Use a singleton collection for the role
        );

//        Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Done");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, "SignIn Successful");

        return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = userServiceImplmentation.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid User Details..");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Need Correct PASSWORD to access");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
