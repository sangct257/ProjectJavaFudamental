package ra.yourprojectname.until;

import java.sql.*;

public class DBUtility {
    public static Connection openConnection() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            try {
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/duanjavafudamental","postgres","123456");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public static void closeConnection(ResultSet rs, Statement stmt, Connection connection) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
