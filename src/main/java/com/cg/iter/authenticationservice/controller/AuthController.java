package com.cg.iter.authenticationservice.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.cg.iter.authenticationservice.entity.request.LoginRequest;
import com.cg.iter.authenticationservice.entity.request.SignupRequest;
import com.cg.iter.authenticationservice.entity.response.JwtResponse;
import com.cg.iter.authenticationservice.exception.NullParameterException;
import com.cg.iter.authenticationservice.service.AuthService;
import com.cg.iter.authenticationservice.util.Validator;

import io.swagger.annotations.ApiOperation;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/app/auth")
public class AuthController {
	
	@Autowired
	AuthService authService;
	@Autowired
	Validator validator;
	
	@ApiOperation(
			value = "Signin",
			notes = "Any authenticated user can signin with this API",
			response = JwtResponse.class
			)
	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.authenticateUser(loginRequest);
	}
	
	@ApiOperation(
			value = "Signup",
			notes = "Any user can signup with this API with valid details",
			response = JwtResponse.class
			)
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if(signUpRequest==null || signUpRequest.getUsername()==null)throw new NullParameterException("Null input! please provide valid user details!");
		return authService.registerUser(signUpRequest);
	}
	

	
}
