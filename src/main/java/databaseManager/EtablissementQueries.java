package databaseManager;

import variables.TypeDEtablissement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EtablissementQueries {


    /****Fonction qui récupère les Ministères (leurs abreviations)***/
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
}
