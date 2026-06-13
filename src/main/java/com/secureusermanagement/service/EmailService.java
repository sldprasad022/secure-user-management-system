package com.secureusermanagement.service;

public interface EmailService {

	void sendRegistrationOtp(String email, String otp, int expiryMinutes);
	
	void sendForgotPasswordOtp(String email,String otp,int expiryMinutes);
	        
	        
	        
}
