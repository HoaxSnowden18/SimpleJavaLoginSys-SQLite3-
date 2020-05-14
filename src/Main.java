import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws InterruptedException, SQLException{
		LoginSys.createDB();
		LoginSys.createTable();
		LoginSys.mainPrompt();
	}
}
