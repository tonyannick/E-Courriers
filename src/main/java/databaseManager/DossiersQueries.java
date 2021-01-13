package databaseManager;

import model.Courrier;
import model.Dossier;
import variables.ActionEtape;
import variables.EtatCourrier;
import variables.EtatEtape;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DossiersQueries {

    public static int nombreDeCourrierDansUnDossier= 0;

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static String recupererLeNomDUnDossierParSonId(String idDossier){
        String nomDossier = null;
        String recupererLesCourriersDUnDossiersSQL = " select nom_dossier from dossier where dossier.id_dossier = '"+idDossier+"' ;";
        ResultSet resultSet = null;
        Connection connection = DatabasConnection.getConnexion();

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

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static List<Courrier> recupererLesCourriersDansUnDossiersDUnUtilisateur(String idUser, String idDossier){
        List<Courrier> mesCourriers = new ArrayList<>();
        mesCourriers.clear();
        String recupererLesCourriersDUnDossiersSQL = " SELECT * FROM `courrier` left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join correspondance_personne_dossier on dossier.id_dossier = correspondance_personne_dossier.id_dossier inner join personne on correspondance_personne_dossier.id_personne = personne.id_personne where personne.id_personne = '"+idUser+"' and dossier.id_dossier = '"+idDossier+"' order by courrier.id_courrier desc";
        ResultSet resultSet = null;
        Connection connection = DatabasConnection.getConnexion();

        try {
            resultSet = connection.createStatement().executeQuery(recupererLesCourriersDUnDossiersSQL);
            while(resultSet.next()){
                mesCourriers.add(new Courrier(
                        resultSet.getString("reference"),
                        resultSet.getString("priorite"),
                        resultSet.getString("objet"),
                        resultSet.getString("courrier.date_enregistrement"),
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

    /***Renommer un dossier***/
    public static void renommerUnDossier(String idDossier,String nomDossier,String message){

        Connection connection = DatabasConnection.getConnexion();
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

    /**Fonction qui recupere les dossier d'un utilisateur**/
    public static List<Dossier> recupererLesDossiersDUnUtilisateur(String idUser){
        List<Dossier> listeDossiers = new ArrayList<>();
        listeDossiers.clear();
        String recupererLesDossiersSQL = "select nom_dossier,nom,prenom,dossier.id_dossier from dossier inner join correspondance_personne_dossier on dossier.id_dossier = correspondance_personne_dossier.id_dossier inner join personne on personne.id_personne = correspondance_personne_dossier.id_personne where personne.id_personne = '"+idUser+"' order by dossier.id_dossier desc";
        ResultSet resultSet = null;
        Connection connection = DatabasConnection.getConnexion();

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
    /***Ajouter un courrier à un dossier***/
    public static void ajouterUnCourrierDansUnDossier(String idDossier, String idCourrier, String idUser){

        Connection connection = DatabasConnection.getConnexion();

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

        Connection connection = DatabasConnection.getConnexion();

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
        Connection connection = DatabasConnection.getConnexion();

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

        Connection connection = DatabasConnection.getConnexion();

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



}
