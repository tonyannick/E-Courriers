package alfresco;

import databaseManager.DirectionQueries;
import sessionManager.SessionUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class NomDesDossiers {

    public static final String fichierDiscussion = "fichier_discussion";
    public static String dossierCourrierAlfrescoEmetteur;
    public static String dossierDiscussionAlfrescoEmetteur;
    public static String dossierAnnexeAlfrescoEmetteur;

   /* public void configDossierAlfrescoEmetteur(){
        HttpSession httpSession = SessionUtils.getSession();
        String directionEmetteur = httpSession.getAttribute("directionUser").toString();
        List<String> directionListe = DirectionQueries.recupererLaListeDesDirections();
        for(int a=0; a < directionListe.size(); a++){
          if(directionListe.get(a).equalsIgnoreCase(directionEmetteur)){
              dossierCourrierAlfrescoEmetteur = "courri"
          }
        }

        switch (directionEmetteur){

        }
    }*/

  /* public List recupererLesDirectionsDuMinistere(){
        DirectionQueries.recupererLaListeDesDirections();
    }*/
}
