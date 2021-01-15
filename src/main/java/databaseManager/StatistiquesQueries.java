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
    public static Map<String, Integer> myMapCourrierRecusParDirection = new HashMap<>();
    public static Map<String, Integer> myMapCourrierEnvoyesParDirection = new HashMap<>();
    public static Map<String, Integer> mapNombreCourrierParTypeDuMoisEnCours = new HashMap<>();

    /***Fonction de récuperation du nombre de courrier traités par mois***/
    public static void recupererLeNombreDeCourrierTraitesParDirectionParMoisPourLAnneeEnCours(String nomDirection) {

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

        String nombreDeCourrierRecusSQL = "select courrier.date_enregistrement,nom_direction from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '" + nomDirection + "'  and courrier.etat = '" + EtatCourrier.courrierEnvoye + "' group by courrier.id_courrier order by courrier.id_courrier desc";
        String nombreDeCourrierEnvoyesSQL = "select courrier.date_enregistrement,nom_direction from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on  envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '" + nomDirection + "'  and courrier.etat = '" + EtatCourrier.courrierEnvoye + "' group by courrier.id_courrier order by courrier.id_courrier desc";
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
    public static void recupererLeNombreDeCourrierRecusParDirection(String nomDirection){

        int nombreDeCourrierTempRecuDCSI = 0;
        int nombreDeCourrierTempRecuDCAF = 0;
        int nombreDeCourrierTempRecuDCRH = 0;
        int nombreDeCourrierTempRecuDGBFIP = 0;
        int nombreDeCourrierTempRecuTRESOR = 0;
        int nombreDeCourrierTempRecuIGS = 0;
        int nombreDeCourrierTempRecuAJE = 0;
        int nombreDeCourrierTempRecuCPPF = 0;
        int nombreDeCourrierTempRecuSGA = 0;
        int nombreDeCourrierTempRecuSG = 0;
        int nombreDeCourrierTempRecuCabinetMinistre = 0;
        int nombreDeCourrierTempRecuCabinetMinistreAdjoint = 0;
        int nombreDeCourrierTempRecuMinistre = 0;
        int nombreDeCourrierTempRecuMinistreDelegue = 0;

        List<String> listeIdCourriersRecus = new ArrayList<>();
        ResultSet resultSetIdRecus = null;
        Connection connectionCourrierIdRecus =  DatabaseConnection.getConnexion();
        String requeteIdCourriersRecusSQL = "select courrier.id_courrier from `recevoir_courrier` inner join `courrier` on recevoir_courrier.id_courrier = courrier.id_courrier inner join personne on recevoir_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '"+nomDirection+"' and recevoir_courrier.archive = '"+ EtatCourrier.archiveNonActive +"' and recevoir_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and etat = '"+EtatCourrier.courrierEnvoye+"'";

        try {
            resultSetIdRecus = connectionCourrierIdRecus.createStatement().executeQuery(requeteIdCourriersRecusSQL);
            while (resultSetIdRecus.next()){
                listeIdCourriersRecus.add(resultSetIdRecus.getString("courrier.id_courrier"));
            }

            for(int a =0; a < listeIdCourriersRecus.size(); a++){
                String valeur = recupererLaDirectionDeLEmetteurDUnCourrierParIdDuCourrier(listeIdCourriersRecus.get(a));
                if(valeur != null){
                    switch (valeur) {
                        case "DCSI":
                            nombreDeCourrierTempRecuDCSI++;
                            break;
                        case "DCAF":
                            nombreDeCourrierTempRecuDCAF++;
                            break;
                        case "DCRH":
                            nombreDeCourrierTempRecuDCRH++;
                            break;
                        case "Sécrétariat Générale":
                            nombreDeCourrierTempRecuSG++;
                            break;
                        case "Sécrétariat Générale Adjoint":
                            nombreDeCourrierTempRecuSGA++;
                            break;
                        case "AJE":
                            nombreDeCourrierTempRecuAJE++;
                            break;
                        case "CPPF":
                            nombreDeCourrierTempRecuCPPF++;
                            break;
                        case "DGBFIP":
                            nombreDeCourrierTempRecuDGBFIP++;
                            break;
                        case "IGS":
                            nombreDeCourrierTempRecuIGS++;
                            break;
                        case "TRESOR":
                            nombreDeCourrierTempRecuTRESOR++;
                            break;
                        case "Cabinet du Ministre Délégué":
                            nombreDeCourrierTempRecuCabinetMinistreAdjoint++;
                            break;
                        case "Cabinet du Ministre":
                            nombreDeCourrierTempRecuCabinetMinistre++;
                            break;
                        case "Ministre Délégué":
                            nombreDeCourrierTempRecuMinistreDelegue++;
                            break;
                        case "Ministre":
                            nombreDeCourrierTempRecuMinistre++;
                    }
                }
            }

            myMapCourrierRecusParDirection.put("DCSI",nombreDeCourrierTempRecuDCSI);
            myMapCourrierRecusParDirection.put("DCRH",nombreDeCourrierTempRecuDCRH);
            myMapCourrierRecusParDirection.put("DCAF",nombreDeCourrierTempRecuDCAF);
            myMapCourrierRecusParDirection.put("IGS",nombreDeCourrierTempRecuIGS);
            myMapCourrierRecusParDirection.put("SG",nombreDeCourrierTempRecuSG);
            myMapCourrierRecusParDirection.put("SGA",nombreDeCourrierTempRecuSGA);
            myMapCourrierRecusParDirection.put("Min D",nombreDeCourrierTempRecuMinistreDelegue);
            myMapCourrierRecusParDirection.put("Min",nombreDeCourrierTempRecuMinistre);
            myMapCourrierRecusParDirection.put("Cab Min D",nombreDeCourrierTempRecuCabinetMinistreAdjoint);
            myMapCourrierRecusParDirection.put("Cab Min",nombreDeCourrierTempRecuCabinetMinistre);
            myMapCourrierRecusParDirection.put("DGBFIP",nombreDeCourrierTempRecuDGBFIP);
            myMapCourrierRecusParDirection.put("TRESOR",nombreDeCourrierTempRecuTRESOR);
            myMapCourrierRecusParDirection.put("AJE",nombreDeCourrierTempRecuAJE);
            myMapCourrierRecusParDirection.put("CPPF",nombreDeCourrierTempRecuCPPF);

            myMapCourrierRecusParDirection.remove(nomDirection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***Fonction qui recupere le nombre de courrier envoyés par direction par mois***/
    public static void recupererLeNombreDeCourrierEnvoyesParDirection(String nomDirection){

        int nombreDeCourrierTempEnvoyesDCSI = 0;
        int nombreDeCourrierTempEnvoyesDCAF = 0;
        int nombreDeCourrierTempEnvoyesDCRH = 0;
        int nombreDeCourrierTempEnvoyesDGBFIP = 0;
        int nombreDeCourrierTempEnvoyesTRESOR = 0;
        int nombreDeCourrierTempEnvoyesIGS = 0;
        int nombreDeCourrierTempEnvoyesAJE = 0;
        int nombreDeCourrierTempEnvoyesCPPF = 0;
        int nombreDeCourrierTempEnvoyesSGA = 0;
        int nombreDeCourrierTempEnvoyesSG = 0;
        int nombreDeCourrierTempEnvoyesCabinetMinistre = 0;
        int nombreDeCourrierTempEnvoyesCabinetMinistreAdjoint = 0;
        int nombreDeCourrierTempEnvoyesMinistre = 0;
        int nombreDeCourrierTempEnvoyesMinistreDelegue = 0;

        List<String> listeIdCourriersEnvoyes = new ArrayList<>();
        ResultSet resultSetIdEnvoyes = null;
        Connection connectionCourrierIdEnvoyes =  DatabaseConnection.getConnexion();
        String requeteIdCourriersEnvoyesSQL = "select courrier.id_courrier from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.nom_direction = '"+nomDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' and envoyer_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and envoyer_courrier.archive =  '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";

        try {
            resultSetIdEnvoyes = connectionCourrierIdEnvoyes.createStatement().executeQuery(requeteIdCourriersEnvoyesSQL);
            while (resultSetIdEnvoyes.next()){
                listeIdCourriersEnvoyes.add(resultSetIdEnvoyes.getString("courrier.id_courrier"));
            }

            for(int a = 0; a < listeIdCourriersEnvoyes.size(); a++){
                String valeur = recupererLaDirectionDuReceveurDUnCourrierParIdDuCourrier(listeIdCourriersEnvoyes.get(a));
                if(valeur != null) {
                    switch (valeur) {
                        case "DCSI":
                            nombreDeCourrierTempEnvoyesDCSI++;
                            break;
                        case "DCAF":
                            nombreDeCourrierTempEnvoyesDCAF++;
                            break;
                        case "DCRH":
                            nombreDeCourrierTempEnvoyesDCRH++;
                            break;
                        case "Sécrétariat Générale":
                            nombreDeCourrierTempEnvoyesSG++;
                            break;
                        case "Sécrétariat Générale Adjoint":
                            nombreDeCourrierTempEnvoyesSGA++;
                            break;
                        case "AJE":
                            nombreDeCourrierTempEnvoyesAJE++;
                            break;
                        case "CPPF":
                            nombreDeCourrierTempEnvoyesCPPF++;
                            break;
                        case "DGBFIP":
                            nombreDeCourrierTempEnvoyesDGBFIP++;
                            break;
                        case "IGS":
                            nombreDeCourrierTempEnvoyesIGS++;
                            break;
                        case "TRESOR":
                            nombreDeCourrierTempEnvoyesTRESOR++;
                            break;
                        case "Cabinet du Ministre Délégué":
                            nombreDeCourrierTempEnvoyesCabinetMinistreAdjoint++;
                            break;
                        case "Cabinet du Ministre":
                            nombreDeCourrierTempEnvoyesCabinetMinistre++;
                            break;
                        case "Ministre Délégué":
                            nombreDeCourrierTempEnvoyesMinistreDelegue++;
                            break;
                        case "Ministre":
                            nombreDeCourrierTempEnvoyesMinistre++;
                    }
                }

            }

            myMapCourrierEnvoyesParDirection.put("DCSI",nombreDeCourrierTempEnvoyesDCSI);
            myMapCourrierEnvoyesParDirection.put("DCRH",nombreDeCourrierTempEnvoyesDCRH);
            myMapCourrierEnvoyesParDirection.put("DCAF",nombreDeCourrierTempEnvoyesDCAF);
            myMapCourrierEnvoyesParDirection.put("IGS",nombreDeCourrierTempEnvoyesIGS);
            myMapCourrierEnvoyesParDirection.put("SG",nombreDeCourrierTempEnvoyesSG);
            myMapCourrierEnvoyesParDirection.put("SGA",nombreDeCourrierTempEnvoyesSGA);
            myMapCourrierEnvoyesParDirection.put("Min D",nombreDeCourrierTempEnvoyesMinistreDelegue);
            myMapCourrierEnvoyesParDirection.put("Min",nombreDeCourrierTempEnvoyesMinistre);
            myMapCourrierEnvoyesParDirection.put("Cab Min D",nombreDeCourrierTempEnvoyesCabinetMinistreAdjoint);
            myMapCourrierEnvoyesParDirection.put("Cab Min",nombreDeCourrierTempEnvoyesCabinetMinistre);
            myMapCourrierEnvoyesParDirection.put("DGBFIP",nombreDeCourrierTempEnvoyesDGBFIP);
            myMapCourrierEnvoyesParDirection.put("TRESOR",nombreDeCourrierTempEnvoyesTRESOR);
            myMapCourrierEnvoyesParDirection.put("AJE",nombreDeCourrierTempEnvoyesAJE);
            myMapCourrierEnvoyesParDirection.put("CPPF",nombreDeCourrierTempEnvoyesCPPF);

            myMapCourrierEnvoyesParDirection.remove(nomDirection);
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



}
