package com.secureusermanagement.exception;

public class OTPMismatchException extends RuntimeException
{
	public OTPMismatchException(String message)
	{
		super(message);
	}
}
