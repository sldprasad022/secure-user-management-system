package com.secureusermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secureusermanagement.dto.APIResponse;
import com.secureusermanagement.dto.EmailRegisterRequestDto;
import com.secureusermanagement.dto.ForgotPasswordDto;
import com.secureusermanagement.dto.ForgotPasswordOtpRequestDto;
import com.secureusermanagement.dto.LoginRequestDto;
import com.secureusermanagement.dto.LoginResponseDto;
import com.secureusermanagement.dto.ResendOtpRequestDto;
import com.secureusermanagement.dto.UserRegisterDto;
import com.secureusermanagement.dto.VerifyEmailOtpDto;
import com.secureusermanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Authentication Module",description = "APIs for user registration, email verification, OTP management, authentication, account recovery, and password reset operations")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController 
{
	@Autowired
	private UserService userService;
	
	@Operation(summary = "Send registration OTP",description = "Sends a one-time password (OTP) to the provided email address for registration verification.")
	@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
		    @ApiResponse(responseCode = "409", description = "Email already registered"),
		    @ApiResponse(responseCode = "400", description = "Invalid request")
	})
	@PostMapping("/register/send-otp")   
	public ResponseEntity<APIResponse<Void>> initiateEmailRegistration(@Valid @RequestBody EmailRegisterRequestDto emailRegisterRequestDto)
	{
		userService.initiateEmailRegistration(emailRegisterRequestDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "OTP sent to email successfully", null));
	}
	
	@Operation(summary = "Verify registration OTP",description = "Validates the OTP sent to the user's email address and marks the email as verified.")
	@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Email verified successfully"),
		    @ApiResponse(responseCode = "401", description = "Invalid OTP"),
		    @ApiResponse(responseCode = "410", description = "OTP expired"),
		    @ApiResponse(responseCode = "404", description = "Email not found")
	})
	@PostMapping("/register/verify-otp")
	public ResponseEntity<APIResponse<Void>> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpDto verifyEmailOtpDto)
	{
		userService.verifyEmailOtp(verifyEmailOtpDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Email verified successfully", null));
	}
	
	@Operation(summary = "Resend registration OTP",description = "Generates and sends a new OTP to the user's email address if registration is not yet completed.")
	@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "OTP resent successfully"),
		    @ApiResponse(responseCode = "429", description = "Too many requests"),
		    @ApiResponse(responseCode = "404", description = "User not found")
	})
	@PostMapping("/register/resend-otp")
	public ResponseEntity<APIResponse<Void>>
	resendRegistrationOtp(@Valid @RequestBody ResendOtpRequestDto requestDto)
	{
	    userService.resendRegistrationOtp(requestDto);
	    return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"OTP resent successfully",null));
	}
	
	@Operation(summary = "Complete user registration",description = "Creates a new user account after successful email verification.")
	@ApiResponses({
		    @ApiResponse(responseCode = "201", description = "User registered successfully"),
		    @ApiResponse(responseCode = "403", description = "Email not verified"),
		    @ApiResponse(responseCode = "409", description = "User already registered")
	})
	@PostMapping("/register")       
	public ResponseEntity<APIResponse<Void>> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto)
	{
		userService.registerUser(userRegisterDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(HttpStatus.CREATED.value(), "User registered successfully", null));
	}
	
	@Operation(summary = "User login", description = "Authenticate user by email or mobile number along with password")
	@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Login successful"),
		    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
		    @ApiResponse(responseCode = "403", description = "Account is inactive")
	})
	@PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request)
	{	
        LoginResponseDto result = userService.login(loginRequestDto, request);
        return ResponseEntity.ok(APIResponse.success(201, "Login successful", result));
    }
	
	@Operation(summary = "Send forgot password OTP",description = "Sends a one-time password (OTP) to the registered email address for password reset verification.")
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
		    @ApiResponse(responseCode = "404", description = "User not found"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	@PostMapping("/forgot-password/send-otp")
	public ResponseEntity<APIResponse<Void>> forgotPasswordSendOtp(@Valid @RequestBody ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto)
	{
	    userService.forgotPasswordSendOTP(forgotPasswordOtpRequestDto);
	    return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"OTP sent successfully",null));                   
	}
	
	@Operation(summary = "Resend forgot password OTP",description = "Resends a new OTP to the registered email address if the previous OTP has expired or was not received.")
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "OTP resent successfully"),
		    @ApiResponse(responseCode = "404", description = "User not found"),
		    @ApiResponse(responseCode = "429", description = "OTP resend limit exceeded"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	@PostMapping("/forgot-password/resend-otp")
	public ResponseEntity<APIResponse<Void>> resendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto)       
	{
	    userService.resendForgotPasswordSendOTP(forgotPasswordOtpRequestDto);
	    return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"OTP resent successfully",null));                   
	}
	
	@Operation(summary = "Reset password using OTP",description = "Validates the OTP sent to the user's email address and updates the account password.")
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
		    @ApiResponse(responseCode = "400", description = "Password validation failed"),
		    @ApiResponse(responseCode = "401", description = "Invalid OTP"),
		    @ApiResponse(responseCode = "404", description = "User not found"),
		    @ApiResponse(responseCode = "410", description = "OTP expired"),
		    @ApiResponse(responseCode = "409", description = "Password reuse is not allowed")
	})
	@PostMapping("/forgot-password/reset")
	public ResponseEntity<APIResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto)       
	{
	    userService.forgotPassword(forgotPasswordDto);
	    return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"Password reset successfully",null));                   
	}
	
}