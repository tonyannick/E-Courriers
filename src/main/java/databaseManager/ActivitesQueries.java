package databaseManager;


import model.Activites;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivitesQueries {

    /***Ajouter une activité à une action***/
    public static String ajouterUneActvitee(String titreActivite, String idCourrier, String idPersonne,String idTypeDActivites,String idDirection){

        String ajouterActiviteeSQL = "INSERT INTO `activites`  (`titre_activites`, `id_courrier`, `id_personne`,`id_type_activites`,`id_direction`) VALUES" +
                " ('" + titreActivite + "',"+"'" +idCourrier + "',"+ "'" +idPersonne+ "',"+"'"+idTypeDActivites+"',"+"'"+idDirection+"')";
        return ajouterActiviteeSQL;
    }

    public static String ajouterUneActvitee(String titreActivite, String idPersonne,String idTypeDActivites,String idDirection){

        String ajouterActiviteeSQL = "INSERT INTO `activites`  (`titre_activites`, `id_personne`,`id_type_activites`,`id_direction`) VALUES" +
                " ('" + titreActivite + "',"+ "'" +idPersonne+ "',"+"'"+idTypeDActivites+"',"+"'"+idDirection+"')";
        return ajouterActiviteeSQL;
    }

    /***Fonction qui retourne l'id du type d'une activité en fonction de son titre***/
    public static String recupererIdTypeDActivitesParSonTitre(String titreDActivite){
        String id = null;
        String recupererIdTypeDActiviteSQL = "select id_type_activites from `type_activites` where titre_type_activites = '" + titreDActivite + "'";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdTypeDActiviteSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_type_activites");
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

    /***Fonction qu irécupere les activités d'un user par son id***/
    public static List recupererLesActivitesDUneDirectionParSonId(String idDirectionUser){
        List<Activites> mesActivites = new ArrayList<>();
        mesActivites.clear();
        String requeteListeDesActivitesSQL = " SELECT titre_activites,heure_activites,date_activites,courrier.id_courrier,objet,identifiant_alfresco,nom,prenom,nom_direction, titre_type_activites,etat FROM `activites` left join courrier on activites.id_courrier = courrier.id_courrier inner join personne on personne.id_personne = activites.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_activites on type_activites.id_type_activites = activites.id_type_activites where activites.id_direction = '"+idDirectionUser+"' order by activites.id_activites desc ;";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteListeDesActivitesSQL);
            while(resultSet.next()){
                mesActivites.add(new Activites(
                        resultSet.getString("titre_activites"),
                        resultSet.getString("heure_activites"),
                        resultSet.getString("date_activites"),
                        resultSet.getString("nom") +" "+ resultSet.getString("prenom"),
                        resultSet.getString("courrier.id_courrier"),
                        resultSet.getString("objet"),
                        resultSet.getString("etat")));
            }


            for (int i = 0; i < mesActivites.size(); i++){
                if(mesActivites.get(i).getDateActivites() != null){
                    String jour = mesActivites.get(i).getDateActivites().substring(mesActivites.get(i).getDateActivites().lastIndexOf("-") +1);
                    String mois = mesActivites.get(i).getDateActivites().substring(mesActivites.get(i).getDateActivites().indexOf("-")+1,mesActivites.get(i).getDateActivites().indexOf("-")+3);
                    String annee = mesActivites.get(i).getDateActivites().substring(0,4);
                    mesActivites.get(i).setDateActivites(jour+"-"+mois+"-"+annee);
                }

                if(mesActivites.get(i).getObjetCourrier() == null){
                    mesActivites.get(i).setObjetCourrier("Aucun");
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

        return mesActivites;
    }
}
