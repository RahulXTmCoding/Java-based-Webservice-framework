package com.thinking.machines.servlets;

public class ServiceResponse
{
private Object response;
private boolean isException;
private boolean isError;
private boolean success;


public ServiceResponse()
{
response=null;
isException=false;
isError=false;
success=false;

}
public void setResponse(Object response)
{
this.response=response;
}
public Object getResponse()
{
return this.response;
}



public void setIsException(boolean isException)
{
this.isException=isException;
}
public boolean getIsException()
{
return this.isException;
}


public void setIsError(boolean isError)
{
this.isError=isError;
}
public boolean getIsError()
{
return this.isError;
}



public void setSuccess(boolean success)
{
this.success=success;
}
public boolean getSuccess()
{
return this.success;
}

}