package databaseManager;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivitesQueries {

    /***Ajouter une activité à une action***/
    public static String ajouterUneActvitee(String titreActivite, String idCourrier, String idPersonne,String idTypeDActivites){

        String ajouterActiviteeSQL = "INSERT INTO `activites`  (`titre_activites`, `id_courrier`, `id_personne`,`id_type_activites`) VALUES" +
                " ('" + titreActivite + "',"+"'" +idCourrier + "',"+ "'" +idPersonne+ "',"+"'"+idTypeDActivites+"')";
        return ajouterActiviteeSQL;
    }

    /***Fonction qui retourne l'id du type d'une activité en fonction de son titre***/
    public  static String recupererIdTypeDActivitesParSonTitre(String titreDActivite){
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

}
