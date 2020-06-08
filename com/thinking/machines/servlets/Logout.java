package com.thinking.machines.servlets;
import javax.servlet.*;
import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import com.google.gson.*;
import java.util.*;
import com.thinking.machines.one.*;
import com.thinking.machines.model.*;
import com.thinking.machines.dao.*;
import com.thinking.machines.servlets.*;
public class Logout extends HttpServlet
{
public void doGet(HttpServletRequest rq,HttpServletResponse rs)
{
HttpSession session=rq.getSession(true);
PrintWriter pw=null;
ServiceResponse sr=new ServiceResponse();
System.out.println("working>>>>>>");
try
{
pw=rs.getWriter();
ServletContext application=rq.getServletContext();
pw=rs.getWriter();

application.removeAttribute("userName");
application.removeAttribute("password");

System.out.println("success logout");


sr.setResponse("done");
sr.setSuccess(true);
}catch(Exception e)
{
System.out.println("failed logout");
e.printStackTrace();
sr.setResponse("failed");
sr.setSuccess(false);
}

Gson g=new Gson();
String response=g.toJson(sr);

    
pw.println(response);





}




}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         