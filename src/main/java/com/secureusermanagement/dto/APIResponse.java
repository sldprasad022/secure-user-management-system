package com.secureusermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)//here We are telling to spring don't include null values in JSON Response. 
public class APIResponse<T> 
{
	private boolean success;
	
	private int statusCode;
	
	private String message;
	
	private T data;
	
	private T errors;
	
	public static <T> APIResponse<T> success(int statusCode, String message, T data)
	{
		return new APIResponse<>(true,statusCode,message,data,null);
	}
	
	// Failure Response without errors object
	public static <T> APIResponse<T> failure(int statusCode, String message)
	{
		return new APIResponse<>(false,statusCode,message,null,null);
	}
	
	// Failure Response with errors object
	public static <T> APIResponse<T> failure(int statusCode, String message, T errors)
	{
		return new APIResponse<>(false,statusCode,message,null,errors);
	}
	
	
}

//<T> and T : it will accept any type of data and it will return any type  of data. it will handle any type of data.

