package com.yaswanth.itsupport.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.LoginResponse;
import com.yaswanth.itsupport.entity.User;
import com.yaswanth.itsupport.expection.InvalidCredentialsException;
import com.yaswanth.itsupport.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

//register
	public String register(String username, String password) {

		if (userRepository.findByUsername(username).isPresent()) {
			return "Username already exists";
		}

		String encryptedPassword = passwordEncoder.encode(password);

		User user = new User();
		user.setUsername(username);
		user.setPassword(encryptedPassword);
		user.setRole("USER");

		userRepository.save(user);

		return "User registered successfully";
	}

	// Login Service
	public LoginResponse login(String username, String password) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Invalid username or password"));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidCredentialsException("Invalid username or password");
		}

		String token = jwtService.generateToken(user.getUsername(), user.getRole());

		return new LoginResponse("Login successful", user.getUsername(), user.getRole(), token);
	}
}