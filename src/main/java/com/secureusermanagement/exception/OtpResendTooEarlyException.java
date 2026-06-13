package com.secureusermanagement.exception;

public class OtpResendTooEarlyException extends RuntimeException
{
    public OtpResendTooEarlyException(String message)
    {
        super(message);
    }
}
