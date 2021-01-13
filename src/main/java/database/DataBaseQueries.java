package database;

import dateAndTime.DateUtils;
import model.*;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;
import variables.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataBaseQueries {

    public static String idPersonne;
    public static String idEtablissement;
    public static String etablissementUser;
    public static String passwordUser;
    public static String emailUser;
    public static String telUser;
    public static String pseudUser;
    public static String nomUser;
    public static String prenomUser;
    public static String nomCompletUser;
    public static String directionUser;
    public static String serviceUser;
    public static String idDirectionUser;
    public static String fonctionUser;
    public static String profilUser;
    public static String typeDeUser;
    public static String photoUser;
    public static int nombreCourrierEnvoyesDuJour = 0;
    public static int nombreCourrierRecusDuJour = 0;
    public static int nombreCourrierUrgentDuJour = 0;
    public static int nombreCourrierConfidentielDuJour = 0;
    public static int nombreCourrierEnvoyesDuMois = 0;
    public static int nombreCourrierRecusDuMois = 0;
    public static int nombreCourrierRecusDeLaSemaine = 0;
    public static int nombreCourrierUrgentDeLaSemaine = 0;
    public static int nombreCourrierConfidentielDeLaSemaine = 0;
    public static int nombreCourrierEnvoyesDeLaSemaine = 0;
    public static int nombreCourrierUrgentDuMois = 0;
    public static int nombreCourrierInterneDuMois = 0;
    public static int nombreCourrierExterneDuMois = 0;
    public static int nombreCourrierPasUrgentDuMois = 0;
    public static int nombreCourrierConfidentielDuMois = 0;
    public static int nombreDeDestinataireDuCourrier= 0;
    public static int nombreDeTacheDunAgentSurCourrier= 0;
    public static int nombreDActionEnCoursDuCourrier= 0;
    public static int nombreDeCourrierDansUnDossier= 0;
    public static int nombreDeCourrierJanvier = 0;
    public static int nombreDeCourrierFevrier = 0;
    public static int nombreDeCourrierMars = 0;
    public static int nombreDeCourrierAvril = 0;
    public static int nombreDeCourrierMai = 0;
    public static int nombreDeCourrierJuin = 0;
    public static int nombreDeCourrierJuillet = 0;
    public static int nombreDeCourrierAout = 0;
    public static int nombreDeCourrierSeptembre = 0;
    public static int nombreDeCourrierOctobre = 0;
    public static int nombreDeCourrierNovembre = 0;
    public static int nombreDeCourrierDecembre = 0;
    public static int nombreDeCourrierDCSI = 0;
    public static int nombreDeCourrierDCAF = 0;
    public static int nombreDeCourrierDCRH = 0;
    public static int nombreDeCourrierDGBFIP = 0;
    public static int nombreDeCourrierTRESOR = 0;
    public static int nombreDeCourrierIGS = 0;
    public static int nombreDeCourrierAJE = 0;
    public static int nombreDeCourrierCPPF = 0;
    public static int nombreDeCourrierSGA = 0;
    public static int nombreDeCourrierSG = 0;
    public static int nombreDeCourrierCabinetMinistre = 0;
    public static int nombreDeCourrierCabinetMinistreAdjoint = 0;
    public static int nombreDeCourrierMinistre = 0;
    public static int nombreDeCourrierMinistreDelegue = 0;
    public static String typeDemetteur;
    public static String idEmetteur;
    public static String ministereEmetteur;
    public static String directeurEmetteur;
    public static String fonctionEmetteur;
    public static String telEmetteurEtablissement;
    public static String telEmetteurPersonne;
    public static String emailEmetteurEtablissement;
    public static String emailEmetteurPersonne;
    public static String adresseEmetteurEtablissement;
    public static String nomEtPrenomEmetteurPersonne;
    public static String dateDeReception;
    public static String dateDEnregistrement;
    public static String objetCourrier;
    public static String referenceCourrier;
    public static String commentairesCourrier;
    public static String typeCourrier;
    public static String prioriteCourrier;
    public static String confidentiel;
    public static String dossierAlfresco;
    public static String accuseDeReception;
    public static String referenceInterne;
    public static String heureDeReception;
    public static String heureDEnregistrement;
    public static String idMinistereDuBudget = "1";
    public static boolean isResponsable = false;
    public static boolean isSecretaire = false;
    private static Date premierDuMois;
    private static Date dernierDuMois;
    public static List<Courrier> listeCourriersRecus = new ArrayList<>();
    public static List<Courrier> listeCourriersEnvoyes = new ArrayList<>();
    private static List<Statistiques> listeNombreDeCourrierParType = new ArrayList<>();
    public static Map<String, Integer> mapNombreCourrierParType = new HashMap<>();
    public static Map<String, Integer> mapNombreCourrierParTypeDuMoisEnCours = new HashMap<>();

    /****Recuperer les infos de session***/
    public static void recupererInfosDeSession(){

        HttpSession session = SessionUtils.getSession();
        String fonction = (String) session.getAttribute("fonctionUser");

        switch (fonction) {
            case FonctionsUtilisateurs.agent :
                isResponsable = false;
                break;
            case FonctionsUtilisateurs.chargeDEtudes :
                isResponsable = false;
                break;
            case FonctionsUtilisateurs.chefDeService :
                isResponsable = false;
                break;
            case FonctionsUtilisateurs.conseiller :
                isResponsable = false;
                break;
            case FonctionsUtilisateurs.directeur :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.directeurCabinet :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.directeurGeneral :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.directeurGeneralAdjoint :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.ministreDelegue :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.ministre :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.secretaire :
                isResponsable = false;
                isSecretaire = true;
                break;
            case FonctionsUtilisateurs.secretaireParticuliere :
                isResponsable = false;
                isSecretaire = true;
                break;
            case FonctionsUtilisateurs.secretaireGeneral :
                isResponsable = true;
                break;
            case FonctionsUtilisateurs.secretaireGeneralAdjoint :
                isResponsable = true;
                break;
            default:
                isResponsable = false;
        }


    }

    /***Fonction de verification de connexion d'un utilisateur***/
    public static boolean verifierUserLogin(String login, String motDePasse){
        boolean connected = false;
        Connection connection =  DatabaseManager.getConnexion();
        String requeteLoginSQL = "select * from `personne` inner join direction on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction inner join profil on personne.id_profil = profil.id_profil where pseudo = '"+login+"' and mot_de_passe = '"+motDePasse+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteLoginSQL);
            if(resultSet.next()){
                idPersonne = resultSet.getString("id_personne");
                idEtablissement = resultSet.getString("id_etablissement");
                nomCompletUser = resultSet.getString("nom") +" "+resultSet.getString("prenom") ;
                directionUser = resultSet.getString("nom_direction");
                fonctionUser = resultSet.getString("titre_fonction");
                profilUser = resultSet.getString("titre_profil");
                idDirectionUser = resultSet.getString("id_direction");
                connected = true;
            }else{
                connected = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return connected;
    }

    /***Fonction qui recupere les infos d'un utilisateur***/
    public static void recupererInfosUsersParSonId(String idPersonne){
        Connection connection =  DatabaseManager.getConnexion();
        String requeteLoginSQL = "select * from `personne` inner join direction on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction inner join etablissement on etablissement.id_etablissement = personne.id_etablissement inner join profil on personne.id_profil = profil.id_profil inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne where id_personne = '"+idPersonne+"' ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteLoginSQL);
            if(resultSet.next()){
                idPersonne = resultSet.getString("id_personne");
                idEtablissement = resultSet.getString("id_etablissement");
                etablissementUser = resultSet.getString("nom_etablissement");
                nomUser = resultSet.getString("nom");
                prenomUser = resultSet.getString("prenom");
                telUser = resultSet.getString("tel");
                emailUser = resultSet.getString("mail");
                pseudUser = resultSet.getString("pseudo");
                passwordUser = resultSet.getString("mot_de_passe");
                directionUser = resultSet.getString("nom_direction");
                serviceUser = resultSet.getString("service");
                fonctionUser = resultSet.getString("titre_fonction");
                profilUser = resultSet.getString("titre_profil");
                typeDeUser = resultSet.getString("titre_type_de_personne");
                photoUser = resultSet.getString("id_alfresco_photo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /***Fonction qui recupere les infos d'un utilisateur***/
    public static List<User> recupererLaListeDesUsers(){
        List<User> usersList = new ArrayList<>();
        Connection connection =  DatabaseManager.getConnexion();
        String requeteLoginSQL = "select * from `personne` inner join direction on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction inner join etablissement on etablissement.id_etablissement = personne.id_etablissement inner join profil on personne.id_profil = profil.id_profil inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne order by personne.id_personne ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteLoginSQL);
            while(resultSet.next()){
                usersList.add(new User(
                        resultSet.getString("id_personne"),
                        resultSet.getString("pseudo"),
                        resultSet.getString("nom") +" "+resultSet.getString("prenom"),
                        resultSet.getString("mail"),
                        resultSet.getString("tel"),
                        resultSet.getString("mot_de_passe"),
                        resultSet.getString("nom_direction"),
                        resultSet.getString("titre_fonction"),
                        resultSet.getString("titre_profil"),
                        resultSet.getString("titre_type_de_personne"),
                        resultSet.getString("etat_du_compte")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return usersList;
    }

    /***Fonction de récuperation des statistiques de la page d'accueil***/
    public static void recupererLesStatistiquesPourLaPageDAccueil() {

        List<Statistiques> listeTypeDeCourrierEnvoyes = new ArrayList<>();
        List<Statistiques> listeTypeDeCourrierRecus = new ArrayList<>();
        listeTypeDeCourrierEnvoyes.clear();
        listeTypeDeCourrierRecus.clear();
        listeCourriersEnvoyes.clear();
        listeCourriersRecus.clear();
        int tempCourrierUrgentRecu = 0;
        int tempCourrierUrgentEnvoye =0;
        int tempCourrierConfidentielRecu = 0;
        int tempCourrierConfidentielEnvoye =0;

        Connection connectionCourrierRecus =  DatabaseManager.getConnexion();
        Connection connectionCourrierEnvoyes =  DatabaseManager.getConnexion();
        Connection connectionCourrierRecusParType =  DatabaseManager.getConnexion();
        Connection connectionCourrierEnvoyesParType =  DatabaseManager.getConnexion();

        String nombreDeCourrierRecusSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier =	courrier.fk_type_courrier where direction.id_direction = '"+idDirectionUser+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        String nombreDeCourrierEnvoyesSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirectionUser+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        String nombreDeCourrierEnvoyesParTypeSQL = "select titre_type_courrier, count(*) from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirectionUser+"' and etat='"+EtatCourrier.courrierEnvoye+"' group by type_courrier.id_type_courrier order by courrier.id_courrier  desc limit 6";
        String nombreDeCourrierRecusParTypeSQL = "select titre_type_courrier, count(*) from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirectionUser+"' group by type_courrier.id_type_courrier order by courrier.id_courrier  desc limit 6";

        nombreCourrierRecusDuJour = 0;
        nombreCourrierEnvoyesDuJour = 0;
        nombreCourrierUrgentDuJour = 0;
        nombreCourrierConfidentielDuJour = 0;
        nombreCourrierUrgentDeLaSemaine = 0;
        nombreCourrierConfidentielDeLaSemaine = 0;
        nombreCourrierEnvoyesDuMois= 0;
        nombreCourrierRecusDuMois = 0;
        nombreCourrierRecusDeLaSemaine = 0;
        nombreCourrierEnvoyesDeLaSemaine = 0;

        int nombreCourrierUrgentRecusDuMois = 0;
        int nombreCourrierPasUrgentRecusDuMois = 0;
        int nombreCourrierUrgentEnvoyesDuMois  = 0;
        int nombreCourrierUrgentEnvoyesDeLaSemaine  = 0;
        int nombreCourrierUrgentRecusDeLaSemaine  = 0;
        int nombreCourrierConfidentielEnvoyesDeLaSemaine  = 0;
        int nombreCourrierConfidentielRecusDeLaSemaine = 0;
        int nombreCourrierPasUrgentEnvoyesDuMois = 0;
        int nombreCourrierConfidentielEnvoyesDuMois  = 0;
        int nombreCourrierConfidentielRecusDuMois = 0;
        int nombreCourrierInterneRecusDuMois = 0;
        int nombreCourrierExterneRecusDuMois = 0;
        int nombreCourrierInterneEnvoyesDuMois = 0;
        int nombreCourrierExterneEnvoyesDuMois = 0;


        Calendar firstMonthOfCalendar = Calendar.getInstance(Locale.FRANCE);
        Calendar lastMonthOfCalendar = Calendar.getInstance(Locale.FRANCE);
        /*Dernier jour du mois en cours*/
        lastMonthOfCalendar.set(Calendar.DAY_OF_MONTH, lastMonthOfCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        /*Premier jour du mois en cours*/
        firstMonthOfCalendar.set(Calendar.DAY_OF_MONTH, firstMonthOfCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        premierDuMois = firstMonthOfCalendar.getTime();
        dernierDuMois = lastMonthOfCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        /***Récupération des jours de la semaine***/
        DateUtils.recupererLePremierEtLeDernierJourDelaSemaine();

        String dateDuJour = DateUtils.recupererSimpleDateEnCours();
        ResultSet resultSetCourriersRecus = null;
        ResultSet resultSetCourriersEnvoyes = null;
        ResultSet resultSetCourriersRecusParType = null;
        ResultSet resultSetCourriersEnvoyesParType = null;

        try {
            resultSetCourriersRecus = connectionCourrierRecus.createStatement().executeQuery(nombreDeCourrierRecusSQL);
            resultSetCourriersEnvoyes = connectionCourrierEnvoyes.createStatement().executeQuery(nombreDeCourrierEnvoyesSQL);
            resultSetCourriersEnvoyesParType = connectionCourrierEnvoyesParType.createStatement().executeQuery(nombreDeCourrierEnvoyesParTypeSQL);
            resultSetCourriersRecusParType = connectionCourrierRecusParType.createStatement().executeQuery(nombreDeCourrierRecusParTypeSQL);

            while (resultSetCourriersRecusParType.next()) {
                listeTypeDeCourrierRecus.add(new Statistiques(
                        resultSetCourriersRecusParType.getString("titre_type_courrier"),
                        resultSetCourriersRecusParType.getString("count(*)")));
            }
            while (resultSetCourriersEnvoyesParType.next()) {
                listeTypeDeCourrierEnvoyes.add(new Statistiques(
                        resultSetCourriersEnvoyesParType.getString("titre_type_courrier"),
                        resultSetCourriersEnvoyesParType.getString("count(*)")));
            }

            while (resultSetCourriersRecus.next()) {
                listeCourriersRecus.add(new Courrier(
                        resultSetCourriersRecus.getString("date_enregistrement"),
                        resultSetCourriersRecus.getString("priorite"),
                        resultSetCourriersRecus.getString("confidentiel"),
                        resultSetCourriersRecus.getString("id_courrier"),
                        resultSetCourriersRecus.getString("genre")));
            }

            while (resultSetCourriersEnvoyes.next()) {
                listeCourriersEnvoyes.add(new Courrier(
                        resultSetCourriersEnvoyes.getString("date_enregistrement"),
                        resultSetCourriersEnvoyes.getString("priorite"),
                        resultSetCourriersEnvoyes.getString("confidentiel"),
                        resultSetCourriersEnvoyes.getString("id_courrier"),
                        resultSetCourriersEnvoyes.getString("genre")));
            }

            listeNombreDeCourrierParType =  Stream.concat(  listeTypeDeCourrierRecus.stream(), listeTypeDeCourrierEnvoyes.stream()).collect(Collectors.toList());

            for(Statistiques myStat : listeNombreDeCourrierParType ){
                String key = myStat.getTypeDeCourrier();
                int valueOfKey = mapNombreCourrierParType.containsKey(key) ? mapNombreCourrierParType.get(key) : 0;
                valueOfKey += Integer.parseInt(myStat.getNombreTypeDeCourrier());
                mapNombreCourrierParType.put(key,valueOfKey);
            }

            if(listeCourriersRecus.size() > 0){

                for (int i = 0; i < listeCourriersRecus.size(); i++) {
                    if (listeCourriersRecus.get(i).getDateDEnregistrement().equals(dateDuJour)) {
                        nombreCourrierRecusDuJour++;
                        if (listeCourriersRecus.get(i).getPrioriteCourrier().equalsIgnoreCase("Urgent")) {
                            tempCourrierUrgentRecu++;
                        }
                        if (listeCourriersRecus.get(i).getPrioriteCourrier().equalsIgnoreCase("Oui")) {
                            tempCourrierConfidentielRecu++;
                        }
                    }

                    try {
                        Date date1 = sdf.parse(listeCourriersRecus.get(i).getDateDEnregistrement());
                        if (date1.after(premierDuMois) && date1.before(dernierDuMois)) {
                            nombreCourrierRecusDuMois++;
                            if(listeCourriersRecus.get(i).getPrioriteCourrier().equals("Normal")){
                                nombreCourrierPasUrgentRecusDuMois++;
                            }else if(listeCourriersRecus.get(i).getPrioriteCourrier().equals("Urgent")){
                                nombreCourrierUrgentRecusDuMois++;
                            }
                            if(listeCourriersRecus.get(i).getConfidentiel().equals("Oui")){
                                nombreCourrierConfidentielRecusDuMois++;
                            }

                            if(listeCourriersRecus.get(i).getGenreCourrier() != null){
                                if(listeCourriersRecus.get(i).getGenreCourrier().equalsIgnoreCase(GenreDeCourrier.courrierInterne)){
                                    nombreCourrierInterneRecusDuMois++;
                                }
                                if(listeCourriersRecus.get(i).getGenreCourrier().equalsIgnoreCase(GenreDeCourrier.courrierExterne)){
                                    nombreCourrierExterneRecusDuMois++;
                                }
                            }

                        }


                        if (date1.after(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.premierJourDeLaSemaineFormatUS)) &&
                                date1.before(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.dernierJourDeLaSemaineFormatUS))) {
                            nombreCourrierRecusDeLaSemaine++;
                            if(listeCourriersRecus.get(i).getPrioriteCourrier().equals("Urgent")){
                                nombreCourrierUrgentRecusDeLaSemaine++;
                            }
                            if(listeCourriersRecus.get(i).getConfidentiel().equals("Oui")){
                                nombreCourrierConfidentielRecusDeLaSemaine++;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(listeCourriersEnvoyes.size() > 0) {
                for (int i = 0; i < listeCourriersEnvoyes.size(); i++) {
                    if (listeCourriersEnvoyes.get(i).getDateDEnregistrement().equals(dateDuJour)) {
                        nombreCourrierEnvoyesDuJour++;
                        if (listeCourriersEnvoyes.get(i).getPrioriteCourrier().equalsIgnoreCase("Oui")) {
                            tempCourrierUrgentEnvoye++;
                        }
                        if (listeCourriersEnvoyes.get(i).getPrioriteCourrier().equalsIgnoreCase("Oui")) {
                            tempCourrierConfidentielEnvoye++;
                        }
                    }

                    try {
                        Date date1 = sdf.parse(listeCourriersEnvoyes.get(i).getDateDEnregistrement());
                        if (date1.after(premierDuMois) && date1.before(dernierDuMois)) {
                            nombreCourrierEnvoyesDuMois++;
                            if(listeCourriersEnvoyes.get(i).getPrioriteCourrier().equals("Normal")){
                                nombreCourrierPasUrgentEnvoyesDuMois++;
                            }else if(listeCourriersEnvoyes.get(i).getPrioriteCourrier().equals("Urgent")){
                                nombreCourrierUrgentEnvoyesDuMois++;
                            }

                            if(listeCourriersEnvoyes.get(i).getConfidentiel().equals("Oui")){
                                nombreCourrierConfidentielEnvoyesDuMois++;
                            }

                            if(listeCourriersEnvoyes.get(i).getGenreCourrier() != null){
                                if(listeCourriersEnvoyes.get(i).getGenreCourrier().equalsIgnoreCase(GenreDeCourrier.courrierInterne)){
                                    nombreCourrierInterneEnvoyesDuMois++;
                                }
                                if(listeCourriersEnvoyes.get(i).getGenreCourrier().equalsIgnoreCase(GenreDeCourrier.courrierExterne)){
                                    nombreCourrierExterneEnvoyesDuMois++;
                                }
                            }

                        }

                        if (date1.after(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.premierJourDeLaSemaineFormatUS)) &&
                                date1.before(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.dernierJourDeLaSemaineFormatUS))) {
                            nombreCourrierEnvoyesDeLaSemaine++;
                            if(listeCourriersEnvoyes.get(i).getPrioriteCourrier().equals("Urgent")){
                                nombreCourrierUrgentEnvoyesDeLaSemaine++;
                            }
                            if(listeCourriersEnvoyes.get(i).getConfidentiel().equals("Oui")){
                                nombreCourrierConfidentielEnvoyesDeLaSemaine++;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            nombreCourrierUrgentDuMois = nombreCourrierUrgentRecusDuMois + nombreCourrierUrgentEnvoyesDuMois;
            nombreCourrierPasUrgentDuMois = nombreCourrierPasUrgentRecusDuMois + nombreCourrierPasUrgentEnvoyesDuMois;
            nombreCourrierUrgentDuJour = tempCourrierUrgentEnvoye + tempCourrierUrgentRecu;
            nombreCourrierConfidentielDuJour = tempCourrierConfidentielEnvoye + tempCourrierConfidentielRecu;
            nombreCourrierConfidentielDuMois = nombreCourrierConfidentielEnvoyesDuMois + nombreCourrierConfidentielRecusDuMois;
            nombreCourrierUrgentDeLaSemaine = nombreCourrierUrgentEnvoyesDeLaSemaine + nombreCourrierUrgentRecusDeLaSemaine;
            nombreCourrierConfidentielDeLaSemaine = nombreCourrierConfidentielEnvoyesDeLaSemaine + nombreCourrierConfidentielRecusDeLaSemaine;
            nombreCourrierInterneDuMois = nombreCourrierInterneEnvoyesDuMois + nombreCourrierInterneRecusDuMois;
            nombreCourrierExterneDuMois = nombreCourrierExterneEnvoyesDuMois + nombreCourrierExterneRecusDuMois;


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSetCourriersRecus != null) {
                try {
                    resultSetCourriersRecus.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connectionCourrierRecus != null) {
                try {
                    connectionCourrierRecus.close();
                } catch (SQLException e) { /* ignored */}
            }

        }
    }


    public static void calculerLesStatistiquesDesCourriersTraitesParTypesDeCourrierDuMoisEnCours(String idDirection){

        List<Statistiques> listeTypeDeCourrierEnvoyes = new ArrayList<>();
        List<Statistiques> listeTypeDeCourrierRecus = new ArrayList<>();
        List<Statistiques> listeNombreDeCourrierParType = new ArrayList<>();

        String nombreDeCourrierEnvoyesParTypeSQL = "select courrier.date_enregistrement, titre_type_courrier, count(*) from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and etat='"+EtatCourrier.courrierEnvoye+"' group by type_courrier.id_type_courrier order by courrier.id_courrier ";
        String nombreDeCourrierRecusParTypeSQL = "select courrier.date_enregistrement, titre_type_courrier, count(*) from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and etat='"+EtatCourrier.courrierEnvoye+"' group by type_courrier.id_type_courrier order by courrier.id_courrier";

        Connection connectionCourrierRecusParType =  DatabaseManager.getConnexion();
        Connection connectionCourrierEnvoyesParType =  DatabaseManager.getConnexion();
        ResultSet resultSetCourriersEnvoyesParType = null;
        ResultSet resultSetCourriersRecusParType = null;
        try {
            resultSetCourriersEnvoyesParType = connectionCourrierEnvoyesParType.createStatement().executeQuery(nombreDeCourrierEnvoyesParTypeSQL);
            resultSetCourriersRecusParType = connectionCourrierRecusParType.createStatement().executeQuery(nombreDeCourrierRecusParTypeSQL);

            while (resultSetCourriersRecusParType.next()) {
                listeTypeDeCourrierRecus.add(new Statistiques(
                        resultSetCourriersRecusParType.getString("titre_type_courrier"),
                        resultSetCourriersRecusParType.getString("count(*)"),
                        resultSetCourriersRecusParType.getString("courrier.date_enregistrement")));
            }
            while (resultSetCourriersEnvoyesParType.next()) {
                listeTypeDeCourrierEnvoyes.add(new Statistiques(
                        resultSetCourriersEnvoyesParType.getString("titre_type_courrier"),
                        resultSetCourriersEnvoyesParType.getString("count(*)"),
                        resultSetCourriersEnvoyesParType.getString("courrier.date_enregistrement")));
            }

            listeNombreDeCourrierParType =  Stream.concat(listeTypeDeCourrierRecus.stream(), listeTypeDeCourrierEnvoyes.stream()).collect(Collectors.toList());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int a = 0; a <  listeNombreDeCourrierParType.size(); a++) {
                try {
                    Date date1 = null;
                    date1 = sdf.parse(listeNombreDeCourrierParType.get(a).getDateDEnregistrement());
                    if (date1.after(premierDuMois) && date1.before(dernierDuMois)) {

                    }else{
                        listeNombreDeCourrierParType.remove(a);
                    }
                } catch (ParseException e) {
                        e.printStackTrace();
                }
            } 


            for(Statistiques myStat : listeNombreDeCourrierParType ){
                String key = myStat.getTypeDeCourrier();
                int valueOfKey = mapNombreCourrierParTypeDuMoisEnCours.containsKey(key) ? mapNombreCourrierParTypeDuMoisEnCours.get(key) : 0;
                valueOfKey += Integer.parseInt(myStat.getNombreTypeDeCourrier());
                mapNombreCourrierParTypeDuMoisEnCours.put(key,valueOfKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public static void calculerLesStatistiquesDesCourriersTraitesParTypesDEmetteur(String idDirection){

        List<Statistiques> listeTypeDeCourrierEnvoyes = new ArrayList<>();
        List<Statistiques> listeTypeDeCourrierRecus = new ArrayList<>();
        List<Statistiques> listeNombreDeCourrierParTypeDEmetteur = new ArrayList<>();

        String nombreDeCourrierEnvoyesParTypeSQL = "select courrier.date_enregistrement, titre_type_courrier, count(*) from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and etat='"+EtatCourrier.courrierEnvoye+"' group by type_courrier.id_type_courrier order by courrier.id_courrier ";
        String nombreDeCourrierRecusParTypeSQL = "select courrier.date_enregistrement, titre_type_courrier, count(*) from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' group by type_courrier.id_type_courrier order by courrier.id_courrier";

        Connection connectionCourrierRecusParType =  DatabaseManager.getConnexion();
        Connection connectionCourrierEnvoyesParType =  DatabaseManager.getConnexion();
        ResultSet resultSetCourriersEnvoyesParType = null;
        ResultSet resultSetCourriersRecusParType = null;
        try {
            resultSetCourriersEnvoyesParType = connectionCourrierEnvoyesParType.createStatement().executeQuery(nombreDeCourrierEnvoyesParTypeSQL);
            resultSetCourriersRecusParType = connectionCourrierRecusParType.createStatement().executeQuery(nombreDeCourrierRecusParTypeSQL);

            while (resultSetCourriersRecusParType.next()) {
                listeTypeDeCourrierRecus.add(new Statistiques(
                        resultSetCourriersRecusParType.getString("titre_type_courrier"),
                        resultSetCourriersRecusParType.getString("count(*)"),
                        resultSetCourriersRecusParType.getString("courrier.date_enregistrement")));
            }
            while (resultSetCourriersEnvoyesParType.next()) {
                listeTypeDeCourrierEnvoyes.add(new Statistiques(
                        resultSetCourriersEnvoyesParType.getString("titre_type_courrier"),
                        resultSetCourriersEnvoyesParType.getString("count(*)"),
                        resultSetCourriersEnvoyesParType.getString("courrier.date_enregistrement")));
            }

            listeNombreDeCourrierParType =  Stream.concat(listeTypeDeCourrierRecus.stream(), listeTypeDeCourrierEnvoyes.stream()).collect(Collectors.toList());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int a = 0; a <  listeNombreDeCourrierParType.size(); a++) {

                try {
                    Date date1 = null;
                    date1 = sdf.parse(listeNombreDeCourrierParType.get(a).getDateDEnregistrement());
                    if (date1.after(premierDuMois) && date1.before(dernierDuMois)) {

                    }else{
                        listeNombreDeCourrierParType.remove(a);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            for (int a = 0; a <  listeNombreDeCourrierParType.size(); a++) {
                System.out.println("listeNombreDeCourrierParType = " + listeNombreDeCourrierParType.get(a).getTypeDeCourrier());
                System.out.println("listeNombreDeCourrierParType = " + listeNombreDeCourrierParType.get(a).getNombreTypeDeCourrier());
                System.out.println("----------------------------------------------------------------------");
            }


            System.out.println("listeNombreDeCourrierParType apres = " + listeNombreDeCourrierParType.size());

            for(Statistiques myStat : listeNombreDeCourrierParType ){
                String key = myStat.getTypeDeCourrier();
                int valueOfKey = mapNombreCourrierParTypeDuMoisEnCours.containsKey(key) ? mapNombreCourrierParTypeDuMoisEnCours.get(key) : 0;
                valueOfKey += Integer.parseInt(myStat.getNombreTypeDeCourrier());
                mapNombreCourrierParTypeDuMoisEnCours.put(key,valueOfKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /***Fonction de récuperation des disucssions ouvertes d'un user***/
    public static List<Discussion> recupererLesDiscusssionsDUnUserEnCours(String idUser){
        List<Discussion> mesDiscussions = new ArrayList<>();
        mesDiscussions.clear();
        String requeteListeDesDiscussionsEnCoursSQL = "select * from `discussion_etape` inner join `personne` on discussion_etape.id_personne = personne.id_personne inner join `correspondance_etape_courrier` on discussion_etape.id_etape = correspondance_etape_courrier.id_etape where personne.id_personne = '"+idUser+"' and etat_discussion = '"+EtatEtape.Ouvert+"' group by discussion_etape.id_etape desc limit 5;";

        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteListeDesDiscussionsEnCoursSQL);
            while(resultSet.next()){
                mesDiscussions.add(new Discussion(
                        resultSet.getString("id_discussion_etape"),
                        resultSet.getString("date_discussion"),
                        resultSet.getString("message_discussion"),
                        resultSet.getString("etat_correspondance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mesDiscussions;

    }

    /****Fonction qui récupère les noms des directions du Ministère***/
    public static List<String> recupererLaListeDesDirections(){

        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select nom_direction from direction inner join etablissement on direction.fk_etablissement = etablissement.id_etablissement where id_etablissement = '"+idMinistereDuBudget+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("nom_direction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les directions (objet) du Ministère***/
    public static List<Direction> recupererLaListeDesDirectionsDuMinistere(){

        List<Direction> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select nom_direction from direction inner join etablissement on direction.fk_etablissement = etablissement.id_etablissement where id_etablissement = '"+idMinistereDuBudget+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(new Direction(resultSet.getString("nom_direction")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /***Fonction qui recupere les agents d'une direction***/
    public static List<User> recupererLesAgentsDUneDirection(String nomDirection) {
        List<User> agentDirectionList = new ArrayList<>();
        agentDirectionList.clear();
        String idTypeDePersonne = DataBaseQueries.recupererIdTypeDePersonneParTitre(TypeDePersonne.agentDuMinistere);
        String requeteAgentSQL = "select * from `personne` inner join `direction` on personne.id_direction = direction.id_direction inner join profil on personne.id_profil = profil.id_profil inner join fonction on personne.id_fonction = fonction.id_fonction where nom_direction = '"+nomDirection+"' and fk_type_personne = '"+idTypeDePersonne+"';";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteAgentSQL);
            while ( resultSet.next()) {
                agentDirectionList.add(new User(
                        resultSet.getString("id_personne"),
                        resultSet.getString("pseudo"),
                        resultSet.getString("nom") +" "+resultSet.getString("prenom"),
                        resultSet.getString("mail"),
                        resultSet.getString("tel"),
                        resultSet.getString("nom_direction"),
                        resultSet.getString("titre_fonction"),
                        resultSet.getString("titre_profil"),
                        resultSet.getString("etat_du_compte")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  agentDirectionList;
    }

    /****Fonction qui récupère les directions du Ministère autre que celle de l'utilisateur en cours et de l'emetteur du courrier***/
    public static List<String> recupererLaListeDesAutresDirections(String nomDirection, String directeurEmetteur){

        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select nom_direction from direction inner join etablissement on direction.fk_etablissement = etablissement.id_etablissement where id_etablissement = '"+idMinistereDuBudget+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("nom_direction"));
            }
            for(int a =0; a < list.size(); a++){
                if(nomDirection.equals(list.get(a))){
                    list.remove(a);
                    break;
                }
            }
            for(int a =0; a < list.size(); a++){
                if(directeurEmetteur.equals(list.get(a))){
                    list.remove(a);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les fonctions du Ministères***/
    public static List<String> recupererLaListeDesFonctions(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select titre_fonction from fonction where type_fonction = '"+ TypeDeFonctions.interne +"' and responsable_courrier = '"+EtatCourrier.responsableCourrier+"' ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("titre_fonction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les fonctions du Ministères en fonction de la direction***/
    public static List<String> recupererLaListeDesFonctionsParDirection(String direction){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select titre_fonction from fonction inner join correspondance_direction_fonction on fonction.id_fonction = correspondance_direction_fonction.fk_fonction inner join direction " +
                " on correspondance_direction_fonction.fk_direction = direction.id_direction where type_fonction = '"+ TypeDeFonctions.interne +"' and responsable_courrier = '"+EtatCourrier.responsableCourrier+"' and nom_direction = '"+direction+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("titre_fonction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les Ministères***/
    public static List<String> recupererLaListeDesMinisteres(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select abreviation from etablissement inner join type_etablissement on etablissement.fk_type_etablissement = type_etablissement.id_type_etablissement where titre_type_etablissement = '"+ TypeDEtablissement.ministere +"' ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("abreviation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les type de courrier***/
    public static List<String> recupererLaListeDeTypesDeCourrier(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select titre_type_courrier from type_courrier order by titre_type_courrier ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("titre_type_courrier"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /****Fonction qui récupère les fonctions des agents d'une direction fonction***/
    public static List<String> recupererLaListeDesFonctionsDesAgentsParDirection(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseManager.getConnexion();
        String requete = "select titre_fonction from fonction where type_fonction = '"+ TypeDeFonctions.interne +"' and responsable_courrier = '"+EtatCourrier.pasResponsableCourrier+"';";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(resultSet.getString("titre_fonction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return list;
    }

    /***Fonction qui recuperer l'id du type de personne par son titre***/
    public static String recupererIdTypeDePersonneParTitre(String typeDePersonne) {
        String id = null;
        String recupererIdTypeDePersonneSQL = "select id_type_de_personne from `type_de_personne` where titre_type_de_personne = '" + typeDePersonne + "'";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdTypeDePersonneSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_type_de_personne");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recuperer l'id du type de courrier par son titre***/
    public static String recupererIdTypeDeCourrierParTitre(String typeDeCourrier) {
        String id = null;
        String recupererIdTypeCourrierSQL = "select id_type_courrier from `type_courrier` where titre_type_courrier = '" + typeDeCourrier + "'";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdTypeCourrierSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_type_courrier");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recuperer  l'id de la direction par son nom***/
    public static String recupererIdDirectionParSonNom(String direction) {
        String id = null;
        String recupererIdDirectionSQL = "select id_direction from `direction` where nom_direction = '" + direction + "'";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdDirectionSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_direction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recuperer  l'id de l'établissement par sonabreviation***/
    public static String recupererIdEtablissementParSonAbreviation(String etablissement) {
        String id = null;
        String recupererIdEtablissementSQL = "select id_etablissement from `etablissement` where abreviation = '" + etablissement + "'";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdEtablissementSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_etablissement");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recuperer  l'id de la fonction par son titre et son typee***/
    public static String recupererIdFonctionParSonTitreEtSonType(String titreFonction, String typeFonction) {
        String id = null;
        String recupererIdFonctionSQL = "select id_fonction from `fonction` where titre_fonction = '" + titreFonction + "' and type_fonction = '"+typeFonction+"' ; ";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdFonctionSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_fonction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /*****/
    public static String recupererIdFonctionDuDernierEnregistrementCorrespondantParSonTitre(String titreFonction) {
        String id = null;
        String recupererIdFonctionSQL = "select id_fonction from `fonction` where titre_fonction = '" + titreFonction + "' order by id_fonction desc limit 1 ; ";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdFonctionSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_fonction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    public static String recupererIdDirectionDuDernierEnregistrementCorrespondantParSonNom(String direction) {
        String id = null;
        String recupererIdDirectionSQL = "select id_direction from `direction` where nom_direction = '" + direction + "' order by id_direction desc limit 1";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdDirectionSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_direction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recupere l'id du type d'etablissement  par son titre***/
    public static String recupererIdTypeDEtablissementParTitre(String type) {
        String id = null;
        String recupererIdTypeDEtablissemenntSQL = "select id_type_etablissement from `type_etablissement` where titre_type_etablissement = '" + type + "'";
        Connection connection = DatabaseManager.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdTypeDEtablissemenntSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_type_etablissement");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }

        }
        return id;
    }

    /***Fonction qui recupere tous les courriers enregistrés d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersEnregistresDUnUtilisateursParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabaseManager.getConnexion();
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
        String requeteMesCourriersSQL = "select * from `ajouter_courrier` inner join `courrier` on ajouter_courrier.id_courrier = courrier.id_courrier left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on dossier.id_dossier =  correspondance_dossier_courrier.id_dossier  inner join personne on ajouter_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and etat = '"+EtatCourrier.courrierEnregistre+"' order by courrier.id_courrier desc;";

        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteMesCourriersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("date_enregistrement"),
                        resultSet.getString("id_courrier"),
                        resultSet.getString("confidentiel"),
                        resultSet.getString("extension_fichier"),
                        resultSet.getString("id_ajouter_courrier"),
                        resultSet.getString("identifiant_alfresco"),
                        resultSet.getString("id_dossier")));
            }

            for (int i = 0; i < mesCourriers.size(); i++){

                String jour = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().lastIndexOf("-") +1);
                String mois = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+1,mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+3);
                String annee = mesCourriers.get(i).getDateDEnregistrement().substring(0,4);

                mesCourriers.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
            }

            for (int i = 0; i < mesCourriers.size(); i++){

                if(mesCourriers.get(i).getExtensionCourrier() != null){
                    if(mesCourriers.get(i).getExtensionCourrier().equals("pdf") || mesCourriers.get(i).getExtensionCourrier().equals("PDF")){
                        mesCourriers.get(i).setImageCourrier("pdf.png");
                    }else if(mesCourriers.get(i).getExtensionCourrier().equals("doc") || mesCourriers.get(i).getExtensionCourrier().equals("docx")  ){
                        mesCourriers.get(i).setImageCourrier("word.png");
                        mesCourriers.get(i).setExtensionCourrier("word");
                    }else if(mesCourriers.get(i).getExtensionCourrier().equals("xls") || mesCourriers.get(i).getExtensionCourrier().equals("xlsx")  ){
                        mesCourriers.get(i).setImageCourrier("excel.png");
                        mesCourriers.get(i).setExtensionCourrier("excel");
                    }else if(mesCourriers.get(i).getExtensionCourrier().equalsIgnoreCase("Fichier_Confidentiel")){
                        mesCourriers.get(i).setImageCourrier("fichierconfidentiel.png");
                        mesCourriers.get(i).setExtensionCourrier("confidentiel");
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return mesCourriers;


    }

    /***Fonction qui recupere tous les courriers envoyés d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersEnvoyesDUnUtilisateursParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabaseManager.getConnexion();
        String requeteMesCourriersSQL = null;
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");

        requeteMesCourriersSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' and envoyer_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and envoyer_courrier.archive =  '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";
        ResultSet resultSet = null;
        try {

            resultSet = connection.createStatement().executeQuery(requeteMesCourriersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("courrier.date_enregistrement"),
                        resultSet.getString("courrier.id_courrier"),
                        resultSet.getString("confidentiel"),
                        resultSet.getString("titre_type_courrier"),
                        resultSet.getString("id_envoyer"),
                        resultSet.getString("identifiant_alfresco"),
                        resultSet.getString("dossier.id_dossier"),
                        resultSet.getString("confirmation_reception")));
            }

            for (int i = 0; i < mesCourriers.size(); i++){
                if(mesCourriers.get(i).getDateDEnregistrement() != null){
                    String jour = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().lastIndexOf("-") +1);
                    String mois = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+1,mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+3);
                    String annee = mesCourriers.get(i).getDateDEnregistrement().substring(0,4);
                    mesCourriers.get(i).setDateDEnregistrement(jour+"-"+mois+"-"+annee);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return mesCourriers;


    }

    /***Fonction qui recupere tous les courriers mis en favoris d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersFavorisDUnUtilisateursParSonId(String idUtilisateur) {
        List<Courrier> mesCourriersEnvoyes = new ArrayList<>();
        List<Courrier> mesCourriersRecus = new ArrayList<>();
        List<Courrier> finalList = new ArrayList<>();
        Connection connection = DatabaseManager.getConnexion();
        String requeteMesCourriersEnvoyesFavorisSQL = null;
        String requeteMesCourriersRecusFavorisSQL = null;
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
       // if(isResponsable){
        requeteMesCourriersEnvoyesFavorisSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier  left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and envoyer_courrier.favoris = '"+EtatCourrier.favoris+"' and envoyer_courrier.archive = '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";
        /*}else{
            requeteMesCourriersSQL = "select * from `ajouter_courrier` inner join `courrier` on ajouter_courrier.id_courrier = courrier.id_courrier left join dossier on courrier.fk_dossier = dossier.id_dossier where id_personne = '"+idUtilisateur+"'and favoris = '"+EtatCourrier.favoris+"' and archive = '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";
        }*/
        requeteMesCourriersRecusFavorisSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier  left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and recevoir_courrier.favoris = '"+EtatCourrier.favoris+"' and recevoir_courrier.archive = '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";

        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        try {

            resultSet1 = connection.createStatement().executeQuery(requeteMesCourriersEnvoyesFavorisSQL);
            resultSet2 = connection.createStatement().executeQuery(requeteMesCourriersRecusFavorisSQL);
            while(resultSet1.next()){
                mesCourriersEnvoyes.add(new Courrier(
                        resultSet1.getString("reference"),
                        resultSet1.getString("priorite"),
                        resultSet1.getString("objet"),
                        resultSet1.getString("courrier.date_enregistrement"),
                        resultSet1.getString("id_courrier"),
                        resultSet1.getString("genre"),
                        resultSet1.getString("identifiant_alfresco")));
            }

            while(resultSet2.next()){
                mesCourriersRecus.add(new Courrier(
                        resultSet2.getString("reference"),
                        resultSet2.getString("priorite"),
                        resultSet2.getString("objet"),
                        resultSet2.getString("courrier.date_enregistrement"),
                        resultSet2.getString("id_courrier"),
                        resultSet2.getString("genre"),
                        resultSet2.getString("identifiant_alfresco")));
            }


            if(mesCourriersEnvoyes.size() > 0){

                for (int i = 0; i < mesCourriersEnvoyes.size(); i++){
                    String jour = mesCourriersEnvoyes.get(i).getDateDeReception().substring(mesCourriersEnvoyes.get(i).getDateDeReception().lastIndexOf("-") +1);
                    String mois = mesCourriersEnvoyes.get(i).getDateDeReception().substring(mesCourriersEnvoyes.get(i).getDateDeReception().indexOf("-")+1,mesCourriersEnvoyes.get(i).getDateDeReception().indexOf("-")+3);
                    String annee = mesCourriersEnvoyes.get(i).getDateDeReception().substring(0,4);
                    mesCourriersEnvoyes.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
                }

            }

            if(mesCourriersRecus.size() > 0){

                for (int i = 0; i < mesCourriersRecus.size(); i++){
                    String jour = mesCourriersRecus.get(i).getDateDeReception().substring(mesCourriersRecus.get(i).getDateDeReception().lastIndexOf("-") +1);
                    String mois = mesCourriersRecus.get(i).getDateDeReception().substring(mesCourriersRecus.get(i).getDateDeReception().indexOf("-")+1,mesCourriersRecus.get(i).getDateDeReception().indexOf("-")+3);
                    String annee = mesCourriersRecus.get(i).getDateDeReception().substring(0,4);
                    mesCourriersRecus.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
                    mesCourriersRecus.get(i).setGenreCourrier("Courrier Reçu");
                }
            }

            finalList = Stream.concat(mesCourriersEnvoyes.stream(), mesCourriersRecus.stream()).collect(Collectors.toList());

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet1 != null) {
                try {
                    resultSet1.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (resultSet2 != null) {
                try {
                    resultSet2.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return finalList;


    }

    /***Fonction qui recupere tous les courriers archivé d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersArchivesDUnUtilisateursParSonId(String idUtilisateur) {

        Connection connection = DatabaseManager.getConnexion();
        List<Courrier> mesCourriersEnvoyes = new ArrayList<>();
        List<Courrier> mesCourriersRecus = new ArrayList<>();
        List<Courrier> finalList = new ArrayList<>();
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
        String requeteMesCourriersEnvoyesArchiveSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier  left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and envoyer_courrier.archive = '"+ EtatCourrier.archiveActive+"' order by courrier.id_courrier desc;";

        String requeteMesCourriersRecusArchiveSQL  = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier  left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and recevoir_courrier.archive = '"+ EtatCourrier.archiveActive +"' order by courrier.id_courrier desc;";

        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        try {

            resultSet1 = connection.createStatement().executeQuery(requeteMesCourriersEnvoyesArchiveSQL);
            resultSet2 = connection.createStatement().executeQuery(requeteMesCourriersRecusArchiveSQL);
            while(resultSet1.next()){
                mesCourriersEnvoyes.add(new Courrier(
                        resultSet1.getString("reference"),
                        resultSet1.getString("priorite"),
                        resultSet1.getString("objet"),
                        resultSet1.getString("courrier.date_enregistrement"),
                        resultSet1.getString("id_courrier"),
                        resultSet1.getString("genre"),
                        resultSet1.getString("identifiant_alfresco")));
            }

            while(resultSet2.next()){
                mesCourriersRecus.add(new Courrier(
                        resultSet2.getString("reference"),
                        resultSet2.getString("priorite"),
                        resultSet2.getString("objet"),
                        resultSet2.getString("courrier.date_enregistrement"),
                        resultSet2.getString("id_courrier"),
                        resultSet2.getString("genre"),
                        resultSet2.getString("identifiant_alfresco")));
            }

            if(mesCourriersEnvoyes.size() > 0){

                for (int i = 0; i < mesCourriersEnvoyes.size(); i++){
                    String jour = mesCourriersEnvoyes.get(i).getDateDeReception().substring(mesCourriersEnvoyes.get(i).getDateDeReception().lastIndexOf("-") +1);
                    String mois = mesCourriersEnvoyes.get(i).getDateDeReception().substring(mesCourriersEnvoyes.get(i).getDateDeReception().indexOf("-")+1,mesCourriersEnvoyes.get(i).getDateDeReception().indexOf("-")+3);
                    String annee = mesCourriersEnvoyes.get(i).getDateDeReception().substring(0,4);
                    mesCourriersEnvoyes.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
                }

            }

            if(mesCourriersRecus.size() > 0){

                for (int i = 0; i < mesCourriersRecus.size(); i++){
                    String jour = mesCourriersRecus.get(i).getDateDeReception().substring(mesCourriersRecus.get(i).getDateDeReception().lastIndexOf("-") +1);
                    String mois = mesCourriersRecus.get(i).getDateDeReception().substring(mesCourriersRecus.get(i).getDateDeReception().indexOf("-")+1,mesCourriersRecus.get(i).getDateDeReception().indexOf("-")+3);
                    String annee = mesCourriersRecus.get(i).getDateDeReception().substring(0,4);
                    mesCourriersRecus.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
                    mesCourriersRecus.get(i).setGenreCourrier("Courrier Reçu");
                }
            }

            finalList = Stream.concat(mesCourriersEnvoyes.stream(), mesCourriersRecus.stream()).collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet1 != null) {
                try {
                    resultSet1.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (resultSet2 != null) {
                try {
                    resultSet2.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  finalList;

    }

    /***Fonction qui recupere tous les courriers reçus par un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersReçusParUnUtilisateurParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabaseManager.getConnexion();
        String requeteMesCourriersSQL = null;
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
        requeteMesCourriersSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier left join correspondance_dossier_courrier\n" +
                " on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier  inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier where direction.id_direction = '"+idDirection+"' and recevoir_courrier.archive = '"+ EtatCourrier.archiveNonActive +"' and recevoir_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc";
        ResultSet resultSet = null;
        try {

            resultSet = connection.createStatement().executeQuery(requeteMesCourriersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("courrier.date_enregistrement"),
                        resultSet.getString("courrier.id_courrier"),
                        resultSet.getString("confidentiel"),
                        resultSet.getString("titre_type_courrier"),
                        resultSet.getString("id_recevoir_courrier"),
                        resultSet.getString("identifiant_alfresco"),
                        resultSet.getString("id_dossier"),
                        resultSet.getString("accuse_reception"),
                        resultSet.getString("transfer")));
            }

            for (int i = 0; i < mesCourriers.size(); i++){
                if(mesCourriers.get(i).getDateDEnregistrement() != null){
                    String jour = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().lastIndexOf("-") +1);
                    String mois = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+1,mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+3);
                    String annee = mesCourriers.get(i).getDateDEnregistrement().substring(0,4);
                    mesCourriers.get(i).setDateDEnregistrement(jour+"-"+mois+"-"+annee);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return mesCourriers;


    }

    /**Fonction qui recuperer le nombre d'annexe par courrier**/
    public static String recupererLeNombreDAnnexeDUnCourrier(String idCourrier){
        String nombreCourrier = null;
        String requeteNombreAnnexeDuCourrierSQL = "select count(*) from `annexe` where id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteNombreAnnexeDuCourrierSQL);
            if(resultSet.next()){
                nombreCourrier = resultSet.getString("count(*)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return nombreCourrier;
    }

    /**Fonction qui recuperer l'emetteur d'un courrier**/
    public static void recupererLEmetteurDUnCourrierParIdCourrier(String idCourrier){
        String requeteDetailEmetteurCourrierSQL = "select * from `envoyer_courrier` inner join `personne` on envoyer_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where envoyer_courrier.id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailEmetteurCourrierSQL);
            if (resultSet.next()){
                typeDemetteur = resultSet.getString("titre_type_de_personne");
                directeurEmetteur = resultSet.getString("nom_direction");
                fonctionEmetteur = resultSet.getString("titre_fonction");
                ministereEmetteur = resultSet.getString("nom_etablissement");

                telEmetteurEtablissement = resultSet.getString("tel_etablissement");
                emailEmetteurEtablissement = resultSet.getString("mail_etablissement");
                adresseEmetteurEtablissement = resultSet.getString("adresse_etablissement");

                idEmetteur = resultSet.getString("envoyer_courrier.id_personne");
                telEmetteurPersonne = resultSet.getString("tel");
                emailEmetteurPersonne = resultSet.getString("mail");
                nomEtPrenomEmetteurPersonne = resultSet.getString("nom") +" "+resultSet.getString("prenom") ;


                if(telEmetteurEtablissement == null){
                    telEmetteurEtablissement = "";
                }
                if(telEmetteurPersonne == null){
                    telEmetteurPersonne = "";
                }
                if(adresseEmetteurEtablissement== null){
                    adresseEmetteurEtablissement= "";
                }
                if(emailEmetteurEtablissement == null){
                    emailEmetteurEtablissement= "";
                }
                if(emailEmetteurPersonne == null){
                    emailEmetteurPersonne = "";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**Fonction qui recuperer le destinataire d'un courrier**/
    public static void recupererLeDestinataireDUnCourrierParIdCourrier(String idCourrier){
        Destinataire destinataire = new Destinataire();
        String requeteDetailEmetteurCourrierSQL = "select * from `recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where recevoir_courrier.id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailEmetteurCourrierSQL);
            while (resultSet.next()){
                destinataire.setTypeDestinataire(resultSet.getString("titre_type_de_personne"));
                destinataire.setFonction(resultSet.getString("titre_fonction"));
                destinataire.setDirection(resultSet.getString("nom_direction"));
                destinataire.setMinistere(resultSet.getString("nom_etablissement"));

                destinataire.setNomParticulier(resultSet.getString("nom"));
                destinataire.setPrenomParticulier(resultSet.getString("prenom"));
                destinataire.setTelephoneParticulier(resultSet.getString("tel"));
                destinataire.setEmailParticulier(resultSet.getString("mail"));

                destinataire.setRaisonSocial(resultSet.getString("nom_etablissement"));
                destinataire.setEmailEntreprise(resultSet.getString("mail_etablissement"));
                destinataire.setAdresseEntreprise(resultSet.getString("adresse_etablissement"));
                destinataire.setTelephoneEntreprise(resultSet.getString("tel_etablissement"));

                destinataire.setMinistereAutreMinistere(resultSet.getString("nom_etablissement"));
                destinataire.setDirectionAutreMinistere(resultSet.getString("nom_direction"));
                destinataire.setFonctionAutreMinistere(resultSet.getString("titre_fonction"));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**Fonction qui recuperer les details d'un courrier**/
    public static void recupererLesDetailsDUnCourrierEnregistre(String idCourrier){
        String requeteDetailCourrierSQL = "select * from `courrier` inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier where id_courrier = " + idCourrier + " ;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailCourrierSQL);
            if (resultSet.next()){
                    dateDEnregistrement = resultSet.getString("date_enregistrement");
                    heureDEnregistrement = resultSet.getString("heure_enregistrement");
                    dateDeReception = resultSet.getString("date_reception");
                    heureDeReception = resultSet.getString("heure_reception");
                    objetCourrier = resultSet.getString("objet");
                    commentairesCourrier = resultSet.getString("commentaires");
                    prioriteCourrier = resultSet.getString("priorite");
                    typeCourrier = resultSet.getString("titre_type_courrier");
                    referenceCourrier = resultSet.getString("reference");
                    confidentiel = resultSet.getString("confidentiel");
                    dossierAlfresco = resultSet.getString("dossier_alfresco_emetteur");
            }

            if(confidentiel != null){
                if(confidentiel.equals("0")){
                    confidentiel = "non";
                }else if(confidentiel.equals("1")){
                    confidentiel = "oui";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**Fonction qui recuperer l'id de la relation envoye_courrire dans database en fonction de l'id d'un courrier**/
    public static void recupererIdEnvoyeCourrierParIdDuCourrier(String idCourrier){
        String requeteSQL = "select id_envoyer_courrier from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier where courrier.id_courrier = " + idCourrier + " ;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteSQL);
            if (resultSet.next()){
                dateDEnregistrement = resultSet.getString("id_envoyer_courrier");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**Fonction qui recuperer les details d'un courrier reçu par un user**/
    public static void recupererLesDetailsDUnCourrierRecu(String idCourrier,String idDirection){
        String requeteDetailCourrierSQL = "select * from `courrier` inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier inner join recevoir_courrier on courrier.id_courrier = recevoir_courrier.id_courrier inner join personne on personne.id_personne =  recevoir_courrier.id_personne where courrier.id_courrier = '" + idCourrier + "' and personne.id_direction = '"+idDirection+"' ;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailCourrierSQL);
            if (resultSet.next()){
                dateDEnregistrement = resultSet.getString("courrier.date_enregistrement");
                heureDEnregistrement = resultSet.getString("courrier.heure_enregistrement");
                dateDeReception = resultSet.getString("courrier.date_reception");
                heureDeReception = resultSet.getString("courrier.heure_reception");
                objetCourrier = resultSet.getString("objet");
                commentairesCourrier = resultSet.getString("commentaires");
                prioriteCourrier = resultSet.getString("priorite");
                typeCourrier = resultSet.getString("titre_type_courrier");
                referenceCourrier = resultSet.getString("reference");
                confidentiel = resultSet.getString("confidentiel");
                dossierAlfresco = resultSet.getString("dossier_alfresco_emetteur");
                accuseDeReception = resultSet.getString("accuse_reception");
                referenceInterne = resultSet.getString("reference_interne");
            }

            if(confidentiel != null){
                if(confidentiel.equals("0")){
                    confidentiel = "non";
                }else if(confidentiel.equals("1")){
                    confidentiel = "oui";
                }
            }


            if(referenceInterne == null){
                referenceInterne = "non";
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**Fonction qui recuperer l"historique d'un courrier**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrier(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"')";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteHistoriqueCourrierSQL);
            while (resultSet.next()) {
                listeHistoriquesActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("prenom"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeHistoriquesActionsCourrier.size(); i++){

                String jour = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(0,4);

                listeHistoriquesActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeHistoriquesActionsCourrier;
    }

    /**Fonction qui recuperer l'historique d'un courrier**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrierEnregistre(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"'  and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"'); ";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteHistoriqueCourrierSQL);
            while (resultSet.next()) {
                listeHistoriquesActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("prenom"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeHistoriquesActionsCourrier.size(); i++){

                String jour = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(0,4);

                listeHistoriquesActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeHistoriquesActionsCourrier;

    }

    /**Fonction qui recuperer l'historique d'un courrier recu**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrierRecu(String idCourrier){
        HttpSession session = SessionUtils.getSession();
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = null;
        if(isCourrierTransferer != null){
            requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and (etat_correspondance ='"+EtatCourrier.courrierEnTransfer+"' or etat_correspondance ='"+EtatCourrier.courrierRecu+"' or etat_correspondance ='"+EtatCourrier.transfererAUneAutreDirection+"' ) and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"') ;";
        }else{
            requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and (etat_correspondance ='"+EtatCourrier.courrierRecu+"' or etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' or etat_correspondance ='"+EtatCourrier.transfererAUneAutreDirection+"' ) and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"') ;";
        }
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteHistoriqueCourrierSQL);
            while (resultSet.next()) {
                listeHistoriquesActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("prenom"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeHistoriquesActionsCourrier.size(); i++){

                String jour = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(0,4);

                listeHistoriquesActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeHistoriquesActionsCourrier;

    }

    /**Fonction qui recuperer l"historique d'un courrier à partir de son envoi**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrierAPartirDuMomentDeSonEnvoi(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and (etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' or etat_correspondance='"+EtatCourrier.courrierEnregistre+"') and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"')";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteHistoriqueCourrierSQL);
            while (resultSet.next()) {
                listeHistoriquesActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("prenom"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeHistoriquesActionsCourrier.size(); i++){

                String jour = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(0,4);

                listeHistoriquesActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeHistoriquesActionsCourrier;

    }

    /**Fonction qui recuperer l"historique d'un courrier à partir du moment de sa reception**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrierAPartirDuMomentDeSaReception(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"')";
        String requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"' and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteHistoriqueCourrierSQL);
            while (resultSet.next()) {
                listeHistoriquesActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("prenom"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeHistoriquesActionsCourrier.size(); i++){

                String jour = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeHistoriquesActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeHistoriquesActionsCourrier.get(i).getDate_debut().substring(0,4);

                listeHistoriquesActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeHistoriquesActionsCourrier;

    }

    public static String repondreAUneTache(String idEtape){
        HttpSession session = SessionUtils.getSession();
        boolean isResponsable = (Boolean) session.getAttribute("isResponsable");
        String natureEtape = session.getAttribute("natureEtape").toString();

        String requeteSQL = "select * from `etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape inner join `courrier` on correspondance_etape_courrier.id_courrier = courrier.id_courrier where etape.id_etape = '"+idEtape+"'";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteSQL);
            if (resultSet.next()){
                String idCourrier = resultSet.getString("id_courrier");
                String idAlfresco = resultSet.getString("identifiant_alfresco");
                session.setAttribute("idCourrier",idCourrier);
                session.setAttribute("courrierId",idCourrier);
                session.setAttribute("idAlfresco",idAlfresco);
                session.setAttribute("alfrescoId",idAlfresco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(isResponsable){
            if(natureEtape.contains("Courrier")){
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "detailduncourrierenregistre?faces-redirect=true";
            }
        }else{
            if(natureEtape.contains("Courrier")){
                return "repondreauncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "repondreaunetache.xhtml?faces-redirect=true";
            }

        }

    }

    public static String allerAUneEtapeAPartirDUneDiscussion(String idDiscussion){
        HttpSession session = SessionUtils.getSession();
        String requeteSQL = "select * from `etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape inner join `courrier` on correspondance_etape_courrier.id_courrier = courrier.id_courrier where etape.id_etape = (select id_etape from `discussion_etape` where id_discussion_etape = '"+idDiscussion+"') ;";

        String etatCorrespondanceEtape = session.getAttribute("etatCorrespondanceEtape").toString();
        boolean isResponsable = (boolean)session.getAttribute("isResponsable");

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteSQL);
            if (resultSet.next()){
                String idCourrier = resultSet.getString("id_courrier");
                String idAlfresco = resultSet.getString("identifiant_alfresco");

                session.setAttribute("idCourrier",idCourrier);
                session.setAttribute("idAlfresco",idAlfresco);
                session.setAttribute("courrierId",idCourrier);
                session.setAttribute("alfrescoId",idAlfresco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(isResponsable){
            if(etatCorrespondanceEtape.contains("Courrier Recu")){
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "detailduncourrierenregistre?faces-redirect=true";
            }
        }else{
            if(etatCorrespondanceEtape.contains("Courrier Enregistré")){
                System.out.println("ici");
                return "repondreaunetache.xhtml?faces-redirect=true";
            }else{
                System.out.println("la");
                return "repondreauncourrierrecus.xhtml?faces-redirect=true";
            }

        }

    }

    /**/
    public static List<Etape> recupererLesEtapesClesDuCourrierPourLaTimeLine(String idCourrier){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        String requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"' and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsCourrierSQL);
            while (resultSet.next()) {
                listeActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("etat"),
                        resultSet.getString("message")));
            }

            nombreDActionEnCoursDuCourrier = listeActionsCourrier.size();
            for (int i = 0; i < listeActionsCourrier.size(); i++){

                String jour = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeActionsCourrier.get(i).getDate_debut().substring(0,4);
                listeActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeActionsCourrier.get(i).getDate_fin() == null){
                    listeActionsCourrier.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().indexOf("-")+1,listeActionsCourrier.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeActionsCourrier.get(i).getDate_fin().substring(0,4);
                    listeActionsCourrier.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeActionsCourrier;

    }

    /**Fonction qui recuperer les destinataires d'un courrier**/
    public static List<Destinataire> recupererLesDestinatairesDUnCourrier(String idCourrier){
        List<Destinataire> listeDestinataire = new ArrayList<>();
        listeDestinataire.clear();
        String avoirTousLesDestinatairesDuCourrierSQL = "select * from (`recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join `fonction` on personne.id_fonction = fonction.id_fonction left join `direction` on personne.id_direction = direction.id_direction left join `etablissement` on personne.id_etablissement = etablissement.id_etablissement ) where id_courrier = " + idCourrier + " and recevoir_courrier.transfer is NULL;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(avoirTousLesDestinatairesDuCourrierSQL);
            while (resultSet.next()) {
                listeDestinataire.add(new Destinataire(
                        resultSet.getString("id_recevoir_courrier"),
                        resultSet.getString("titre_type_de_personne"),
                        resultSet.getString("nom_etablissement"),
                        resultSet.getString("nom_direction"),
                        resultSet.getString("titre_fonction"),
                        resultSet.getString("id_personne"),
                        resultSet.getString("accuse_reception"),
                        resultSet.getString("transfer")));

            }

            nombreDeDestinataireDuCourrier = listeDestinataire.size();
            for (int i = 0; i <  listeDestinataire.size(); i++){
                
                if ( listeDestinataire.get(i).getFonction() == null){
                     listeDestinataire.get(i).setFonction("Aucun");
                }
                if ( listeDestinataire.get(i).getDirection() == null){
                     listeDestinataire.get(i).setDirection("Aucun");
                }
                if ( listeDestinataire.get(i).getMinistere() == null){
                     listeDestinataire.get(i).setMinistere("Aucun");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeDestinataire;

    }

    /**Fonction qui recuperer les destinataires d'un courrier par transfer**/
    public static List<Destinataire> recupererLesDestinatairesParTransferDUnCourrier(String idCourrier){
        List<Destinataire> listeDestinataire = new ArrayList<>();
        listeDestinataire.clear();
        String avoirTousLesDestinatairesDuCourrierSQL = "select * from (`recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join `fonction` on personne.id_fonction = fonction.id_fonction left join `direction` on personne.id_direction = direction.id_direction left join `etablissement` on personne.id_etablissement = etablissement.id_etablissement ) where id_courrier = " + idCourrier + " and recevoir_courrier.transfer = '"+EtatCourrier.courrierTransferer+"';";
         ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(avoirTousLesDestinatairesDuCourrierSQL);
            while (resultSet.next()) {
                listeDestinataire.add(new Destinataire(
                        resultSet.getString("id_recevoir_courrier"),
                        resultSet.getString("titre_type_de_personne"),
                        resultSet.getString("nom_etablissement"),
                        resultSet.getString("nom_direction"),
                        resultSet.getString("titre_fonction"),
                        resultSet.getString("id_personne"),
                        resultSet.getString("accuse_reception"),
                        resultSet.getString("transfer"),
                        resultSet.getString("date_reception"),
                        resultSet.getString("heure_reception")));

            }


            for (int i = 0; i <  listeDestinataire.size(); i++){

                if ( listeDestinataire.get(i).getDateReception() != null){
                    String jour = listeDestinataire.get(i).getDateReception().substring(listeDestinataire.get(i).getDateReception().lastIndexOf("-") +1);
                    String mois = listeDestinataire.get(i).getDateReception().substring(listeDestinataire.get(i).getDateReception().indexOf("-")+1,listeDestinataire.get(i).getDateReception().indexOf("-")+3);
                    String annee = listeDestinataire.get(i).getDateReception().substring(0,4);
                    listeDestinataire.get(i).setDateReception(jour+"-"+mois+"-"+annee);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeDestinataire;

    }

    public static List<Etape> recupererLesTachesCreesParUnUser(String idUser){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        String requeteActionsUsersSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idUser+"' and correspondance_personne_etape.role_agent = '"+RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsUsersSQL);
            while (resultSet.next()) {
                listeActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("etat"),
                        resultSet.getString("nature_etape"),
                        resultSet.getString("message")));
            }

            String dateDuJour = DateUtils.recupererSimpleDateEnCours();
            for (int i = 0; i < listeActionsCourrier.size(); i++){

                try {

                    if(listeActionsCourrier.get(i).getDate_fin() != null){
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listeActionsCourrier.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listeActionsCourrier.get(i).getEtat().equals(EtatEtape.termine)){

                            if (date1.before(date2)){
                                listeActionsCourrier.get(i).setEtat("En retard");
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            nombreDActionEnCoursDuCourrier = listeActionsCourrier.size();
            for (int i = 0; i < listeActionsCourrier.size(); i++){

                String jour = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeActionsCourrier.get(i).getDate_debut().substring(0,4);
                listeActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeActionsCourrier.get(i).getDate_fin() == null){
                    listeActionsCourrier.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().indexOf("-")+1,listeActionsCourrier.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeActionsCourrier.get(i).getDate_fin().substring(0,4);
                    listeActionsCourrier.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeActionsCourrier;

    }

    public static List<Etape> recupererLesCinqsDernieresTachesEnCoursDeTraitementCreesParUnUser(String idUser){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        String requeteActionsUsersSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idUser+"' and correspondance_personne_etape.role_agent = '"+RoleEtape.AffecteurTache+"' and etat = '"+EtatEtape.enTraitement+"' order by etape.id_etape desc limit 5;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsUsersSQL);
            while (resultSet.next()) {
                listeActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("nature_etape"),
                        resultSet.getString("message")));
            }

            nombreDActionEnCoursDuCourrier = listeActionsCourrier.size();
            for (int i = 0; i < listeActionsCourrier.size(); i++){

                String jour = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeActionsCourrier.get(i).getDate_debut().substring(0,4);
                listeActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeActionsCourrier.get(i).getDate_fin() == null){
                    listeActionsCourrier.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().indexOf("-")+1,listeActionsCourrier.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeActionsCourrier.get(i).getDate_fin().substring(0,4);
                    listeActionsCourrier.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeActionsCourrier;

    }

    public static List<Etape> recupererLesActionsEffectueesSurUnCourrier(String idCourrier){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        String requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"' and etat_correspondance ='"+EtatCourrier.courrierEnregistre+"' and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";

        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsCourrierSQL);
            while (resultSet.next()) {
                listeActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("etat"),
                        resultSet.getString("message")));
            }

            String dateDuJour = DateUtils.recupererSimpleDateEnCours();

            for (int i = 0; i < listeActionsCourrier.size(); i++){

                try {
                    if(listeActionsCourrier.get(i).getDate_fin() != null){
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listeActionsCourrier.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listeActionsCourrier.get(i).getEtat().equals(EtatEtape.termine)){
                            if (date1.before(date2)){
                                listeActionsCourrier.get(i).setEtat("En retard");
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            nombreDActionEnCoursDuCourrier = listeActionsCourrier.size();
            for (int i = 0; i < listeActionsCourrier.size(); i++){

                String jour = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeActionsCourrier.get(i).getDate_debut().substring(0,4);
                listeActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeActionsCourrier.get(i).getDate_fin() == null){
                    listeActionsCourrier.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().indexOf("-")+1,listeActionsCourrier.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeActionsCourrier.get(i).getDate_fin().substring(0,4);
                    listeActionsCourrier.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeActionsCourrier;

    }

    public static List<Etape> recupererLesActionsEffectueesSurUnCourrierPourLaTimeLine(String idCourrier){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        HttpSession session = SessionUtils.getSession();
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        String requeteActionsCourrierSQL = null;
        if(isCourrierTransferer != null){
            requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"'  and (etat_correspondance ='"+EtatCourrier.courrierEnTransfer+"'  or etat_correspondance ='"+EtatCourrier.transfererAUneAutreDirection+"') and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";
        }else{
            requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"'  and (etat_correspondance ='"+EtatCourrier.courrierRecu+"' or etat_correspondance ='"+EtatCourrier.transfererAUneAutreDirection+"') and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and (correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' or correspondance_personne_etape.role_agent = '"+ RoleEtape.createurReponseAuCourrier+"')order by etape.id_etape desc;";

        }


        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        String dateDuJour = DateUtils.recupererSimpleDateEnCours();
        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsCourrierSQL);
            while (resultSet.next()) {
                listeActionsCourrier.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("etat"),
                        resultSet.getString("titre"),
                        resultSet.getString("message")));
            }

            for (int i = 0; i < listeActionsCourrier.size(); i++){

                try {
                    if(listeActionsCourrier.get(i).getDate_fin() != null){
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listeActionsCourrier.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listeActionsCourrier.get(i).getEtat().equals(EtatEtape.termine)){
                            if (date1.before(date2)){
                                listeActionsCourrier.get(i).setEtat("En retard");
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            for (int i = 0; i < listeActionsCourrier.size(); i++){

                String jour = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeActionsCourrier.get(i).getDate_debut().substring(listeActionsCourrier.get(i).getDate_debut().indexOf("-")+1,listeActionsCourrier.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeActionsCourrier.get(i).getDate_debut().substring(0,4);
                listeActionsCourrier.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeActionsCourrier.get(i).getDate_fin() == null){
                    listeActionsCourrier.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeActionsCourrier.get(i).getDate_fin().substring(listeActionsCourrier.get(i).getDate_fin().indexOf("-")+1,listeActionsCourrier.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeActionsCourrier.get(i).getDate_fin().substring(0,4);
                    listeActionsCourrier.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }


            }


            nombreDActionEnCoursDuCourrier = listeActionsCourrier.size();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeActionsCourrier;

    }


    public static List<Etape> recupererLesActionsAffecteesAUnAgents(String idAgent, String idCourrier){
        List<Etape> listeDesActionsAffecteesAUnAgent = new ArrayList<>();
        listeDesActionsAffecteesAUnAgent.clear();
        String requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"' and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.ReceveurTache+"' and  personne.id_personne = '"+idAgent+"' order by etape.id_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteActionsCourrierSQL);
            while (resultSet.next()) {
                listeDesActionsAffecteesAUnAgent.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("etat"),
                        resultSet.getString("message")));
            }


            nombreDeTacheDunAgentSurCourrier = listeDesActionsAffecteesAUnAgent.size();
            String dateDuJour = DateUtils.recupererSimpleDateEnCours();
            for (int i = 0; i < listeDesActionsAffecteesAUnAgent.size(); i++){

                try {

                    if(listeDesActionsAffecteesAUnAgent.get(i).getDate_fin() != null){
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listeDesActionsAffecteesAUnAgent.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listeDesActionsAffecteesAUnAgent.get(i).getEtat().equals(EtatEtape.termine)){
                            if (date1.before(date2)){
                                listeDesActionsAffecteesAUnAgent.get(i).setEtat("En retard");
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < listeDesActionsAffecteesAUnAgent.size(); i++){

                String jour = listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().substring(listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().lastIndexOf("-") +1);
                String mois = listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().substring(listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().indexOf("-")+1,listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().indexOf("-")+3);
                String annee = listeDesActionsAffecteesAUnAgent.get(i).getDate_debut().substring(0,4);
                listeDesActionsAffecteesAUnAgent.get(i).setDate_debut(jour+"-"+mois+"-"+annee);

                if (listeDesActionsAffecteesAUnAgent.get(i).getDate_fin() == null){
                    listeDesActionsAffecteesAUnAgent.get(i).setDate_fin("Aucun");
                }else{
                    String jourFin = listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().substring(listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().lastIndexOf("-") +1);
                    String moisFin = listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().substring(listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().indexOf("-")+1,listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().indexOf("-")+3);
                    String anneeFin = listeDesActionsAffecteesAUnAgent.get(i).getDate_fin().substring(0,4);
                    listeDesActionsAffecteesAUnAgent.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeDesActionsAffecteesAUnAgent;

    }

    /***Fonction qui récupere toutes les taches d'un agent***/
    public static List<Etape> recupererToutesLesTachesDUnAgent(String idAgent){
        List<Etape> listDesTaches = new ArrayList<>();
        listDesTaches.clear();
        String requeteListeDesTachesSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idAgent+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' order by etape.id_etape desc";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(requeteListeDesTachesSQL);
            while (resultSet.next()) {
                listDesTaches.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("date_fin"),
                        resultSet.getString("nom"),
                        resultSet.getString("etat"),
                        resultSet.getString("nature_etape"),
                        resultSet.getString("message")));

            }

            String dateDuJour = DateUtils.recupererSimpleDateEnCours();

            for (int i = 0; i < listDesTaches.size(); i++){

                try {

                    if(listDesTaches.get(i).getDate_fin() != null){
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listDesTaches.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listDesTaches.get(i).getEtat().equals(EtatEtape.termine)){
                            if (date1.before(date2)){
                                listDesTaches.get(i).setEtat("En retard");
                            }
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < listDesTaches.size(); i++){

                String jourDebut = listDesTaches.get(i).getDate_debut().substring(listDesTaches.get(i).getDate_debut().lastIndexOf("-") +1);
                String moisDebut = listDesTaches.get(i).getDate_debut().substring(listDesTaches.get(i).getDate_debut().indexOf("-")+1,
                        listDesTaches.get(i).getDate_debut().indexOf("-")+3);
                String anneeDebut = listDesTaches.get(i).getDate_debut().substring(0,4);

                listDesTaches.get(i).setDate_debut(jourDebut+"-"+moisDebut+"-"+anneeDebut);
                if(listDesTaches.get(i).getDate_fin() != null) {
                    try {
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listDesTaches.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listDesTaches.get(i).getEtat().equals(EtatEtape.termine)) {
                            if (date1.before(date2)) {
                                listDesTaches.get(i).setEtat("En retard");
                            }
                        }

                        String jourFin = listDesTaches.get(i).getDate_fin().substring(listDesTaches.get(i).getDate_fin().lastIndexOf("-") +1);
                        String moisFin = listDesTaches.get(i).getDate_fin().substring(listDesTaches.get(i).getDate_fin().indexOf("-")+1,
                                listDesTaches.get(i).getDate_fin().indexOf("-")+3);
                        String anneeFin = listDesTaches.get(i).getDate_fin().substring(0,4);

                        listDesTaches.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    listDesTaches.get(i).setDate_fin("Aucun");
                }

            }

        } catch (SQLException e) {
           e.printStackTrace();
        }finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return  listDesTaches;


    }


    public static int recupererNombredeTachesAffecteesAUnAgent(String idAgent){
        int nbre = 0;
        String requeteNombreDeTachesSQL = "select count(*) from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idAgent+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"'";
        Connection connection = DatabaseManager.getConnexion();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(requeteNombreDeTachesSQL);
            while (resultSet.next()) {
                nbre = resultSet.getInt("count(*)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nbre;

    }

    /***Fonction qui récupere toutes les taches d'un agent***/
    public static List<Etape> recupererLesCinqDernieresTachesEnTraitementDUnAgent(String idAgent){
        List<Etape> listDesTaches = new ArrayList<>();
        listDesTaches.clear();
        String requeteListeDesTachesSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idAgent+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' and etat = '"+EtatEtape.enTraitement+"'order by etape.id_etape desc limit 5";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        String dateDuJour = DateUtils.recupererSimpleDateEnCours();

        try {
            resultSet = connection.createStatement().executeQuery(requeteListeDesTachesSQL);
            while (resultSet.next()) {
                listDesTaches.add(new Etape(
                        resultSet.getString("id_etape"),
                        resultSet.getString("date_debut"),
                        resultSet.getString("nature_etape"),
                        resultSet.getString("message")));

            }
            for (int i = 0; i < listDesTaches.size(); i++){

                String jourDebut = listDesTaches.get(i).getDate_debut().substring(listDesTaches.get(i).getDate_debut().lastIndexOf("-") +1);
                String moisDebut = listDesTaches.get(i).getDate_debut().substring(listDesTaches.get(i).getDate_debut().indexOf("-")+1,
                        listDesTaches.get(i).getDate_debut().indexOf("-")+3);
                String anneeDebut = listDesTaches.get(i).getDate_debut().substring(0,4);

                listDesTaches.get(i).setDate_debut(jourDebut+"-"+moisDebut+"-"+anneeDebut);
                if(listDesTaches.get(i).getDate_fin() != null) {
                    try {
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(listDesTaches.get(i).getDate_fin());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuJour);
                        if (!listDesTaches.get(i).getEtat().equals(EtatEtape.termine)) {
                            if (date1.before(date2)) {
                                listDesTaches.get(i).setEtat("En retard");
                            }
                        }

                        String jourFin = listDesTaches.get(i).getDate_fin().substring(listDesTaches.get(i).getDate_fin().lastIndexOf("-") +1);
                        String moisFin = listDesTaches.get(i).getDate_fin().substring(listDesTaches.get(i).getDate_fin().indexOf("-")+1,
                                listDesTaches.get(i).getDate_fin().indexOf("-")+3);
                        String anneeFin = listDesTaches.get(i).getDate_fin().substring(0,4);

                        listDesTaches.get(i).setDate_fin(jourFin+"-"+moisFin+"-"+anneeFin);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    listDesTaches.get(i).setDate_fin("Aucun");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return  listDesTaches;


    }

    /**Fonction qui recupere les annotations d'un courrier**/
    public static List<Annotation> recupererLesAnnotationsDUnCourrier(String idCourrier){
        List<Annotation> listeAnnotationsCourrier = new ArrayList<>();
        listeAnnotationsCourrier.clear();
        String recupererAnnotationsDuCourrierSQL = "select * from `annotation` inner join personne on personne.id_personne = annotation.id_personne where id_courrier = " + idCourrier + "  order by id_annotation desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererAnnotationsDuCourrierSQL);
            while (resultSet.next()) {
                listeAnnotationsCourrier.add(new Annotation(
                        resultSet.getString("id_annotation"),
                        resultSet.getString("texte"),
                        resultSet.getString("date_saisie"),
                        resultSet.getString("heure_saisie"),
                        resultSet.getString("id_courrier"),
                        resultSet.getString("nom") +" "+resultSet.getString("prenom")));
            }

            for (int i = 0; i < listeAnnotationsCourrier.size(); i++){
                String jour = listeAnnotationsCourrier.get(i).getDateNote().substring(listeAnnotationsCourrier.get(i).getDateNote().lastIndexOf("-") +1);
                String mois = listeAnnotationsCourrier.get(i).getDateNote().substring(listeAnnotationsCourrier.get(i).getDateNote().indexOf("-")+1,listeAnnotationsCourrier.get(i).getDateNote().indexOf("-")+3);
                String annee = listeAnnotationsCourrier.get(i).getDateNote().substring(0,4);
                listeAnnotationsCourrier.get(i).setDateNote(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeAnnotationsCourrier;

    }

    /**Fonction qui recupere les réponses d'un courrier**/
    public static List<ReponseCourrier> recupererLesReponsesDUnCourrier(String idCourrier){
        List<ReponseCourrier> listeReponseCourrier = new ArrayList<>();
        listeReponseCourrier.clear();
        String recupererReponseDuCourrierSQL = "select message_reponse_courrier,date_reponse_courrier,heure_reponse_courrier,nom,prenom,identifiant_alfresco_reponse_courrier,role from `reponse_courrier` inner join courrier on reponse_courrier.fk_courrier = courrier.id_courrier inner join correspondance_personne_reponse_courrier on correspondance_personne_reponse_courrier.id_reponse_courrier = reponse_courrier.id_reponse_courrier inner join personne on correspondance_personne_reponse_courrier.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' order by reponse_courrier.id_reponse_courrier desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererReponseDuCourrierSQL);
            while (resultSet.next()) {
                listeReponseCourrier.add(new ReponseCourrier(
                        resultSet.getString("message_reponse_courrier"),
                        resultSet.getString("nom") +" "+resultSet.getString("prenom"),
                        resultSet.getString("date_reponse_courrier"),
                        resultSet.getString("heure_reponse_courrier"),
                        resultSet.getString("identifiant_alfresco_reponse_courrier"),
                        resultSet.getString("role")));
            }

            for (int i = 0; i < listeReponseCourrier.size(); i++){
                String jour = listeReponseCourrier.get(i).getDate().substring(listeReponseCourrier.get(i).getDate().lastIndexOf("-") +1);
                String mois = listeReponseCourrier.get(i).getDate().substring(listeReponseCourrier.get(i).getDate().indexOf("-")+1,listeReponseCourrier.get(i).getDate().indexOf("-")+3);
                String annee = listeReponseCourrier.get(i).getDate().substring(0,4);
                listeReponseCourrier.get(i).setDate(jour+"-"+mois+"-"+annee);
               /* if(listeReponseCourrier.get(i).getIdentifiantAlfresco() != null){
                    System.out.println("listeReponseCourrier = " + listeReponseCourrier.get(i).getIdentifiantAlfresco());
                }*/

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return listeReponseCourrier;

    }

    /**Fonction qui recupere les discussions d'une étape**/
    public static List<Discussion> recupererLesDiscussionsDUneEtape(String idEtape){
        List<Discussion> listeDesDiscussion = new ArrayList<>();
        listeDesDiscussion.clear();
        String discussionSQL = "select * from `discussion_etape` inner join  `personne` on discussion_etape.id_personne = personne.id_personne where id_etape = " +  idEtape + " order by id_discussion_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(discussionSQL);
            while (resultSet.next()) {
                listeDesDiscussion.add(new Discussion(
                        resultSet.getString("id_discussion_etape"),
                        resultSet.getString("date_discussion"),
                        resultSet.getString("message_discussion"),
                        resultSet.getString("nom") + " "+ resultSet.getString("prenom"),
                        resultSet.getString("prenom")));
            }

            for (int i = 0; i < listeDesDiscussion.size(); i++){
                String jour = listeDesDiscussion.get(i).getDateDiscussion().substring(listeDesDiscussion.get(i).getDateDiscussion().lastIndexOf("-") +1);
                String mois = listeDesDiscussion.get(i).getDateDiscussion().substring(listeDesDiscussion.get(i).getDateDiscussion().indexOf("-")+1,listeDesDiscussion.get(i).getDateDiscussion().indexOf("-")+3);
                String annee = listeDesDiscussion.get(i).getDateDiscussion().substring(0,4);
                listeDesDiscussion.get(i).setDateDiscussion(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return listeDesDiscussion;
    }

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static List<Dossier> recupererLesDossiersDUnUtilisateur(String idUser){
        List<Dossier> listeDossiers = new ArrayList<>();
        listeDossiers.clear();
        String recupererLesDossiersSQL = "select nom_dossier,nom,prenom,dossier.id_dossier from dossier inner join correspondance_personne_dossier on dossier.id_dossier = correspondance_personne_dossier.id_dossier inner join personne on personne.id_personne = correspondance_personne_dossier.id_personne where personne.id_personne = '"+idUser+"' order by dossier.id_dossier desc";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererLesDossiersSQL);
            while (resultSet.next()) {
                listeDossiers.add(new Dossier(
                        resultSet.getString("id_dossier"),
                        resultSet.getString("nom_dossier"),
                        resultSet.getString("nom") + " "+ resultSet.getString("prenom")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  listeDossiers;

    }

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static List<Courrier> recupererLesCourriersDansUnDossiersDUnUtilisateur(String idUser, String idDossier){
        List<Courrier> mesCourriers = new ArrayList<>();
        mesCourriers.clear();
        String recupererLesCourriersDUnDossiersSQL = " SELECT * FROM `courrier` left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join correspondance_personne_dossier on dossier.id_dossier = correspondance_personne_dossier.id_dossier inner join personne on correspondance_personne_dossier.id_personne = personne.id_personne where personne.id_personne = '"+idUser+"' and dossier.id_dossier = '"+idDossier+"' order by courrier.id_courrier desc";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererLesCourriersDUnDossiersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("date_reception"),
                        resultSet.getString("id_courrier"),
                        resultSet.getString("genre"),
                        resultSet.getString("identifiant_alfresco")));
            }

            nombreDeCourrierDansUnDossier = mesCourriers.size();

            for (int i = 0; i < mesCourriers.size(); i++){

                String jour = mesCourriers.get(i).getDateDeReception().substring(mesCourriers.get(i).getDateDeReception().lastIndexOf("-") +1);
                String mois = mesCourriers.get(i).getDateDeReception().substring(mesCourriers.get(i).getDateDeReception().indexOf("-")+1,mesCourriers.get(i).getDateDeReception().indexOf("-")+3);
                String annee = mesCourriers.get(i).getDateDeReception().substring(0,4);
                mesCourriers.get(i).setDateDeReception(jour+"-"+mois+"-"+annee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  mesCourriers;

    }

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static String recupererLeNomDUnDossierParSonId(String idDossier){
        String nomDossier = null;
        String recupererLesCourriersDUnDossiersSQL = " select nom_dossier from dossier where dossier.id_dossier = '"+idDossier+"' ;";
        ResultSet resultSet = null;
        Connection connection = DatabaseManager.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererLesCourriersDUnDossiersSQL);
            if(resultSet.next()){
               nomDossier = resultSet.getString("nom_dossier");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  nomDossier;

    }

    /***Renommer un dossier***/
    public static void renommerUnDossier(String idDossier,String nomDossier,String message){

        Connection connection = DatabaseManager.getConnexion();
        String renommerDossierSQL = "update `dossier` set `nom_dossier` = '"+ nomDossier+"' where id_dossier= '"+idDossier+"' ; ";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(renommerDossierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(message, new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Le dossier à bien été renommer!!!"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(message, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreurManager s'est produite!!!"));
            e.printStackTrace();
        }finally {
            if ( statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }



    }


    /***Ajouter un courrier à un dossier***/
    public static void ajouterUnCourrierDansUnDossier(String idDossier, String idCourrier, String idUser){

        Connection connection = DatabaseManager.getConnexion();

        String ajouterCorrespondanceDossierCourrierSQL = "INSERT INTO `correspondance_dossier_courrier` (`id_courrier`,`id_dossier`) VALUES" +
                "('"+ idCourrier +"',"+"'"+idDossier+"')";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierAjouterDossier+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`) VALUES" +
                "('"+ idCourrier +"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(ajouterCorrespondanceDossierCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Le courrier à bien été ajouté au dossier !!!"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreurManager s'est produite !!!"));
            e.printStackTrace();
        }finally {
            if ( statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }

    /***Ajouter un courrier à un dossier***/
    public static void ajouterUnCourrierRecuDansUnDossier(String idDossier, String idCourrier, String idUser){

        Connection connection = DatabaseManager.getConnexion();

        String ajouterCorrespondanceDossierCourrierSQL = "INSERT INTO `correspondance_dossier_courrier` (`id_courrier`,`id_dossier`) VALUES" +
                "('"+ idCourrier +"',"+"'"+idDossier+"')";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierAjouterDossier+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(ajouterCorrespondanceDossierCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Le courrier à bien été ajouté au dossier !!!"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreurManager s'est produite !!!"));
            e.printStackTrace();
        }finally {
            if ( statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }


    /***Verifier si un courrier est dans un dossier***/
    public static String  voirSiUnCourrierEstDejaDansUnDossier(String idCourrier,String idDossier){
        String idCheckDossier = null;
        Connection connection = DatabaseManager.getConnexion();

        String checkCourrierSQL = "select * from courrier left join `correspondance_dossier_courrier` on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left  join dossier on dossier.id_dossier = correspondance_dossier_courrier.id_dossier where dossier.id_dossier = '"+idDossier+"' and courrier.id_courrier='"+idCourrier+"' ; ";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(checkCourrierSQL);
            if(resultSet.next()){
                idCheckDossier = resultSet.getString("dossier.id_dossier");
            }
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreurManager s'est produite !!!"));
            e.printStackTrace();
        }finally {
            if ( resultSet!= null) {
                try {
                   resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return idCheckDossier;

    }

    /***Créer un dossier***/
    public static void creerUnDossier(String idUser, String nomDossier, String descriptionDossier){

        Connection connection = DatabaseManager.getConnexion();

        String creerDossierSQL = "INSERT INTO `dossier` (`nom_dossier`, `description_dossier`) VALUES" +
                " ('" + nomDossier +"',"+"'"+ descriptionDossier+"')";

        String ajouterCorrespondancePersonneDossierSQL = "INSERT INTO `correspondance_personne_dossier` (`id_dossier`,`id_personne`) VALUES" +
                "("+"(select id_dossier from dossier where nom_dossier = '"+nomDossier+"' order by id_dossier desc limit 1)"+", '"+idUser+"')";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(creerDossierSQL);
            statement.addBatch(ajouterCorrespondancePersonneDossierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Le dossier à bien été créer, actualisé la page pour voir votre dossier !!!"));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreurManager s'est produite !!!"));
            e.printStackTrace();
        }finally {
            if ( statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }


    /***Clore une discussion***/
    public static void cloreUneDiscussion(String idEtape){

        String cloreDiscussionSQL = "update `discussion_etape` set `etat_discussion` = '"+EtatEtape.Fermer+"' where id_etape = '"+idEtape+"'";
        String cloreEtatEtapeSQL = "update `etape` set `etat` = '"+EtatEtape.termine+"' where id_etape = '"+idEtape+"'";

        Connection connection = DatabaseManager.getConnexion();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(cloreDiscussionSQL);
            statement.addBatch(cloreEtatEtapeSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            PrimeFaces.current().executeScript("new Toast({message: 'La discussion à été définitivement fermée !',type: 'warning'})");
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("new Toast({ message: 'Une erreurManager à été detectée lors de la fermeture de la discussion',type: 'danger'}))");
            e.printStackTrace();
        }finally {

            if ( statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { /* ignored */}
            }
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }




}
