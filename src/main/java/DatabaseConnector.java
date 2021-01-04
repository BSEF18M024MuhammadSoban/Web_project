import java.sql.*;

public class DatabaseConnector {
    private final String MYSQL_SERVER = "jdbc:mysql://localhost:3306/";
    private final String dbDriver = "com.mysql.jdbc.Driver";
    private final String MYSQL_USERNAME = "root";
    private final String MYSQL_PASSWORD = "";
    private final String MYSQL_DATABASE = "teammanagement";
    private Connection conn;
    public DatabaseConnector()
            throws SQLException, ClassNotFoundException
    {
        Class.forName(dbDriver);
        conn = DriverManager.getConnection(MYSQL_SERVER+MYSQL_DATABASE, MYSQL_USERNAME, MYSQL_PASSWORD);
    }

    public ResultSet getData(String statement, Object... parameters) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(statement);
        for (int i = 0; i<parameters.length; i++) {
            stat.setObject(i+1, parameters[i]);
        }
        return stat.executeQuery();
    }

    public int execute(String statement, Object... parameters) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(statement);
        for (int i = 0; i < parameters.length; i++) {
            stat.setObject(i + 1, parameters[i]);
        }
        return stat.executeUpdate();
    }
}