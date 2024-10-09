package com.ardur.dom.services;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.ardur.dom.exceptions.UserException;
import com.ardur.dom.model.User;

public interface UserService {
	

	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public List<User> findAllUsers();

	User addUser(User user);

	List<User> uploadEmployeesFromExcel(MultipartFile file) throws IOException;

}
