package dao;

import java.sql.*;
import java.util.*;

import javax.naming.NamingException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.ConnectionPool;

public class FeedDAO {
	public boolean insert(String jsonstr) throws NamingException, SQLException, ParseException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			synchronized(this) {
				conn = ConnectionPool.get();
				
				// phase 1. add "no" property ----------
				String sql = "SELECT no FROM feed ORDER BY no DESC LIMIT 1";
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				
				int max = (!rs.next()) ? 0 : rs.getInt("no");
				
				JSONParser parser = new JSONParser();
				JSONObject jsonobj = (JSONObject) parser.parse(jsonstr);
				jsonobj.put("no", max + 1);
				
				stmt.close(); rs.close();
				
				//phase 2. add "user" property ----------
				String uid = jsonobj.get("id").toString();
				
				sql = "SELECT jsonstr FROM user WHERE id = ?";
				
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, uid);
				rs = stmt.executeQuery();
				
				if(rs.next()) {
					String usrstr = rs.getString("jsonstr");
					JSONObject usrobj = (JSONObject) parser.parse(usrstr);
					usrobj.remove("password");
					usrobj.remove("ts");
					jsonobj.put("user", usrobj);
				}
				
				stmt.close(); rs.close();
				
				//phase 3. insert jsonobj to the table ---------
				
				sql = "INSERT INTO feed(no, id, jsonstr) VALUES (?, ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, max + 1);
				stmt.setString(2, uid);
				stmt.setString(3, jsonobj.toJSONString());
				
				int count = stmt.executeUpdate();
				return (count == 1) ? true : false;
			}
			
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}
	}
	
	public String getList(String uid) throws NamingException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT jsonstr FROM feed ORDER BY no DESC";
			
			conn = ConnectionPool.get();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			String str = "[";
			int cnt = 0;
			
			while(rs.next()) {
				if(cnt++ > 0) str += ", ";
				str += rs.getString("jsonstr");
			}
			
			return str + "]";
			
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}
	}
	
	public String getMyGroup(String uid) throws NamingException, SQLException {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = ConnectionPool.get();
	        
	        String sql = "SELECT jsonstr FROM feed WHERE id = ?";
	        
	        stmt = conn.prepareStatement(sql);
	        stmt.setString(1, uid);
	        
	        rs = stmt.executeQuery();
	        
	        String str = "[";
	        
	        int cnt = 0;
	        while (rs.next()) {
	            if (cnt++ > 0) str += ", ";
	            str += rs.getString("jsonstr");
	        }
	        return str + "]";
	    } finally {
	        if (rs != null) rs.close();
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
	    }
	}
	
	public boolean update(int feedNo, String jsonstr) throws NamingException, SQLException, ParseException {
	    Connection conn = null;
	    PreparedStatement stmt = null;

	    try {
	        conn = ConnectionPool.get();
	        
	        String existingJsonStr = getFeedJsonStr(feedNo, conn);

	        if (existingJsonStr != null) {
	            JSONParser parser = new JSONParser();
	            JSONObject existingJsonObj = (JSONObject) parser.parse(existingJsonStr);
	            JSONObject newJsonObj = (JSONObject) parser.parse(jsonstr);

	            newJsonObj.put("no", existingJsonObj.get("no"));
	            newJsonObj.put("user", existingJsonObj.get("user"));
	            newJsonObj.put("name", existingJsonObj.get("name"));

	            String updatedJsonStr = newJsonObj.toJSONString();

	            String sql = "UPDATE feed SET jsonstr = ? WHERE no = ?";
	            stmt = conn.prepareStatement(sql);
	            stmt.setString(1, updatedJsonStr);
	            stmt.setInt(2, feedNo);

	            int count = stmt.executeUpdate();
	            return (count == 1) ? true : false;
	        } else {
	            return false;
	        }
	    } finally {
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
	    }
	}
	
	private String getFeedJsonStr(int feedNo, Connection conn) throws SQLException {
	    String sql = "SELECT jsonstr FROM feed WHERE no = ?";
	    PreparedStatement stmt = conn.prepareStatement(sql);
	    stmt.setInt(1, feedNo);

	    ResultSet rs = stmt.executeQuery();

	    if (rs.next()) {
	        return rs.getString("jsonstr");
	    } else {
	        return null;
	    }
	}
	
	public String getGroup(String frids, String maxNo) throws NamingException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT jsonstr FROM feed WHERE id IN (" + frids + ")";
			
			if(maxNo != null) {
				sql += " AND no < " + maxNo;
			}
			sql += " ORDER BY no DESC LIMIT 3";
			
			conn = ConnectionPool.get();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			String str = "[";
			
			int cnt = 0;
			while(rs.next()) {
				if(cnt++ > 0) str += ", ";
				str += rs.getString("jsonstr");
			}
			return str + "]";
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	} 

}
