package com.mobius.software.android.iotbroker.main;

public class MalformedMessageException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public MalformedMessageException(String message)
    {
        super(message);
    }
}
