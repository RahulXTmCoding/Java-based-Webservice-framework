
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
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.thinking.machines.events.*;
public class DocTool 
{
	public static Document document ;
  public static PdfWriter writer;
  public static Document document1 ;
  public static PdfWriter writer1;
	public static String path;
  public static int x=0;
  public static int y=0;
private static void addMetaData(Document document) {
        document.addTitle("Services Data");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Rahul singh");
        document.addCreator("Rahul singh");

    }


private static void addTitlePage(Document document) throws Exception
            {
       Image img = Image.getInstance("../../../../../../images/tmlogo.png");
       
        img.scaleToFit(500f, 300f);
        document.add(img);
        Paragraph p=new Paragraph("This pdf contain information related to services of the server using our Framework");
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.newPage();
    }

public static void setPath(String p)
{
if(p.endsWith("/"))
{
  path=p;
}
else
{
  path=p+"/";
}
}
public static void main(String[] args) 
{

setPath(args[0]);
            
	try{

document = new Document();
       File f=new File(path+"service.pdf");
       if(f.exists()==false)
       {
       // f.mkdir();
       }
         writer = PdfWriter.getInstance(document,
                new FileOutputStream(f));
         Rectangle rect = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("art", rect);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();

        writer.setPageEvent(event);
        document.open();
         document.add(new Chunk("")); 
       
addMetaData(document);
addTitlePage(document);



document1 = new Document();

         f=new File(path+"error.pdf");
       if(f.exists()==false)
       {
        //f.mkdir();
       }
         writer1 = PdfWriter.getInstance(document1,
                new FileOutputStream(f));
          rect = new Rectangle(30, 30, 550, 800);
        writer1.setBoxSize("art", rect);
         event = new HeaderFooterPageEvent();

        writer1.setPageEvent(event);
        document1.open();
         document1.add(new Chunk("")); 
       
addMetaData(document1);
addTitlePage(document1);



	Map<String,ServiceBox> services=new HashMap<>();
	
	
	 f=new File("../../../../urlPaths.path");
    FileInputStream fis = new FileInputStream(f);
    byte[] data = new byte[(int) f.length()];
    fis.read(data);
    fis.close();

String str = new String(data, "UTF-8");	
Gson g=new Gson();
UrlPaths urlPaths=g.fromJson(str,UrlPaths.class);
java.util.List<String> listOfUrls=urlPaths.getList();
DocTool doc=new DocTool();
for(String s: listOfUrls)
{
File dir=new File(s);
doc.findClasses(dir,"",services);
}


}catch(Exception e)
{
	e.printStackTrace();
}
document.close();
document1.close();
}

public static void FindService(Class c,String parentPath,Map<String,ServiceBox> services) throws IOException,ClassNotFoundException,Exception
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
  System.out.println(url);
  if(services.get(url)!=null)
  {
    ServiceBox ss=services.get(url);
    y++;
   document1.add(new Paragraph(y+")"));
   document1.add(new Paragraph("service path:-"+url+"  ,Is already in use with following service"));


   document1.add(new Paragraph("Service Class:-"+ss.serviceClass.getName()));
String methodata ="Service Method:-"+ss.returnType.getName()+" "+ss.serviceMethod.getName()+"(";
for(Parameter param:ss.serviceMethod.getParameters())
{
methodata+=param.getParameterizedType()+" "+",";

}
if(ss.serviceMethod.getParameters().length!=0)
{
methodata=methodata.substring(0, methodata.length()-1)+")";
}
else
{
 methodata+=")"; 
}
document1.add(new Paragraph(methodata));

document1.add(new Paragraph("so,It cannot be used with following service:-"));
   
document1.add(new Paragraph("Service Class:-"+c.getName()));
 methodata ="Service Method:-"+m.getReturnType().getName()+" "+m.getName()+"(";
for(Parameter param:m.getParameters())
{
methodata+=param.getParameterizedType()+" "+",";

}
if(m.getParameters().length!=0)
{
methodata=methodata.substring(0, methodata.length()-1)+")";
}
else
{
 methodata+=")"; 
}
document1.add(new Paragraph(methodata));   

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

	x++;

	ServiceBox service=new ServiceBox();
	service.serviceMethod=m;
	service.serviceClass=c;
	service.path=url;
	service.listl=m.getParameterAnnotations();  


service.requestDataBag=new LinkedList<>();
service.isRequestDataAnnotationPresentParametersList=new LinkedList<>();

int i=0;
for(i=0;i<service.listl.length;i++)
{

String requestedParameterName="";
boolean flag1=false;
for(java.lang.annotation.Annotation a:service.listl[i])
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

y++;

document1.add(new Paragraph(y+")"));
document1.add(new Paragraph("Secured Annotation Error"));
   document1.add(new Paragraph("service path:-"+url));


document1.add(new Paragraph(cn+" class defination cannot be found "));


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

y++;

document1.add(new Paragraph(y+")"));
document1.add(new Paragraph("Secured Annotation class Level Error"));
   document1.add(new Paragraph("service path:-"+url));


document1.add(new Paragraph(cn+" class defination cannot be found "));


  }
  }
    service.returnType=m.getReturnType();
    service.isResponseTypeAnnotationPresent=m.isAnnotationPresent(ResponseType.class);
    if(service.isResponseTypeAnnotationPresent)
    {
    	service.responseType=m.getAnnotation(ResponseType.class).type();
      if(service.responseType.equals("nothing")==false && service.responseType.equals("json")==false && service.responseType.equals("text/html")==false)
{


y++;

document1.add(new Paragraph(y+")"));
document1.add(new Paragraph("ResponseType Annotation Error"));
document1.add(new Paragraph("service path:-"+url));


document1.add(new Paragraph("Annotation value is incorrect:-"+service.responseType+"\n"+"only one of the following values are allowed :-'json','nothing','text/html'"));



}
    }
    service.isForwardAnnotationPresent=m.isAnnotationPresent(Forward.class);
    if(service.isForwardAnnotationPresent)
    {
service.forwardValue=m.getAnnotation(Forward.class).value();
if(service.forwardValue.contains(".")==true && service.forwardValue.endsWith(".jsp")==false)
{


y++;

document1.add(new Paragraph(y+")"));
document1.add(new Paragraph("Forward Annotation Error"));
document1.add(new Paragraph("service path:-"+url));


document1.add(new Paragraph("Annotation value is incorrect:-"+service.forwardValue+"\n"+"only path to jsp or to another service is allowed"));



}
    }
    service.isFileUploadAnnotationPresent=m.isAnnotationPresent(FileUpload.class);
if(service.isFileUploadAnnotationPresent && service.pTypeList[0].getName().equals("[Ljava.io.File;")==false)
{


y++;

document1.add(new Paragraph(y+")"));
document1.add(new Paragraph("FileUpload Annotation Error"));
document1.add(new Paragraph("service path:-"+url));


document1.add(new Paragraph("First parameter should be of type File[]"));



}

	services.put(url,service);



document.add(new Paragraph(x+") "+service.path));

document.add(new Paragraph("Service Class:-"+service.serviceClass.getName()));
String methodata ="Service Method:-"+service.returnType.getName()+" "+service.serviceMethod.getName()+"(";
for(Parameter param:service.serviceMethod.getParameters())
{
methodata+=param.getParameterizedType()+" "+",";

}
if(service.serviceMethod.getParameters().length!=0)
{
methodata=methodata.substring(0, methodata.length()-1)+")";
}
else
{
 methodata+=")"; 
}
document.add(new Paragraph(methodata));
if(service.requestDataBag.size()>0)
{
document.add(new Paragraph("  "));
document.add(new Paragraph("Request data Annotation on method parameters:-"));
document.add(new Paragraph("  "));
PdfPTable table = new PdfPTable(4); // 3 columns.

            PdfPCell cell1 = new PdfPCell(new Paragraph("Parameter No"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Type"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("RequestData_annotation"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("RequestData_value"));
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
int l=0;
for(;l<service.requestDataBag.size();l++)
{
System.out.println("hhhh");
table.addCell(new PdfPCell(new Paragraph(""+l)));
table.addCell(new PdfPCell(new Paragraph(service.pTypeList[l].getName())));
if(service.isRequestDataAnnotationPresentParametersList.get(l))
{
table.addCell(new PdfPCell(new Paragraph("Present")));
table.addCell(new PdfPCell(new Paragraph(service.requestDataBag.get(l))));

}
else
{
table.addCell(new PdfPCell(new Paragraph("Not Present")));
table.addCell(new PdfPCell(new Paragraph(service.requestDataBag.get(l))));

}

}

document.add(table);
}



document.add(new Paragraph("  "));
document.add(new Paragraph(" Annotations on Service Method:-"));
document.add(new Paragraph("  "));
PdfPTable table1 = new PdfPTable(4); // 3 columns.

            PdfPCell cell11 = new PdfPCell(new Paragraph("S.No"));
            PdfPCell cell21 = new PdfPCell(new Paragraph("Annotation"));
            PdfPCell cell31 = new PdfPCell(new Paragraph("availability"));
            PdfPCell cell41 = new PdfPCell(new Paragraph("value"));
            table1.addCell(cell11);
            table1.addCell(cell21);
            table1.addCell(cell31);
            table1.addCell(cell41);
 


table1.addCell(new PdfPCell(new Paragraph("1")));
table1.addCell(new PdfPCell(new Paragraph("ResponseType")));
if(service.isResponseTypeAnnotationPresent)
{
table1.addCell(new PdfPCell(new Paragraph("Present")));
table1.addCell(new PdfPCell(new Paragraph(service.responseType)));

}
else
{
table1.addCell(new PdfPCell(new Paragraph("Not Present")));
table1.addCell(new PdfPCell(new Paragraph("---")));

}



table1.addCell(new PdfPCell(new Paragraph("2")));
table1.addCell(new PdfPCell(new Paragraph("Secured")));
if(service.isSecuredMethodLevelAnnotationPresent)
{
table1.addCell(new PdfPCell(new Paragraph("Present")));
if(service.securedMethodLevelClass!=null)
table1.addCell(new PdfPCell(new Paragraph(service.securedMethodLevelClass.getName())));
else
table1.addCell(new PdfPCell(new Paragraph("wrong class name ")));

}
else
{
table1.addCell(new PdfPCell(new Paragraph("Not Present")));
table1.addCell(new PdfPCell(new Paragraph("---")));

}

table1.addCell(new PdfPCell(new Paragraph("3")));
table1.addCell(new PdfPCell(new Paragraph("Forward")));
if(service.isForwardAnnotationPresent)
{
table1.addCell(new PdfPCell(new Paragraph("Present")));
table1.addCell(new PdfPCell(new Paragraph(service.forwardValue)));

}
else
{
table1.addCell(new PdfPCell(new Paragraph("Not Present")));
table1.addCell(new PdfPCell(new Paragraph("---")));

}

table1.addCell(new PdfPCell(new Paragraph("4")));
table1.addCell(new PdfPCell(new Paragraph("FileUpload")));
if(service.isFileUploadAnnotationPresent)
{
table1.addCell(new PdfPCell(new Paragraph("Present")));
table1.addCell(new PdfPCell(new Paragraph("---")));

}
else
{
table1.addCell(new PdfPCell(new Paragraph("Not Present")));
table1.addCell(new PdfPCell(new Paragraph("---")));

}
table1.addCell(new PdfPCell(new Paragraph("5")));
table1.addCell(new PdfPCell(new Paragraph("Path")));
table1.addCell(new PdfPCell(new Paragraph("Present")));
table1.addCell(new PdfPCell(new Paragraph(mp)));


document.add(table1);









document.add(new Paragraph("  "));
document.add(new Paragraph(" Annotations on Service Class:-"));
document.add(new Paragraph("  "));
PdfPTable table2 = new PdfPTable(4); // 3 columns.

            PdfPCell cell12 = new PdfPCell(new Paragraph("S.No"));
            PdfPCell cell22 = new PdfPCell(new Paragraph("Annotation"));
            PdfPCell cell32 = new PdfPCell(new Paragraph("availability"));
            PdfPCell cell42 = new PdfPCell(new Paragraph("value"));
            table2.addCell(cell12);
            table2.addCell(cell22);
            table2.addCell(cell32);
            table2.addCell(cell42);
 





table2.addCell(new PdfPCell(new Paragraph("1")));
table2.addCell(new PdfPCell(new Paragraph("Secured")));
if(service.isSecuredClassLevelAnnotationPresent)
{
table2.addCell(new PdfPCell(new Paragraph("Present")));
if(service.securedClassLevelClass!=null)
table1.addCell(new PdfPCell(new Paragraph(service.securedClassLevelClass.getName())));
else
table1.addCell(new PdfPCell(new Paragraph("wrong class name ")));


}
else
{
table2.addCell(new PdfPCell(new Paragraph("Not Present")));
table2.addCell(new PdfPCell(new Paragraph("---")));

}

table2.addCell(new PdfPCell(new Paragraph("2")));
table2.addCell(new PdfPCell(new Paragraph("Path")));
table2.addCell(new PdfPCell(new Paragraph("Present")));
table2.addCell(new PdfPCell(new Paragraph(cp)));


document.add(table2);








	System.out.println("method loop ke last mai");
}
System.out.println("inner classes ke phle");

for(Class cc :c.getDeclaredClasses())
{
  System.out.println("inner");

  System.out.println(parentPath+cp);
FindService(cc,parentPath+cp,services);
}
}
public void findClasses(File dir,String pack,Map<String,ServiceBox> services)throws IOException,ClassNotFoundException,Exception
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