import java.sql.*;

/*
 * CREATE USER test IDENTIFIED BY test;
 * GRANT ALL PRIVILEGES TO test;
 *
 * create table emp
 * (
 * 　　emp_id char(3) ,
 * 　　emp_name varchar2(10),
 * 　　primary key( emp_id )
 * );
 *
 * insert into emp values (1,'fujisaki');
 *
 */

public class OracleSample {
  public static void main (String[] args) throws Exception {
   Class.forName ("oracle.jdbc.OracleDriver");

   Connection conn = DriverManager.getConnection
     ("jdbc:oracle:thin:@//localhost:1521/xe", "test", "test");
                        // @//machineName:port/SID,   userid,  password
   try {
     Statement stmt = conn.createStatement();
     try {
       //ResultSet rset = stmt.executeQuery("select BANNER from SYS.V_$VERSION");
       ResultSet rset = stmt.executeQuery("select emp_name from emp");
       try {
         while (rset.next())
           System.out.println (rset.getString(1));   // Print col 1
       } 
       finally {
          try { rset.close(); } catch (Exception ignore) {}
       }
     } 
     finally {
       try { stmt.close(); } catch (Exception ignore) {}
     }
   } 
   finally {
     try { conn.close(); } catch (Exception ignore) {}
   }
  }
}
