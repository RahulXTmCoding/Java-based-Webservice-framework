# Java-based-Webservice-framework
Framework makes it easy to create applications and services with absolute minimum fuss.Small over view of the frame work and user can easily use the framework to produce backends of the web applications/projects.

You can use this framework to create backend/serverside services for web requests, Now user not need to know about servlets or edit Web.xml,user can simply use the framework and all its need can be solved.

Benifits of using this FrameWork:-
===========================

1)Absolutely no requirement for XML configuration

2)No need to write servlets Classes for every new web Request.

3)User dont have to worry about get/post request and how to Handle them.

4)user dont have to worry about how to handle multipart requests and how to parse them and process them in case of File Upload.

5)user can use various tools in framework to find errors in services.

Getting Started.(steps to use the Framework)
===========================

1)Download this git repository.

2)Extract the zip File.

3)Copy.cut urlPaths.path file to tomcat9/Webapps/"project name"/WEB-INF/classes.
urlPaths contains a json String storing list of paths to where the services which are using this Framework exists:-
{
	
	"list_of_paths":[
    
     "/opt/tomcat/webapps/"Project Name"/WEB-INF/classes",
     "/home/java/classes"
	]
}
the above example show how to list paths to all the places where services classes are located (services classes are classes using the framework to create web service for requests).

4)Copy/cut web.xml to tomcat9/Webapps/"Project Name"/WEB-INF/.
Now you can forget about web.Xml as you dont have to change or configure it again.

5)Now copy Frame_work.jar to tomcat9/Webapps/"Project Name"/WEB-INF/lib/.
Tomcat search for servlet classes in classes folder or lib folder.

6)Now copy all files inside Dependencies folder and paste them inside tomcat9/lib/.
these are all the files you will ever need to create a web service.Our framework is Dependent on some of the these files.some of the jar file may already be present there you can skip those files.

7)You are done setting up the environment,now you can use the frameWork easily.

Modules/Examples/tutorials:-
===========================

user can create Webh service by using these annotations on class and Methods.User dont have to worry about how these webservices will run when request arrives.user can request Data, HttpServletRequest and httpServletResponse from framework.

Annotations User can use are:
---------------------
1)@Path(value="/employee")
Path annotation can be applied to class and method.value of path should starts with frount Slash followed by path.
Example:-
``` markdow
import com.thinking.machines.annotations.*;
@Path(value="/employee")
public class Employee
{
@Path(value="/view")
public void view(HttpServletRequest rq,HttpServletResponse rs)
{
	PrintWriter pw=null;
try{
pw=rs.getWriter();
pw.println("Success");
}catch(Exception e)
{
	e.printStackTrace();
	
}
}}
```
user can access this service by sending request to service/employee/view.

2)@RequestData(variableName="name").
user can use the following annotation to request data from framework which arrives as web request.
framework finds the value of the annotation an search for data with given name in request Bag and if found provide this requestded data to user without user having to worry about conversions 
Example:-
```
import com.thinking.machines.annotations.*;
@Path(value="/employee")
public class Employee
{
@Path(value="/add")
@ResponseType(type="text/html")
public String add(@RequestData(variableName="name") String name,@RequestData(variableName="gender") String gender,boolean human,@RequestData(variableName="indian") boolean indian)
{
System.out.println(name+"----"+gender+"-----"+indian);
return "Add model service Used";
}
}
```
Example url to access add service
http://localhost:8080/assignment-a-js/service/employee/add?name=Rahul+Singh&gender=male&indian=True
To access Boolean data client user must send data as True or TRUE ot true and same goes for it counterpart.

3)@Secured(value="")
By using this annotation user dont have to write verification code for every service that need to be secured,user can just apply this annotation to all the services that are needed to be secured from unidentified access.
Example:-
```
import com.thinking.machines.annotations.*;
@Path(value="/employee")
public class Employee
{
@Path(value="/delete")
@Secured(value="full classname to your verification class")
@ResponseType(type="text/html")
@Forward(value="index.jsp")
public String delete()
{

	return "Delete model service used";
}
}
```
value of Secured annotation should be a full name to the actual class that verify user based on the users details.
for this your verification class have to implement SecuredInerface of the Framework Module com.thinking.machines.interfaces.SecuredInterface;

4)@Forward(value="/employee/view")
Using this annotation user can forward request to another web service or to some jsp file
above example show how to use forward annotaton to forward request to index.jsp,you can also forward to some other service also.by giving service path as value of forward annotation.

5)@ResponseType(type="json")
user can specify what type of response it wants to send as a response for the request.
user can choose 3 of the value as a response "json", "text/html" ,"nothing"
if ResponseType annotation is not used then default response is text/html.
example is shown above.

6)@FileUpload()
user can use this annotaion in case of file upload request which is multipart request.user dont have to worry about how to handle the request as frame will handle the request for you and will provide the array of Files which user can easil use to save File wherever it wants.
In case of fileUpload annotation user have to extract extra data by using RequestData annotation or by requesting for HttpServletRequest object's address and accessing the data by using getAttribute method of httpservletRequest.
example:
```
import com.thinking.machines.annotations.*;
@Path(value="/employee")
public class Employee
{
@Path(value="/upload")
@FileUpload()
public String upload(File f[],@RequestData(variableName="name") String name,HttpServletRequest rq,HttpServletResponse rs)
{
System.out.println(name);
try{
for(File file:f)
{
FileInputStream fis=new FileInputStream(file);

File yourFile = new File("/media/rahul/New Volume/"+file.getName());
yourFile.createNewFile(); 
FileOutputStream fos=new FileOutputStream(yourFile);
byte []buffer=new byte[1024];
int length;
while((length=fis.read(buffer))>0)
{
	fos.write(buffer,0,length);
}

fis.close();
fos.close();
}
}catch(Exception e)
{
	e.printStackTrace();
	return "unable to upload";
}

return "Uploaded";
}}
```
to use this service send post request to /service/employee/upload.
First Parameter Should be of type file[ ] to safely use Fileupload service.

7) If user send raw data in post Request user can just  use it simply as:-
```
@Path(value="/add")
@ResponseType(type="json")
public Student add(Student s)
{

	return s;
}
```
Framework will automaically find the type of patameter and parse the raw data into Studenrt object and pass it as argument when invoking this service method


After Creating all the services user can use DockTool of the Framework to create two files service.pdf and errors.pdf docTool will analyse all the service and create these pdf .
service.pdf contains all the details of the unique services .
errors.pdf will contain all the errors and mistakes done by user in creating the services and thus user can fix the error before hand.
java -cp "path to Frame work jar";"path to tomcat lib for dependencies";. DocTool "path where you want to save those pdf files".
