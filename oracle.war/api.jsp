<%@ page language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>
<%@ page import="javax.sql.*,javax.naming.*,java.sql.*" %>

<%
   int bukken_id = 0; 
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
       query = "select \"bukken_id\" from \"actionhistory\"" + " where \"user_id\" = ?";
       pstmt = conn.prepareStatement(query);
       pstmt.setInt(1, user_id);
       ResultSet rs = pstmt.executeQuery();
       try {
           rs.next();
           bukken_id = rs.getInt(1); 
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
{bukken_id : <%=bukken_id%>}
