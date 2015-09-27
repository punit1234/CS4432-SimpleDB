import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import simpledb.remote.SimpleDriver;

/**
 * CS4432-Project1
 * Task 1 of warm up and setup
 * 
 * @author yuan wen and punit dharani
 *
 */
public class WarmUpAndSetup {

	public static void main(String[] args) {
		// create two application schema
		Connection conn = null;
		try {
			// START SCHEMA 1
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();
			String s = "create table StarcraftUser(id int, username varchar(10), rank varchar(10))";
			stmt.executeUpdate(s);
			System.out.println("Table StarcraftUser created.");

			s = "insert into StarcraftUser(id, username, rank) values ";
			String[] starcraftUserData = { "(1, 'jaedong', 'gm')", "(2, 'classic', 'masters')", "(3, 'parting', 'gm')",
					"(4, 'admiraldooke', 'diamond')", "(5, 'huk', 'plat')", "(6, 'tlo', 'bronze')",
					"(7, 'artosis', 'silver')" };
			for (int i = 0; i < starcraftUserData.length; i++)
				stmt.executeUpdate(s + starcraftUserData[i]);
			System.out.println("StarcraftUser records inserted.");
			// END SCHEMA 1

			// START SCHEMA 2
			Statement stmt2 = conn.createStatement();
			String s2 = "create table StarcraftUnit(name varchar(10), damage int)";
			stmt2.executeUpdate(s2);
			System.out.println("Table StarcraftUnit created.");

			s2 = "insert into StarcraftUnit(name, damage) values ";
			String[] starcraftUnitData = { "('roach', 10)", "('hydralisk', 20)", "('immortal', 25)" };
			for (int i = 0; i < starcraftUnitData.length; i++)
				stmt.executeUpdate(s2 + starcraftUnitData[i]);
//			System.out.println("StarcraftUnit records inserted.");
			// END SCHEMA 2

			// START 'FEW' SQL QUERY
			System.out.println("Here are the units in starcraft dealing 20 damage");
			System.out.println("Name\tDamage");
			Statement stmt3 = conn.createStatement();
			String qry = "select name, damage " + "from StarcraftUnit "+ "where damage = 20 " ;
			ResultSet rs = stmt3.executeQuery(qry);
			while (rs.next()) {
				String starcraftUnitname = rs.getString("name");
				int starcraftDamage = rs.getInt("damage");
				System.out.println(starcraftUnitname + "\t"+ starcraftDamage);
			}
			rs.close();
			//END QUERY 1
			//START QUERY 2
			System.out.println("Here are the users in starcraft in gm");
			System.out.println("Name\tRank");
			Statement stmt4 = conn.createStatement();
			String qry2 = "select username , rank " + "from StarcraftUser "+ "where rank = 'gm' " ;
			ResultSet rs2 = stmt4.executeQuery(qry2);
			while (rs2.next()) {
				String starcraftUserName = rs2.getString("username");
				String starcraftRank = rs2.getString("rank");
				System.out.println(starcraftUserName + "\t"+ starcraftRank);
			}
			//END QUERY 2
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}
}
