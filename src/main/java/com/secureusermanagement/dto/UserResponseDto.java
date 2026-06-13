package com.secureusermanagement.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.secureusermanagement.entity.User;
import com.secureusermanagement.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto 
{
	private Long userId;
	
	private String userName;
	
	private String email;
	
	private String mobileNumber;
	
	private boolean isActive;
	
	private Role role;
	
	public static UserResponseDto fromEntity(User user)
	{
		return new UserResponseDto(user.getUserId(),user.getUserName(),user.getEmail(),user.getMobileNumber(),user.isActive(),user.getRole());
	}
	
	public static Page<UserResponseDto> fromEntityPage(Page<User> userPage)
	{
		List<UserResponseDto> data = userPage.getContent()
											 .stream()
											 .map(UserResponseDto::fromEntity)
											 .toList();
		
		return new PageImpl<>(data, userPage.getPageable(),userPage.getTotalElements());
	}
	
	
}

//.map(UserResponseDto::fromEntity)               Method Reference
//.map(user -> UserResponseDto.fromEntity(user))  Lambda Expression
