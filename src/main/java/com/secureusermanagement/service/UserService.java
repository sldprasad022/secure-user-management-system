package com.secureusermanagement.service;

import org.springframework.data.domain.Page;

import com.secureusermanagement.dto.ChangePasswordRequestDto;
import com.secureusermanagement.dto.EmailRegisterRequestDto;
import com.secureusermanagement.dto.ForgotPasswordDto;
import com.secureusermanagement.dto.ForgotPasswordOtpRequestDto;
import com.secureusermanagement.dto.LoginRequestDto;
import com.secureusermanagement.dto.LoginResponseDto;
import com.secureusermanagement.dto.ResendOtpRequestDto;
import com.secureusermanagement.dto.UserRegisterDto;
import com.secureusermanagement.dto.UserResponseDto;
import com.secureusermanagement.dto.UserSummaryCountDto;
import com.secureusermanagement.dto.UserUpdateRequestDto;
import com.secureusermanagement.dto.VerifyEmailOtpDto;


public interface UserService 
{
	void initiateEmailRegistration(EmailRegisterRequestDto emailRegisterRequestDto);
	
	void verifyEmailOtp(VerifyEmailOtpDto verifyEmailOtpDto);
	
	void resendRegistrationOtp(ResendOtpRequestDto requestDto);
	
	void registerUser(UserRegisterDto userRegisterDto);
	
    UserResponseDto getMyProfile();
	
	void updateMyProfile(UserUpdateRequestDto userUpdateRequestDto);
	
	//-----------------------------------------------------
	LoginResponseDto login(LoginRequestDto loginRequestDto);
	
	void forgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto);
	
	void forgotPassword(ForgotPasswordDto forgotPasswordDto);
	
	void resendForgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto);
	
	void changePassword(ChangePasswordRequestDto changePasswordRequestDto);
	//----------------------------------------------------------------------
	
	Page<UserResponseDto> getAll(int page, int size); 
	
	void deleteUser(Long userId);
	
	UserSummaryCountDto getUserStatusCount();
	
    String toggleUserStatus(Long userId);
	
	Page<UserResponseDto> searchUsers(String keyword, int page,int size);
	
	Page<UserResponseDto> getActiveUsers(int page, int size);
	
	Page<UserResponseDto> getInActiveUsers(int page, int size);
	
	
	
	
}
