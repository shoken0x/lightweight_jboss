package lw.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

			// 行動履歴から物件のリストを取得
			String bukkenIds = getBukkenIdsFromAH(conn, pstmt, rs, user_id);

			// 物件情報リストから物件に含まれるキーワードごとの合計を計算
			Map<String, Integer> keywordMap = getKeywordMap(conn, pstmt, rs, bukkenIds);

			// キーワード上位10件を配列化
			String keywords = getKeyBestTen(keywordMap);

			// キーワードを含む物件のIDを取得
			Map<Integer, Integer> bukkenMap = getBukkenMapFromKW(conn, pstmt, rs, keywords);
			
			bukkenIds = getKeyBestTwo(bukkenMap);
			
			// 物件上位2件をrequestにセット
			request.setAttribute("json", getJsonFromBK(conn, pstmt, rs, bukkenIds));

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

	private static String getBukkenIdsFromAH(Connection conn,
			PreparedStatement pstmt, ResultSet rs,int user_id) throws SQLException {
		StringBuffer bukkenIdBuff = new StringBuffer();
		String query = "select \"bukken_id\" from \"actionhistory\""
				+ " where \"user_id\" = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, user_id);
		rs = pstmt.executeQuery();

		while (rs.next()) {
			bukkenIdBuff.append(",");
			bukkenIdBuff.append(rs.getString(1));
		}
		bukkenIdBuff.deleteCharAt(0);

		return bukkenIdBuff.toString();
	}

	private static Map<String, Integer> getKeywordMap(Connection conn,
			PreparedStatement pstmt, ResultSet rs, String bukkenIds) throws SQLException {
		Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		String query = "select \"keyword\" from \"keyword\""
				+ " where \"bukken_id\" in (" + bukkenIds + ")";
		pstmt = conn.prepareStatement(query);
		rs = pstmt.executeQuery();
		int val_count = 0;
		String key = null;

		while (rs.next()) {
			val_count = 1;
			key = rs.getString(1);
			if (keywordMap.containsKey(key)) {
				val_count = keywordMap.get(key) + 1;
				keywordMap.put(key, val_count);
			} else {
				keywordMap.put(key, val_count);
			}
		}
			// keyword_buff.deleteCharAt(0);
		return keywordMap;
	}
	
	private static String getKeyBestTen(Map<String, Integer> keywordMap){
		StringBuffer keywordBuff = new StringBuffer();
		int count = 0;
		ArrayList entries = new ArrayList(keywordMap.entrySet());
		
		Collections.sort(entries,new Comparator(){
		 public int compare(Object o1, Object o2){
		  Map.Entry e1 =(Map.Entry)o1;
		  Map.Entry e2 =(Map.Entry)o2;
		  
		  Integer e1Value = new Integer(e1.getValue().toString());
		  Integer e2Value = new Integer(e2.getValue().toString()); 
		  return e1Value.compareTo(e2Value);
		 }
		});
		
		for (Object o : entries) {
			if (count > 10) {
				break;
			}
			Map.Entry e =(Map.Entry)o;
			keywordBuff.append(",");
			keywordBuff.append("'");
			keywordBuff.append(e.getKey());
			keywordBuff.append("'");
			
			count ++;
		}
		keywordBuff.deleteCharAt(0);
		
		return keywordBuff.toString();
	}
	
	private static Map<Integer, Integer> getBukkenMapFromKW(Connection conn,
			PreparedStatement pstmt, ResultSet rs,String keywords) throws SQLException{
		Map<Integer, Integer> bukkenMap = new HashMap<Integer, Integer>();
		String query = "select \"bukken_id\" from \"keyword\""
			+ " where \"keyword\" in (" + keywords + ")";
		pstmt = conn.prepareStatement(query);
		rs = pstmt.executeQuery();
		int val_count = 0;
		int key = 0;
		
		while (rs.next()) {
			val_count = 1;
			key = rs.getInt(1);
			if (bukkenMap.containsKey(key)) {
				val_count = bukkenMap.get(key) + 1;
				bukkenMap.put(key, val_count);
			} else {
				bukkenMap.put(key, val_count);
			}
		}
		
		return bukkenMap;
	}
	
	private static String getKeyBestTwo(Map bukkenMap){
		StringBuffer bukkenBuff = new StringBuffer();
		int count = 0;
		ArrayList entries = new ArrayList(bukkenMap.entrySet());
		
		Collections.sort(entries,new Comparator(){
		 public int compare(Object o1, Object o2){
		  Map.Entry e1 =(Map.Entry)o1;
		  Map.Entry e2 =(Map.Entry)o2;
		  
		  Integer e1Value = new Integer(e1.getValue().toString());
		  Integer e2Value = new Integer(e2.getValue().toString()); 
		  return e1Value.compareTo(e2Value);
		 }
		});
		
		for (Object o : entries) {
			if (count > 2) {
				break;
			}
			Map.Entry e =(Map.Entry)o;
			bukkenBuff.append(",");
			bukkenBuff.append(e.getKey());
			
			count ++;
		}
		bukkenBuff.deleteCharAt(0);
		
		return bukkenBuff.toString();
	}
	
	private static String getJsonFromBK(Connection conn,
			PreparedStatement pstmt, ResultSet rs, String bukenIds) throws SQLException{
		StringBuffer jsonBuff = new StringBuffer();
		String query = "select * from \"bukken\""
			+ " where \"bukken_id\" in (" + bukenIds + ")";
		pstmt = conn.prepareStatement(query);
		rs = pstmt.executeQuery();

		jsonBuff.append("[");
		while (rs.next()) {
			jsonBuff.append(",");

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
		}
		jsonBuff.deleteCharAt(1);
		jsonBuff.append("]");

		return jsonBuff.toString();
	}

}
