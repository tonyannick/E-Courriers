package fileManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFilesReader {

    public static String identifiantUser;
    public static String motDePasseUser;
    public static String nomBaseDeDonnees;
    public static String urlBaseDeDonnees;
    public static String alfrescoUploadFileUrl;
    public static String alfrescoTicketUrl;
    public static String alfresscoDownloadFileUrl;
    public static String alfresscoDeletedFileUrl;


    public static String lireLeFichierDuMotSecret(String nomFichier){
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("cle_cryptage_motdepasse");
    }

    public static void LireLeFichierDeConnexionAlaBaseDeDonnees(String nomFichier){
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        identifiantUser = properties.getProperty("username");
        motDePasseUser = properties.getProperty("password");
        nomBaseDeDonnees = properties.getProperty("databaseName");
        urlBaseDeDonnees = properties.getProperty("urlDatabase");
    }


    public static void lireLeFichierDeProprietesDAlfresco(String nomFichier){
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        alfrescoTicketUrl = properties.getProperty("alfrescoticketurl");
        alfrescoUploadFileUrl = properties.getProperty("alfrescoUploadFileUrl");
        alfresscoDownloadFileUrl = properties.getProperty("alfresscoDownloadFileUrl");
    }
}
