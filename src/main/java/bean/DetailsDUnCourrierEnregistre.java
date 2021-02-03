package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.*;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import fileManager.PropertiesFilesReader;
import messages.FacesMessages;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import sessionManager.SessionUtils;
import model.Annotation;
import variables.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Named
@SessionScoped
public class DetailsDUnCourrierEnregistre implements Serializable {

    private static final long serialVersionUID = 3023305479829711674L;
    private Courrier courrier;
    private Annexe annexe;
    private Emetteur emetteur;
    private Destinataire destinataire;
    private Etape etape;
    private Annotation annotation;
    private Discussion discussion;
    private User user;
    private Fonction fonction;
    private Direction direction;
    private Etablissement etablissement;
    private String idAgentAffecteAUneTache = null;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private List<String> mesAgentsListe = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private String finalUniqueIDDestinataire;
    private String idRecevoirCourrier;
    private String elementDuCourrierAModifier;
    private boolean fichierDiscussionAjouter = false;
    private boolean isResponsable = false;
    private boolean isSecretaire = false;
    private Date dateFinalTemp = null;
    private boolean droitAjoutDestinataireExterne = false;
    private String dossierCourrierAlfresco;
    private String dossierDiscussionAlfresco;
    private String nomEtPrenomAjouteurCourrier;
    private String fonctionAjouteurCourrier;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
        annexe = new Annexe();
        emetteur = new Emetteur();
        destinataire = new Destinataire();
        etape = new Etape();
        user = new User();
        annotation = new Annotation();
        discussion = new Discussion();
        fonction = new Fonction();
        direction = new Direction();
        etablissement = new Etablissement();
        recupererListeDirection();
        recupererListeMinisteres();
        recupererListeTypeDeCourrier();
        HttpSession httpSession = SessionUtils.getSession();
        user.setUserDirection((httpSession.getAttribute("directionUser").toString()));
        if(user.getUserDirection().equalsIgnoreCase("sécrétariat générale adjoint")){
            user.setUserDirection("sga");
        }
        if(user.getUserDirection().equalsIgnoreCase("sécrétariat générale")){
            user.setUserDirection("sg");
        }
        PropertiesFilesReader.trouverLesDossiersDeLaDirectionDansAlfresco("dossiersAlfrescoMinistere.properties",user.getUserDirection());
        dossierCourrierAlfresco = user.getUserDirection().toLowerCase()+"/"+PropertiesFilesReader.mapDossiersDirectionDansAlfresco.get("courrier_"+user.getUserDirection().toLowerCase());
    }

    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
        }
    }

    public void checkDesDroitsPourRajouterDesDestinataireExterne(){
        HttpSession httpSession = SessionUtils.getSession();
        String fonctionUser = httpSession.getAttribute("fonctionUser").toString();
        switch (fonctionUser) {
            case FonctionsUtilisateurs.secretaireGeneral:
                droitAjoutDestinataireExterne = true;
                break;
            case FonctionsUtilisateurs.secretaireGeneralAdjoint:
               droitAjoutDestinataireExterne = true;
                break;
            case FonctionsUtilisateurs.directeurCabinet:
               droitAjoutDestinataireExterne = true;
                break;
            case FonctionsUtilisateurs.directeurGeneral:
               droitAjoutDestinataireExterne = true;
                break;
            case FonctionsUtilisateurs.directeurGeneralAdjoint:
               droitAjoutDestinataireExterne = true;
                break;
        }
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String typeDestinataireChoisie = params.get("destinataire");
        if(droitAjoutDestinataireExterne){

            switch (typeDestinataireChoisie){
                case "agentAutreMinistere":
                    PrimeFaces.current().executeScript("PF('dialogueAjouterDestinataireAutreMinistere').show()");
                    break;
                case "entrepriseOuAssociation":
                    PrimeFaces.current().executeScript("PF('dialogueAjouterDestinataireEntreprise').show()");
                    break;
                case "particulier":
                    PrimeFaces.current().executeScript("PF('dialogueAjouterDestinataireParticulier').show()");
                    break;
            }

        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets pas de réaliser cette action', 'warning');");
        }
    }

    public void voirAnnexe(ActionEvent actionEvent){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String annexeNumber = params.get("annexeACliquer");
        switch (annexeNumber){
            case "annexeUn":
                PrimeFaces.current().executeScript("affichageAnnexeUn()");
                FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 1" ,"Fichier Annexe 1"));
                break;
            case "annexeDeux":
                if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 1){
                    PrimeFaces.current().executeScript("affichageAnnexeDeux()");
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 2" ,"Fichier Annexe 2"));
                }else{
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_WARN,"Info" ,"Ce courrier n'a pas de deuxième fichier en annexe"));
                }
                break;
            case "annexeTrois":
                if (Integer.parseInt(annexe.getNombreDAnnexe()) > 2){
                    PrimeFaces.current().executeScript("affichageAnnexeTrois()");
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 3" ,"Fichier Annexe 3"));
                }else{
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_WARN,"Info" ,"Ce courrier n'a pas de troisieme fichier en annexe"));
                }
                break;
            case "annexeQuatre":
                if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 3){
                    PrimeFaces.current().executeScript("affichageAnnexeQuatre()");
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 4" ,"Fichier Annexe 4"));
                }else{
                    FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_WARN,"Info" ,"Ce courrier n'a pas de quatrième fichier en annexe"));
                }
                break;
        }
    }

    public void gestionDesAnnexesAuChargementDeLaPage(){
        listeIdAnnexeAlfresco.clear();
        HttpSession session = SessionUtils.getSession();
        courrier.setIdCourrier((String) session.getAttribute( "idCourrier"));
        annexe.setNombreDAnnexe(DataBaseQueries.recupererLeNombreDAnnexeDUnCourrier(courrier.getIdCourrier()));

        PrimeFaces.current().executeScript("affichageGridAnnexe()");
        if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 0){
            String voirListeAnnexeSQL = "Select * from `annexe` where id_courrier = "+courrier.getIdCourrier()+";";
            Connection connection = DatabaseConnection.getConnexion();
            ResultSet resultSet = null;

            try {
                resultSet = connection.createStatement().executeQuery(voirListeAnnexeSQL);;

                while (resultSet.next()){
                    listeIdAnnexeAlfresco.add(resultSet.getString("identifiant_alfresco_annexe"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if(resultSet != null){
                    try{
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if(connection != null){
                    try{
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            //FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 1" ,"Fichier Annexe 1"));
            annexe.setStreamAnnexeUn(ConnexionAlfresco.telechargerDocumentDansAlfresco(listeIdAnnexeAlfresco.get(0)));
            if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 1){
                annexe.setStreamAnnexeDeux(ConnexionAlfresco.telechargerDocumentDansAlfresco(listeIdAnnexeAlfresco.get(1)));
            }
            if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 2){
                annexe.setStreamAnnexeTrois(ConnexionAlfresco.telechargerDocumentDansAlfresco(listeIdAnnexeAlfresco.get(2)));
            }
            if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 3){
                annexe.setStreamAnnexeQuatre(ConnexionAlfresco.telechargerDocumentDansAlfresco(listeIdAnnexeAlfresco.get(3)));
            }

        }
    }

    public void recupererElementAModifier(ActionEvent actionEvent){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        elementDuCourrierAModifier = String.valueOf(params.get("valeurAModifier"));
    }

    public void recupererListeTypeDeCourrier(){
        courrier.setListeTypeDeCourier(CourriersQueries.recupererLaListeDeTypesDeCourrier());
    }

    public void miseAjourDUnElementDuCourrier(){

        HttpSession session = SessionUtils.getSession();

        String idUser = (String) session.getAttribute( "idUser" );
        String idCourrierAEnvoyer = (String) session.getAttribute( "idCourrier" );
        String table = null;
        String colonne = null;
        String idTableAModifier = null;
        String idValeurAConsiderer = null;
        String idTypeDeCourrier = null;

        String updateValueSQL = null;
        String faceMessageCibleDansFacelet = null;


        switch (elementDuCourrierAModifier){

            case "type":
                table = "courrier";
                colonne = "fk_type_courrier";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                idTypeDeCourrier = CourriersQueries.recupererIdTypeDeCourrierParTitre(courrier.getTempValeurAModifier());
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+idTypeDeCourrier+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messagesTypeCourrier";
                break;
            case "reference":
                table = "courrier";
                colonne = "reference";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+courrier.getTempValeurAModifier().trim().replaceAll("'"," ")+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messagesObjetReferenceCommentaireCourrier";
                break;
            case "objet":
                table = "courrier";
                colonne = "objet";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+courrier.getTempValeurAModifier().trim().replaceAll("'"," ")+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messagesObjetReferenceCommentaireCourrier";
                break;
            case "commentaires":
                table = "courrier";
                colonne = "commentaires";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+courrier.getTempValeurAModifier().trim().replaceAll("'"," ")+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messagesObjetReferenceCommentaireCourrier";
                break;
            case "priorite":
                table = "courrier";
                colonne = "priorite";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+courrier.getTempValeurAModifier().trim().replaceAll("'"," ")+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messagesPrioriteCourrier";
                break;
            case "confidentiel":
                table = "courrier";
                colonne = "confidentiel";
                idTableAModifier = "id_courrier";
                idValeurAConsiderer = idCourrierAEnvoyer;
                updateValueSQL = "update `"+table+"` set `"+colonne+"` = '"+courrier.getTempValeurAModifier().trim().replaceAll("'"," ")+"' where "+idTableAModifier+" = "+idValeurAConsiderer;
                faceMessageCibleDansFacelet = "messageConfidentielCourrier";
                break;


        }

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.miseAJour +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.miseAJourContenu+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+  idCourrierAEnvoyer+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        if(courrier.getTempValeurAModifier().isEmpty()){
            FacesContext.getCurrentInstance().addMessage(faceMessageCibleDansFacelet, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Vous devez renseigner une valeur"));
        }else{
            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;

            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(updateValueSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.executeBatch();
                connection.commit();
                courrier.setTempValeurAModifier(null);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                PrimeFaces.current().executeScript("PF('listeFonctionMinistere').hide()");
                FacesContext.getCurrentInstance().addMessage(faceMessageCibleDansFacelet, new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Modification réussie"));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage(faceMessageCibleDansFacelet, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreure s'est produite"));
                e.printStackTrace();
            }finally {
                if ( statement!= null) {
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

    public void actualisationDesDetailsDuCourrierAuChargementDeLaPage(){
        HttpSession session = SessionUtils.getSession();
        String idDocumementAlfresco = (String) session.getAttribute("idAlfresco");
        courrier.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(idDocumementAlfresco));
        if(!ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
            courrier.setIsPDF("non");
            PrimeFaces.current().executeScript("affichagePDF()");
        }else if(ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
            courrier.setIsPDF("oui");
            PrimeFaces.current().executeScript("affichagePDF()");
        }
    }

    public void recupererToutesLesInfosDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        CourriersQueries.recupererLEmetteurDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLeDestinataireDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLesDetailsDUnCourrierEnregistre(idCourrier);
        emetteur.setTypeDEmetteur(CourriersQueries.typeDemetteur);
        emetteur.setMinistere(CourriersQueries.ministereEmetteur);
        emetteur.setDirection(CourriersQueries.directeurEmetteur);
        emetteur.setFonction(CourriersQueries.fonctionEmetteur);
        courrier.setObjetCourrier(CourriersQueries.objetCourrier);
        courrier.setReferenceCourrier(CourriersQueries.referenceCourrier);
        courrier.setPrioriteCourrier(CourriersQueries.prioriteCourrier);
        courrier.setTypeCourrier(CourriersQueries.typeCourrier);

        courrier.setDossierAlfresco(CourriersQueries.dossierAlfresco);
        courrier.setConfidentiel(CourriersQueries.confidentiel);
        courrier.setHeureDeReception(CourriersQueries.heureDeReception);
        courrier.setDateDEnregistrement(CourriersQueries.dateDEnregistrement);
        courrier.setDateDeReception(CourriersQueries.dateDeReception);
        courrier.setCommentairesCourrier(CourriersQueries.commentairesCourrier);

        nomEtPrenomAjouteurCourrier = CourriersQueries.nomEtPrenomPersonneAjouteurDuCourrier;
        fonctionAjouteurCourrier = CourriersQueries.fonctionPersonneAjouteurDuCourrier;

        String jour = courrier.getDateDeReception().substring(courrier.getDateDeReception().lastIndexOf("-") +1);
        String mois = courrier.getDateDeReception().substring(courrier.getDateDeReception().indexOf("-")+1,courrier.getDateDeReception().indexOf("-")+3);
        String annee = courrier.getDateDeReception().substring(0,4);

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);

        courrier.setDateDeReception(jour+"-"+mois+"-"+annee);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);
        courrier.setHeureDEnregistrement(CourriersQueries.heureDEnregistrement);
    }

    public void recupererHistoriqueDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        etape.setListeHistoriques(DataBaseQueries.recupererLHistoriqueDesActionsSurUnCourrierEnregistre(idCourrier));
    }

    public void recupererLesDestinatairesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        destinataire.setListeDestinataire(CourriersQueries.recupererLesDestinatairesDUnCourrier(idCourrier));
        destinataire.setNombreDeDestinataire(String.valueOf(CourriersQueries.nombreDeDestinataireDuCourrier));
    }

    public void recupererLesActionsParEtapeDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        etape.setListeDesActionsSurLeCourrier(DataBaseQueries.recupererLesActionsEffectueesSurUnCourrier(idCourrier));
        etape.setNombreDeTache(String.valueOf(etape.getListeDesActionsSurLeCourrier().size()));
    }

    public void recupererLesAnnotationsDUnCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        annotation.setListeDesAnnotations(CourriersQueries.recupererLesAnnotationsDUnCourrier(idCourrier));
    }

    public List<Discussion> recupererLesDiscussionsDUneEtape(){
        discussion.setListeDiscussion(DiscussionsQueries.recupererLesDiscussionsDUneEtape(discussion.getIdEtape()));
        return discussion.getListeDiscussion();
    }

    public void clickExpansionToggleEtape(ToggleEvent toggleEvent){
        discussion.setIdEtape(((Etape)toggleEvent.getData()).getId());
        discussion.setListeDiscussion(DiscussionsQueries.recupererLesDiscussionsDUneEtape(discussion.getIdEtape()));

        PrimeFaces.current().executeScript("PF('panelloadingdiscussion').close()");
    }

    public void ajouterUneAnnotation(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier= (String) session.getAttribute( "idCourrier");
        String idUser = (String) session.getAttribute( "idUser");

        if(annotation.getMessageNote().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageAnnotation", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Erreur!!!", "Vous devez ecrire un message !!!"));
        }else{
            String ajouterAnnotationAuCourrierSQL = "INSERT INTO `annotation` (`texte`,`id_courrier`,`id_personne`) VALUES" +
                    " ('"+annotation.getMessageNote().trim().replaceAll("'"," ") +"',"+"'"+ idCourrier+ "',"+"'"+idUser+"')";

            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                    " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajouterUneNote+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnregistre+"')";


            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                    " ('" + idUser +"')";

            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(ajouterAnnotationAuCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.executeBatch();
                connection.commit();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                annotation.setMessageNote(null);
                FacesContext.getCurrentInstance().addMessage("messageAnnotation", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Annotation bien ajoutée !!!"));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("messageAnnotation", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Erreur!!!", "Une erreur s'est produite lors de l'ajout de votre annotation, veuillez réessayer !!!"));
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

    public List<String> recupererLesAgentsDUneDirection(String query) {
        userList.clear();
        mesAgentsListe.clear();
        HttpSession session = SessionUtils.getSession();
        String direction = (String) session.getAttribute("directionUser");

        String idTypeDePersonne = DataBaseQueries.recupererIdTypeDePersonneParTitre(TypeDePersonne.agentDuMinistere);
        String requeteAgentSQL = "select * from `personne` inner join `direction` on personne.id_direction = direction.id_direction inner join profil on personne.id_profil = profil.id_profil inner join fonction on personne.id_fonction = fonction.id_fonction where nom_direction = '"+direction+"' and fk_type_personne = '"+idTypeDePersonne+"';";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
             resultSet = connection.createStatement().executeQuery(requeteAgentSQL);
            while ( resultSet.next()) {
               userList.add(new User(
                         resultSet.getString("id_personne"),
                         resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                         resultSet.getString("titre_fonction")));
            }

            for (int i = 0; i <  userList.size(); i++){
                mesAgentsListe.add(  userList.get(i).getUserName()+" ("+ userList.get(i).getUserFonction()+")");
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

        return  mesAgentsListe;
    }

    public void recupererIdDeLAgentChoisi(SelectEvent event) {
        for(int i = 0; i < userList.size(); i++){
            if (userList.get(i).getUserName().contains(event.getObject().toString().substring(0,event.getObject().toString().indexOf("(") - 1))){
                idAgentAffecteAUneTache = userList.get(i).getUserId();
            }
        }
    }

    public void ouvrirDialogueTransmettreAUnAgent(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            PrimeFaces.current().executeScript("PF('dlgAffecter').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
        }
    }

    public void ouvrirDialogueAjouterUneAnnotation(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            PrimeFaces.current().executeScript("PF('ajouterAnnotation').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
        }
    }

    public void ouvrirDialogueRemplacerLeFichierDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            PrimeFaces.current().executeScript("ouvrirModalBoxChangerFichierDuCourrier()");
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
        }
    }

    public void ouvrirDialogueEnvoyerLeCourrier(){
        HttpSession session = SessionUtils.getSession();

        isResponsable = (Boolean) session.getAttribute("isResponsable");
        isSecretaire = (Boolean) session.getAttribute("isSecretaire");
        if(isResponsable){
            PrimeFaces.current().executeScript("PF('dialogueValiderEnvoiDuCourrier').show()");
        }else{
            if(isSecretaire){
                PrimeFaces.current().executeScript("PF('dialogueValiderEnvoiDuCourrier').show()");
            }else{
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
            }

        }
    }

    public void transmettreAUnAgent(){

        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute( "idCourrier");
        String ajouterEtapeCourrierSQL;
        if (idAgentAffecteAUneTache == null ){
            FacesMessages.warningMessage("messageaffectation","Erreur", "Vous devez selectionner un agent !!");
        }else{
            if(etape.getMessage().isEmpty() ){
                FacesMessages.warningMessage("messageaffectation","Erreur", "Vous devez ecrire un message !!");
            }else{
                if (dateFinalTemp == null){
                    ajouterEtapeCourrierSQL = "insert into `etape` ( `titre`, `etat`, `message` ) VALUES ('"+ ActionEtape.transmisPourTraitement+"',"+"'"+EtatEtape.enTraitement+"'," +"'"+etape.getMessage().trim().replaceAll("'", " ")+"')";
                }else{
                    etape.setDate_fin( DateUtils.convertirDateEnString(dateFinalTemp));
                    ajouterEtapeCourrierSQL = "insert into `etape` ( `titre`, `etat`, `date_fin`, `message` ) VALUES ('"+ ActionEtape.transmisPourTraitement+"',"+"'"+EtatEtape.enTraitement+"',"+"'"+ etape.getDate_fin()+"'," +"'"+etape.getMessage().trim().replaceAll("'", " ").replaceAll("<p>","").replaceAll("</p>","")+"')";
                }

                String ajouterCorrespondanceEtapePersonneAffecteurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`) VALUES" +
                        " ('" + idUser +"',"+"'"+TypeDePersonne.affecteurTache+"')";

                String ajouterCorrespondanceEtapePersonneReceveurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`, `etat_tache`) VALUES" +
                        " ('" + idAgentAffecteAUneTache+"',"+"'"+TypeDePersonne.receveurTache+"',"+"'"+EtatEtape.PasRepondu +"')";

                String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                        "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnregistre+"')";

                Connection connection = DatabaseConnection.getConnexion();
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    statement.addBatch(ajouterCorrespondanceEtapePersonneAffecteurSQL);
                    statement.addBatch(ajouterCorrespondanceEtapePersonneReceveurSQL);
                    statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                    statement.addBatch(ajouterEtapeCourrierSQL);
                    statement.executeBatch();
                    connection.commit();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesMessages.infoMessage("messageaffectation","Validation", "Transmision effectuée avec succés");
                    etape.setMessage(null);
                    etape.setDate_fin(null);
                    etape.setActeur(null);
                } catch (SQLException e) {
                    FacesMessages.errorMessage("messageaffectation","Erreur", "Une erreur avec la base de données s'est produite !!!");
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


    }

    public void recupererLeDestinataireDUneTache(ActionEvent actionEvent){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idTache = String.valueOf(params.get("etapeId"));
        String  requeteDestinataireTacheSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' order by etape.id_etape desc";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try{
            resultSet = connection.createStatement().executeQuery(requeteDestinataireTacheSQL);
            while (resultSet.next()){
                etape.setDestinataire(resultSet.getString("nom")+" "+resultSet.getString("prenom"));
            }
            PrimeFaces.current().executeScript("PF('dlgDetailsEtape').show()");
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagerreurvoirdestinatairedunetache",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur avec la base de données s'est produitre"));
        }

    }

    public void voirEtatDeLaDiscussion(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idEtape = String.valueOf(params.get("etapeId"));
        etape.setId(idEtape);
        for (int i = 0; i < etape.getListeDesActionsSurLeCourrier().size(); i++) {
            if ( etape.getListeDesActionsSurLeCourrier().get(i).getId().equals(idEtape)){
                if (etape.getListeDesActionsSurLeCourrier().get(i).getEtat().equals(EtatEtape.termine)){
                    PrimeFaces.current().executeScript("swal('Oups','Cette discussion à déjé été cloturée', 'warning');");
                }else{
                    PrimeFaces.current().executeScript("openFormDiscussion()");
                }
            }
        }
    }

    public void voirLeFichierDUneDiscussion(ActionEvent actionEvent){
        discussion.setStreamedContentAlfresco(null);
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDiscussionEtape = String.valueOf(params.get("discussionEtapeId"));
        String  requeteDiscussionEtapeSQL = "select identifiant_alfresco_discussion from `discussion_etape` where id_discussion_etape = '"+idDiscussionEtape+"' ; ";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try{
            resultSet = connection.createStatement().executeQuery(requeteDiscussionEtapeSQL);
            if (resultSet.next()){

                discussion.setIdAlfresco(resultSet.getString("identifiant_alfresco_discussion"));

                if(discussion.getIdAlfresco() != null){
                    discussion.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(discussion.getIdAlfresco()));
                    if(!ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
                        discussion.setIsFichierDiscussionPDF("non");
                    }else if(ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
                        discussion.setIsFichierDiscussionPDF("oui");
                    }
                    PrimeFaces.current().executeScript("PF('dlgFichierDiscussion').show()");
                    PrimeFaces.current().executeScript("affichagePDFDuFichierDeLaDiscussion()");
                }else{

                    PrimeFaces.current().executeScript("swal('Aucun fichier','Pas de fichier joint à ce message', 'warning');");
                }

            }

        } catch (SQLException e) {
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite avec la base de données', 'warning');");
            e.printStackTrace();
        }

    }

    public void creerUneDiscussion(){/*TODO bloque un message vide*/
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = session.getAttribute("directionUser").toString();
        PropertiesFilesReader.trouverLesDossiersDeLaDirectionDansAlfresco("dossiersAlfrescoMinistere.properties",directionUser);

        if(directionUser.equalsIgnoreCase("sécrétariat générale adjoint")){
            directionUser = "sga";
        }
        if(directionUser.equalsIgnoreCase("sécrétariat générale")){
            directionUser = "sg";
        }

        dossierCourrierAlfresco = directionUser.toLowerCase()+"/"+PropertiesFilesReader.mapDossiersDirectionDansAlfresco.get("courrier_"+directionUser.toLowerCase());
        dossierDiscussionAlfresco = directionUser.toLowerCase()+"/"+PropertiesFilesReader.mapDossiersDirectionDansAlfresco.get("discussion_"+directionUser.toLowerCase());
        Connection connection = DatabaseConnection.getConnexion();

        if(etape.getReponseTache() == null || etape.getReponseTache().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageerreurdiscussion", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez ecrire un message!!"));
        }else{
            String creerDiscussionSQL = "insert into `discussion_etape` (`etat_discussion`,`message_discussion`,`id_etape`,`id_personne`) VALUES" +
                    " ('" + EtatEtape.Ouvert +"',"+"'"+etape.getReponseTache().replaceAll("'", " ")+"',"+"'"+etape.getId()+"',"+"'"+idUser+"')";

            String updateIdAlfrescoDansDiscussionSQL = null;
            String updateNomFichierDansDiscussionSQL = null;

            if(fichierDiscussionAjouter){
                discussion.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(discussion.getCheminFichierDiscussionSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(discussion.getNomFichierDiscussion())), dossierDiscussionAlfresco));
                updateIdAlfrescoDansDiscussionSQL = "update `discussion_etape` set `identifiant_alfresco_discussion` = '"+discussion.getIdAlfresco()+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
                updateNomFichierDansDiscussionSQL = "update `discussion_etape` set `nom_fichier_discussion` = '"+discussion.getNomFichierDiscussion()+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
            }

            String changerCorrespondanceEtapePersonneReceveurSQL = "UPDATE `correspondance_personne_etape` set `etat_tache` = '"+EtatEtape.Repondu+"' where correspondance_personne_etape.id_etape = '"+etape.getId()+"'";

            Statement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(creerDiscussionSQL);
                statement.addBatch(changerCorrespondanceEtapePersonneReceveurSQL);
                if (updateIdAlfrescoDansDiscussionSQL != null) {
                    statement.addBatch(updateIdAlfrescoDansDiscussionSQL);
                }
                if (updateNomFichierDansDiscussionSQL != null) {
                    statement.addBatch(updateNomFichierDansDiscussionSQL);
                }
                statement.executeBatch();
                connection.commit();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                PrimeFaces.current().executeScript("swal('Validation','Votre message à bien été posté', 'success');");
                etape.setReponseTache(null);
                PrimeFaces.current().executeScript("closeFormDiscussion()");
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite avec la base de donnée', 'error');");
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

    public void fermerLaDiscussionDUneEtape(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idEtape = String.valueOf(params.get("etapeId"));
        etape.setId(idEtape);
        for (int i = 0; i < etape.getListeDesActionsSurLeCourrier().size(); i++) {
            if (etape.getListeDesActionsSurLeCourrier().get(i).getId().equals(idEtape)){
                if (etape.getListeDesActionsSurLeCourrier().get(i).getEtat().equals(EtatEtape.termine)){
                    PrimeFaces.current().executeScript("swal('Oups','Cette tache et les discussions associées ont déja étés cloturées', 'warning');");
                }else{
                    PrimeFaces.current().executeScript("PF('confirmerClotureDiscussion').show()");
                }
            }
        }
    }


    public void fermerDefinitivementUneDiscussion(){
        DiscussionsQueries.cloreUneDiscussion(etape.getId());
    }

    public List<String> recupererListeFonctionsParDirectionDuDestinataire(){
        fonction.setListeFonction(DataBaseQueries.recupererLaListeDesFonctionsParDirection(destinataire.getDirection()));
        return fonction.getListeFonction();
    }

    private void genererUniqueIDPourEmetteurEtDestinataire(){
        UUID randomDestinataireID = UUID.randomUUID();
        finalUniqueIDDestinataire = "destinataire" +"_"+randomDestinataireID.toString();
    }

    public void ajouterDestinataireAgentDuMinistere(){

        if(destinataire.getFonction() == null ||destinataire.getFonction().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentDuMinistere", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez renseigner la fonction !!!"));
        }else{
            if(destinataire.getDirection() == null){
                FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentDuMinistere", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez renseigner la direction!!!"));
            }else{
                HttpSession session = SessionUtils.getSession();

                String idUser = (String) session.getAttribute( "idUser");
                String idCourrierAEnvoyer = (String) session.getAttribute( "idCourrier");

                genererUniqueIDPourEmetteurEtDestinataire();
                String ajouterDestinataireSQL;
                String recevoirCourrierSQL;
                String idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre("Agent du Ministere");
                String idFonctionDestinataire = DirectionQueries.recupererIdFonctionParSonTitreEtSonType(destinataire.getFonction(), TypeDeFonctions.interne);
                String idDirectionDestinataire = DirectionQueries.recupererIdDirectionParSonNom(destinataire.getDirection());
                String idEtablissementDestinataire = EtablissementQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);

                ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                        " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+"'"+idCourrierAEnvoyer+"',(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"'), '"+EtatCourrier.accuseDeReceptionNon+"');";

                String ajouterEtapeCourrierSQL = "insert into `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDestinataire+"')";

                String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                        "('"+  idCourrierAEnvoyer+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

                String ajouterCorrespondanceEtapePersonneSQL = "insert into `correspondance_personne_etape` (`id_personne`) VALUES" +
                        " ('" + idUser +"')";
                Connection connection = DatabaseConnection.getConnexion();
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    statement.addBatch(ajouterDestinataireSQL);
                    statement.addBatch(recevoirCourrierSQL);
                    statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                    statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                    statement.addBatch(ajouterEtapeCourrierSQL);
                    statement.executeBatch();
                    connection.commit();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    destinataire.setFonction(null);
                    destinataire.setDirection(null);
                    FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentDuMinistere", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Un destinataire à bien été ajouté au courrier !!"));
                    //PrimeFaces.current().executeScript("new Toast({message: 'Un destinataire à bien été ajouté au courrier !',type: 'success'})");
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentDuMinistere", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur avec la base de données s'est produite !!!"));
                    //PrimeFaces.current().executeScript("new Toast({ message: 'Une erreur à été detectée',type: 'danger'}))");
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



    }

    public void ajouterDestinataireAgentAutreMinistere(){

        if(destinataire.getMinistereAutreMinistere() == null || destinataire.getMinistereAutreMinistere().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentAutreMinistere",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner le ministère !!!"));
        }else{
            if(destinataire.getFonctionAutreMinistere().isEmpty()){
                FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentAutreMinistere",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner la fonction !!!"));
            }else{
                if(destinataire.getDirectionAutreMinistere().isEmpty()){
                    FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentAutreMinistere",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner la direction !!!"));
                }else{

                    HttpSession session = SessionUtils.getSession();
                    String idUser = (String) session.getAttribute( "idUser");
                    String idCourrierAEnvoyer = (String) session.getAttribute( "idCourrier");
                    genererUniqueIDPourEmetteurEtDestinataire();

                    String idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre("Agent autre Ministère");

                    String idEtablissementDestinataireAutreMinistere = EtablissementQueries.recupererIdEtablissementParSonAbreviation(destinataire.getMinistereAutreMinistere());

                    String ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + destinataire.getFonctionAutreMinistere().trim().replaceAll("'"," ") +"')";

                    String ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + destinataire.getDirectionAutreMinistere().trim().replaceAll("'"," ").toUpperCase() + "'," +"'"+  idEtablissementDestinataireAutreMinistere + "')";

                    String ajouterDestinataireSQL = "INSERT INTO `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                            " ('" +finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"(select id_fonction from `fonction` where titre_fonction = '" + destinataire.getFonctionAutreMinistere() + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + destinataire.getDirectionAutreMinistere() + "' order by id_direction desc limit 1)" + ","+ "'" +idEtablissementDestinataireAutreMinistere + "')";

                    String recevoirCourrierSQL = "insert into recevoir_courrier (`id_courrier`,`id_personne`) VALUES ('"+idCourrierAEnvoyer+"',(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"'));";

                    String ajouterEtapeCourrierSQL = "insert into `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDestinataire+"')";

                    String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                            "('"+  idCourrierAEnvoyer+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

                    String ajouterCorrespondanceEtapePersonneSQL = "insert into `correspondance_personne_etape` (`id_personne`) VALUES" +
                            " ('" + idUser +"')";
                    Connection connection = DatabaseConnection.getConnexion();
                    Statement statement = null;

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(ajouterFonctionSQL);
                        statement.addBatch(ajouterDirectionSQL);
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);
                        statement.executeBatch();
                        connection.commit();
                        destinataire.setMinistereAutreMinistere(null);
                        destinataire.setDirectionAutreMinistere(null);
                        destinataire.setFonctionAutreMinistere(null);
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentAutreMinistere", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Un nouveau destinataire à bien été ajouté à ce courrier !!!"));
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("ajouterDestinataireAgentAutreMinistere", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur inconnue s'est produite !!!"));
                        e.printStackTrace();
                    }finally {
                        if ( statement!= null) {
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

        }


    }

    public void ajouterDestinataireEntreprise(){

        if(destinataire.getRaisonSocial().isEmpty() ){
            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner la raison social ministère !!!"));
        }else{
            if(destinataire.getDirectionEntreprise().isEmpty() ){
                FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner la direction !!!"));
            }else{
                if(destinataire.getFonctionEntreprise().isEmpty() ){
                    FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner la fonction !!!"));
                }else{
                    if(destinataire.getTelephoneEntreprise().isEmpty() ){
                        FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise",
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner le téléphone!!!"));
                    } else{
                        HttpSession session = SessionUtils.getSession();
                        String idUser = (String) session.getAttribute( "idUser");
                        String idCourrierAEnvoyer = (String) session.getAttribute( "idCourrier");
                        genererUniqueIDPourEmetteurEtDestinataire();

                        String idTypeDetablissement= EtablissementQueries.recupererIdTypeDEtablissementParTitre(TypeDEtablissement.entreprisePrivee);
                        String idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre("Entreprise");

                        String ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + destinataire.getFonctionEntreprise().trim().replaceAll("'"," ") +"')";

                        String ajouterEtablissementSQL = "INSERT INTO `etablissement` (`nom_etablissement`, `tel_etablissement`, `mail_etablissement`, `adresse_etablissement`, `fk_type_etablissement`) VALUES" +
                                " ('" + destinataire.getRaisonSocial().trim().replaceAll("'"," ")+"',"+"'" +destinataire.getTelephoneEntreprise()+"',"+"'"+ destinataire.getEmailEntreprise() + "',"+"'" +destinataire.getAdresseEntreprise().trim().replaceAll("'"," ")+ "',"+ "'" +idTypeDetablissement + "')";

                        String ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + destinataire.getDirectionEntreprise().trim().replaceAll("'"," ") + "'," +  "(select id_etablissement from `etablissement` where nom_etablissement = '" + destinataire.getRaisonSocial().trim().replaceAll("'"," ") + "' and tel_etablissement  = '"+destinataire.getTelephoneEntreprise() +"' and mail_etablissement = '"+destinataire.getEmailEntreprise()+"'  and adresse_etablissement = '"+destinataire.getAdresseEntreprise().trim().replaceAll("'"," ") +"' order by  id_etablissement desc limit 1)"+ ")";

                        String ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + destinataire.getFonctionEntreprise().trim().replaceAll("'"," ") + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + destinataire.getDirectionEntreprise().trim().replaceAll("'"," ") + "' order by id_direction desc limit 1)" + ","+ "(select id_etablissement from `etablissement` where nom_etablissement = '" + destinataire.getRaisonSocial().trim().replaceAll("'"," ") + "' and tel_etablissement  = '"+destinataire.getTelephoneEntreprise() +"' and mail_etablissement = '"+destinataire.getEmailEntreprise().trim().replaceAll("'"," ")+"'  and adresse_etablissement = '"+destinataire.getAdresseEntreprise().trim().replaceAll("'"," ") +"' order by id_etablissement desc limit 1)"+ ")";

                        String recevoirCourrierSQL = "insert into recevoir_courrier (`id_courrier`,`id_personne`) VALUES ('"+idCourrierAEnvoyer+"',(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"'));";


                        String ajouterEtapeCourrierSQL = "insert into `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDestinataire+"')";

                        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                                "('"+  idCourrierAEnvoyer+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

                        String ajouterCorrespondanceEtapePersonneSQL = "insert into `correspondance_personne_etape` (`id_personne`) VALUES" +
                                " ('" + idUser +"')";
                        Connection connection = DatabaseConnection.getConnexion();
                        Statement statement = null;

                        try {
                            connection.setAutoCommit(false);
                            statement = connection.createStatement();
                            statement.addBatch(ajouterFonctionSQL);
                            statement.addBatch(ajouterEtablissementSQL);
                            statement.addBatch(ajouterDirectionSQL);
                            statement.addBatch(ajouterDestinataireSQL);
                            statement.addBatch(recevoirCourrierSQL);
                            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                            statement.addBatch(ajouterEtapeCourrierSQL);
                            statement.executeBatch();
                            connection.commit();
                            destinataire.setRaisonSocial(null);
                            destinataire.setTelephoneEntreprise(null);
                            destinataire.setAdresseEntreprise(null);
                            destinataire.setEmailEntreprise(null);
                            destinataire.setFonctionEntreprise(null);
                            destinataire.setDirectionEntreprise(null);
                            FacesContext context = FacesContext.getCurrentInstance();
                            context.getExternalContext().getFlash().setKeepMessages(true);
                            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Un nouveau destinataire à bien été ajouté à ce courrier !!!"));
                        } catch (SQLException e) {
                            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireEntreprise", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur inconnue s'est produite !!!"));
                            e.printStackTrace();
                        }finally {
                            if ( statement!= null) {
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
            }
        }

    }

    public void ajouterDestinataireParticulier(){

        if(destinataire.getNomParticulier().isEmpty() ){
            FacesContext.getCurrentInstance().addMessage("ajouterDestinataireParticulier",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner le nom !!!"));
        }else{
            if(destinataire.getPrenomParticulier().isEmpty() ){
                FacesContext.getCurrentInstance().addMessage("ajouterDestinataireParticulier",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner le prénom !!!"));
            }else{
                if(destinataire.getTelephoneParticulier().isEmpty() ){
                    FacesContext.getCurrentInstance().addMessage("ajouterDestinataireParticulier",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", " Vous devez renseigner le téléphone !!!"));
                }else{
                    HttpSession session = SessionUtils.getSession();
                    String idUser = (String) session.getAttribute( "idUser");
                    String idCourrierAEnvoyer = (String) session.getAttribute( "idCourrier");
                    genererUniqueIDPourEmetteurEtDestinataire();
                    String idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre("Particulier");

                    String ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `nom`, `prenom`, `tel`, `mail`, `fk_type_personne`) VALUES " +
                            " ('" +finalUniqueIDDestinataire+ "',"+"'"+ destinataire.getNomParticulier().trim().replaceAll("'"," ").toUpperCase() + "'," + "'" + destinataire.getPrenomParticulier().trim().replaceAll("'"," ")+ "'," + "'" + destinataire.getTelephoneParticulier() + "'," + "'" + destinataire.getEmailParticulier()+ "'," + "'" +idTypeDeDestinataire+ "')";

                    String recevoirCourrierSQL = "insert into recevoir_courrier (`id_courrier`,`id_personne`) VALUES ('"+idCourrierAEnvoyer+"',(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"'));";

                    String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDestinataire+"')";

                    String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                            "('"+  idCourrierAEnvoyer+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

                    String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                            " ('" + idUser +"')";

                    Connection connection = DatabaseConnection.getConnexion();
                    Statement statement = null;

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);
                        statement.executeBatch();
                        connection.commit();
                        destinataire.setNomParticulier(null);
                        destinataire.setPrenomParticulier(null);
                        destinataire.setTelephoneParticulier(null);
                        destinataire.setEmailParticulier(null);
                        FacesContext context = FacesContext.getCurrentInstance();
                        context.getExternalContext().getFlash().setKeepMessages(true);
                        FacesContext.getCurrentInstance().addMessage("ajouterDestinataireParticulier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Un nouveau destinataire à bien été ajouté à ce courrier !!!"));
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("ajouterDestinataireParticulier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur inconnue s'est produite !!!"));
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
        }

    }

    public void afficherLesDetailsDuDestinataireDUnCourrier(ActionEvent actionEvent){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDestinataire = String.valueOf(params.get("destinataireId"));
        String typeDestinataire = String.valueOf(params.get("typeDestinataireId"));
        String requeteDetailDestinataireSQL = null;
        String choixTypeDestinataire = null;
        switch (typeDestinataire)  {
            case "Particulier" :
                choixTypeDestinataire = "Particulier";
                requeteDetailDestinataireSQL = "select nom,prenom,tel,mail from `personne` where personne.id_personne = '"+idDestinataire+"' ; ";
                break;
            case "Entreprise" :
                choixTypeDestinataire = "Entreprise";
                requeteDetailDestinataireSQL = "select nom_etablissement,tel_etablissement,mail_etablissement,adresse_etablissement,titre_fonction,nom_direction from `etablissement` inner join `personne` on etablissement.id_etablissement = personne.id_etablissement inner join `direction` on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction where  personne.id_personne = '"+idDestinataire+"' ; ";
                break;
            case "Agent autre Ministère" :
                choixTypeDestinataire = "Agent Autre Ministere";
                requeteDetailDestinataireSQL = "select nom_etablissement,tel_etablissement,mail_etablissement,adresse_etablissement,titre_fonction,nom_direction from `etablissement` inner join `personne` on etablissement.id_etablissement = personne.id_etablissement inner join `direction` on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction  where  personne.id_personne = '"+idDestinataire+"' ; ";
                break;
            case "Agent du Ministère" :
                choixTypeDestinataire = "Agent du Ministere";
                requeteDetailDestinataireSQL = "select nom_etablissement,tel_etablissement,mail_etablissement,adresse_etablissement,titre_fonction,nom_direction from `etablissement` inner join `personne` on etablissement.id_etablissement = personne.id_etablissement inner join `direction` on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction  where  personne.id_personne = '"+idDestinataire+"' ; ";
                break;
        }

        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try{
            resultSet = connection.createStatement().executeQuery(requeteDetailDestinataireSQL);

            if (resultSet.next()){
                if(choixTypeDestinataire.equals("Particulier")){
                    destinataire.setNomParticulier(resultSet.getString("nom"));
                    destinataire.setPrenomParticulier(resultSet.getString("prenom"));
                    destinataire.setTelephoneParticulier(resultSet.getString("tel"));
                    destinataire.setEmailParticulier(resultSet.getString("mail"));
                    PrimeFaces.current().executeScript("PF('detailsDestinataireParticulier').show()");
                }else if(choixTypeDestinataire.equals("Entreprise")){
                    destinataire.setRaisonSocial(resultSet.getString("nom_etablissement"));
                    destinataire.setTelephoneEntreprise(resultSet.getString("tel_etablissement"));
                    destinataire.setEmailEntreprise(resultSet.getString("mail_etablissement"));
                    destinataire.setAdresseEntreprise(resultSet.getString("adresse_etablissement"));
                    destinataire.setFonctionEntreprise(resultSet.getString("titre_fonction"));
                    destinataire.setDirectionEntreprise(resultSet.getString("nom_direction"));
                    PrimeFaces.current().executeScript("PF('detailsDestinataireEntreprise').show()");
                }else if(choixTypeDestinataire.equals("Agent Autre Ministere")){
                    destinataire.setMinistereAutreMinistere(resultSet.getString("nom_etablissement"));
                    destinataire.setDirectionAutreMinistere(resultSet.getString("nom_direction"));
                    destinataire.setFonctionAutreMinistere(resultSet.getString("titre_fonction"));
                    PrimeFaces.current().executeScript("PF('detailsDestinataireAutreMinistere').show()");
                }else if(choixTypeDestinataire.equals("Agent du Ministere")){
                    destinataire.setDirection(resultSet.getString("nom_direction"));
                    destinataire.setFonction(resultSet.getString("titre_fonction"));
                    PrimeFaces.current().executeScript("PF('detailsDestinataireMinistere').show()");
                }
            }

        } catch (SQLException e) {
            PrimeFaces.current().executeScript("swal('Oups','Une erreur s'est produite avec la base de données', 'warning');");
            e.printStackTrace();
        }

    }

    public void recupererIdRecevoirCourrierPourSupprimerUnDestinataire(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        idRecevoirCourrier  = String.valueOf(params.get("idRecevoirCourrier"));
    }

    public void supprimerUnDestinataire(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute( "idCourrier");

        if(  destinataire.getNombreDeDestinataire().equals("1")){
            PrimeFaces.current().executeScript("swal('Oups', 'Impossible de supprimer cet unique destinataire', 'warning');");
        }else{

            String supprimerDestinataireSQL = "delete from `recevoir_courrier` where id_recevoir_courrier = '"+idRecevoirCourrier+"';";

            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.suppressionCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.suppressionDestinataire+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+  idCourrier+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                    " ('" + idUser +"')";

            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;

            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(supprimerDestinataireSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.executeBatch();
                connection.commit();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                PrimeFaces.current().executeScript("swal('Validation','Destinataire bien retiré du courrier', 'success');");
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("swal('Validation','Une erreur s'est produite', 'error');");
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

    public void recupererListeDirection(){
        HttpSession session = SessionUtils.getSession();
        String directionUser = (String) session.getAttribute( "directionUser");
        direction.setListeDirection(DirectionQueries.recupererLaListeDesDirections());
        direction.getListeDirection().removeIf(e -> e.equals(directionUser));
    }

    public void recupererListeMinisteres(){
        etablissement.setListeEtablissement(EtablissementQueries.recupererLaListeDesMinisteres());
        etablissement.getListeEtablissement().removeIf(e -> e.equals(Ministere.MinistereDuBudget));
    }

    public void ajoutDuFichierRemplacantDuCourrier(FileUploadEvent fileUploadEvent){
        byte[] bytes = null;
        courrier.setNomCourrier(DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName());
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        courrier.setCheminCourrierSurPC(FileManager.tempDirectoryOSPath +"courrier_"+courrier.getNomCourrier());
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(courrier.getCheminCourrierSurPC())));
            stream.write(bytes);
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().addMessage("messageinfo", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Fichier bien ajouté !!"));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("messageinfo", new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "Une erreur s'est produite lors de l'ajout du fichier !!"));
            e.printStackTrace();
        }finally {
            try {
                if(stream != null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ajouterUnFichierAUneDiscussion(FileUploadEvent fileUploadEvent){
        byte[] bytes = null;
        discussion.setNomFichierDiscussion(DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName());
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        discussion.setCheminFichierDiscussionSurPC(FileManager.tempDirectoryOSPath +"fichier_dicussion_"+discussion.getNomFichierDiscussion());
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(discussion.getCheminFichierDiscussionSurPC())));
            stream.write(bytes);
            stream.flush();
            stream.close();
            fichierDiscussionAjouter = true;
            FacesContext.getCurrentInstance().addMessage("messageinfodiscussion", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Fichier bien ajouté !!"));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("messageinfodiscussion", new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "Une erreur s'est produite lors de l'ajout du fichier !!"));
            e.printStackTrace();
        }finally {
            try {
                if(stream != null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remplacerLeFichierDuCourrier(){

        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        String idUser = (String) session.getAttribute( "idUser");
        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES ('" + EtatCourrier.miseAJour +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.changementDuFichier+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+  idCourrier+"',"+"'"+EtatCourrier.courrierEnregistre+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrier.getDossierAlfresco()));

        String updateIdAlfrescoDuCourrierSQL = "update `courrier` set `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' where id_courrier  = '"+idCourrier+"';";

        String updateNomFichierDuCourrierSQL = "update `courrier` set `nom_fichier` = '"+courrier.getNomCourrier().replaceAll("'","_")+"' where id_courrier  = '"+idCourrier+"';";

        Connection connection = DatabaseConnection.getConnexion();
        Statement statement = null;

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(updateIdAlfrescoDuCourrierSQL);
            statement.addBatch(updateNomFichierDuCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.executeBatch();
            connection.commit();
            session.setAttribute("idAlfresco", courrier.getIdAlfresco());
            PrimeFaces.current().executeScript("swal('Validation','Le fichier du courrier à été remplacé', 'success');");
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("retirerLoading()");
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite', 'error');");
            e.printStackTrace();
        }

    }

    public void envoyerCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        String idUser = (String) session.getAttribute( "idUser");
        String idDirectionUser = (String) session.getAttribute( "idDirectionUser");
        String updateEtatCourrierSQL = "update `courrier` SET `etat` = 'Courrier envoyé' WHERE id_courrier = '"+idCourrier+"';";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.courrierEnvoye +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierEnvoye+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnvoye+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        String cloreDiscussionSQL = null;
        String cloreEtatEtapeSQL = null;

        Connection connection = DatabaseConnection.getConnexion();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String idTypeDactivite = ActivitesQueries.recupererIdTypeDActivitesParSonTitre(TypeDActivites.courrierEnvoye);
            for(int a = 0; a < etape.getListeDesActionsSurLeCourrier().size(); a++){
                cloreDiscussionSQL = "update `discussion_etape` set `etat_discussion` = '"+EtatEtape.Fermer+"' where id_etape = '"+etape.getListeDesActionsSurLeCourrier().get(a).getId()+"'";
                cloreEtatEtapeSQL = "update `etape` set `etat` = '"+EtatEtape.termine+"' where id_etape = '"+etape.getListeDesActionsSurLeCourrier().get(a).getId()+"'";
                statement.addBatch(cloreDiscussionSQL);
                statement.addBatch(cloreEtatEtapeSQL);
            }
            statement.addBatch(updateEtatCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.addBatch(ActivitesQueries.ajouterUneActvitee(TitreActivites.courrierEnvoye, idCourrier ,idUser,idTypeDactivite,idDirectionUser));
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoye').show()");
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite', 'error');");
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

    public String retourALaListeDesCourriersEnregistres(){
        PrimeFaces.current().executeScript("PF('dialoguecourrierbienenvoye').hide()");
        return "mescourriers?faces-redirect=true";
    }

    /****Getter et setter***/
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Fonction getFonction() {
        return fonction;
    }

    public void setFonction(Fonction fonction) {
        this.fonction = fonction;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Etape getEtape() {
        return etape;
    }

    public void setEtape(Etape etape) {
        this.etape = etape;
    }

    public Emetteur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Emetteur emetteur) {
        this.emetteur = emetteur;
    }

    public Destinataire getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Destinataire destinataire) {
        this.destinataire = destinataire;
    }

    public Annexe getAnnexe() {
        return annexe;
    }

    public void setAnnexe(Annexe annexe) {
        this.annexe = annexe;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public Date getDateFinalTemp() {
        return dateFinalTemp;
    }

    public void setDateFinalTemp(Date dateFinalTemp) {
        this.dateFinalTemp = dateFinalTemp;
    }

    public String getNomEtPrenomAjouteurCourrier() {
        return nomEtPrenomAjouteurCourrier;
    }

    public void setNomEtPrenomAjouteurCourrier(String nomEtPrenomAjouteurCourrier) {
        this.nomEtPrenomAjouteurCourrier = nomEtPrenomAjouteurCourrier;
    }

    public String getFonctionAjouteurCourrier() {
        return fonctionAjouteurCourrier;
    }

    public void setFonctionAjouteurCourrier(String fonctionAjouteurCourrier) {
        this.fonctionAjouteurCourrier = fonctionAjouteurCourrier;
    }
}
