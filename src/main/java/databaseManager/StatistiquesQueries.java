package databaseManager;

import dateAndTime.DateUtils;
import model.Courrier;
import variables.EtatCourrier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatistiquesQueries {
    
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
    public static int nombreCourrierUrgentDuMois = 0;
    public static int nombreCourrierPasUrgentDuMois = 0;
    public static int nombreCourrierConfidentielDuMois = 0;
    public static int nombreCourrierPasConfidentielDuMois = 0;
    public static int nombreCourrierRecusDeLaSemaine = 0;
    public static int nombreCourrierEnvoyesDeLaSemaine = 0;
    public static int nombreCourrierUrgentDeLaSemaine = 0;
    public static int nombreCourrierPasUrgentDeLaSemaine = 0;
    public static int nombreCourrierConfidentielDeLaSemaine = 0;
    public static int nombreCourrierLundiDeLaSemaine = 0;
    public static int nombreCourrierMardiDeLaSemaine = 0;
    public static int nombreCourrierMercrediDeLaSemaine = 0;
    public static int nombreCourrierJeudiDeLaSemaine = 0;
    public static int nombreCourrierVendrediDeLaSemaine = 0;
    public static int nombreCourrierSamediDeLaSemaine = 0;
    public static int nombreCourrierDimancheDeLaSemaine = 0;
    public static int nombreCourrierEnvoyesDuMois = 0;
    public static int nombreCourrierRecusDuMois = 0;
    public static Map<String, Integer> mapNombreCourrierParTypeDuMoisEnCours = new HashMap<>();
    public static Map<String, Integer> mapNombreCourrierRecusParDirectionDuMoisEnCours = new HashMap<>();
    public static Map<String, Integer> mapNombreCourrierEnvoyesParDirectionDuMoisEnCours = new HashMap<>();
    private static List<Courrier> listeCourriersRecus = new ArrayList<>();
    private static List<Courrier> listeCourriersEnvoyes = new ArrayList<>();

    /***Fonction de récuperation du nombre de courrier traités par mois***/
    public static void recupererLeNombreDeCourrierTraitesParMoisPourLAnneeEnCours(String nomDirection) {

        int nombreDeCourrierTempJanvier = 0;
        int nombreDeCourrierTempFevrier = 0;
        int nombreDeCourrierTempMars = 0;
        int nombreDeCourrierTempAvril = 0;
        int nombreDeCourrierTempMai = 0;
        int nombreDeCourrierTempJuin = 0;
        int nombreDeCourrierTempJuillet = 0;
        int nombreDeCourrierTempAout = 0;
        int nombreDeCourrierTempSeptembre = 0;
        int nombreDeCourrierTempOctobre = 0;
        int nombreDeCourrierTempNovembre = 0;
        int nombreDeCourrierTempDecembre = 0;

        List<String> listeCourriersEnvoyes = new ArrayList<>();
        List<String> listeCourriersRecus = new ArrayList<>();
        List<String> listeCourriersTraites = new ArrayList<>();

        listeCourriersEnvoyes.clear();
        listeCourriersRecus.clear();

        Connection connectionCourrierRecus = DatabaseConnection.getConnexion();
        Connection connectionCourrierEnvoyes = DatabaseConnection.getConnexion();

        String nombreDeCourrierRecusSQL = "select courrier.date_enregistrement,nom_direction,confidentiel,priorite from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '" + nomDirection + "'  and courrier.etat = '" + EtatCourrier.courrierEnvoye + "' group by courrier.id_courrier order by courrier.id_courrier desc";
        String nombreDeCourrierEnvoyesSQL = "select courrier.date_enregistrement,nom_direction,confidentiel,priorite from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on  envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '" + nomDirection + "'  and courrier.etat = '" + EtatCourrier.courrierEnvoye + "' group by courrier.id_courrier order by courrier.id_courrier desc";
        ResultSet resultSetCourriersRecus = null;
        ResultSet resultSetCourriersEnvoyes = null;

        try {
            resultSetCourriersRecus = connectionCourrierRecus.createStatement().executeQuery(nombreDeCourrierRecusSQL);
            resultSetCourriersEnvoyes = connectionCourrierEnvoyes.createStatement().executeQuery(nombreDeCourrierEnvoyesSQL);

            while (resultSetCourriersRecus.next()) {
                listeCourriersRecus.add(resultSetCourriersRecus.getString("courrier.date_enregistrement"));
            }

            while (resultSetCourriersEnvoyes.next()) {
                listeCourriersEnvoyes.add(resultSetCourriersEnvoyes.getString("courrier.date_enregistrement"));
            }

            listeCourriersTraites = Stream.concat(listeCourriersRecus.stream(), listeCourriersEnvoyes.stream()).collect(Collectors.toList());

            String anneeEnCours = DateUtils.recupererLAnneeEnCours();
            listeCourriersTraites.removeIf(e -> !e.contains(anneeEnCours));
            for (int a = 0; a < listeCourriersTraites.size(); a++) {
                try {
                    if (listeCourriersTraites.get(a) != null) {
                        switch (DateUtils.recupererNomDuMoisParUneDate(listeCourriersTraites.get(a))) {
                            case "janvier":
                                nombreDeCourrierTempJanvier++;
                                break;
                            case "fevrier":
                                nombreDeCourrierTempFevrier++;
                                break;
                            case "mars":
                                nombreDeCourrierTempMars++;
                                break;
                            case "avril":
                                nombreDeCourrierTempAvril++;
                                break;
                            case "mai":
                                nombreDeCourrierTempMai++;
                                break;
                            case "juin":
                                nombreDeCourrierTempJuin++;
                                break;
                            case "juillet":
                                nombreDeCourrierTempJuillet++;
                                break;
                            case "août":
                                nombreDeCourrierTempAout++;
                                break;
                            case "septembre":
                                nombreDeCourrierTempSeptembre++;
                                break;
                            case "octobre":
                                nombreDeCourrierTempOctobre++;
                                break;
                            case "novembre":
                                nombreDeCourrierTempNovembre++;
                                break;
                            case "décembre":
                                nombreDeCourrierTempDecembre++;
                                break;
                            default:
                                System.out.println("bad");
                                break;
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            nombreDeCourrierJanvier = nombreDeCourrierTempJanvier;
            nombreDeCourrierFevrier = nombreDeCourrierTempFevrier;
            nombreDeCourrierMars = nombreDeCourrierTempMars;
            nombreDeCourrierAvril = nombreDeCourrierTempAvril;
            nombreDeCourrierMai = nombreDeCourrierTempMai;
            nombreDeCourrierJuin = nombreDeCourrierTempJuin;
            nombreDeCourrierJuillet = nombreDeCourrierTempJuillet;
            nombreDeCourrierAout = nombreDeCourrierTempAout;
            nombreDeCourrierSeptembre = nombreDeCourrierTempSeptembre;
            nombreDeCourrierOctobre = nombreDeCourrierTempOctobre;
            nombreDeCourrierNovembre = nombreDeCourrierTempNovembre;
            nombreDeCourrierDecembre = nombreDeCourrierTempDecembre;


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSetCourriersRecus != null) {
                try {
                    resultSetCourriersRecus.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (resultSetCourriersEnvoyes != null) {
                try {
                    resultSetCourriersEnvoyes.close();
                } catch (SQLException e) { /* ignored */}
            }

        }
    }

    /***Fonction qui recupere le nombre de courrier reçu par direction par mois***/
    public static void calculerLeNombreDeCourrierRecusParDirectionLeMoisCourant(String nomDirection){
        mapNombreCourrierRecusParDirectionDuMoisEnCours.clear();
        List<Courrier> listeIdCourriersRecus = new ArrayList<>();
        List<Courrier> finalListeCourriers = new ArrayList<>();
        ResultSet resultSetIdRecus = null;
        Connection connectionCourrierIdRecus =  DatabaseConnection.getConnexion();
        String requeteIdCourriersRecusSQL = "select courrier.id_courrier,date_enregistrement from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '"+nomDirection+"' and etat = '"+EtatCourrier.courrierEnvoye+"'";

        try {
            resultSetIdRecus = connectionCourrierIdRecus.createStatement().executeQuery(requeteIdCourriersRecusSQL);
            while (resultSetIdRecus.next()){
                listeIdCourriersRecus.add(new Courrier(resultSetIdRecus.getString("date_enregistrement"),resultSetIdRecus.getString("courrier.id_courrier")));
            }

            DateUtils.recupererLePremierEtLeDernierJourDuMoisEnCours();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int a = 0; a < listeIdCourriersRecus.size(); a++) {
                Date currentDate = null;
                try {
                    currentDate = sdf.parse(listeIdCourriersRecus.get(a).getDateDEnregistrement());
                    if (currentDate.after(DateUtils.premierJourDuMois) && currentDate.before(DateUtils.dernierJourDuMois)) {
                        finalListeCourriers.add(listeIdCourriersRecus.get(a));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for(int a =0; a <  finalListeCourriers.size(); a++){
                String direction = recupererLaDirectionDeLEmetteurDUnCourrierParIdDuCourrier( finalListeCourriers.get(a).getTypeCourrier());
                int valueOfKey = mapNombreCourrierRecusParDirectionDuMoisEnCours.containsKey(direction) ? mapNombreCourrierRecusParDirectionDuMoisEnCours.get(direction) : 0;
                valueOfKey++;
                mapNombreCourrierRecusParDirectionDuMoisEnCours.put(direction,valueOfKey);
            }

            mapNombreCourrierRecusParDirectionDuMoisEnCours.remove(nomDirection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***Fonction qui recupere le nombre de courrier envoyés par direction par mois***/
    public static void calculerLeNombreDeCourrierEnvoyesParDirectionLeMoisCourant(String nomDirection){

        mapNombreCourrierEnvoyesParDirectionDuMoisEnCours.clear();
        List<Courrier> listeIdCourriersEnvoyes = new ArrayList<>();
        List<Courrier> finalListeCourriers = new ArrayList<>();
        ResultSet resultSetIdEnvoyes = null;
        Connection connectionCourrierIdRecus =  DatabaseConnection.getConnexion();
        String requeteIdCourriersEnvoyesSQL = "select courrier.id_courrier,date_enregistrement from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '"+nomDirection+"' and etat = '"+EtatCourrier.courrierEnvoye+"'";
        try {
            resultSetIdEnvoyes = connectionCourrierIdRecus.createStatement().executeQuery(requeteIdCourriersEnvoyesSQL);
            while (resultSetIdEnvoyes.next()){
                listeIdCourriersEnvoyes.add(new Courrier(resultSetIdEnvoyes.getString("date_enregistrement"),resultSetIdEnvoyes.getString("courrier.id_courrier")));
            }

            DateUtils.recupererLePremierEtLeDernierJourDuMoisEnCours();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int a = 0; a < listeIdCourriersEnvoyes.size(); a++) {
                Date currentDate = null;
                try {
                    currentDate = sdf.parse(listeIdCourriersEnvoyes.get(a).getDateDEnregistrement());
                    if (currentDate.after(DateUtils.premierJourDuMois) && currentDate.before(DateUtils.dernierJourDuMois)) {
                        finalListeCourriers.add(listeIdCourriersEnvoyes.get(a));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for(int a =0; a <  finalListeCourriers.size(); a++){
                String direction = recupererLaDirectionDuReceveurDUnCourrierParIdDuCourrier( finalListeCourriers.get(a).getTypeCourrier());
                int valueOfKey = mapNombreCourrierEnvoyesParDirectionDuMoisEnCours.containsKey(direction) ? mapNombreCourrierEnvoyesParDirectionDuMoisEnCours.get(direction) : 0;
                valueOfKey++;
                mapNombreCourrierEnvoyesParDirectionDuMoisEnCours.put(direction,valueOfKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***Recuperer la direction de l'émeteur du courrier par id du courrier***/
    public static String recupererLaDirectionDeLEmetteurDUnCourrierParIdDuCourrier(String idCourrier){
        String requeteDetailEmetteurCourrierSQL = "select * from `envoyer_courrier` inner join `personne` on envoyer_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where envoyer_courrier.id_courrier = " + idCourrier + ";";
        String nomDirection = null;
        String idTypePersonne = null;
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailEmetteurCourrierSQL);
            if (resultSet.next()){
                idTypePersonne = resultSet.getString("id_type_de_personne");
                nomDirection = resultSet.getString("nom_direction");
            }
            if(idTypePersonne != null && idTypePersonne.equals("1") ){
                return nomDirection;
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

        return nomDirection;
    }

    /***Recuperer la direction du receveur  du courrier***/
    public static String recupererLaDirectionDuReceveurDUnCourrierParIdDuCourrier(String idCourrier){
        String requeteDetailEmetteurCourrierSQL = "select * from `recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where recevoir_courrier.id_courrier = " + idCourrier + ";";
        String nomDirection = null;
        String idTypePersonne = null;
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(requeteDetailEmetteurCourrierSQL);
            if (resultSet.next()){
                idTypePersonne = resultSet.getString("id_type_de_personne");
                nomDirection = resultSet.getString("nom_direction");
            }
            if(idTypePersonne != null && idTypePersonne.equals("1") ){
                return nomDirection;
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

        return nomDirection;
    }

    public static void calculerLesStatistiquesDesCourriersTraitesParTypesDeCourrierDuMoisEnCours(String idDirection){
        mapNombreCourrierParTypeDuMoisEnCours.clear();
        List<Courrier> listeCourriersRecus = new ArrayList<>();
        List<Courrier> listeCourriersEnvoyes = new ArrayList<>();
        List<Courrier> finalListeCourriers = new ArrayList<>();

        String listeDesCourriersRecusSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier =	courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        String listeDesCourriersEnvoyesSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        Connection connectionCourrierRecus =  DatabaseConnection.getConnexion();
        Connection connectionCourrierEnvoyes =  DatabaseConnection.getConnexion();

        ResultSet resultSetCourriersRecus = null;
        ResultSet resultSetCourriersEnvoyes = null;

        try {
            resultSetCourriersRecus = connectionCourrierRecus.createStatement().executeQuery(listeDesCourriersRecusSQL);
            resultSetCourriersEnvoyes = connectionCourrierEnvoyes.createStatement().executeQuery(listeDesCourriersEnvoyesSQL);

            while (resultSetCourriersRecus.next()) {
                listeCourriersRecus.add(new Courrier(
                        resultSetCourriersRecus.getString("date_enregistrement"),
                        resultSetCourriersRecus.getString("titre_type_courrier")));
            }

            while (resultSetCourriersEnvoyes.next()) {
                listeCourriersEnvoyes.add(new Courrier(
                        resultSetCourriersEnvoyes.getString("date_enregistrement"),
                        resultSetCourriersEnvoyes.getString("titre_type_courrier")));
            }

            List<Courrier> listeDeTousLesCourriers =  Stream.of(listeCourriersEnvoyes, listeCourriersRecus).flatMap(x -> x.stream()).collect(Collectors.toList());

            DateUtils.recupererLePremierEtLeDernierJourDuMoisEnCours();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int a = 0; a < listeDeTousLesCourriers.size(); a++) {
                Date currentDate = null;
                try {
                    currentDate = sdf.parse(listeDeTousLesCourriers.get(a).getDateDEnregistrement());
                    if (currentDate.after(DateUtils.premierJourDuMois) && currentDate.before(DateUtils.dernierJourDuMois)) {
                        finalListeCourriers.add(listeDeTousLesCourriers.get(a));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            for(Courrier myStat : finalListeCourriers ){
                String key = myStat.getTypeCourrier();
                int valueOfKey = mapNombreCourrierParTypeDuMoisEnCours.containsKey(key) ? mapNombreCourrierParTypeDuMoisEnCours.get(key) : 0;
                valueOfKey++;
                mapNombreCourrierParTypeDuMoisEnCours.put(key,valueOfKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /***Fonction qui calcul le nombre de courrier envoyés et recus pour la semaine***/
    public static void calculerLesStatistiquesDeLaSemainesPourUneDirection(String idDirection){

        nombreCourrierRecusDeLaSemaine = 0;
        nombreCourrierEnvoyesDeLaSemaine = 0;
        nombreCourrierUrgentDeLaSemaine = 0;
        nombreCourrierPasUrgentDeLaSemaine = 0;
        nombreCourrierConfidentielDeLaSemaine = 0;
        nombreCourrierLundiDeLaSemaine = 0;
        nombreCourrierMardiDeLaSemaine = 0;
        nombreCourrierMercrediDeLaSemaine = 0;
        nombreCourrierJeudiDeLaSemaine = 0;
        nombreCourrierVendrediDeLaSemaine = 0;
        nombreCourrierSamediDeLaSemaine = 0;
        nombreCourrierDimancheDeLaSemaine = 0;

        Connection connectionCourrierRecus =  DatabaseConnection.getConnexion();
        Connection connectionCourrierEnvoyes =  DatabaseConnection.getConnexion();
        String nombreDeCourrierRecusSQL = "select * from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier =	courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";
        String nombreDeCourrierEnvoyesSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on type_courrier.id_type_courrier = courrier.fk_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' group by courrier.id_courrier order by courrier.id_courrier desc ";

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

            DateUtils.recupererLePremierEtLeDernierJourDelaSemaine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int a = 0; a < listeCourriersRecus.size(); a++) {
                try {
                    Date dateAConsiderer = sdf.parse(listeCourriersRecus.get(a).getDateDEnregistrement());
                    if (dateAConsiderer.after(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.premierJourDeLaSemaineFormatUS)) &&
                            dateAConsiderer.before(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.dernierJourDeLaSemaineFormatUS))) {
                        nombreCourrierRecusDeLaSemaine++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (int a = 0; a < listeCourriersEnvoyes.size(); a++) {
                try {
                    Date dateAConsiderer = sdf.parse(listeCourriersEnvoyes.get(a).getDateDEnregistrement());
                    if (dateAConsiderer.after(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.premierJourDeLaSemaineFormatUS)) &&
                            dateAConsiderer.before(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.dernierJourDeLaSemaineFormatUS))) {
                        nombreCourrierEnvoyesDeLaSemaine++;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            List<Courrier> listeDeTousLesCourriersDeLaSemaine =  Stream.of(listeCourriersEnvoyes, listeCourriersRecus).flatMap(x -> x.stream()).collect(Collectors.toList());

            for (int a = 0; a < listeDeTousLesCourriersDeLaSemaine.size(); a++) {
                try {
                    Date dateAConsiderer = sdf.parse(listeDeTousLesCourriersDeLaSemaine.get(a).getDateDEnregistrement());
                    if (dateAConsiderer.after(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.premierJourDeLaSemaineFormatUS)) &&
                            dateAConsiderer.before(DateUtils.convertirStringEnDateAuFormatUS(DateUtils.dernierJourDeLaSemaineFormatUS))) {
                       // System.out.println("listeDeTousLesCourriersDeLaSemaine.get(a).getDateDEnregistrement() = " + listeDeTousLesCourriersDeLaSemaine.get(a).getDateDEnregistrement());
                        if(listeDeTousLesCourriersDeLaSemaine.get(a).getPrioriteCourrier().equals("Urgent")){
                            nombreCourrierUrgentDeLaSemaine++;
                        }else if(listeDeTousLesCourriersDeLaSemaine.get(a).getPrioriteCourrier().equals("Normal")){
                            nombreCourrierPasUrgentDeLaSemaine++;
                        }
                        if(listeDeTousLesCourriersDeLaSemaine.get(a).getConfidentiel().equals("Oui")){
                            nombreCourrierConfidentielDeLaSemaine++;
                        }

                        for (Map.Entry<String,String> entry :  DateUtils.recupererTousLesJoursDeLaSemaineEnCours().entrySet()){
                            if(listeDeTousLesCourriersDeLaSemaine.get(a).getDateDEnregistrement().equalsIgnoreCase(entry.getValue())){
                                if(entry.getKey().equals("Lundi")){
                                    nombreCourrierLundiDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Mardi")){
                                    nombreCourrierMardiDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Mercredi")){
                                    nombreCourrierMercrediDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Jeudi")){
                                    nombreCourrierJeudiDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Vendredi")){
                                    nombreCourrierVendrediDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Samedi")){
                                    nombreCourrierSamediDeLaSemaine++;
                                }
                                if(entry.getKey().equals("Dimanche")){
                                    nombreCourrierDimancheDeLaSemaine++;
                                }
                            }
                        }

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (resultSetCourriersRecus != null) {
                try {
                    resultSetCourriersRecus.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (resultSetCourriersEnvoyes != null) {
                try {
                    resultSetCourriersEnvoyes.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connectionCourrierRecus != null) {
                try {
                    connectionCourrierRecus.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connectionCourrierEnvoyes != null) {
                try {
                    connectionCourrierEnvoyes.close();
                } catch (SQLException e) { /* ignored */}
            }

        }

    }

    public static void calculerLesStatistiquesDuMoisEnCoursPourUneDirection(String idDirection){

        int tempCourrierEnvoyesDuMois = 0;
        int tempCourrierRecusDuMois =0;
        int tempCourrierUrgentEnvoye =0;
        int tempCourrierPasUrgentEnvoye =0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateUtils.recupererLePremierEtLeDernierJourDuMoisEnCours();
        if(listeCourriersRecus.size() > 0){
            for (int i = 0; i < listeCourriersRecus.size(); i++) {
                try {
                    Date date = sdf.parse(listeCourriersRecus.get(i).getDateDEnregistrement());
                    if (date.after(DateUtils.premierJourDuMois) && date.before(DateUtils.dernierJourDuMois)) {
                        tempCourrierRecusDuMois++ ;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if(listeCourriersEnvoyes.size() > 0){
            for (int i = 0; i < listeCourriersEnvoyes.size(); i++) {
                try {
                    Date date = sdf.parse(listeCourriersEnvoyes.get(i).getDateDEnregistrement());
                    if (date.after(DateUtils.premierJourDuMois) && date.before(DateUtils.dernierJourDuMois)) {
                        tempCourrierEnvoyesDuMois++ ;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        List<Courrier> listeCourriersTraites = Stream.of(listeCourriersEnvoyes, listeCourriersRecus).flatMap(x -> x.stream()).collect(Collectors.toList());
        if(listeCourriersTraites.size() > 0){
            Date currentDate = null;
            for (int a = 0; a <listeCourriersTraites.size(); a++) {
                try {
                    currentDate = sdf.parse(listeCourriersTraites.get(a).getDateDEnregistrement());
                    if (currentDate.after(DateUtils.premierJourDuMois) && currentDate.before(DateUtils.dernierJourDuMois)){

                        if (listeCourriersTraites.get(a).getPrioriteCourrier().equalsIgnoreCase("Urgent")) {
                            nombreCourrierUrgentDuMois++;
                        }
                        if(listeCourriersTraites.get(a).getPrioriteCourrier().equals("Normal")){
                            nombreCourrierPasUrgentDuMois++;
                        }
                        if(listeCourriersTraites.get(a).getConfidentiel().equals("Oui")){
                            nombreCourrierConfidentielDuMois++;
                        }
                        if(listeCourriersTraites.get(a).getConfidentiel().equals("Non")){
                            nombreCourrierPasConfidentielDuMois++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        nombreCourrierRecusDuMois = tempCourrierRecusDuMois;
        nombreCourrierEnvoyesDuMois = tempCourrierEnvoyesDuMois;
    }


    /****Fonction qui calcul du nombre de courrier recu par direction du SG****/
    public static void recupererLeNombreDeCourrierParDirectionDuSecreteriatGeneral(){

    }

}
