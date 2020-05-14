import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.io.Console;
import java.io.IOException;

public class LoginSys {
	static String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	static Scanner in = new Scanner(System.in);
    public static String getSHA512(String input){

    	String hashed= null;
    	try {
    		MessageDigest digest = MessageDigest.getInstance("SHA-512");
    		digest.reset();
    		digest.update(input.getBytes("utf8"));
    		hashed = String.format("%0128x", new BigInteger(1, digest.digest()));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	
    	return hashed;
    }
	
	public static void mainPrompt() throws InterruptedException, SQLException {
		System.out.println("Do you want to login or register:");
		System.out.println("Type '1' to Login");
		System.out.println("Type '2' to Register");
		System.out.println("Type '3' to Exit \n");
		while (true) {
			System.out.print(">>> ");
			String userChoice = in.nextLine();
			if (userChoice.equals("1")) {
				System.out.println("Okay, just wait for a few seconds");
				Thread.sleep(1000);
				clrscr();
				login();
				break;
			} else if (userChoice.equals("2")) {
				System.out.println("Okay, redirecting you to the register prompt");
				Thread.sleep(1000);
				clrscr();
				register();
				break;
			} else if (userChoice.equals("3")) { 
				System.out.println("Okay, exiting....");
				Thread.sleep(500);
				System.out.print("Enter any text to exit: ");
				String passer = in.nextLine();
				clrscr();
				System.exit(0);
				break;
			} else {
				System.out.println("Wrong Input, please choose either 1, 2 or 3 \n");
			}
			
		}
	}
	
	//Create Database File named users.db in the src folder
	public static void createDB() {
		//Get Connection to the sqlite3 database
		Connection conn = null;
		//Try to connect to db file
		try {
			//Opens DB and create the db file if not exists
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
		//If it gives error, then we exit
		} catch ( Exception e ) {

			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}	
	}
	//Create a table
	public static void createTable() {
		//Get Connection to the sqlite3 database
		Connection conn = null;
		//This is our cursor
		Statement c = null;
		//Try to connect to db file
		try {
			//Opens DB
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.createStatement();
			String sqlQuery = "CREATE TABLE IF NOT EXISTS usersLog(firstName TEXT, lastName TEXT, userName TEXT, userPass TEXT, dateRegistered TEXT)";
			c.executeUpdate(sqlQuery);
			c.close(); //Closes the cursor
			conn.close(); //Closes the connection
		
		} catch ( Exception e ) {

			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}	
	}
	
	public static void insertDataToDB(String fname, String lname, String uname, String upass) {
		Connection conn = null;
		PreparedStatement c = null;
		String sql = "INSERT INTO usersLog(firstName, lastName, userName, userPass, dateRegistered) VALUES (?,?,?,?,?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, fname);
			c.setString(2, lname);
			c.setString(3, uname);
			c.setString(4, upass);
			c.setString(5, date);
			c.executeUpdate();
			c.close();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static String selectUserFromDB(String uname, String upass) throws SQLException {
		Connection conn = null;
		PreparedStatement c = null;
		ResultSet rs = null;
		String name = null;
		String sql = "SELECT * FROM usersLog WHERE userName = (?) AND userPass = (?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, uname);
			c.setString(2, upass);
			rs = c.executeQuery();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} 
		while(rs.next()) {
			name = rs.getString("userName");
		}
		return name;
	}
	
	public static String selectPassFromDB(String uname, String upass) throws SQLException {
		Connection conn = null;
		PreparedStatement c = null;
		ResultSet rs = null;
		String pass = null;
		String sql = "SELECT * FROM usersLog WHERE userName = (?) AND userPass = (?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, uname);
			c.setString(2, upass);
			rs = c.executeQuery();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} 
		while(rs.next()) {
			pass = rs.getString("userPass");
		}
		return pass;
	}
	
	
	
	public static void register() throws InterruptedException, SQLException {
		Console console = System.console();
		System.out.println("Register Please!");
		System.out.print("Enter your first name: ");
		String fname = in.nextLine();
		System.out.print("Enter your last name: ");
		String lname = in.nextLine();
		System.out.print("Enter your username(Will be used to login!): ");
		String uname = in.nextLine();
		while (true) {
			String upass = new String(console.readPassword("Enter your password: "));
			String upassVerify = new String(console.readPassword("Enter your password again: "));
			if (upass.equals(upassVerify)) {
				upass = getSHA512(upass);
				System.out.println("Are these info are correct?");
				System.out.println("First Name: "+fname);
				System.out.println("Last Name: "+lname);
				System.out.println("Username: "+uname);
				System.out.println("Yes(Y) or No(N)\n>>> ");
				String userC = in.nextLine();
				if (userC.toLowerCase().equals("y")) {
					System.out.println("Account Creating...");
					Thread.sleep(500);
					insertDataToDB(fname, lname, uname, upass);
					System.out.println("Account Created");
					Thread.sleep(500);
					System.out.print("\nEnter any text to continue.. ");
					String passer = in.nextLine();
					Thread.sleep(500);
					clrscr();
					mainPrompt();
					break;
				} else if (userC.toLowerCase().equals("n")) {
					System.out.println("Okay Try Again");
					Thread.sleep(500);
					register();
					break;
				} else {
					System.out.println("Wrong Input, Y or N only");
				}
			} else {
				System.out.println("Passwords do not match. Try again");
			}
		}	
	}
	
	public static void seeInfo(String uname, String upass) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement c = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM usersLog WHERE userName = (?) AND userPass = (?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, uname);
			c.setString(2, upass);
			rs = c.executeQuery();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("\n\nGathering Info...");
		Thread.sleep(500);
		System.out.println("Info Gathered!");
		while (rs.next()) {
			System.out.println("First Name: " + rs.getString("firstName"));
			System.out.println("Last Name: " + rs.getString("lastName"));
			System.out.println("Username: " + rs.getString("userName"));
			System.out.println("Date Registered: " + rs.getString("dateRegistered"));
			System.out.println("\nPress any key then enter to continue!");
			String passer = in.nextLine();
			clrscr();
		}
	}
	
	public static void renameAcc(String name, String pass, String newname) {
		Connection conn = null;
		PreparedStatement c = null;
		String sql = "UPDATE usersLog SET userName = (?) WHERE userName=(?) AND userPass=(?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, newname);
			c.setString(2, name);
			c.setString(3, pass);
			c.executeUpdate();
			c.close();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static void changePass(String name, String pass, String newpass) {
		Connection conn = null;
		PreparedStatement c = null;
		String sql = "UPDATE usersLog SET userPass = (?) WHERE userName=(?) AND userPass=(?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, newpass);
			c.setString(2, name);
			c.setString(3, pass);
			c.executeUpdate();
			c.close();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static void delAcc(String username, String pass) {
		Connection conn = null;
		PreparedStatement c = null;
		String sql = "DELETE FROM usersLog WHERE userName = (?) AND userPass = (?)";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
			c = conn.prepareStatement(sql);
			c.setString(1, username);
			c.setString(2, pass);
			c.executeUpdate();
			c.close();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} 
	}
	
	public static void loginRights(String name, String pass) throws SQLException, InterruptedException {
		//Password is hashed already here when passed in
		System.out.println("Welcome back " + selectUserFromDB(name, pass));
		System.out.println("1. See my info");
		System.out.println("2. Rename Account (Username)");
		System.out.println("3. Change Password");
		System.out.println("4. Delete Account");
		System.out.println("5. Logout");
		
		while(true) {
			System.out.print(">>> ");
			String userChoice = in.nextLine();
			//I'm used to do this if elif else because i came from python, but you can use switch if you want
			if (userChoice.equals("1")) {
				seeInfo(name, pass);
				loginRights(name, pass);
				break;
			} else if (userChoice.equals("2")) {
				System.out.print("Enter you new username: ");
				String newname = in.nextLine();
				System.out.println("Renaming Account Please Wait..");
				Thread.sleep(500);
				renameAcc(name, pass, newname);
				System.out.println("Account Renamed Successfully");
				System.out.print("\nEnter any text to continue");
				String passer = in.nextLine();
				Thread.sleep(500);
				clrscr();
				loginRights(newname, pass);
				break;
			} else if (userChoice.equals("3")) {
				while (true) {
					Console console = System.console();
					String newpass = new String(console.readPassword("Enter your New Password: "));
					String newpassVerify = new String(console.readPassword("Enter again: "));
					if (newpass.equals(newpassVerify)) {
						newpass = getSHA512(newpass);
						System.out.println("Changing Account Password..");
						Thread.sleep(500);
						changePass(name, pass, newpass);
						System.out.println("Changed Successfully");
						System.out.print("\nPlease enter any text to continue");
						String passer = in.nextLine();
						Thread.sleep(500);
						clrscr();
						loginRights(name, newpass);
						break;
					} else {
						System.out.println("Passwords do not match, try again");
					}
				loginRights(name, pass);
				break;
				}
			} else if (userChoice.equals("4")) {
				System.out.println("Deleting Account...");
				Thread.sleep(500);
				System.out.print("Account Deleted, you will be redirected to main prompt in 3 seconds");
				Thread.sleep(3000);
				delAcc(name, pass);
				clrscr();
				mainPrompt();
				break;
			} else if (userChoice.equals("5")) {
				System.out.println("Logging out...");
				Thread.sleep(3000);
				clrscr();
				mainPrompt();
				break;
			}
			
		}
	}
	
	public static void login() throws SQLException, InterruptedException {
		Console console = System.console();
		System.out.println("Please Login");
		String uname = null;
		String upass = null;
		while (true) {
			System.out.print("Enter Username: ");
			uname = in.nextLine();
			upass = new String(console.readPassword("Enter Password: "));
				if( (uname.equals(selectUserFromDB(uname, getSHA512(upass))) && (getSHA512(upass).equals(selectPassFromDB(uname, getSHA512(upass))))) ) {
					System.out.println("Logging In...");
					Thread.sleep(500);
					clrscr();
					loginRights(uname, getSHA512(upass));
					break;
				} else {
					System.out.println("Username doesn't match with the password or The account is still not created");
				}
		}
	}


	
	public static void clrscr(){
	    //Clears Screen in java
	    try {
	        if (System.getProperty("os.name").contains("Windows")) {
	            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
	    	    System.out.print("\033[H\033[2J");  
	        	System.out.flush();
	        }
	    } catch (IOException | InterruptedException ex) {}
	}
}
