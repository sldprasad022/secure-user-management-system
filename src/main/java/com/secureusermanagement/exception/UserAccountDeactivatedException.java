package com.secureusermanagement.exception;

public class UserAccountDeactivatedException extends RuntimeException
{
	public UserAccountDeactivatedException(String message)
	{
		super(message);
	}
}
