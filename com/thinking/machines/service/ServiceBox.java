package com.thinking.machines.service;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class ServiceBox
{
public Class serviceClass=null;
public Method serviceMethod=null;
public Object instance=null;
public String path;
public Annotation listl[][];
public List<String> requestDataBag=null;
public List<Boolean> isRequestDataAnnotationPresentParametersList=null;
public Class[] pTypeList;
public boolean isSecuredMethodLevelAnnotationPresent=false;
public Class securedMethodLevelClass=null;
public boolean isSecuredClassLevelAnnotationPresent=false;
public Class securedClassLevelClass=null;
public Class returnType=null;
public boolean isResponseTypeAnnotationPresent=false;
public String responseType;
public boolean isForwardAnnotationPresent=false;
public String forwardValue;
public boolean isFileUploadAnnotationPresent=false;
}