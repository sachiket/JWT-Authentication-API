package com.cg.iter.authenticationservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cg.iter.authenticationservice.entity.ERole;
import com.cg.iter.authenticationservice.entity.User;
import com.cg.iter.authenticationservice.entity.request.LoginRequest;
import com.cg.iter.authenticationservice.entity.request.SignupRequest;
import com.cg.iter.authenticationservice.entity.response.JwtResponse;
import com.cg.iter.authenticationservice.repository.UserRepository;
import com.cg.iter.authenticationservice.util.GenerateUserId;
import com.cg.iter.authenticationservice.util.JwtUtil;
import com.cg.iter.authenticationservice.util.Validator;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	GenerateUserId generate;
	
	@Autowired
	JwtUtil jwtUtils;
	
	@Autowired
	Validator validator;
	
	
	/*
	 * Name: authenticateUser
	 * Description: it will authenticate the user with user id and password and return details and authentication token as response
	 */
	@Override
	public ResponseEntity<JwtResponse> authenticateUser(@Valid LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail()).get();
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 userDetails.getPhoneno(),
												 roles));
	}

	
	

	 
	 
	/*
	 * Name: registerUser
	 * Description: it will authenticate the user with user , create a new user and return success of failed message.
	 */
	@Override
	public ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest) {
		
		validator.checkPassword(signUpRequest.getPassword());
		validator.checkPhoneNumber(signUpRequest.getPhoneno());
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body("Error: Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body("Error: Email is already in use!");
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
				encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getEmail(),
							 signUpRequest.getPhoneno());

		Set<String> strRoles = signUpRequest.getRole();
		Set<String> roles = new HashSet<>();

		if (strRoles == null) {
			new RuntimeException("Error: Role is not found.");	
		}
		
		
		else {
			strRoles.forEach(role -> {
				
				switch (role) {
				case "admin":
					roles.add(ERole.ROLE_ADMIN.toString());
					break;
					
				case "master":
					roles.add(ERole.ROLE_PRODUCT_MASTER.toString());
					break;
				
				case "retailer":
					roles.add(ERole.ROLE_RETAILER.toString());
					break;
					
				default:
					roles.add(ERole.ROLE_USER.toString());
				}
			});
		}

		
		user.setRoles(roles);
		user.setId(generate.generateUserId(signUpRequest.getPhoneno()));
		userRepository.save(user);
		
		//authenticate the same user to login
		LoginRequest login = new LoginRequest();
		login.setEmail(signUpRequest.getEmail());
		login.setPassword(signUpRequest.getPassword());

		return authenticateUser(login);
	}





	/*
	 * Name: editUser
	 * Description: Any actor can edit existing actor details and return a boolean value.
	 */
	@Override
	public boolean editUser(User user) {
		if(userRepository.existsByEmail(user.getEmail())) {
			userRepository.save(user);
			return true;
		}
		return false;
	}





	
	/*
	 * Name: deleteUser
	 * Description: Admin can delete existing user or product master and return a boolean value.
	 */
	@Override
	public boolean deleteUser(User user) {
		userRepository.delete(user);
		return true;
	}





	@Override
	public long getDbCount() {
		return userRepository.count();
	}





	@Override
	public boolean addUser(User user) {
		user.setId(generate.generateUserId(user.getPhoneno()));
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return true;
	}

}
