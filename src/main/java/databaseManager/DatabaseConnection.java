package databaseManager;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection connect;

    public static Connection getConnexion(){
        String databaseName = "e_courrier";
        String username = "root";
        String password = "";
        String urlDatabase = "jdbc:mysql://localhost:3306";
        //String urlDatabase = "jdbc:mysql://41.159.133.101:3306";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection(urlDatabase+"/"+databaseName,username,password);
            DatabaseMetaData databaseMetaData  = connect.getMetaData();
            int maxTimeOut = connect.getNetworkTimeout();
            int maxConnection = databaseMetaData.getMaxConnections();

           /* System.out.println("maxConnection = " + maxConnection);
            System.out.println("maxTimeOut = " + maxTimeOut);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connect;
    }
}
