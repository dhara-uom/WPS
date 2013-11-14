<%@ page import="java.io.File" %>
<%--
  Created by IntelliJ IDEA.
  User: newair
  Date: 11/12/13
  Time: 4:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

    String realPath = getServletConfig().getServletContext().getRealPath("/");




    String classAsString = request.getParameter("class");
    String classNameWithPackages = request.getParameter("classname");

    //String attribute = (String) request.getParameter("input");

    String[] classNameElments = classNameWithPackages.split("\\.");

    String className;
    String pckgPart = "/";
    String pckgPartConf= "";

    for (int i = 0; i < classNameElments.length - 1; i++) {

        pckgPart = pckgPart + classNameElments[i] + "/";
        pckgPartConf = pckgPartConf + classNameElments[i]+".";
    }
    className = classNameElments[classNameElments.length - 1];

    String pathForCreatedFile = realPath + "/WEB-INF/classes" + pckgPart + className + ".java";


    File file = new File(pathForCreatedFile);

     String result;

    if(file.exists()){
         result = "uploadingOk";
       response.getWriter().print(result);

    }       else{
        result="uploadingNotOk";
        response.getWriter().print(result);
    }



%>