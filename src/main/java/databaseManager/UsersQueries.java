package databaseManager;

import model.User;
import sessionManager.SessionUtils;
import variables.FonctionsUtilisateurs;
import variables.TypeDePersonne;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersQueries {

    public static String idPersonne;
    public static String idEtablissement;
    public static String etablissementUser;
    public static String passwordUser;
    public static String emailUser;
    public static String telUser;
    public static String serviceUser;
    public static String pseudUser;
    public static String nomUser;
    public static String prenomUser;
    public static String nomCompletUser;
    public static String directionUser;
    public static String idDirectionUser;
    public static String fonctionUser;
    public static String profilUser;
    public static String typeDeUser;
    public static String photoUser;
    public static boolean isResponsable = false;
    public static boolean isSecretaire = false;

    /***Fonction de verification de connexion d'un utilisateur***/
    public static boolean verifierUserLogin(String login, String motDePasse){
        boolean connected = false;
        Connection connection =  DatabaseConnection.getConnexion();
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
    public static List<User> recupererLaListeDesUsers(){
        List<User> usersList = new ArrayList<>();
        Connection connection =  DatabaseConnection.getConnexion();
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

    /***Fonction qui recupere les agents d'une direction***/
    public static List<User> recupererLesAgentsDUneDirection(String nomDirection) {
        List<User> agentDirectionList = new ArrayList<>();
        agentDirectionList.clear();
        String idTypeDePersonne = DataBaseQueries.recupererIdTypeDePersonneParTitre(TypeDePersonne.agentDuMinistere);
        String requeteAgentSQL = "select * from `personne` inner join `direction` on personne.id_direction = direction.id_direction inner join profil on personne.id_profil = profil.id_profil inner join fonction on personne.id_fonction = fonction.id_fonction where nom_direction = '"+nomDirection+"' and fk_type_personne = '"+idTypeDePersonne+"';";
        Connection connection = DatabaseConnection.getConnexion();
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

    /***Fonction qui recupere les infos d'un utilisateur***/
    public static void recupererInfosUsersParSonId(String idPersonne){
        Connection connection =  DatabaseConnection.getConnexion();
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

}
