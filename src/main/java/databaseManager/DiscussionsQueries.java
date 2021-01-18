package databaseManager;

import model.Discussion;
import org.primefaces.PrimeFaces;
import variables.EtatEtape;

import javax.faces.context.FacesContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DiscussionsQueries {

    /***Clore une discussion***/
    public static void cloreUneDiscussion(String idEtape){

        String cloreDiscussionSQL = "update `discussion_etape` set `etat_discussion` = '"+ EtatEtape.Fermer+"' where id_etape = '"+idEtape+"'";
        String cloreEtatEtapeSQL = "update `etape` set `etat` = '"+EtatEtape.termine+"' where id_etape = '"+idEtape+"'";

        Connection connection = DatabaseConnection.getConnexion();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(cloreDiscussionSQL);
            statement.addBatch(cloreEtatEtapeSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            PrimeFaces.current().executeScript("new Toast({message: 'La discussion à été définitivement fermée !',type: 'warning'})");
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("new Toast({ message: 'Une erreurManager à été detectée lors de la fermeture de la discussion',type: 'danger'}))");
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

    /**Fonction qui recupere les discussions d'une étape**/
    public static List<Discussion> recupererLesDiscussionsDUneEtape(String idEtape){
        List<Discussion> listeDesDiscussion = new ArrayList<>();
        listeDesDiscussion.clear();
        String discussionSQL = "select * from `discussion_etape` inner join  `personne` on discussion_etape.id_personne = personne.id_personne where id_etape = " +  idEtape + " order by id_discussion_etape desc;";
        ResultSet resultSet = null;
        Connection connection = DatabaseConnection.getConnexion();
        try {
            resultSet = connection.createStatement().executeQuery(discussionSQL);
            while (resultSet.next()) {
                listeDesDiscussion.add(new Discussion(
                        resultSet.getString("id_discussion_etape"),
                        resultSet.getString("date_discussion"),
                        resultSet.getString("message_discussion"),
                        resultSet.getString("nom") + " "+ resultSet.getString("prenom"),
                        resultSet.getString("prenom")));
            }

            for (int i = 0; i < listeDesDiscussion.size(); i++){
                String jour = listeDesDiscussion.get(i).getDateDiscussion().substring(listeDesDiscussion.get(i).getDateDiscussion().lastIndexOf("-") +1);
                String mois = listeDesDiscussion.get(i).getDateDiscussion().substring(listeDesDiscussion.get(i).getDateDiscussion().indexOf("-")+1,listeDesDiscussion.get(i).getDateDiscussion().indexOf("-")+3);
                String annee = listeDesDiscussion.get(i).getDateDiscussion().substring(0,4);
                listeDesDiscussion.get(i).setDateDiscussion(jour+"-"+mois+"-"+annee);
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
        return listeDesDiscussion;
    }
}
