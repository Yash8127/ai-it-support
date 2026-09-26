package com.yaswanth.itsupport.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
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
	private final EmailService emailService;
	@Value("${app.frontend.url}")
	private String frontendUrl;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			EmailService emailService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}

	// Register
	public String register(String username, String email, String password) {

		if (userRepository.findByUsername(username).isPresent()) {
			return "Username already exists";
		}

		if (userRepository.findByEmail(email).isPresent()) {
			return "Email already exists";
		}

		String encryptedPassword = passwordEncoder.encode(password);

		User user = new User();

		user.setUsername(username);
		user.setEmail(email);
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

	// Forgot Password
	public String forgotPassword(String email) {

		User user = userRepository.findByEmail(email).orElse(null);

		/*
		 * Always return the same message. This prevents revealing whether an email
		 * exists in the system.
		 */
		if (user == null) {
			return "If the email exists, a password reset link has been generated.";
		}

		SecureRandom secureRandom = new SecureRandom();

		byte[] tokenBytes = new byte[32];

		secureRandom.nextBytes(tokenBytes);

		String resetToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

		LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

		user.setResetToken(resetToken);
		user.setResetTokenExpiry(expiry);

		userRepository.save(user);

		String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

		emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);

		return "If the email exists, a password reset link has been generated.";
	}

	// Validate Reset Token
	public boolean validateResetToken(String resetToken) {

		User user = userRepository.findByResetToken(resetToken).orElse(null);

		// Token does not exist
		if (user == null) {
			return false;
		}

		// Token has expired
		if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

			return false;
		}

		return true;
	}

	// Reset Password
	public String resetPassword(String resetToken, String newPassword) {

		User user = userRepository.findByResetToken(resetToken).orElse(null);

		// Token does not exist
		if (user == null) {
			return "Invalid or expired reset token";
		}

		// Token has expired
		if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

			return "Invalid or expired reset token";
		}
		// Check whether new password is same as current password
		if (passwordEncoder.matches(newPassword, user.getPassword())) {

			return "New password must be different from your current password";
		}

		// Encrypt the new password
		String encryptedPassword = passwordEncoder.encode(newPassword);

		user.setPassword(encryptedPassword);

		// Make the token single-use
		user.setResetToken(null);
		user.setResetTokenExpiry(null);

		userRepository.save(user);

		return "Password reset successfully";
	}

}