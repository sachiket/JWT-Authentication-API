package com.cg.iter.authenticationservice.service;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;

import com.cg.iter.authenticationservice.entity.User;
import com.cg.iter.authenticationservice.entity.request.LoginRequest;
import com.cg.iter.authenticationservice.entity.request.SignupRequest;
import com.cg.iter.authenticationservice.entity.response.JwtResponse;

public interface AuthService {

	/*
	 * Name: authenticateUser
	 * Description: it will authenticate the user with user id and password and return details and authentication token as response
	 */
	ResponseEntity<JwtResponse> authenticateUser(@Valid LoginRequest loginRequest);

	
	/*
	 * Name: registerUser
	 * Description: it will authenticate the user with user , create a new user and return success of failed message.
	 */
	ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest);
	
	
	/*
	 * Name: editUser
	 * Description: Any actor can edit existing actor details and return a boolean value.
	 */
	boolean editUser(User user);
	
	
	/*
	 * Name: deleteUser
	 * Description: Admin can delete existing user or product master and return a boolean value.
	 */
	boolean deleteUser(User user);
	
	boolean addUser(User user);
	
	
	long getDbCount();

}
