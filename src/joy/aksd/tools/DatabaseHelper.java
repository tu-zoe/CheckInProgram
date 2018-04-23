package joy.aksd.tools;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sun.jdbc.rowset.CachedRowSet;

public class DatabaseHelper {
    
	static {
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection(){
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/block?useUnicode=true&characterEncoding=utf8","root","923788");
		}
		catch(Exception e) {
			
			System.out.println(e);
		}
		return null;
	}
	public static boolean JDBCexit()//检测数据库能否顺利连接
	{
		try {
			 DriverManager.getConnection("jdbc:mysql://localhost:3305/block?useUnicode=true&characterEncoding=utf8","root","923788");
			 return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static void execute(String sql) {
    	Connection conn = getConnection();//从接口上避免update、delete操作可以通过判断sql语句前6个是否是insert
    	try {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate(sql);
    		stmt.close();
    		conn.close();
    	}
    	catch(SQLException e) {
    		System.out.println(e);
    	}
    }
    
    public static ResultSet query(String sql) {
    	Connection conn = getConnection();
    	ResultSet rs = null;
    	CachedRowSet crs = null;
    	try {
    		crs = new CachedRowSet();
    		Statement stmt = conn.createStatement();
    		rs = stmt.executeQuery(sql);
    		crs.populate(rs);
    		stmt.close();
    		conn.close();
    	}
    	catch(SQLException e) {
    		System.out.println(e);
    	}
    	return crs;
    }
    
   
}


