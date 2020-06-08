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
import com.thinking.machines.interfaces.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;
public class Service extends HttpServlet
{

public void doGet(HttpServletRequest rq,HttpServletResponse rs)
{
PrintWriter pw=null;
String response="";

ServletContext sc=rq.getServletContext();
Map<String,ServiceBox> serviceMap=(Map<String,ServiceBox>)sc.getAttribute("serviceMap");
try{
pw=rs.getWriter();
String url=rq.getRequestURI();

String[] urlParts=url.split("service");



response=processRequest(urlParts[1],serviceMap,rq,rs);




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
public String processRequest(String requestPath,Map<String,ServiceBox> serviceMap,HttpServletRequest rq,HttpServletResponse rs)throws Exception
{
ServiceBox sb=null;

sb=serviceMap.get(requestPath);
String response="";
Gson g=new Gson();

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
Annotation listl[][]=sb.listl;  
Class[] pTypeList=sb.pTypeList;


if(sb.isSecuredMethodLevelAnnotationPresent)
{
	Class sc=sb.securedMethodLevelClass;
    SecuredInterface si=(SecuredInterface)sc.newInstance();
    if(si.isVerifiedUser(rq.getSession(),rq,rq.getServletContext())==false)
    {

    response=si.response(rq.getSession(),rq,rq.getServletContext());
    return response;	
    }
}

if(sb.isSecuredClassLevelAnnotationPresent)
{
    Class sc=sb.securedClassLevelClass;
    SecuredInterface si=(SecuredInterface)sc.newInstance();
    if(si.isVerifiedUser(rq.getSession(),rq,rq.getServletContext())==false)
    {

    response=si.response(rq.getSession(),rq,rq.getServletContext());
    return response;	
    }
}



Object []args=new Object[pTypeList.length];
for(i=0;i<listl.length;i++)
{
Class parameterType=pTypeList[i];
String data="0";
if(sb.isRequestDataAnnotationPresentParametersList.get(i))
{
	data=rq.getParameter(sb.requestDataBag.get(i));
}

String name=parameterType.getName();


if(name.equals("int")||name.equals("Integer"))
{
	args[i]=(Object)Integer.parseInt(data);
}
else if(name.equals("long")||name.equals("Long"))
{
	args[i]=(Object)Long.parseLong(data);
}
else if(name.equals("float")||name.equals("Float"))
{
	args[i]=(Object)Float.parseFloat(data);
}
else if(name.equals("double")||name.equals("Double"))
{
	args[i]=(Object)Double.parseDouble(data);
}
else if(name.equals("char")||name.equals("Character"))
{
	char[] charArray = data.toCharArray();
	args[i]=new Character(charArray[0]);
}
else if(name.equals("boolean")||name.equals("Boolean"))
{
	boolean b; 
	
if(data.equals("true")||data.equals("True")||data.equals("TRUE"))
{
b=true;	
}
else
{
b=false;
}
args[i]=b;
}
else if(name.equals("short")||name.equals("Short"))
{
	args[i]=(Object)Short.parseShort(data);
}
else if(name.equals("byte")||name.equals("Byte"))
{
	args[i]=(Object)Byte.parseByte(data);
}
else if(name.equals("java.lang.String"))
{
	args[i]=(Object)data;
}
else
if(name.equals("javax.servlet.http.HttpServletRequest"))
{
	args[i]=rq;
}
else
if(name.equals("javax.servlet.http.HttpServletResponse"))
{
args[i]=rs;
}
else
if(name.equals("javax.servlet.ServletContext"))
{
	args[i]=rq.getServletContext();
}
else
if(name.equals("javax.servlet.http.HttpSession"))
{
	args[i]=rq.getSession(true);
}
else {
	
	args[i]=(Object)g.fromJson(data,parameterType);
}


}
Class returnType=sb.returnType;

String type="text/html";

 if(sb.isResponseTypeAnnotationPresent)
 {

type=sb.responseType;



if(type.equals("nothing"))
{
	Object o=returnType.cast(sb.serviceMethod.invoke(sb.instance,args));

	response="";
}
else
if(type.equals("text/html"))
{

  if(returnType.getName().equals("void"))
  {
 sb.serviceMethod.invoke(sb.instance,args);
 response="";   
  }
else
{
 response=(String)sb.serviceMethod.invoke(sb.instance,args);

}}
else
if(type.equals("json"))
{

  if(returnType.getName().equals("void"))
  {
 sb.serviceMethod.invoke(sb.instance,args);
 response="";   
  }
else
{
Object o=returnType.cast(sb.serviceMethod.invoke(sb.instance,args));
response=g.toJson(o);
}}
}
else
{
if(returnType.getName().equals("void"))
{
sb.serviceMethod.invoke(sb.instance,args);
response="";
}
else
{
  
response=(String)sb.serviceMethod.invoke(sb.instance,args);
}}

if(sb.isForwardAnnotationPresent)
{
	String subPath=sb.forwardValue;
	serviceMap.get(subPath);
	if(serviceMap.get(subPath)!=null)
	{
	response=processRequest(subPath,serviceMap,rq,rs);	
	}
	else
	{
    String list11[]=requestPath.split("/");
    list11[list11.length-1]=subPath;
    String newPath="";
    int n=list11.length;
    for(i=1;i<n;i++)
    {
    	if(i!=n-1)
    	{
    	newPath+="/"+list11[i];
    }
    else
    {
    	newPath+=list11[i];
    }
    }
   
    if(serviceMap.get(newPath)!=null)
	{

    response=processRequest(newPath,serviceMap,rq,rs);
	

	}
	else
	if(subPath.endsWith(".jsp"))
	{
		 RequestDispatcher dispatcher = rq.getServletContext().getRequestDispatcher(subPath);
    dispatcher.forward(rq, rs);
    return "";
	}	
	}

	
}
}

return response;
}

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

String[] urlParts=url.split("service");


response=processPost(urlParts[1],serviceMap,rq,rs);



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
    return (clazz.equals(Boolean.class) || 
        clazz.equals(Integer.class) ||
        clazz.equals(Character.class) ||
        clazz.equals(Byte.class) ||
        clazz.equals(Short.class) ||
        clazz.equals(Double.class) ||
        clazz.equals(Long.class) ||
        clazz.equals(Float.class)||
        clazz.equals(java.lang.String.class))||
    clazz.equals(javax.servlet.http.HttpServletRequest.class)||
clazz.equals(javax.servlet.http.HttpServletResponse.class)||
clazz.equals(javax.servlet.ServletContext.class)||
clazz.equals(javax.servlet.http.HttpSession.class);
}
public String processPost(String requestPath,Map<String,ServiceBox> serviceMap,HttpServletRequest rq,HttpServletResponse rs)throws Exception
{
ServiceBox sb=null;
sb=serviceMap.get(requestPath);
String response="";

if(sb.isSecuredMethodLevelAnnotationPresent)
{
	Class sc=sb.securedMethodLevelClass;
    SecuredInterface si=(SecuredInterface)sc.newInstance();
    if(si.isVerifiedUser(rq.getSession(),rq,rq.getServletContext())==false)
    {

    response=si.response(rq.getSession(),rq,rq.getServletContext());
    return response;	
    }
}

if(sb.isSecuredClassLevelAnnotationPresent)
{
    Class sc=sb.securedClassLevelClass;
    SecuredInterface si=(SecuredInterface)sc.newInstance();
    if(si.isVerifiedUser(rq.getSession(),rq,rq.getServletContext())==false)
    {

    response=si.response(rq.getSession(),rq,rq.getServletContext());
    return response;	
    }
}


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
Gson g=new Gson();
int i=0;
Annotation listl[][]=sb.listl;
Class[] pTypeList=sb.pTypeList;
Object []args=new Object[pTypeList.length];
String data="";
for(i=0;i<listl.length;i++)
{
Class parameterType=pTypeList[i];


if(i==0)
{
boolean flag2=false;


if(isUserDefined(pTypeList[0])==false)
{

flag2=false;
if(sb.isRequestDataAnnotationPresentParametersList.get(0)==true)
{
flag2=true;
break;
}
if(flag2==false)
{
if(sb.isFileUploadAnnotationPresent==false)
{
BufferedReader br=rq.getReader();
StringBuilder stb=new StringBuilder();
String line;
while((line=br.readLine())!=null)
{
stb.append(line);
}
br.reset();
String json=stb.toString();


args[0]=g.fromJson(json,pTypeList[0]);
continue;
}
else
{


boolean isMultipart = ServletFileUpload.isMultipartContent(rq);
     
      if( !isMultipart ) {
         return "Unable to Upload";
      }
  
      DiskFileItemFactory factory = new DiskFileItemFactory();
      String path=rq.getServletContext().getRealPath("");
       path=path+"WEB-INF/files";
      File ff=new File(path);
      if(ff.exists()==false)
      {
      	ff.mkdir();
      }
      factory.setRepository(new File(path));
      ServletFileUpload upload = new ServletFileUpload(factory);
   
     List<File> filesList=new ArrayList<>();
      try { 
         List<FileItem> fileItems = upload.parseRequest(rq);
   
         for (FileItem fi:fileItems) {
            if ( !fi.isFormField () ) {
               
               String fileName = fi.getName();
               fileName=new File(fileName).getName();
               File newFile=new File(path +"/"+ fileName);
               if(newFile.exists())
               {
                int ik=1;
                while(true)
                {
                  newFile=new File(path +"/"+ fileName+"("+ik+")");
                  if(newFile.exists()==false) break;
                  ik++;

                }
               }
               fi.write(newFile) ;
               filesList.add(newFile);
              
            }
            else
            {
                           rq.setAttribute(fi.getFieldName(),fi.getString());
            }
         }
                  
         } catch(Exception ex) {
            System.out.println(ex);
         }


args[0]=filesList.toArray(new File[filesList.size()]);

continue;


}
}

}






}



if(sb.isRequestDataAnnotationPresentParametersList.get(i))
{
  
data=rq.getParameter(sb.requestDataBag.get(i));
if(data==null)
{
  data=(String)rq.getAttribute(sb.requestDataBag.get(i));
}

}



String name=parameterType.getName();


if(name.equals("int")||name.equals("Integer"))
{
	args[i]=(Object)Integer.parseInt(data);
}
else if(name.equals("long")||name.equals("Long"))
{
	args[i]=(Object)Long.parseLong(data);
}
else if(name.equals("float")||name.equals("Float"))
{
	args[i]=(Object)Float.parseFloat(data);
}
else if(name.equals("double")||name.equals("Double"))
{
	args[i]=(Object)Double.parseDouble(data);
}
else if(name.equals("char")||name.equals("Character"))
{
	char[] charArray = data.toCharArray();
	args[i]=new Character(charArray[0]);
}
else if(name.equals("boolean")||name.equals("Boolean"))
{
	boolean b; 
	
if(data.equals("true")||data.equals("True")||data.equals("TRUE"))
{
b=true;	
}
else
{
b=false;
}
args[i]=b;
}
else if(name.equals("short")||name.equals("Short"))
{
	args[i]=(Object)Short.parseShort(data);
}
else if(name.equals("byte")||name.equals("Byte"))
{
	args[i]=(Object)Byte.parseByte(data);
}
else if(name.equals("java.lang.String"))
{
	args[i]=(Object)data;
}
else
if(name.equals("javax.servlet.http.HttpServletRequest"))
{
	args[i]=rq;
}
else
if(name.equals("javax.servlet.http.HttpServletResponse"))
{
args[i]=rs;
}
else
if(name.equals("javax.servlet.ServletContext"))
{
	args[i]=rq.getServletContext();
}
else
if(name.equals("javax.servlet.http.HttpSession"))
{
	args[i]=rq.getSession(true);
}
else {
	
	args[i]=(Object)g.fromJson(data,parameterType);
}


}

//for end 
Class returnType=sb.returnType;
String type="text/html";
 if(sb.isResponseTypeAnnotationPresent)
 {
type=sb.responseType;
}
if(type.equals("nothing"))
{
	Object o=returnType.cast(sb.serviceMethod.invoke(sb.instance,args));

	response="";
}
else
if(type.equals("text/html"))
{
  if(returnType.getName().equals("void"))
  {
 sb.serviceMethod.invoke(sb.instance,args);
 response="";   
  }
else
{
	Object o=returnType.cast(sb.serviceMethod.invoke(sb.instance,args));

	response=o.toString();
}}
else
if(type.equals("json"))
{

  if(returnType.getName().equals("void"))
  {
 sb.serviceMethod.invoke(sb.instance,args);
 response="";   
  }
else
{
Object o=returnType.cast(sb.serviceMethod.invoke(sb.instance,args));
response=g.toJson(o);
}}

if(sb.isForwardAnnotationPresent)
{
	String subPath=sb.forwardValue;
	serviceMap.get(subPath);
	if(serviceMap.get(subPath)!=null)
	{
	response=processRequest(subPath,serviceMap,rq,rs);	
	}
	else
	{
    String list11[]=requestPath.split("/");
    list11[list11.length-1]=subPath;
    String newPath="";
    int n=list11.length;
    for(i=1;i<n;i++)
    {
    	if(i!=n-1)
    	{
    	newPath+="/"+list11[i];
    }
    else
    {
    	newPath+=list11[i];
    }
    }
   
    if(serviceMap.get(newPath)!=null)
	{

    response=processRequest(newPath,serviceMap,rq,rs);
	

	}
	else
	if(subPath.endsWith(".jsp"))
	{
		 RequestDispatcher dispatcher = rq.getServletContext().getRequestDispatcher(subPath);
    dispatcher.forward(rq, rs);
    return "";
	}	
	}

	
}
}
return response;
}

}
