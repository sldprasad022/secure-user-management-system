package com.secureusermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secureusermanagement.dto.APIResponse;
import com.secureusermanagement.dto.ChangePasswordRequestDto;
import com.secureusermanagement.dto.UserResponseDto;
import com.secureusermanagement.dto.UserUpdateRequestDto;
import com.secureusermanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User Module",description = "APIs for authenticated users to manage their profile and account settings")
@RestController
@RequestMapping("/api/v1/user")
public class UserController 
{
	@Autowired
	private UserService userService;
	
	@Operation(summary = "Retrieve authenticated user profile",description = "Fetches the profile information of the currently logged-in user.")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/profile")
    public ResponseEntity<APIResponse<UserResponseDto>> getMyProfile()
    {
		UserResponseDto result = userService.getMyProfile();
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"Profile fetched successfully",result));
    }
	
	@Operation(summary = "Update authenticated user profile",description = "Allows the authenticated user to update profile information such as username and mobile number.")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PutMapping("/profile")
    public ResponseEntity<APIResponse<Void>> updateMyProfile(@Valid @RequestBody UserUpdateRequestDto dto)
    {
        userService.updateMyProfile(dto);
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"Profile updated successfully",null));
    }
	
	@Operation(summary = "Change account password",description = "Allows the authenticated user to change their password after validating the current password.")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PostMapping("/change-password")
    public ResponseEntity<APIResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto)
            
    {
        userService.changePassword(changePasswordRequestDto);
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(),"Password changed successfully", null));   
    }

}
