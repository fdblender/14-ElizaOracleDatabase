// Modified 11-Eliza to use an Oracle Database
// F. Blendermann

package ElizaDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class ElizaOracleDatabase {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		try {
		if( conn == null) {			
			Class.forName("oracle.jdbc.driver.OracleDriver");
		// 	con = DriverManager.getConnection("jdbc:oracle:thin:sys as
		// sysdba/oracle@localhost:1521:orcl");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:ora1/ora1@localhost:1521:orcl");
		}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;		
	}
	
	public static void openDatabase(Map<String, String> replacements) {				
		try {
			Connection con;
			ResultSet rs;
			Statement stmt;
			con = getConnection();
			stmt = con.createStatement();
			
			// load replacements map		
			String word, replacement;
			String sql = "select * from eliza_replacements";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				word = rs.getString(1);
				replacement = rs.getString(2);
				replacements.put(word, replacement);
				//System.out.println(word +" replacement: "+replacement + "\t");
				//System.out.println(rs.getString(2));
			}
			//System.out.println("");	
			//stmt.close();
			//conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
	
	public static void closeDatabase() {
		try {
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String makeReplacements(String input, Map<String, String> replacements) {
		String[] words = null;
		String newString = "";
		words = input.split(" ");
		for (String word: words) {
			if (replacements.containsKey(word.toLowerCase())) {
				newString += replacements.get(word.toLowerCase())+" ";
				//System.out.print(newString+" | ");
			} else {
				newString += word+" ";
			}
		}
		//System.out.println("New string with replacements: "+newString);
		return(newString);
	}

	public static String getRandomString(boolean gethedge) throws SQLException{
		ResultSet rs = null;	
		PreparedStatement pstmt = null;
		Connection con = getConnection();
		if (con == null) {
			System.out.println("con is null");
		}		
		if (gethedge) {
		pstmt = con.prepareStatement(
			"SELECT * FROM (SELECT * FROM eliza_hedges ORDER BY DBMS_RANDOM.RANDOM) WHERE rownum=1");
		} else {
			pstmt = 
					con.prepareStatement(
					"SELECT * FROM (SELECT * FROM eliza_qualifiers ORDER BY DBMS_RANDOM.RANDOM) WHERE rownum=1");
		}
		rs = pstmt.executeQuery();
		String randomString = "";
		if (rs.next()) {
			randomString = rs.getString(1);
		}
		rs.close();
		pstmt.close();
		return randomString;		
	}

	public static int getRandom(int size) {
		Random rnd = new Random();
		int i = rnd.nextInt(size);
		//System.out.println("random no: " + i);
		return (i);
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		// Oracle connection, prepared statement and result set
		ResultSet rs = null;		
		Statement stmt = null;
		Connection con = null;		
		// replacements map
		Map<String, String> replacementMap = new HashMap<String, String>();

		int choice;		
		String statement = "";
		String response = "";
		String qualifier;
		String newstring;
		
		// init Oracle database and load replacement map from Oracle table
		openDatabase(replacementMap);		

		System.out.println("Good day. What is your problem? " + "Enter your response here or Q to quit: ");
		
		try {
			
			while (true) {
				statement = scan.nextLine();
				if (statement.toUpperCase().equals("Q")) {
					break;
				}
				choice = getRandom(2);
				if (choice == 1) {
					// get a random hedge
					newstring = getRandomString(true);
				} else {
					// get a random qualifier
					qualifier = getRandomString(false);
					newstring = makeReplacements(statement, replacementMap);
					newstring = qualifier + " " + newstring;
				}
				System.out.println(newstring);
			}
			System.out.println("Thank you for talking with me today.");
			closeDatabase();

		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}





