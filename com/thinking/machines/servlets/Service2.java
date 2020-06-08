package com.thinking.machines.servlets;
import com.thinking.machines.annotations.*;
import java.lang.annotation.*;
import com.thinking.machines.service.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import com.google.gson.*;

public class Service2 extends HttpServlet
{

public void doPost(HttpServletRequest rq,HttpServletResponse rs)
{
PrintWriter pw=null;
String response="";
ServiceBox sb=null;
ServletContext sc=rq.getServletContext();
Map<String,ServiceBox> serviceMap=(Map<String,ServiceBox>)sc.getAttribute("serviceMap");
try{
pw=rs.getWriter();
String url=rq.getRequestURI();
System.out.println(url);
String[] urlParts=url.split("service2");

sb=serviceMap.get(urlParts[1]);
if(sb==null)
{
response="Unable to find Requested Service";

}
else
{
if(sb.instance==null)
{
String[] classNames=sb.serviceClass.getName().split("\\$");
Object innerMost=null;
Class enclosingClass=Class.forName(classNames[0]);
Object enclosingInstance=Class.forName(classNames[0]).newInstance();
String outerClassString=classNames[0];
for(int i=1;i<classNames.length;i++)
{
outerClassString=outerClassString+"$"+classNames[i];
Class interM=Class.forName(outerClassString);
Constructor ctor = interM.getConstructor(enclosingClass);

Object innerInstance = ctor.newInstance(enclosingInstance);

enclosingInstance=innerInstance;
enclosingClass=interM;
}
sb.instance=enclosingInstance;
}
int i=0;
Annotation listl[][]=sb.serviceMethod.getParameterAnnotations();  
Class[] pTypeList=sb.serviceMethod.getParameterTypes();
System.out.println(pTypeList.length+"     "+listl.length);
Object []args=new Object[pTypeList.length];
Class annotationType=RequestData.class;
String data="";
BufferedReader br=rq.getReader();
StringBuilder stb=new StringBuilder();
String line;
while((line=br.readLine())!=null)
{
stb.append(line);
}
br.reset();
String json=stb.toString();
System.out.println(json);
Gson g=new Gson();
args[0]=g.fromJson(json,pTypeList[0]);
String type="text/html";
 if(sb.serviceMethod.isAnnotationPresent(ResponseType.class))
 {
type=sb.serviceMethod.getAnnotation(ResponseType.class).type();
}
if(type.equals("nohing"))
{
	Object o=pTypeList[0].cast(sb.serviceMethod.invoke(sb.instance,args));

	response="";
}
else
if(type.equals("type/html"))
{
	Object o=pTypeList[0].cast(sb.serviceMethod.invoke(sb.instance,args));

	response=o.toString();
}
else
if(type.equals("json"))
{
Object o=pTypeList[0].cast(sb.serviceMethod.invoke(sb.instance,args));
response=g.toJson(o);
}

}



}catch(ClassNotFoundException c)
{
response="Unable to find Requested Service-Exception occured";	

}
catch(Exception e)
{
e.printStackTrace();
response="Unable to find Requested Service-Exception-Occured";
}
pw.println(response);
}

public static boolean isUserDefined(Class<?> clazz) {
    return !(clazz.equals(Boolean.class) || 
        clazz.equals(Integer.class) ||
        clazz.equals(Character.class) ||
        clazz.equals(Byte.class) ||
        clazz.equals(Short.class) ||
        clazz.equals(Double.class) ||
        clazz.equals(Long.class) ||
        clazz.equals(Float.class)||
        clazz.equals(java.lang.String.class));
}

}
