import java.sql.*;

public class DatabaseConnector {
    private static String MYSQL_SERVER;
    private static String dbDriver = "com.mysql.jdbc.Driver";
    private static String MYSQL_USERNAME;
    private static String MYSQL_PASSWORD;
    private static String MYSQL_DATABASE;
    private Connection conn;
    public static void initDatabaseStuff(String host, String uname, String pass, String db)
    {
        MYSQL_SERVER = host;
        MYSQL_USERNAME = uname;
        MYSQL_PASSWORD = pass;
        MYSQL_DATABASE = db;
    }
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
        System.out.println(statement);
        PreparedStatement stat = conn.prepareStatement(statement);
        System.out.println(stat);
        for (int i = 0; i < parameters.length; i++) {
            stat.setObject(i + 1, parameters[i]);
        }
        return stat.executeUpdate();
    }
}