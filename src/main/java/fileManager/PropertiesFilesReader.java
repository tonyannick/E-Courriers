package fileManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesFilesReader {

    public static String identifiantUser;
    public static String motDePasseUser;
    public static String nomBaseDeDonnees;
    public static String urlBaseDeDonnees;
    public static String alfrescoUploadFileUrl;
    public static String alfrescoTicketUrl;
    public static String alfresscoDownloadFileUrl;
    public static String reponseCourrierDossier;
    public static String dossierPhotos;
    public static String alfresscoDeletedFileUrl;
    public static Map<String,String> mapDossiersDirectionDansAlfresco = new HashMap<>();

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

    public static void trouverLesDossiersDeLaDirectionDansAlfresco(String nomFichier,String nomDirectionUser){
        nomDirectionUser = nomDirectionUser.toLowerCase();
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(nomDirectionUser.equalsIgnoreCase("sécrétariat générale adjoint")){
            nomDirectionUser = "sga";
        }
        if(nomDirectionUser.equalsIgnoreCase("sécrétariat générale")){
            nomDirectionUser = "sg";
        }
        mapDossiersDirectionDansAlfresco.clear();
        mapDossiersDirectionDansAlfresco.put("courrier_"+nomDirectionUser,properties.getProperty("courrier_"+nomDirectionUser));
        mapDossiersDirectionDansAlfresco.put("annexe_"+nomDirectionUser,properties.getProperty("annexe_"+nomDirectionUser));
        mapDossiersDirectionDansAlfresco.put("discussion_"+nomDirectionUser,properties.getProperty("discussion_"+nomDirectionUser));
        reponseCourrierDossier = properties.getProperty("reponses_courriers");
        dossierPhotos = properties.getProperty("photos");

    }

    public static void ajoutDesProprietesDesDossiersDUneDirection(String nomFichier,String nomDirection){
        Properties properties = new Properties();
        properties.setProperty("property1", "value1");
        properties.setProperty("property2", "value2");
        properties.setProperty("property3", "value3");
        //InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        OutputStream output = null;
        try {
            output = new FileOutputStream(nomFichier);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
