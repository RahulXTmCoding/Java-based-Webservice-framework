package com.thinking.machines.servlets;
import javax.servlet.*;
import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.thinking.machines.model.*;
import com.thinking.machines.dao.*;
import com.thinking.machines.servlets.*;
import com.thinking.machines.one.*;
public class DataBaseInstallation extends HttpServlet
{







public void doPost(HttpServletRequest rq,HttpServletResponse rs)
{PrintWriter pw=null;
ServiceResponse sr=new ServiceResponse();
System.out.println("working>>>>>>");
try
{

if(rq.getParameter("AdminId").length()>20)
{

throw new Exception("Admin id too long");
}



pw=rs.getWriter();
ServletContext application=rq.getServletContext();

DAOConnection daoc=new DAOConnection();


Connection c=daoc.getConnection(rq.getParameter("dbName"),rq.getParameter("server"),rq.getParameter("port"),rq.getParameter("userId"),rq.getParameter("password"));

Statement s=c.createStatement();
Statement s2=c.createStatement();
String path=application.getRealPath("");
String finalPath=path+"WEB-INF"+File.separator+"scripts"+File.separator+"tables.sql";
System.out.println(finalPath);





StringBuilder query=new StringBuilder();

File f=new File(finalPath);
RandomAccessFile raf=new RandomAccessFile(f,"rw");

if(raf.length()>0)
{
            while(raf.getFilePointer()<raf.length()) {
                char num;
                try {
                    
String sss=raf.readLine();
                    System.out.println("Reading from file: " +sss);
                   
                       if(!sss.equals("")&&sss!=null)
{
                                         s.executeUpdate(sss);
}
            
                }
                catch (EOFException ex1) {
                    break; //EOF reached.
                }
                catch (IOException ex2) {
                    System.err.println("An IOException was caught: " + ex2.getMessage());
                    ex2.printStackTrace();
                }
            }

}






File file=new File(path+"WEB-INF"+File.separator+"scripts"+File.separator+"dataBase.data");
RandomAccessFile ran=new RandomAccessFile(file,"rw");
ran.writeBytes("DataBaseName:"+rq.getParameter("dbName")+"\n");
ran.writeBytes("server:"+rq.getParameter("server")+"\n");
ran.writeBytes("port:"+rq.getParameter("port")+"\n");
ran.writeBytes("userId:"+rq.getParameter("userId")+"\n");
ran.writeBytes("password:"+rq.getParameter("password")+"\n");
ran.close();
raf.close();
System.out.println("rahullllllll");
AES aes=new AES();
aes.setKey("Rahulsingh@8");
String uuid = UUID.randomUUID().toString().replace("-", "");
String encPass=aes.encrypt(rq.getParameter("AdminPassword"),uuid);



String qq="insert into administrator(username,password,password_key) values('"+rq.getParameter("AdminId")+"','"+encPass+"','"+uuid +"');";
System.out.println(qq);
s2.executeUpdate(qq);
System.out.println("loooooooooool");
sr.setResponse("Success");
sr.setSuccess(true);

((Model)application.getAttribute("model")).setDAOConnection(daoc);

}catch(Exception e)
{
e.printStackTrace();
sr.setResponse(e.toString());
sr.setSuccess(false);
}

Gson g=new Gson();
String response=g.toJson(sr);

pw.println(response);





}




}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         