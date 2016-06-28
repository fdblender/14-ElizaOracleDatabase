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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;



public class Eliza {
	
	public static void openDatabase(Statement stmt, Connection con) {
		String sql = "select * from pet";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// con = DriverManager.getConnection("jdbc:oracle:thin:sys as
			// sysdba/oracle@localhost:1521:orcl");
			con = DriverManager.getConnection("jdbc:oracle:thin:ora1/ora1@localhost:1521:orcl");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public static void closeDatabase(ResultSet rs, Statement stmt, Connection con) {
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String getQualifier(String input, Set<String> qualifiers, 
			Map<String, String> replacements) {
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
		newString = getRandomString(qualifiers) + " " +newString;
		//System.out.println(newString);
		return newString;
	}

	public static String getRandomString(boolean gethedge, Connection con) throws SQLException{
		ResultSet rs = null;	
		PreparedStatement pstmt = null;
		if (gethedge) {
		pstmt = con.prepareStatement(
			"SELECT * FROM(SELECT * FROM eliza_hedges ORDER BY DBMS_RANDOM.RANDOM) WHERE rownum=1;");
		} else {
			pstmt = 
					con.prepareStatement(
					"SELECT * FROM(SELECT * FROM eliza_qualifiers ORDER BY DBMS_RANDOM.RANDOM) WHERE rownum=1;");
		}
		rs = pstmt.executeQuery();
		if (rs.next()) 
			return(rs.getString(2));
		else {
			return("");
		}
	}

	public static int getRandom(int size) {
		Random rnd = new Random();
		int i = rnd.nextInt(size);
		//System.out.println("random no: " + i);
		return (i);
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		// Oracle connection and result set
		ResultSet rs = null;		
		Statement stmt = null;
		Connection con = null;		

		/*Set<String> hedgeSet = new HashSet<String>();
		Set<String> qualifierSet = new HashSet<String>();
		Map<String, String> replacementMap = new HashMap<String, String>();*/

		int choice;		
		String statement = "";
		String response = "";
		openDatabase(stmt, con);		

		System.out.println("Good day. What is your problem? " + "Enter your response here or Q to quit: ");
		
		try {
			
			while (true) {
				statement = scan.nextLine();
				if (statement.toUpperCase().equals("Q")) {
					break;
				}
				choice = getRandom(2);
				if (choice == 1) {
					response = getRandomString(true, con);
				} else {
					response = getRandomString(false, con);
					response = getQualifier(statement, qualifierSet, replacementMap);
				}
				System.out.println(response);
			}
			System.out.println("Thank you for talking with me today.");
			closeDatabase(rs, stmt, con);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
