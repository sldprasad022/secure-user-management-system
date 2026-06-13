package com.secureusermanagement.serviceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.secureusermanagement.entity.User;
import com.secureusermanagement.enums.Role;
import com.secureusermanagement.exception.EmailAlreadyExistsException;
import com.secureusermanagement.exception.EmailNotVerifiedException;
import com.secureusermanagement.exception.InvalidCredentialsException;
import com.secureusermanagement.exception.InvalidPasswordException;
import com.secureusermanagement.exception.MobileNumberAlreadyExistsException;
import com.secureusermanagement.exception.OTPMismatchException;
import com.secureusermanagement.exception.OtpExpiredException;
import com.secureusermanagement.exception.OtpNotRequestedException;
import com.secureusermanagement.exception.OtpResendTooEarlyException;
import com.secureusermanagement.exception.PasswordMismatchException;
import com.secureusermanagement.exception.PasswordReuseException;
import com.secureusermanagement.exception.UserAccountDeactivatedException;
import com.secureusermanagement.exception.UserAlreadyRegisteredException;
import com.secureusermanagement.exception.UserNotFoundException;
import com.secureusermanagement.repository.UserRepository;
import com.secureusermanagement.service.EmailService;
import com.secureusermanagement.service.UserService;
import com.secureusermanagement.utils.JwtUtils;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	private static final int OTP_VALIDITY_MINUTES = 2;
	
	private static final int OTP_RESEND_INTERVAL_SECONDS = 60;


	public static String generateOTP() 
	{
		// Random random = new Random();
		SecureRandom random = new SecureRandom();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
	
	@Override
	public void initiateEmailRegistration(EmailRegisterRequestDto emailRegisterRequestDto)
	{

	    String email = emailRegisterRequestDto.getEmail();
	    
	    String generatedOtp = generateOTP();

	    Optional<User> existingUserOpt = userRepository.findByEmail(email);
	    User user;
	    if (existingUserOpt.isPresent()) 
	    {
	        user = existingUserOpt.get();
	        if (user.isEmailVerified())
	        {
	            throw new EmailAlreadyExistsException("Email already exists and verified");
	        }
	        // Rate limiting (60 seconds)
	        if (user.getLastOtpSentAt() != null &&user.getLastOtpSentAt().plusSeconds(OTP_RESEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now()))
	        {
	        	throw new OtpResendTooEarlyException("Please wait "+OTP_RESEND_INTERVAL_SECONDS+" seconds before requesting another OTP");
	        }

	        user.setOtp(passwordEncoder.encode(generatedOtp));
	        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	        user.setLastOtpSentAt(LocalDateTime.now());
	        user.setUpdatedAt(LocalDateTime.now());
	    }
	    else 
	    {
	    	user = new User();
	        user.setEmail(email);
	        user.setOtp(passwordEncoder.encode(generatedOtp));
	        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	        user.setLastOtpSentAt(LocalDateTime.now());
	        user.setCreatedAt(LocalDateTime.now());
	        user.setEmailVerified(false);
	        user.setProfileCompleted(false);
	        user.setActive(false);
	    }
	    userRepository.save(user);
	    emailService.sendRegistrationOtp(email,generatedOtp,OTP_VALIDITY_MINUTES);
	    
	}
	
	@Override
	public void verifyEmailOtp(VerifyEmailOtpDto verifyEmailOtpDto) 
	{
		User user = userRepository.findByEmail(verifyEmailOtpDto.getEmail()).orElseThrow(() -> new UserNotFoundException("Email not found"));	
		if (user.isEmailVerified())
        {
            throw new EmailAlreadyExistsException("Email already exists and verified");
        }	        	
		if (user.getOtp()==null || user.getOtpExpiry()==null)
		{
			throw new OtpNotRequestedException("No OTP request found");
		}
		if (user.getOtpExpiry().isBefore(LocalDateTime.now()))
		{
			throw new OtpExpiredException("OTP has expired");
		}
		if (!passwordEncoder.matches(verifyEmailOtpDto.getOtp(), user.getOtp())) 
		{
			throw new OTPMismatchException(" Invalid OTP. Please check and try again.");
		}
		
		user.setEmailVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);
		userRepository.save(user);

	}
	
	@Override
	public void resendRegistrationOtp(ResendOtpRequestDto requestDto)
	{
	    User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(() ->new UserNotFoundException("Email not found"));

	    if(user.isEmailVerified())
	    {
	        throw new EmailAlreadyExistsException("Email already verified");
	    }
	    // Allow resend only after 60 seconds
	    if(user.getLastOtpSentAt() != null &&user.getLastOtpSentAt().plusSeconds(OTP_RESEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now()))
	    {
	        throw new OtpResendTooEarlyException("Please wait "+OTP_RESEND_INTERVAL_SECONDS+" seconds before requesting another OTP");
	    }

	    String generatedOtp = generateOTP();
	    user.setOtp(passwordEncoder.encode(generatedOtp));
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	    user.setLastOtpSentAt(LocalDateTime.now());
	    userRepository.save(user);

	    emailService.sendRegistrationOtp(user.getEmail(),generatedOtp,OTP_VALIDITY_MINUTES);
	}
	
	
	@Override
	public void registerUser(UserRegisterDto userRegisterDto) 
	{
	    User user = userRepository.findByEmail(userRegisterDto.getEmail()).orElseThrow(() -> new UserNotFoundException("Email not found"));     
	    if (!user.isEmailVerified())
	    {
	        throw new EmailNotVerifiedException("Email is not verified.Please verify email first");       
	    } 
	    if (userRepository.existsByMobileNumber(userRegisterDto.getMobileNumber()))
	    {
	        throw new MobileNumberAlreadyExistsException("Mobile number already exists. Please try again with another mobile number....");
	    }
	    if (user.isProfileCompleted()) 
	    {
	    	throw new UserAlreadyRegisteredException("User already registered");   
		}
	    if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword()))
	    {
			throw new PasswordMismatchException("Password and Confirm Password do not match");
		}
	    	
	    user.setUserName(userRegisterDto.getUserName());
	    user.setMobileNumber(userRegisterDto.getMobileNumber());
	    user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
	    user.setRole(Role.ROLE_USER);
	    user.setProfileCompleted(true);
	    user.setActive(true);
	    user.setUpdatedAt(LocalDateTime.now());
	    
	    user.setOtp(null);
	    user.setOtpExpiry(null);
	    user.setLastOtpSentAt(null);

	    userRepository.save(user);
	}
	
	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto)
	{
		User user = userRepository.findByEmailOrMobileNumber(loginRequestDto.getEmailOrMobileNumber(),
															loginRequestDto.getEmailOrMobileNumber())
				                                            .orElseThrow(() -> new InvalidCredentialsException("Invalid email/mobile number or password"));
		
		if (!user.isActive()) 
	    {
	        throw new UserAccountDeactivatedException("Your account has been deactivated. Please contact admin.");      
	    }
		if (!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())) {

		    throw new InvalidCredentialsException("Invalid email/mobile number or password");
		}

		String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
		UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);
		
		return new LoginResponseDto(token, userResponseDto);
	}
	
	@Override
	public void forgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto)
	{
	    User user = userRepository.findByEmail(forgotPasswordOtpRequestDto.getEmail()).orElseThrow(() ->new UserNotFoundException("Email not found"));

	    String otp = generateOTP();
	    user.setOtp(passwordEncoder.encode(otp));
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	    user.setLastOtpSentAt(LocalDateTime.now());

	    userRepository.save(user);

	    emailService.sendForgotPasswordOtp(user.getEmail(),otp,OTP_VALIDITY_MINUTES);
	}
	
	@Override
	public void resendForgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto)
	{
	    User user = userRepository.findByEmail(forgotPasswordOtpRequestDto.getEmail()).orElseThrow(() ->new UserNotFoundException("Email not found"));
	    
	    // Allow resend only after 60 seconds
	    if(user.getLastOtpSentAt() != null &&user.getLastOtpSentAt().plusSeconds(OTP_RESEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now()))
	    {
	        throw new OtpResendTooEarlyException("Please wait "+OTP_RESEND_INTERVAL_SECONDS+" seconds before requesting another OTP");
	    }

	    String otp = generateOTP();
	    user.setOtp(passwordEncoder.encode(otp));
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	    user.setLastOtpSentAt(LocalDateTime.now());
	    userRepository.save(user);

	    emailService.sendForgotPasswordOtp(user.getEmail(),otp,OTP_VALIDITY_MINUTES);
	}
	
	@Override
	public void forgotPassword(ForgotPasswordDto dto)
	{
	    User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->new UserNotFoundException("Email not found"));
	            
	    if (user.getOtp() == null ||user.getOtpExpiry() == null)
	    {
	        throw new OtpNotRequestedException("No OTP request found");
	    }
	    if (user.getOtpExpiry().isBefore(LocalDateTime.now()))
	    {
	        throw new OtpExpiredException("OTP has expired");
	    }
	    if (!passwordEncoder.matches(dto.getOtp(),user.getOtp()))
	    {
	        throw new OTPMismatchException("Invalid OTP");
	    }
	    if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
	    {
	        throw new PasswordMismatchException("Password and Confirm Password do not match");
	    }
	    if (passwordEncoder.matches(dto.getNewPassword(),user.getPassword()))
	    {
	        throw new PasswordReuseException("New password must be different from current password");
	    }

	    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
	    user.setOtp(null);
	    user.setOtpExpiry(null);
	    user.setLastOtpSentAt(null);

	    userRepository.save(user);
	}
	
	@Override
	public UserResponseDto getMyProfile()
	{
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();
	    User user = userRepository.findByEmail(email).orElseThrow(() ->new UserNotFoundException("User not found"));
	    return UserResponseDto.fromEntity(user);
	}

	@Override
	public void updateMyProfile(UserUpdateRequestDto userUpdateRequestDto)
	{
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();
	            
	    User user = userRepository.findByEmail(email).orElseThrow(() ->new UserNotFoundException("User not found"));        
	    user.setUserName(userUpdateRequestDto.getUserName());
	    user.setMobileNumber(userUpdateRequestDto.getMobileNumber());
	    user.setUpdatedAt(LocalDateTime.now());

	    userRepository.save(user);
	}
	
	@Override
	public void changePassword(ChangePasswordRequestDto changePasswordRequestDto)
	{
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow(() ->new UserNotFoundException("User not found"));

	    if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(),user.getPassword()))
	    {
	        throw new InvalidPasswordException("Current password is incorrect");
	    }
	    if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(),user.getPassword()))
	    {
	        throw new PasswordReuseException("New password must be different from current password");
	    }
	    if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword()))
	    {
	        throw new PasswordMismatchException("Password and confirm password do not match");
	    }
	    user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

	    userRepository.save(user);
	}
	
	@Override
	public Page<UserResponseDto> getAll(int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size,Sort.by("userId").ascending());
		Page<User> paginatedUsers = userRepository.findAll(pageable);
		return UserResponseDto.fromEntityPage(paginatedUsers);
	}

	
	@Override
	public void deleteUser(Long userId)
	{
		User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
		userRepository.delete(user);
	}
	
	@Override
	public UserSummaryCountDto getUserStatusCount() 
	{
		UserSummaryCountDto userSummaryCountDto = new UserSummaryCountDto();
		userSummaryCountDto.setTotalUsersCount(userRepository.count());
		userSummaryCountDto.setTotalActiveUsersCount(userRepository.countByIsActiveTrue());
		userSummaryCountDto.setTotalInactiveUsersCount(userRepository.countByIsActiveFalse());
		return userSummaryCountDto;
	}
	
	@Override
	public String toggleUserStatus(Long userId) 
	{
		User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
		user.setActive(!user.isActive());
		userRepository.save(user);
		
		if (user.isActive()) 
		{
			return "User activated successfully";
		}
		else
		{
			return "User deactivated successfully";
		}
	}

	@Override
	public Page<UserResponseDto> searchUsers(String keyword, int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
		Page<User> result = userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
		return UserResponseDto.fromEntityPage(result);
	}

	@Override
	public Page<UserResponseDto> getActiveUsers(int page, int size)
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
		Page<User> result = userRepository.findByIsActive(true, pageable);
		return UserResponseDto.fromEntityPage(result);
	}

	@Override
	public Page<UserResponseDto> getInActiveUsers(int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
		Page<User> result = userRepository.findByIsActive(false, pageable);
		return UserResponseDto.fromEntityPage(result);
	}
}
