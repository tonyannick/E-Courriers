package databaseManager;

import model.Annotation;
import model.Courrier;
import model.Destinataire;
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

    public static int nombreDeDestinataireDuCourrier= 0;
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
    public static String heureDEnvoi;
    public static String dateDEnvoi;
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
    public static String fonctionPersonneAjouteurDuCourrier;
    public static String nomEtPrenomPersonneAjouteurDuCourrier;


    /***Fonction qui recupere tous les courriers reçus par un utilisateur***/
    public static List<Courrier> recupererTousLesCourriersReçusParUnUtilisateurParSonId(String idUtilisateur) {
        List<Courrier> mesCourriers = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnexion();
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
                        resultSet.getString("recevoir_courrier.date_reception"),
                        resultSet.getString("courrier.id_courrier"),
                        resultSet.getString("confidentiel"),
                        resultSet.getString("titre_type_courrier"),
                        resultSet.getString("id_recevoir_courrier"),
                        resultSet.getString("identifiant_alfresco"),
                        resultSet.getString("id_dossier"),
                        resultSet.getString("accuse_reception"),
                        resultSet.getString("transfer"),
                        resultSet.getString("mots_cles"),
                        resultSet.getString("reference_interne")));
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
        Connection connection = DatabaseConnection.getConnexion();
        String requeteMesCourriersSQL = null;
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
        requeteMesCourriersSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier where direction.id_direction = '"+idDirection+"' and courrier.etat = '"+EtatCourrier.courrierEnvoye+"' and envoyer_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and envoyer_courrier.archive =  '"+ EtatCourrier.archiveNonActive +"' order by envoyer_courrier.date_envoi desc;";
        System.out.println("requeteMesCourriersSQL = " + requeteMesCourriersSQL);
        ResultSet resultSet = null;
        try {

            resultSet = connection.createStatement().executeQuery(requeteMesCourriersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("envoyer_courrier.date_envoi"),
                        resultSet.getString("courrier.id_courrier"),
                        resultSet.getString("confidentiel"),
                        resultSet.getString("titre_type_courrier"),
                        resultSet.getString("id_envoyer"),
                        resultSet.getString("identifiant_alfresco"),
                        resultSet.getString("dossier.id_dossier"),
                        resultSet.getString("confirmation_reception"),
                        resultSet.getString("mots_cles")));
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
        Connection connection = DatabaseConnection.getConnexion();
        String requeteMesCourriersEnvoyesFavorisSQL = null;
        String requeteMesCourriersRecusFavorisSQL = null;
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");

        requeteMesCourriersEnvoyesFavorisSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier  left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction where direction.id_direction = '"+idDirection+"' and envoyer_courrier.favoris = '"+EtatCourrier.favoris+"' and envoyer_courrier.archive = '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";

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
                        resultSet1.getString("date_enregistrement"),
                        resultSet1.getString("id_courrier"),
                        resultSet1.getString("genre"),
                        resultSet1.getString("identifiant_alfresco")));
            }

            while(resultSet2.next()){
                mesCourriersRecus.add(new Courrier(
                        resultSet2.getString("reference"),
                        resultSet2.getString("priorite"),
                        resultSet2.getString("objet"),
                        resultSet1.getString("date_enregistrement"),
                        resultSet2.getString("id_courrier"),
                        resultSet2.getString("genre"),
                        resultSet2.getString("identifiant_alfresco")));
            }

            finalList = Stream.of(mesCourriersEnvoyes, mesCourriersRecus).flatMap(x -> x.stream()).collect(Collectors.toList());


           /* if(mesCourriersEnvoyes.size() > 0){

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

            finalList = Stream.concat(mesCourriersEnvoyes.stream(), mesCourriersRecus.stream()).collect(Collectors.toList());*/

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

        Connection connection = DatabaseConnection.getConnexion();
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
                        resultSet1.getString("date_enregistrement"),
                        resultSet1.getString("id_courrier"),
                        resultSet1.getString("genre"),
                        resultSet1.getString("identifiant_alfresco")));
            }

            while(resultSet2.next()){
                mesCourriersRecus.add(new Courrier(
                        resultSet2.getString("reference"),
                        resultSet2.getString("priorite"),
                        resultSet2.getString("objet"),
                        resultSet1.getString("date_enregistrement"),
                        resultSet2.getString("id_courrier"),
                        resultSet2.getString("genre"),
                        resultSet2.getString("identifiant_alfresco")));
            }
            finalList = Stream.of(mesCourriersEnvoyes, mesCourriersRecus).flatMap(x -> x.stream()).collect(Collectors.toList());
         /*   if(mesCourriersEnvoyes.size() > 0){

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
            }*/

            //finalList = Stream.concat(mesCourriersEnvoyes.stream(), mesCourriersRecus.stream()).collect(Collectors.toList());
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
        Connection connection = DatabaseConnection.getConnexion();
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
                        resultSet.getString("heure_enregistrement")));
            }

            for (int i = 0; i < mesCourriers.size(); i++){
                String jour = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().lastIndexOf("-") +1);
                String mois = mesCourriers.get(i).getDateDEnregistrement().substring(mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+1,mesCourriers.get(i).getDateDEnregistrement().indexOf("-")+3);
                String annee = mesCourriers.get(i).getDateDEnregistrement().substring(0,4);
                mesCourriers.get(i).setDateDEnregistrement(jour+"-"+mois+"-"+annee);
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
        Connection connection = DatabaseConnection.getConnexion();
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
        Connection connection = DatabaseConnection.getConnexion();
        String requete = "select titre_type_courrier from type_courrier order by titre_type_courrier ;";
        System.out.println("requete = " + requete);
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

    /****Fonction qui récupère les type de courrier (objet)***/
    public static List<Courrier> recupererTousLesTypesDeCourrier(){
        List<Courrier> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
        String requete = "select titre_type_courrier from type_courrier order by titre_type_courrier ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requete);
            while (resultSet.next()){
                list.add(new Courrier(
                        resultSet.getString("titre_type_courrier")));
            }
            
            for(int a =0; a <list.size(); a++){
                System.out.println("list.get(a).getTypeCourrier() = " + list.get(a).getTypeCourrier());
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

    /**Fonction qui recuperer les destinataires d'un courrier**/
    public static List<Destinataire> recupererLesDestinatairesDUnCourrier(String idCourrier){
        List<Destinataire> listeDestinataire = new ArrayList<>();
        listeDestinataire.clear();
        String avoirTousLesDestinatairesDuCourrierSQL = "select * from (`recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join `fonction` on personne.id_fonction = fonction.id_fonction left join `direction` on personne.id_direction = direction.id_direction left join `etablissement` on personne.id_etablissement = etablissement.id_etablissement ) where id_courrier = " + idCourrier + " and recevoir_courrier.transfer is NULL;";

        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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
        Connection connection = DatabaseConnection.getConnexion();

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

    /**Fonction qui recuperer le destinataire d'un courrier**/
    public static void recupererLeDestinataireDUnCourrierParIdCourrier(String idCourrier){
        Destinataire destinataire = new Destinataire();
        String requeteDetailEmetteurCourrierSQL = "select * from `recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where recevoir_courrier.id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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
        String requeteDetailCourrierSQL = "select * from `courrier` inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier inner join ajouter_courrier on courrier.id_courrier = ajouter_courrier.id_courrier inner join personne on ajouter_courrier.id_personne = personne.id_personne inner join fonction on personne.id_fonction = fonction.id_fonction where courrier.id_courrier  = " + idCourrier + " ;";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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
                nomEtPrenomPersonneAjouteurDuCourrier = resultSet.getString("nom")+" "+resultSet.getString("prenom");
                fonctionPersonneAjouteurDuCourrier = resultSet.getString("titre_fonction");
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

    /**Fonction qui recuperer l'emetteur d'un courrier**/
    public static void recupererLEmetteurDUnCourrierParIdCourrier(String idCourrier){
        String requeteDetailEmetteurCourrierSQL = "select * from `envoyer_courrier` inner join `personne` on envoyer_courrier.id_personne = personne.id_personne left join fonction on fonction.id_fonction = personne.id_fonction left join direction on personne.id_direction = direction.id_direction inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join etablissement on personne.id_etablissement = etablissement.id_etablissement where envoyer_courrier.id_courrier = " + idCourrier + ";";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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

                heureDEnvoi = resultSet.getString("heure_envoi");
                dateDEnvoi = resultSet.getString("date_envoi");

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

    /**Fonction qui recuperer les details d'un courrier reçu par un user**/
    public static void recupererLesDetailsDUnCourrierRecu(String idCourrier,String idDirection){
        String requeteDetailCourrierSQL = "select * from `courrier` inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier inner join recevoir_courrier on courrier.id_courrier = recevoir_courrier.id_courrier inner join personne on personne.id_personne =  recevoir_courrier.id_personne where courrier.id_courrier = '" + idCourrier + "' and personne.id_direction = '"+idDirection+"' ;";

        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
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

    /**Fonction qui recupere les annotations d'un courrier**/
    public static List<Annotation> recupererLesAnnotationsDUnCourrier(String idCourrier){
        List<Annotation> listeAnnotationsCourrier = new ArrayList<>();
        listeAnnotationsCourrier.clear();
        String recupererAnnotationsDuCourrierSQL = "select * from `annotation` inner join personne on personne.id_personne = annotation.id_personne where id_courrier = " + idCourrier + "  order by id_annotation desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();

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

    /**Fonction qui check si une personne est le destinataire d'un courrier par son id**/
    public static boolean voirSiUnePersonneEstUnDestinataireOuUnEmetteurParSonId(String idPersonne,String idCourrier,String nomTableDansDatabase){
        boolean isEmetteur = false;
        String requeteSQL = "select * from nomTableDansDatabase where id_courrier = '"+idCourrier+"' and id_personne = '"+idPersonne+"';";
        System.out.println("requeteSQL = " + requeteSQL);
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteSQL);
            if(resultSet.next()){
                isEmetteur = true;
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
        return isEmetteur;
    }
}
