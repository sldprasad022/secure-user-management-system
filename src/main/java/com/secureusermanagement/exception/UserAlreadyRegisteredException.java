package com.secureusermanagement.exception;

public class UserAlreadyRegisteredException extends RuntimeException
{
	public UserAlreadyRegisteredException(String message)
	{
		super(message);
	}
}
