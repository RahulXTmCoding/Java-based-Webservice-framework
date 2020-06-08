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
public class LoginUser extends HttpServlet
{
public void doPost(HttpServletRequest rq,HttpServletResponse rs)
{
HttpSession session=rq.getSession(true);
PrintWriter pw=null;
ServiceResponse sr=new ServiceResponse();
System.out.println("working>>>>>>");
try
{
pw=rs.getWriter();
ServletContext application=rq.getServletContext();



Connection c=((Model)application.getAttribute("model")).getDAOConnection().getConnection();
System.out.println("c null hai kya:"+c.toString());
Statement s=c.createStatement();
ResultSet r=s.executeQuery("select * from administrator where username='"+rq.getParameter("userId")+"';");
System.out.println("rahullllllll"+rq.getParameter("userId")+"   ---"+rq.getParameter("password"));

if(r.next())
{
AES aes=new AES();
aes.setKey("Rahulsingh@8");
String dePass=aes.decrypt(r.getString(2),r.getString(3));


System.out.println(r.getString(1)+"----"+r.getString(2));
if(dePass.compareTo(rq.getParameter("password"))==0)
{
session.setAttribute("userName",rq.getParameter("userId"));
System.out.println("session ka kaam hau");
session.setAttribute("password",rq.getParameter("password"));
sr.setResponse("Success");
sr.setSuccess(true);

}
else
{
sr.setResponse("FALSE");
sr.setSuccess(false);
}
}
else
{
sr.setResponse("failed");
sr.setSuccess(false);

}


}catch(Exception e)
{
e.printStackTrace();
sr.setResponse("failed");
sr.setSuccess(false);
}

Gson g=new Gson();
String response=g.toJson(sr);
String sessionId = session.getId();
    Cookie userCookie = new Cookie("JSESSIONID", sessionId);

    rs.addCookie(userCookie);
 userCookie = new Cookie("userId", rq.getParameter("userId"));

    rs.addCookie(userCookie);
pw.println(response);





}




}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         