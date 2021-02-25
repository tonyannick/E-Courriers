package fileManager;

import java.io.*;
import java.nio.charset.Charset;
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
    public static String copyright;
    public static String alfresscoDeletedFileUrl;
    public static Map<String,String> mapDossiersDirectionDansAlfresco = new HashMap<>();
    public static Map<String,String> mapTitreDesPages = new HashMap<>();
    public static Map<String,String> mapMessageApplication = new HashMap<>();

    public static String lireLeFichierDuMotSecret(String nomFichier){
        return chargerUnFichierDeProprietes(nomFichier).getProperty("cle_cryptage_motdepasse");
    }

    public static void LireLeFichierDeConnexionAlaBaseDeDonnees(String nomFichier){
        identifiantUser = chargerUnFichierDeProprietes(nomFichier).getProperty("username");
        motDePasseUser = chargerUnFichierDeProprietes(nomFichier).getProperty("password");
        nomBaseDeDonnees = chargerUnFichierDeProprietes(nomFichier).getProperty("databaseName");
        urlBaseDeDonnees = chargerUnFichierDeProprietes(nomFichier).getProperty("urlDatabase");
    }

    public static void lireLeFichierDeProprietesDAlfresco(String nomFichier){
        alfrescoTicketUrl = chargerUnFichierDeProprietes(nomFichier).getProperty("alfrescoticketurl");
        alfrescoUploadFileUrl = chargerUnFichierDeProprietes(nomFichier).getProperty("alfrescoUploadFileUrl");
        alfresscoDownloadFileUrl = chargerUnFichierDeProprietes(nomFichier).getProperty("alfresscoDownloadFileUrl");
    }

    public static void trouverLesDossiersDeLaDirectionDansAlfresco(String nomFichier,String nomDirectionUser){
        nomDirectionUser = nomDirectionUser.toLowerCase();
        if(nomDirectionUser.equalsIgnoreCase("sécrétariat générale adjoint")){
            nomDirectionUser = "sga";
        }
        if(nomDirectionUser.equalsIgnoreCase("sécrétariat générale")){
            nomDirectionUser = "sg";
        }
        mapDossiersDirectionDansAlfresco.clear();
        mapDossiersDirectionDansAlfresco.put("courrier_"+nomDirectionUser,chargerUnFichierDeProprietes(nomFichier).getProperty("courrier_"+nomDirectionUser));
        mapDossiersDirectionDansAlfresco.put("annexe_"+nomDirectionUser,chargerUnFichierDeProprietes(nomFichier).getProperty("annexe_"+nomDirectionUser));
        mapDossiersDirectionDansAlfresco.put("discussion_"+nomDirectionUser,chargerUnFichierDeProprietes(nomFichier).getProperty("discussion_"+nomDirectionUser));
        reponseCourrierDossier = chargerUnFichierDeProprietes(nomFichier).getProperty("reponses_courriers");
        dossierPhotos = chargerUnFichierDeProprietes(nomFichier).getProperty("photos");

    }

    public static void lireLeFichierDuCopyright(String nomFichier){
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(new InputStreamReader(inputStream, Charset.forName(System.getProperty("file.encoding"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        copyright = properties.getProperty("copyright");
    }

    public static void lireLeFichierDesTitresDesPages(String nomFichier){
        mapTitreDesPages.put("mesTaches",chargerUnFichierDeProprietes(nomFichier).getProperty("mesTaches"));
        mapTitreDesPages.put("tachesQueVousAvezCrees",chargerUnFichierDeProprietes(nomFichier).getProperty("tachesQueVousAvezCrees"));
        mapTitreDesPages.put("mesCourriersEnregistres",chargerUnFichierDeProprietes(nomFichier).getProperty("mesCourriersEnregistres"));
        mapTitreDesPages.put("tableauDeBord",chargerUnFichierDeProprietes(nomFichier).getProperty("tableauDeBord"));
        mapTitreDesPages.put("activites",chargerUnFichierDeProprietes(nomFichier).getProperty("activites"));
        mapTitreDesPages.put("formulairenouveaucourrier",chargerUnFichierDeProprietes(nomFichier).getProperty("formulairenouveaucourrier"));
        mapTitreDesPages.put("statistiques",chargerUnFichierDeProprietes(nomFichier).getProperty("statistiques"));
        mapTitreDesPages.put("courriersEnvoyes",chargerUnFichierDeProprietes(nomFichier).getProperty("courriersEnvoyes"));
        mapTitreDesPages.put("courriersRecus",chargerUnFichierDeProprietes(nomFichier).getProperty("courriersRecus"));
        mapTitreDesPages.put("favoris",chargerUnFichierDeProprietes(nomFichier).getProperty("favoris"));
        mapTitreDesPages.put("archives",chargerUnFichierDeProprietes(nomFichier).getProperty("archives"));
        mapTitreDesPages.put("mesDossiers",chargerUnFichierDeProprietes(nomFichier).getProperty("mesDossiers"));
    }

    public static void lireFichierProprietesLog4J(String nomFichier){
        chargerUnFichierDeProprietes(nomFichier).getProperty("mesDossiers");
    }

    public static void lireLeFichierDesMessages(String nomFichier,String parametre){
        mapMessageApplication.put(parametre,chargerUnFichierDeProprietes(nomFichier).getProperty("indicationSuppressionDossier"));
    }

    private static Properties chargerUnFichierDeProprietes(String nomFichier){
        Properties properties = new Properties();
        InputStream inputStream = PropertiesFilesReader.class.getClassLoader().getResourceAsStream(nomFichier);
        try {
            properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
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
