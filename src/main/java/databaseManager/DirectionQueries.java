package databaseManager;

import model.Direction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectionQueries {

    public static String idMinistereDuBudget = "1";

    /****Fonction qui récupère les directions (objet) du Ministère***/
    public static List<Direction> recupererLaListeDesDirectionsDuMinistere(){

        List<Direction> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
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

    /****Fonction qui récupère les directions du Ministère autre que celle de l'utilisateur en cours et de l'emetteur du courrier***/
    public static List<String> recupererLaListeDesAutresDirections(String nomDirection, String directeurEmetteur){

        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
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

    /****Fonction qui récupère les noms des directions du Ministère***/
    public static List<String> recupererLaListeDesDirections(){
        List<String> list = new ArrayList<>();
        list.clear();
        Connection connection = DatabaseConnection.getConnexion();
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
}
