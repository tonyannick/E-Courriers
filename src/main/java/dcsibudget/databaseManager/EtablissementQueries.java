package dcsibudget.databaseManager;

import dcsibudget.logsManager.LoggerCreator;
import dcsibudget.variables.TypeDEtablissement;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EtablissementQueries {

    public static String nomCompletMinistere;
    public static String abreviationMinistere;
    private static Logger EtablissementQueriesLogger = LoggerCreator.creerUnLog("EtablissementQueries");

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
            LoggerCreator.definirMessageErreur(EtablissementQueriesLogger,"Erreur sql: "+ e);
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

    /***Fonction qui recuperer  l'id de l'établissement par son abreviation***/
    public static String recupererIdEtablissementParSonAbreviation(String etablissement) {
        String id = null;
        String recupererIdEtablissementSQL = "select id_etablissement from `etablissement` where abreviation = '" + etablissement + "'";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdEtablissementSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_etablissement");
            }
        } catch (SQLException e) {
            LoggerCreator.definirMessageErreur(EtablissementQueriesLogger,"Erreur sql: "+ e);
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

    /***Fonction qui recupere l'id du type d'etablissement  par son titre***/
    public static String recupererIdTypeDEtablissementParTitre(String type) {
        String id = null;
        String recupererIdTypeDEtablissemenntSQL = "select id_type_etablissement from `type_etablissement` where titre_type_etablissement = '" + type + "'";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(recupererIdTypeDEtablissemenntSQL);
            if (resultSet.next()) {
                id = resultSet.getString("id_type_etablissement");
            }
        } catch (SQLException e) {
            LoggerCreator.definirMessageErreur(EtablissementQueriesLogger,"Erreur sql: "+ e);
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

    public static void recupererInfosMinistereParSonId(String idMinistere){
        Connection connection =  DatabaseConnection.getConnexion();/** TODO à ameliorer**/
        String requeteLoginSQL = "select * from `etablissement` where id_etablissement = '"+idMinistere+"' and  fk_type_etablissement = '1' ;";
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteLoginSQL);
            if (resultSet.next()) {
                abreviationMinistere = resultSet.getString("abreviation");
                nomCompletMinistere = resultSet.getString("nom_etablissement");
            }
        } catch (SQLException e) {
            LoggerCreator.definirMessageErreur(EtablissementQueriesLogger,"Erreur sql: "+ e);
            e.printStackTrace();
        } finally {
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


}
