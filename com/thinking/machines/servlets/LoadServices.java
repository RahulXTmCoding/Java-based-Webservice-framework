package com.thinking.machines.servlets;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.thinking.machines.service.*;
import com.thinking.machines.annotations.*;
import com.google.gson.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import  java.util.zip.*;
public class LoadServices extends HttpServlet
{
public  void init()
{
	try{
	Map<String,ServiceBox> services=new HashMap<>();
	ServletContext sc=getServletContext();
	String path=sc.getRealPath("");
	
	File f=new File(path+"WEB-INF/classes/urlPaths.path");
    FileInputStream fis = new FileInputStream(f);
    byte[] data = new byte[(int) f.length()];
    fis.read(data);
    fis.close();

String str = new String(data, "UTF-8");	
Gson g=new Gson();
UrlPaths urlPaths=g.fromJson(str,UrlPaths.class);
List<String> listOfUrls=urlPaths.getList();

for(String s: listOfUrls)
{
File dir=new File(s);
findClasses(dir,"",services);
}

sc.setAttribute("serviceMap",services);

}catch(Exception e)
{
	e.printStackTrace();
}
}

public static void FindService(Class c,String parentPath,Map<String,ServiceBox> services) throws IOException,ClassNotFoundException
{


if(c.isAnnotationPresent(com.thinking.machines.annotations.Path.class)==false)
{
return;	
}
com.thinking.machines.annotations.Path p=(com.thinking.machines.annotations.Path)c.getAnnotation(com.thinking.machines.annotations.Path.class);
String cp=p.value();
Method[] methods=c.getMethods();
for (Method m:methods)
{
	if(m.isAnnotationPresent(com.thinking.machines.annotations.Path.class)==false)  
      {
      continue;	
      }
	String mp=m.getAnnotation(com.thinking.machines.annotations.Path.class).value();
	String url=parentPath+cp+mp;
	
	 if(services.get(url)!=null)
	 {
	 	continue;
	 }
	 Set<String> keys = services.keySet();
        boolean alreadyUsed=false;
        for (String k : keys) {
           
            if(k.endsWith(url))
            {
            	alreadyUsed=true;
            	break;
            }
            }
        if(alreadyUsed==true)
        {
        	continue;
        }

        
	ServiceBox service=new ServiceBox();
	service.serviceMethod=m;
	service.serviceClass=c;
	service.path=url;
	service.listl=m.getParameterAnnotations();  

if(service.listl.length>0)
{
service.requestDataBag=new LinkedList<>();
service.isRequestDataAnnotationPresentParametersList=new LinkedList<>();
}
int i=0;
for(i=0;i<service.listl.length;i++)
{

String requestedParameterName="";
boolean flag1=false;
for(Annotation a:service.listl[i])
{
if(a instanceof RequestData)
{
requestedParameterName=((RequestData)a).variableName();
flag1=true;
break;
}
}

if(flag1==true)
{
service.requestDataBag.add(requestedParameterName);
service.isRequestDataAnnotationPresentParametersList.add(true);

}
else
{
service.requestDataBag.add("");
service.isRequestDataAnnotationPresentParametersList.add(false);

}

}





	service.pTypeList=m.getParameterTypes();
	service.isSecuredMethodLevelAnnotationPresent=m.isAnnotationPresent(Secured.class);
	String cn="";
	if(service.isSecuredMethodLevelAnnotationPresent)
	{
   
    cn=m.getAnnotation(Secured.class).value();
    try{
	service.securedMethodLevelClass=Class.forName(cn);
		}catch(Exception e)
  {
  }
	}
	service.isSecuredClassLevelAnnotationPresent=c.isAnnotationPresent(Secured.class);
    if(service.isSecuredClassLevelAnnotationPresent)
    {
    cn=((Secured)c.getAnnotation(Secured.class)).value();
	try{
	service.securedClassLevelClass=Class.forName(cn);
		}catch(Exception e)
  {

  }
	}
    service.returnType=m.getReturnType();
    service.isResponseTypeAnnotationPresent=m.isAnnotationPresent(ResponseType.class);
    if(service.isResponseTypeAnnotationPresent)
    {
    	service.responseType=m.getAnnotation(ResponseType.class).type();
    }
    service.isForwardAnnotationPresent=m.isAnnotationPresent(Forward.class);
    if(service.isForwardAnnotationPresent)
    {
    	service.forwardValue=m.getAnnotation(Forward.class).value();
    }
    service.isFileUploadAnnotationPresent=m.isAnnotationPresent(FileUpload.class);
	services.put(url,service);
	
}


for(Class cc :c.getDeclaredClasses())
{
FindService(cc,parentPath+cp,services);
}
}
public void findClasses(File dir,String pack,Map<String,ServiceBox> services)throws IOException,ClassNotFoundException
{
if(!dir.exists())
{
	return;
	
}

File[] files=dir.listFiles();
for(File f :files)
{ 
	if(f.isDirectory())
	{
	String pak="";
	if(pack.compareTo("")==0)
	{
     pak=f.getName();
	}
	else
	{
		pak=pack+"."+f.getName();
	}
	findClasses(f,pak,services);
}
else if (f.getName().endsWith(".class")) {
	String pak="";
	if(pack.compareTo("")==0)
	{
     pak=f.getName().substring(0, f.getName().length() - 6);
	}
	else
	{
		pak=pack+"."+f.getName().substring(0, f.getName().length() - 6);
	}
	
	Class c=null;
	try{
c=Class.forName(pak);
}catch(NoClassDefFoundError ne)
{
	continue;
}
if(c!=null){
FindService(c,"",services);
}

	
}else if (f.getName().endsWith(".jar")) {
	
ZipInputStream zipIn = new ZipInputStream(new FileInputStream(f));
   ZipEntry entry = zipIn.getNextEntry();
        
  while (entry != null) {
            
  if (entry.isDirectory()) {
  entry = zipIn.getNextEntry();	
  continue;
                
  } else {
                
  if(entry.getName().endsWith(".class"))
   {
    String cName=entry.getName();
   
  Class c=null;
 try{
 	c=ClassLoader.getSystemClassLoader().loadClass(cName.replaceAll("/",".").substring(0,cName.length() - 6));
    }catch(Exception ne)
{
entry = zipIn.getNextEntry();
continue;
}
 if(c!=null){
 FindService(c,"",services);
 }
 }
 }
 zipIn.closeEntry();
 entry = zipIn.getNextEntry();
 }
 zipIn.close();

	
}
}


}
}