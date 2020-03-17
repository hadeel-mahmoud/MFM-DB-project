import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class DBConn {

	private static Connection con;
	private static DBConn dbc = null;

	private DBConn(String URL, String port, String dbName, String dbUsername, String dbPassword)
			throws ClassNotFoundException, SQLException {

		String dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName + "?verifyServerCertificate=false";
		Properties p = new Properties();
		p.setProperty("user", dbUsername);
		p.setProperty("password", dbPassword);
		p.setProperty("useSSL", "false");
		p.setProperty("autoReconnect", "true");

		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(dbURL, p);
	}

	public static Connection getInstances(String URL, String port, String dbName, String dbUsername, String dbPassword)
			throws Exception {

		if (dbc == null) {
			dbc = new DBConn(URL, port, dbName, dbUsername, dbPassword);
		}

		return con;

	}

}