package com.thinking.machines.interfaces;
import javax.servlet.http.*;
import javax.servlet.*;
public interface SecuredInterface
{

	public boolean isVerifiedUser(HttpSession session,HttpServletRequest request,ServletContext application);
	public String response(HttpSession session,HttpServletRequest request,ServletContext application);
	
}