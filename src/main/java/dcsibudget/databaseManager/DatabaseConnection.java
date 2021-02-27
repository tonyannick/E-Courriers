package dcsibudget.databaseManager;


import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection connect;
    private static Logger DatabaseConnectionLogger = LoggerCreator.creerUnLog("DatabaseConnection");

    public static Connection getConnexion(){
        //String urlDatabase = "jdbc:mysql://41.159.133.101:3306";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            PropertiesFilesReader.LireLeFichierDeConnexionAlaBaseDeDonnees("basededonnees.properties");
            //connect = DriverManager.getConnection(urlDatabase+"/"+databaseName,username,password);
            connect = DriverManager.getConnection(PropertiesFilesReader.urlBaseDeDonnees+"/"+PropertiesFilesReader.nomBaseDeDonnees,PropertiesFilesReader.identifiantUser,
                    PropertiesFilesReader.motDePasseUser);
            DatabaseMetaData databaseMetaData  = connect.getMetaData();
            int maxTimeOut = connect.getNetworkTimeout();
            int maxConnection = databaseMetaData.getMaxConnections();

           /* System.out.println("maxConnection = " + maxConnection);
            System.out.println("maxTimeOut = " + maxTimeOut);*/
        } catch (Exception e) {
            LoggerCreator.definirMessageErreur(DatabaseConnectionLogger,"Erreur sql : "+ e);
            e.printStackTrace();
        }

        return connect;
    }
}
