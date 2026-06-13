package com.secureusermanagement.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto 
{
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Username must contain only letters and spaces")
    private String userName;

	@Pattern(regexp = "^[6-9]\\d{9}$",message = "Mobile number must start with 6-9 and contain exactly 10 digits")
	private String mobileNumber;
}
