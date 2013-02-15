<%@ page language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>

<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.naming.NamingException" %>

<html>
<head>
<title>物件情報</title>
</head>
<body>

データソースを使用した接続<br>

<%
   int bukken_id = Integer.parseInt(request.getParameter("bukken_id"));
   int user_id = Integer.parseInt(request.getParameter("user_id"));
   String name = null;
   String image1 = null;
   Context ctx = new InitialContext();
   DataSource ds = (DataSource)ctx.lookup("java:jboss/datasources/oracle");
   Connection conn = ds.getConnection();
   String query = null;
   
   try {
     PreparedStatement pstmt = null;
     try {
       query = "select \"name\", \"image1\" from \"bukken\"" + " where \"bukken_id\" = ?";
       pstmt = conn.prepareStatement(query);
       pstmt.setInt(1, bukken_id);
       ResultSet rs = pstmt.executeQuery();
       try {
           rs.next();
           name = rs.getString(1);
           image1 = rs.getString(2);
       }
       finally {
          try { rs.close(); } catch (Exception e) {e.printStackTrace();}
       }
     }
     finally {
       try { pstmt.close(); } catch (Exception e) {e.printStackTrace();}
     }
   }
   finally {
     try { conn.close(); } catch (Exception e) {e.printStackTrace();}
   }
%>
<br>name :<%=name%>
<br><img src="/oracle<%=image1%>?<%=bukken_id%>" />
<br>
<br>## request parameter
<br>bukken_id :<%=bukken_id%>
<br>user_id :<%=user_id%>
</body>
</html>

