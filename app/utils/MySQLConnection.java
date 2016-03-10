package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.Logger;
/**
 * This class is used to create connections to the MySql DB and execute queries
 * @author colmcarew
 *
 */

public class MySQLConnection {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/pacemaker";
//	static final String DB_URL = "jdbc:mysql://pacemakerdb.chhhwpxruumu.eu-west-1.rds.amazonaws.com/pacemaker";
	static final String USER = "writer";
	static final String PASS = "pacemaker";
	
	/**
	 * default constructor
	 */
	public MySQLConnection() {
		
	}
	
	/**
	 * Method used to connect to the MySQL DB and get User IDs
	 * Based on the input query
	 * @param query
	 * @return
	 */
	public List<Long> getUserIdsFromMysqlQuery(String query) {
		List<Long> ids = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			Logger.info(new Date() + " About to run the following query:\n" + query + " at this DB: " + DB_URL);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				ids.add(rs.getLong("id"));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			Logger.info(se.toString());
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					Logger.info(se.toString());
				}
			}
		}
		return ids;
	}
}
