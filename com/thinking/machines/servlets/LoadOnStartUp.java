package com.thinking.machines.servlets;
import javax.servlet.*;
import com.thinking.machines.model.*;
import javax.servlet.http.*;
import java.io.*;
import com.thinking.machines.dao.*;
public class LoadOnStartUp extends HttpServlet
{
public void init() throws ServletException  
{
try{



ServletContext context = getServletContext();
Model m=new Model();
String path=context.getRealPath("");
path=path+"WEB-INF"+File.separator+"scripts"+File.separator+"dataBase.data";
System.out.println(path);
File f=new File(path);
boolean b=f.exists();

if(b==true)
{
System.out.println("file hai");
RandomAccessFile ran=new RandomAccessFile(f,"rw");
String dbName=ran.readLine().split(":",0)[1];
String server=ran.readLine().split(":",0)[1];
String port=ran.readLine().split(":",0)[1];
String user=ran.readLine().split(":",0)[1];
String password=ran.readLine().split(":",0)[1];

System.out.println(dbName+" "+user+" "+password);
System.out.println("connection se phle");

DAOConnection c=new DAOConnection();
c.getConnection(dbName,server,port,user,password);
m.setDAOConnection(c);
System.out.println("m set hogaya ");
}
context.setAttribute("model",m);
System.out.println("load end ");
}




catch(Exception e)
{
e.printStackTrace();
}

}
}