package lw.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/api")
public class LwApi extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    int user_id = Integer.parseInt(request.getParameter("user_id"));
    Context ctx = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = getConnection(ctx, ds);
      
      //user_idをキーに行動履歴から過去の物件参照情報を取得してcountでソートし
      //上位1件の物件idを取得
      int bukkenId = getBukkenIdFromAH(conn, pstmt, rs, user_id);
      
      //上位1件の物件のidからキーワードを取得
      String keyword = getKeywordFromKW(conn, pstmt, rs, bukkenId);
      
      //キーワードを含む物件のidを10件取得
      List<Integer> bukkenIdList = getBukkenIdListFromKW(conn, pstmt, rs, keyword);
      
      //ランダムに選択した物件をjsonにセットして返す
      request.setAttribute("json", getJsonFromBK(conn, pstmt, rs, bukkenIdList));     

    } catch (NamingException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if(rs != null){rs.close();}
      } catch (Exception e) {e.printStackTrace();}
      try {
        if(pstmt != null){pstmt.close();}
      } catch (Exception e) {e.printStackTrace();}
      try {
        if(conn != null){conn.close();}
      } catch (Exception e) {e.printStackTrace();}
    }

    response.setContentType("text/plain; charset=UTF-8");
    String path = "/api.jsp";
    RequestDispatcher dispatch = request.getRequestDispatcher(path);
    dispatch.forward(request, response);
  }

  private static Connection getConnection(Context ctx, DataSource ds)
      throws NamingException, SQLException {
    ctx = new InitialContext();
    ds = (DataSource) ctx.lookup("java:jboss/datasources/oracle");
    Connection conn = ds.getConnection();
    return conn;
  }

  private static int getBukkenIdFromAH(Connection conn,
      PreparedStatement pstmt, ResultSet rs,int user_id) throws SQLException {
    String query = "select \"bukken_id\" from \"actionhistory\""
        + " where \"user_id\" = ? order by \"count\" desc";
    pstmt = conn.prepareStatement(query);
    pstmt.setInt(1, user_id);
    rs = pstmt.executeQuery();

    rs.next();
    int bukkenId = rs.getInt(1);
    
    return bukkenId;
  }
  
  private static String getKeywordFromKW(Connection conn,
      PreparedStatement pstmt, ResultSet rs, int bukkenId) throws SQLException {

    System.out.println("-------------------------------------");
    System.out.println(bukkenId);
    String query = "select \"keyword\" from \"keyword\""
        + " where \"bukken_id\" = ?";
    pstmt = conn.prepareStatement(query);
    pstmt.setInt(1, bukkenId);
    rs = pstmt.executeQuery();
    
    rs.next();
    String keyword = rs.getString(1);
    
    return keyword;
  }
  
  private static List<Integer> getBukkenIdListFromKW(Connection conn,
      PreparedStatement pstmt, ResultSet rs, String keyword) throws SQLException {
    List<Integer> bukkenIdList = new ArrayList<Integer>();
    String query = "select \"bukken_id\" from \"keyword\""
        + " where \"keyword\" = ?";
    pstmt = conn.prepareStatement(query);
    pstmt.setString(1, keyword);
    rs = pstmt.executeQuery();
    
    while (rs.next()) {
      bukkenIdList.add(rs.getInt(1));
    }
  
    return bukkenIdList;
  }

  private static String getJsonFromBK(Connection conn,
      PreparedStatement pstmt, ResultSet rs, List<Integer> bukenIdList) throws SQLException{
    StringBuffer jsonBuff = new StringBuffer();
    
    Collections.shuffle(bukenIdList);
    int bukkenId = bukenIdList.get(0);
    
    String query = "select * from \"bukken\""
      + " where \"bukken_id\" = ?";
    pstmt = conn.prepareStatement(query);
    pstmt.setInt(1, bukkenId);
    rs = pstmt.executeQuery();
    
    jsonBuff.append("[");
    while (rs.next()) {
      jsonBuff.append("{");
      jsonBuff.append("\"bukkenId\" : " + rs.getString(1));
      jsonBuff.append("\"name\" : " + rs.getString(2));
      jsonBuff.append("\"category\" : " + rs.getString(3));
      jsonBuff.append("\"area_id\" : " + rs.getString(4));
      jsonBuff.append("\"eki_info\" : " + rs.getString(5));
      jsonBuff.append("\"description\" : " + rs.getString(6));
      jsonBuff.append("\"kakaku\" : " + rs.getString(7));
      jsonBuff.append("\"kakaku_disp\" : " + rs.getString(8));
      jsonBuff.append("\"tochimenseki\" : " + rs.getString(9));
      jsonBuff.append("\"image1\" : " + rs.getString(10));
      jsonBuff.append("\"image2\" : " + rs.getString(11));
      jsonBuff.append("\"image3\" : " + rs.getString(12));
      jsonBuff.append("\"image4\" : " + rs.getString(13));
      jsonBuff.append("\"image5\" : " + rs.getString(14));
      jsonBuff.append("\"image6\" : " + rs.getString(15));
      jsonBuff.append("}");
      
      jsonBuff.append(",");
    }
    jsonBuff.deleteCharAt(0);
    jsonBuff.append("]");

    return jsonBuff.toString();
  }
}
