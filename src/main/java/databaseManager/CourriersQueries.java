package databaseManager;

import model.Courrier;
import sessionManager.SessionUtils;
import variables.EtatCourrier;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourriersQueries {

    /***Fonction qui recupere tous les courriers reçus par un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersReçusParUnUtilisateurParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabasConnection.getConnexion();
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

    /***Fonction qui recupere tous les courriers envoyés d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersEnvoyesDUnUtilisateursParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabasConnection.getConnexion();
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
        Connection connection = DatabasConnection.getConnexion();
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

    /***Fonction qui recupere tous les courriers archivés d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersArchivesDUnUtilisateursParSonId(String idUtilisateur) {

        Connection connection = DatabasConnection.getConnexion();
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

    /***Fonction qui recupere tous les courriers enregistrés d'un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersEnregistresDUnUtilisateursParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabasConnection.getConnexion();
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

    /***Fonction qui recuperer l'id du type de courrier par son titre***/
    public static String recupererIdTypeDeCourrierParTitre(String typeDeCourrier) {
        String id = null;
        String recupererIdTypeCourrierSQL = "select id_type_courrier from `type_courrier` where titre_type_courrier = '" + typeDeCourrier + "'";
        Connection connection = DatabasConnection.getConnexion();
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

    /****Fonction qui récupère les type de courrier***/
    public static List<String> recupererLaListeDeTypesDeCourrier(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabasConnection.getConnexion();
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


}
