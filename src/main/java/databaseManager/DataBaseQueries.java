package databaseManager;

import dateAndTime.DateUtils;
import model.*;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;
import variables.*;

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



    public static String directionUser;;
    public static String fonctionUser;
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

    public static int nombreDeTacheDunAgentSurCourrier= 0;
    public static int nombreDActionEnCoursDuCourrier= 0;
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
    public static String heureDeReception;
    public static String heureDEnregistrement;
    public static boolean isResponsable = false;
    public static boolean isSecretaire = false;
    private static Date premierDuMois;
    private static Date dernierDuMois;
    public static List<Courrier> listeCourriersRecus = new ArrayList<>();
    public static List<Courrier> listeCourriersEnvoyes = new ArrayList<>();
    private static List<Statistiques> listeNombreDeCourrierParType = new ArrayList<>();
    public static Map<String, Integer> mapNombreCourrierParType = new HashMap<>();

    /***Fonction de récuperation des statistiques de la page d'accueil***/
    public static void recupererLesStatistiquesPourLaPageDAccueil() {

        listeCourriersEnvoyes.clear();
        listeCourriersRecus.clear();
        int tempCourrierUrgentRecu = 0;
        int tempCourrierUrgentEnvoye =0;
        int tempCourrierConfidentielRecu = 0;
        int tempCourrierConfidentielEnvoye =0;

        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");

        Connection connectionCourrierRecus =  DatabaseConnection.getConnexion();
        Connection connectionCourrierEnvoyes =  DatabaseConnection.getConnexion();

        String nombreDeCourrierRecusSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier =	courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        String nombreDeCourrierEnvoyesSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";

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

        try {
            resultSetCourriersRecus = connectionCourrierRecus.createStatement().executeQuery(nombreDeCourrierRecusSQL);
            resultSetCourriersEnvoyes = connectionCourrierEnvoyes.createStatement().executeQuery(nombreDeCourrierEnvoyesSQL);



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

    /***Fonction de récuperation des disucssions ouvertes d'un user***/
    public static List<Discussion> recupererLesDiscusssionsDUnUserEnCours(String idUser){
        List<Discussion> mesDiscussions = new ArrayList<>();
        mesDiscussions.clear();
        String requeteListeDesDiscussionsEnCoursSQL = "select * from `discussion_etape` inner join `personne` on discussion_etape.id_personne = personne.id_personne inner join `correspondance_etape_courrier` on discussion_etape.id_etape = correspondance_etape_courrier.id_etape where personne.id_personne = '"+idUser+"' and etat_discussion = '"+EtatEtape.Ouvert+"' group by discussion_etape.id_etape desc limit 5;";
       // System.out.println("requeteListeDesDiscussionsEnCoursSQL = " + requeteListeDesDiscussionsEnCoursSQL);
        Connection connection = DatabaseConnection.getConnexion();
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

        return mesDiscussions;

    }


    /****Fonction qui récupère les fonctions du Ministères***/
    public static List<String> recupererLaListeDesFonctions(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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

    /****Fonction qui récupère les fonctions des agents d'une direction fonction***/
    public static List<String> recupererLaListeDesFonctionsDesAgentsParDirection(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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

    /***Fonction qui recuperer  l'id de la direction par son nom***/
    public static String recupererIdDirectionParSonNom(String direction) {
        String id = null;
        String recupererIdDirectionSQL = "select id_direction from `direction` where nom_direction = '" + direction + "'";
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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

    /***Fonction qui recuperer  l'id de la fonction par son titre et son type***/
    public static String recupererIdFonctionParSonTitreEtSonType(String titreFonction, String typeFonction) {
        String id = null;
        String recupererIdFonctionSQL = "select id_fonction from `fonction` where titre_fonction = '" + titreFonction + "' and type_fonction = '"+typeFonction+"' ; ";
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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

    /**Fonction qui recuperer le nombre d'annexe par courrier**/
    public static String recupererLeNombreDAnnexeDUnCourrier(String idCourrier){
        String nombreCourrier = null;
        String requeteNombreAnnexeDuCourrierSQL = "select count(*) from `annexe` where id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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

    /**Fonction qui recuperer l'id de la relation envoye_courrire dans databaseManager en fonction de l'id d'un courrier**/
    public static void recupererIdEnvoyeCourrierParIdDuCourrier(String idCourrier){
        String requeteSQL = "select id_envoyer_courrier from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier where courrier.id_courrier = " + idCourrier + " ;";

        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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

    /**Fonction qui recuperer l"historique d'un courrier**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrier(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"')";

        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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

    /**Fonction qui recuperer l'historique d'un courrier à partir du moment de sa reception**/
    public static List<Etape> recupererLHistoriqueDesActionsSurUnCourrierAPartirDuMomentDeSaReception(String idCourrier){
        List<Etape> listeHistoriquesActionsCourrier = new ArrayList<>();
        listeHistoriquesActionsCourrier.clear();
        String requeteHistoriqueCourrierSQL = " select * from (`courrier` inner join `correspondance_etape_courrier` on courrier.id_courrier = correspondance_etape_courrier.id_courrier  inner join `etape` on correspondance_etape_courrier.id_etape = etape.id_etape)  inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join personne on correspondance_personne_etape.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' and etat_correspondance ='"+EtatCourrier.courrierEnvoye+"' and ( etape.etat = '"+ EtatEtape.nouveauCourrier+"' or etape.etat = '"+EtatEtape.termine+"')";
        String requeteActionsCourrierSQL = "select * from ((`etape` inner join `correspondance_etape_courrier` on etape.id_etape = correspondance_etape_courrier.id_etape) inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape ) inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne  where correspondance_etape_courrier.id_courrier = '"+idCourrier+"' and etape.titre = '"+ActionEtape.transmisPourTraitement+"' and correspondance_personne_etape.role_agent = '"+ RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";

        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
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
            if(etatCorrespondanceEtape.contains("Courrier")){
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "detailduncourrierenregistre?faces-redirect=true";
            }
        }else{
            if(etatCorrespondanceEtape.contains("Courrier Enregistré")){
                return "repondreaunetache.xhtml?faces-redirect=true";
            }else{
                return "repondreauncourrierrecus.xhtml?faces-redirect=true";
            }

        }

    }

    public static List<Etape> recupererLesTachesCreesParUnUser(String idUser){
        List<Etape> listeActionsCourrier = new ArrayList<>();
        listeActionsCourrier.clear();
        String requeteActionsUsersSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idUser+"' and correspondance_personne_etape.role_agent = '"+RoleEtape.AffecteurTache+"' order by etape.id_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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
       // System.out.println("requeteActionsUsersSQL = " + requeteActionsUsersSQL);
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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

    /***Fonction qui récupere toutes les taches d'un agent***/
    public static List<Etape> recupererLesCinqDernieresTachesEnTraitementDUnAgent(String idAgent){
        List<Etape> listDesTaches = new ArrayList<>();
        listDesTaches.clear();
        String requeteListeDesTachesSQL = "select * from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where personne.id_personne = '"+idAgent+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' and etat = '"+EtatEtape.enTraitement+"'order by etape.id_etape desc limit 5";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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

    /**Fonction qui recupere les réponses d'un courrier**/
    public static List<ReponseCourrier> recupererLesReponsesDUnCourrier(String idCourrier){
        List<ReponseCourrier> listeReponseCourrier = new ArrayList<>();
        listeReponseCourrier.clear();
        String recupererReponseDuCourrierSQL = "select message_reponse_courrier,date_reponse_courrier,heure_reponse_courrier,nom,prenom,identifiant_alfresco_reponse_courrier,role from `reponse_courrier` inner join courrier on reponse_courrier.fk_courrier = courrier.id_courrier inner join correspondance_personne_reponse_courrier on correspondance_personne_reponse_courrier.id_reponse_courrier = reponse_courrier.id_reponse_courrier inner join personne on correspondance_personne_reponse_courrier.id_personne = personne.id_personne where courrier.id_courrier = '"+idCourrier+"' order by reponse_courrier.id_reponse_courrier desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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




}
