<%@ page language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>
<%@ page import="javax.sql.*,javax.naming.*,java.sql.*" %>

<html>
<head>
<title>TEST</title>
</head>
<body>

データソースを使用した接続<br>

<%
   String banner = null;
   Context ctx = new InitialContext();
   DataSource ds = (DataSource)ctx.lookup("java:jboss/datasources/oracle");
   Connection conn = ds.getConnection();
   
   try {
     Statement stmt = conn.createStatement();
     try {
       ResultSet rs = stmt.executeQuery("select BANNER from SYS.V_$VERSION");
       //ResultSet rs = stmt.executeQuery("select emp_name from emp");
       try {
           rs.next();
           banner = rs.getString(1); 
           System.out.println(banner);
       }
       finally {
          try { rs.close(); } catch (Exception ignore) {}
       }
     }
     finally {
       try { stmt.close(); } catch (Exception ignore) {}
     }
   }
   finally {
     try { conn.close(); } catch (Exception ignore) {}
   }
%> 
<br><%=banner%><br>
</body>
</html>
