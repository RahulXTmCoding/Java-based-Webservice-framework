package com.thinking.machines.service;
import com.thinking.machines.interfaces.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Security implements SecuredInterface
{

public boolean isVerifiedUser(HttpSession session,HttpServletRequest request,ServletContext application)
{


try
{
if(session.getAttribute("userName")==null)
{
System.out.println("suspicious user");
return false;
}
System.out.println("Verified user");
return true;

}catch(Exception e)
{
e.printStackTrace();
}

return true;
}

public String response(HttpSession session,HttpServletRequest request,ServletContext application)
{

return "only Verified user can access this service";

}


}