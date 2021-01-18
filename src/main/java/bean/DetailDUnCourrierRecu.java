package bean;

import alfresco.ConnexionAlfresco;
import alfresco.NomDesDossiers;
import databaseManager.*;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import sessionManager.SessionUtils;
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
import java.time.LocalDateTime;
import java.util.*;

@Named
@SessionScoped
public class DetailDUnCourrierRecu implements Serializable {

    private static final long serialVersionUID = 5888883238133064548L;
    private Courrier courrier;
    private Emetteur emetteur;
    private Direction direction;
    private Destinataire destinataire;
    private Annexe annexe;
    private Annotation annotation;
    private Discussion discussion;
    private Etape etape;
    private Dossier dossier;
    private ReponseCourrier reponseCourrier;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private List<String> mesAgentsListe = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private String idAgentAffecteAUneTache = null;
    private boolean fichierDiscussionAjouter = false;
    private boolean fichierReponseCourrierAjouter = false;
    private String premiereEtapeActeur;
    private String premiereEtapeMessage;
    private String premiereEtapeDate;
    private String messageEtape;
    private String dateButoirEtape;
    private String idDirectionATransfererLeCourrier;
    private ScheduleModel eventModel;
    private Date dateFinalTemp = null;
    private String referenceInterneCourrierTemp;
    private String existenceDestinataireDeTransfer = "non";
    private String finalUniqueIDDestinataire;
    private String nomActeurDuTransfer;
    private String nomAgentAccuseDeReception;
    private String dateAccusseDeReception;
    private String messageAccuseDeReception;
    private boolean isResponsable = false;
    private String motAppercuDetailEtape;
    private boolean droitTransfererCourrier = false;

    @PostConstruct
    public void initialisation(){
        dossier = new Dossier();
        courrier = new Courrier();
        emetteur = new Emetteur();
        annexe = new Annexe();
        etape = new Etape();
        annotation = new Annotation();
        destinataire = new Destinataire();
        discussion = new Discussion();
        direction  = new Direction();
        eventModel = new DefaultScheduleModel();
        reponseCourrier = new ReponseCourrier();
    }

    public void recupererLesActionsParEtapeDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String directionUser = (String) session.getAttribute("directionUser");

        etape.setListeDesActionsSurLeCourrier(DataBaseQueries.recupererLesActionsEffectueesSurUnCourrierPourLaTimeLine(idCourrier));
        etape.setNombreDeTache(String.valueOf(DataBaseQueries.nombreDActionEnCoursDuCourrier));

        String annee = null;
        String mois = null;
        String jour = null;
        String anneeAccusseDeReception = null;
        String moisAccusseDeReception = null;
        String jourAccusseDeReception = null;

        if(premiereEtapeDate != null){
            annee = premiereEtapeDate.substring(premiereEtapeDate.lastIndexOf("-") +1);
            mois = premiereEtapeDate.substring(premiereEtapeDate.indexOf("-")+1,premiereEtapeDate.indexOf("-")+3);
            jour = premiereEtapeDate.substring(0,2);
        }

        eventModel.clear();
        DefaultScheduleEvent defaultScheduleEvent = DefaultScheduleEvent.builder().title(premiereEtapeMessage)
                .startDate(LocalDateTime.of(Integer.parseInt(annee),Integer.parseInt(mois),Integer.parseInt(jour),0,0))
                .endDate(LocalDateTime.of(Integer.parseInt(annee),Integer.parseInt(mois),Integer.parseInt(jour),0,0))
                .build();

        if(dateAccusseDeReception != null){
            anneeAccusseDeReception = dateAccusseDeReception.substring(dateAccusseDeReception.lastIndexOf("-") +1);
            moisAccusseDeReception = dateAccusseDeReception.substring(dateAccusseDeReception.indexOf("-")+1,dateAccusseDeReception.indexOf("-")+3);
            jourAccusseDeReception = dateAccusseDeReception.substring(0,2);

            DefaultScheduleEvent defaultScheduleEventAccusseDeReception  = DefaultScheduleEvent.builder().title(messageAccuseDeReception)
                    .startDate(LocalDateTime.of(Integer.parseInt(anneeAccusseDeReception),Integer.parseInt(moisAccusseDeReception),Integer.parseInt(jourAccusseDeReception),0,0))
                    .endDate(LocalDateTime.of(Integer.parseInt(anneeAccusseDeReception),Integer.parseInt(moisAccusseDeReception),Integer.parseInt(jourAccusseDeReception),0,0))
                    .build();
            eventModel.addEvent(defaultScheduleEventAccusseDeReception);
        }


        eventModel.addEvent(defaultScheduleEvent);
        for(int a = 0; a < etape.getListeDesActionsSurLeCourrier().size(); a++){

            String jourDebutAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().substring(0,etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().indexOf("-"));
            String moisDebutAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().substring(etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().indexOf("-")+1,etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().indexOf("-")+3);
            String anneeDebutAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().substring(etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut().lastIndexOf("-") +1);

            DefaultScheduleEvent traitementEvent = null;
            if(!etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().equals("Aucun") ){
                String jourFinAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().substring(0,etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().indexOf("-"));
                String moisFinAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().substring(etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().indexOf("-")+1,etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().indexOf("-")+3);
                String anneeFinAction = etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().substring(etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin().lastIndexOf("-") +1);

                jourFinAction = jourFinAction.substring(1);
                int day = Integer.parseInt(jourFinAction);
                day = day + 1;

                traitementEvent = DefaultScheduleEvent.builder().title( etape.getListeDesActionsSurLeCourrier().get(a).getTitre() +" \n" +etape.getListeDesActionsSurLeCourrier().get(a).getMessage()
                        +"\n" + "Date de début : "+etape.getListeDesActionsSurLeCourrier().get(a).getDate_debut()
                        +"\n" + "Date de fin : "+ etape.getListeDesActionsSurLeCourrier().get(a).getDate_fin())
                        .startDate(LocalDateTime.of(Integer.parseInt(anneeDebutAction),Integer.parseInt(moisDebutAction),Integer.parseInt(jourDebutAction),0,0,0))
                        .endDate(LocalDateTime.of(Integer.parseInt(anneeFinAction),Integer.parseInt(moisFinAction),day,0,0,0))
                        .description(etape.getListeDesActionsSurLeCourrier().get(a).getMessage())
                        .build();
            }else{
                traitementEvent = DefaultScheduleEvent.builder().title( etape.getListeDesActionsSurLeCourrier().get(a).getTitre() + "\n "+etape.getListeDesActionsSurLeCourrier().get(a).getMessage())
                        .startDate(LocalDateTime.of(Integer.parseInt(anneeDebutAction),Integer.parseInt(moisDebutAction),Integer.parseInt(jourDebutAction),0,0,0))
                        .endDate(LocalDateTime.of(Integer.parseInt(anneeDebutAction),Integer.parseInt(moisDebutAction),Integer.parseInt(jourDebutAction),0,0,0))
                        .description(etape.getListeDesActionsSurLeCourrier().get(a).getMessage())
                        .build();
            }

            if(etape.getListeDesActionsSurLeCourrier().get(a).getEtat().equals("En retard")){
                traitementEvent.setStyleClass("en-retard");
            }else if(etape.getListeDesActionsSurLeCourrier().get(a).getEtat().equals("En traitement")){
                traitementEvent.setStyleClass("en-traitement");
            }else if(etape.getListeDesActionsSurLeCourrier().get(a).getEtat().equals("Terminé")){
                traitementEvent.setStyleClass("terminee");
            }

            eventModel.addEvent(traitementEvent);
            if(etape.getListeDesActionsSurLeCourrier().get(a).getMessage().contains("Courrier à transferer à")){
                if(etape.getListeDesActionsSurLeCourrier().get(a).getMessage().contains(directionUser)){
                    nomActeurDuTransfer =  etape.getListeDesActionsSurLeCourrier().get(a).getActeur();
                    etape.getListeDesActionsSurLeCourrier().remove(a);
                }
            }

        }

    }

    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
        }
    }

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DossiersQueries.recupererLesDossiersDUnUtilisateur(idUser));
    }

    public void recupererLesReponsesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        reponseCourrier.setReponseCourrierList(DataBaseQueries.recupererLesReponsesDUnCourrier(idCourrier));
    }

    public void recupererToutesLesInformationsDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idDirection = (String) session.getAttribute( "idDirectionUser");
        CourriersQueries.recupererLEmetteurDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLeDestinataireDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLesDetailsDUnCourrierRecu(idCourrier,idDirection);

        emetteur.setTypeDEmetteur(CourriersQueries.typeDemetteur);

        emetteur.setMinistere(CourriersQueries.ministereEmetteur);
        emetteur.setDirection(CourriersQueries.directeurEmetteur);
        emetteur.setFonction(CourriersQueries.fonctionEmetteur);

        emetteur.setMinistereAutreMinistere(CourriersQueries.ministereEmetteur);
        emetteur.setDirectionAutreMinistere(CourriersQueries.directeurEmetteur);
        emetteur.setFonctionAutreMinistere(CourriersQueries.fonctionEmetteur);

        emetteur.setRaisonSocial(CourriersQueries.ministereEmetteur);
        emetteur.setDirectionEntreprise(CourriersQueries.directeurEmetteur);
        emetteur.setFonctionEntreprise(CourriersQueries.fonctionEmetteur);
        emetteur.setTelephoneEntreprise(CourriersQueries.telEmetteurEtablissement);
        emetteur.setEmailEntreprise(CourriersQueries.emailEmetteurEtablissement);
        emetteur.setAdresseEntreprise(CourriersQueries.adresseEmetteurEtablissement);

        emetteur.setNomParticulier(CourriersQueries.nomEtPrenomEmetteurPersonne);
        emetteur.setTelephoneParticulier(CourriersQueries.telEmetteurPersonne);
        emetteur.setEmailParticulier(CourriersQueries.emailEmetteurPersonne);

        PrimeFaces.current().executeScript("affichageEnFonctionDuTypeEmetteur()");
        PrimeFaces.current().executeScript("affichageDivAccuseDeReception()");

        courrier.setObjetCourrier(CourriersQueries.objetCourrier);
        courrier.setReferenceCourrier(CourriersQueries.referenceCourrier);
        courrier.setPrioriteCourrier(CourriersQueries.prioriteCourrier);
        courrier.setTypeCourrier(CourriersQueries.typeCourrier);
        courrier.setAccuseDeReception(CourriersQueries.accuseDeReception);
        courrier.setCommentairesCourrier(CourriersQueries.commentairesCourrier);
        courrier.setDossierAlfresco(CourriersQueries.dossierAlfresco);
        courrier.setConfidentiel(CourriersQueries.confidentiel);
        courrier.setHeureDeReception(CourriersQueries.heureDeReception);
        courrier.setDateDEnregistrement(CourriersQueries.dateDEnregistrement);
        courrier.setDateDeReception(CourriersQueries.dateDeReception);
        courrier.setReferenceInterne(CourriersQueries.referenceInterne);

        PrimeFaces.current().executeScript("affichageMessageDivReferenceInterne()");

        String jour = courrier.getDateDeReception().substring(courrier.getDateDeReception().lastIndexOf("-") +1);
        String mois = courrier.getDateDeReception().substring(courrier.getDateDeReception().indexOf("-")+1,courrier.getDateDeReception().indexOf("-")+3);
        String annee = courrier.getDateDeReception().substring(0,4);
        courrier.setDateDeReception(jour+"-"+mois+"-"+annee);

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);

        courrier.setHeureDEnregistrement(CourriersQueries.heureDEnregistrement);
    }

    public void fermerDialogueAccuseDeReception(){
        PrimeFaces.current().executeScript("PF('toggleReception').uncheck()");
    }

    public void ouvrirDialogueRepondreAUnCourrier(){
        PrimeFaces.current().executeScript("ouvrirModalBoxRepondreAUnCourrier()");
        //PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
    }

    public void ajouterUnFichierDeLaReponseAuCourrier(FileUploadEvent fileUploadEvent){
        byte[] bytes = null;
        reponseCourrier.setNomFichier(DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName());
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        reponseCourrier.setCheminFichierSurPC(FileManager.tempDirectoryOSPath +"fichier_reponseaucourrier_"+reponseCourrier.getNomFichier());
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(reponseCourrier.getCheminFichierSurPC())));
            stream.write(bytes);
            stream.flush();
            stream.close();
            fichierReponseCourrierAjouter = true;
            FacesContext.getCurrentInstance().addMessage("messagefichiertemp", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Fichier bien ajouté !!"));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("messagefichiertemp", new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "Une erreur s'est produite lors de l'ajout du fichier !!"));
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

    public void ajouterUneReponseAuCourrier(){

        String uniqueID = UUID.randomUUID().toString();
        if(reponseCourrier.getText().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez ecrire un message"));
        }else{
            PrimeFaces.current().executeScript("PF('inputtextmessagereponsecourrier').disable()");
            HttpSession session = SessionUtils.getSession();
            String idCourrier = (String) session.getAttribute("courrierId");
            String idUser = (String) session.getAttribute( "idUser");
            String idDirectionUser = (String) session.getAttribute( "idDirectionUser");
            String repondreSQL = "INSERT INTO `reponse_courrier` (`message_reponse_courrier`, `identifiant_unique_reponse`,`fk_courrier`, `fk_personne`) " +
                    "VALUES ( '"+reponseCourrier.getText().replaceAll("'", " ")+"','"+uniqueID+"' ,'"+idCourrier+"', '"+idUser+"');";

            String updateIdAlfrescoSQL = null;
            String updateNomFichierSQL = null;

            if(fichierReponseCourrierAjouter){
                reponseCourrier.setIdentifiantAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(reponseCourrier.getCheminFichierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(reponseCourrier.getNomFichier())), "courrier_ministre"));
                updateIdAlfrescoSQL = "update `reponse_courrier` set `identifiant_alfresco_reponse_courrier` = '"+reponseCourrier.getIdentifiantAlfresco()+"' where id_reponse_courrier  = (select id_reponse_courrier from (select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' ) as temp)";
                updateNomFichierSQL = "update `reponse_courrier` set `nom_fichier_reponse_courrier` = '"+reponseCourrier.getNomFichier()+"' where id_reponse_courrier  = (select id_reponse_courrier from (select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' ) as temp)";
            }
            String idTypeDactivite = ActivitesQueries.recupererIdTypeDActivitesParSonTitre(TypeDActivites.reponseAUnCourrier);
            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                    " ('" + EtatCourrier.ajoutDUneReponseAuCourrier+"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDUneReponseAuCourrier+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`) VALUES" +
                    " ('" + idUser +"',"+"'"+RoleEtape.createurReponseAuCourrier+"')";
            String ajouterCorrespondancePersonneReponseCourrierSQL = "INSERT INTO `correspondance_personne_reponse_courrier` (`id_personne`,`role`,`id_reponse_courrier`) VALUES" +
                    "('"+ idUser +"',"+"'"+RoleEtape.createurReponseAuCourrier+"',"+"(select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' )"+")";

            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(repondreSQL);
                if(updateIdAlfrescoSQL != null){
                    statement.addBatch(updateIdAlfrescoSQL);
                }
                if(updateNomFichierSQL != null){
                    statement.addBatch(updateNomFichierSQL);
                }
                statement.addBatch(ajouterCorrespondancePersonneReponseCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.addBatch(ActivitesQueries.ajouterUneActvitee(TitreActivites.reponseAjoutee, idCourrier ,idUser,idTypeDactivite,idDirectionUser));
                statement.executeBatch();
                connection.commit();
                reponseCourrier.setText(null);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","Votre réponse à bien été ajouté"));
                recupererLesReponsesDuCourrier();
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite sur le réseau"));
                e.printStackTrace();
            }


        }
    }

    public void voirLeFichierAttacheAUneReponse(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String reponseIdentifiantAlfresco = null;
        reponseIdentifiantAlfresco= String.valueOf(params.get("reponseIdentifiantAlfresco"));
        if(reponseIdentifiantAlfresco.contains("SpacesStore")) {
            reponseCourrier.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(reponseIdentifiantAlfresco));
            PrimeFaces.current().executeScript("PF('dlgFichierReponseCourrier').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Aucun fichier','Pas de fichier joint à ce message', 'warning');");
        }
    }

    public void confirmerAccuseDeReceptionDuCourrier(){
        PrimeFaces.current().executeScript("PF('panelboutons').close()");
        PrimeFaces.current().executeScript("PF('panelloading').toggle()");
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        String idUserAccuseDeReception = null;
        System.out.println("isCourrierTransferer = " + isCourrierTransferer);
        System.out.println("directionUser = " + directionUser);
        System.out.println("fonctionUser = " + fonctionUser);
        if(isCourrierTransferer != null){
            for(int a = 0; a < destinataire.getListeDestinataireParTransfer().size(); a++ ){
                if(destinataire.getListeDestinataireParTransfer().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataireParTransfer().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataireParTransfer().get(a).getIdDestinataire();
                }
            }
        }else{
            for(int a = 0; a < destinataire.getListeDestinataire().size(); a++ ){
                if(destinataire.getListeDestinataire().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataire().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataire().get(a).getIdDestinataire();
                }
            }
        }
        if(idUserAccuseDeReception != null){
            Connection connection = DatabaseConnection.getConnexion();
            String accuseReceptionDuCourrierSQL = "update `recevoir_courrier` set `accuse_reception` = '"+ EtatCourrier.accuseDeReception +"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ idUserAccuseDeReception+"'; ";

            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                    " ('" + EtatCourrier.accuseDeReceptionTitre +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.confirmerReceptionDuCourrier+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                    " ('" + idUser +"')";
            Statement statement = null;

            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(accuseReceptionDuCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.executeBatch();
                connection.commit();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                PrimeFaces.current().executeScript("PF('panelloading').close()");
                FacesContext.getCurrentInstance().addMessage("messageaccusedereception",new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","La réception physique du courrier à bien été enregistré"));
                PrimeFaces.current().executeScript("PF('panelrechargerlapage').toggle()");
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("PF('panelloading').close()");
                PrimeFaces.current().executeScript("PF('panelrechargerlapage').toggle()");
                FacesContext.getCurrentInstance().addMessage("messageaccusedereception",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite avec la base de donnée"));
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
        }else{
            PrimeFaces.current().executeScript("PF('panelloading').close()");
            FacesContext.getCurrentInstance().addMessage("messageaccusedereception",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite"));
            PrimeFaces.current().executeScript("PF('panelrechargerlapage').toggle()");
        }
    }

    public void afficherLeFichierDuCourier(){
        HttpSession session = SessionUtils.getSession();
        String idDocumementAlfresco = (String) session.getAttribute("alfrescoId");
        courrier.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(idDocumementAlfresco));
        /***TODO gerer le null ponter exception quand alfresco est pas allumé***/
        if(!ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
            courrier.setIsPDF("non");
            PrimeFaces.current().executeScript("affichagePDF()");
        }else if(ConnexionAlfresco.mimeDocument.equals("application/pdf;charset=UTF-8")){
            courrier.setIsPDF("oui");
            PrimeFaces.current().executeScript("affichagePDF()");
        }
    }

    public void gestionDesAnnexesAuChargementDeLaPage(){
        listeIdAnnexeAlfresco.clear();
        HttpSession session = SessionUtils.getSession();
        courrier.setIdCourrier((String) session.getAttribute( "courrierId"));
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

    public void recupererLesAnnotationsDUnCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        annotation.setListeDesAnnotations(CourriersQueries.recupererLesAnnotationsDUnCourrier(idCourrier));
    }

    public void recupererLesDestinatairesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        destinataire.setListeDestinataire(CourriersQueries.recupererLesDestinatairesDUnCourrier(idCourrier));
        destinataire.setNombreDeDestinataire(String.valueOf(CourriersQueries.nombreDeDestinataireDuCourrier));
        destinataire.setListeDestinataireParTransfer(CourriersQueries.recupererLesDestinatairesParTransferDUnCourrier(idCourrier));
        if(destinataire.getListeDestinataireParTransfer().size() > 0){
            existenceDestinataireDeTransfer = "oui";
        }
        if(isCourrierTransferer == null){
            PrimeFaces.current().executeScript("affichageBlockTransfer()");
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

    public void recupererLHistoriqueDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        etape.setListeHistoriques(DataBaseQueries.recupererLHistoriqueDesActionsSurUnCourrierRecu(idCourrier));
        for(int a = 0; a < etape.getListeHistoriques().size(); a++){
            if(isCourrierTransferer != null){
                etape.getListeHistoriques().get(0).setTitre("Courrier transferé"); /*TODO optimiser ca */
                etape.getListeHistoriques().get(0).setMessage("Ce courrier vous a été transferé");
            }else{
                etape.getListeHistoriques().get(0).setTitre("Courrier Reçu"); /*TODO optimiser ca */
                etape.getListeHistoriques().get(0).setMessage("Le courrier à été reçu");
                premiereEtapeActeur = etape.getListeHistoriques().get(0).getActeur();
                etape.getListeHistoriques().get(0).setActeur(nomActeurDuTransfer);
            }
            if(etape.getListeHistoriques().get(a).getMessage().contains("Confirmation physique de la réception du courrier")){
                nomAgentAccuseDeReception =  etape.getListeHistoriques().get(a).getActeur();
                dateAccusseDeReception = etape.getListeHistoriques().get(a).getDate_debut();
                messageAccuseDeReception = etape.getListeHistoriques().get(a).getMessage();
            }
            if(etape.getListeHistoriques().get(a).getMessage().contains("Courrier transferé à une autre direction")){
                etape.getListeHistoriques().remove(a);
            }
            premiereEtapeMessage = EtatCourrier.courrierRecu;
            premiereEtapeDate = etape.getListeHistoriques().get(0).getDate_debut();

        }
    }

    public void mettreLeCourrierEnFavoris(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        String idUserAccuseDeReception = null;

        for(int a = 0; a < destinataire.getListeDestinataire().size(); a++ ){
            if(destinataire.getListeDestinataire().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataire().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                idUserAccuseDeReception = destinataire.getListeDestinataire().get(a).getIdDestinataire();
            }
        }

        if(idUserAccuseDeReception != null){
            Connection connection = DatabaseConnection.getConnexion();
            String mettreCourrierEnFavorisSQL = "update `recevoir_courrier` set `favoris` = '"+ EtatCourrier.favoris +"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ idUserAccuseDeReception+"'; ";

            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                    " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierEnFavoris+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                    " ('" + idUser +"')";

            Statement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(mettreCourrierEnFavorisSQL);
                statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                statement.addBatch(ajouterEtapeCourrierSQL);
                statement.executeBatch();
                connection.commit();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                PrimeFaces.current().executeScript("swal('Validation','Le courrier à bien été ajouté à vos favoris', 'success');");
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
        }else{
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite', 'error');");
        }

    }

    public void ajouterLaReferenceInterneDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        String idUserAccuseDeReception = null;
        if(isCourrierTransferer != null){
            for(int a = 0; a < destinataire.getListeDestinataireParTransfer().size(); a++ ){
                if(destinataire.getListeDestinataireParTransfer().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataireParTransfer().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataireParTransfer().get(a).getIdDestinataire();
                }
            }
        }else{
            for(int a = 0; a < destinataire.getListeDestinataire().size(); a++ ){
                if(destinataire.getListeDestinataire().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataire().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataire().get(a).getIdDestinataire();
                }
            }
        }

        /*PrimeFaces.current().executeScript("PF('panelloadingReferenceinterne').close()");
        PrimeFaces.current().executeScript("PF('panelboutonajouterreference').toggle()");*/
        if(idUserAccuseDeReception != null){

            if(referenceInterneCourrierTemp.isEmpty()){

                FacesContext.getCurrentInstance().addMessage("messagereferenceinterne",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vosu devez renseigner la reference"));
            }else{
                PrimeFaces.current().executeScript("PF('panelboutonajouterreference').close()");
                PrimeFaces.current().executeScript("PF('panelloadingReferenceinterne').toggle()");
                Connection connection = DatabaseConnection.getConnexion();
                String ajouterReferenceInterneSQL = "update `recevoir_courrier` set `reference_interne` = '"+ referenceInterneCourrierTemp +"' where id_courrier = '"+idCourrier+"' and id_personne = '"+idUserAccuseDeReception+ "'; ";
                String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                        " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.miseAJourReferenceInterne+"')";
                String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                        "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";
                String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                        " ('" + idUser +"')";
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    statement.addBatch(ajouterReferenceInterneSQL);
                    statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                    statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                    statement.addBatch(ajouterEtapeCourrierSQL);
                    statement.executeBatch();
                    connection.commit();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    PrimeFaces.current().executeScript("PF('panelloadingReferenceinterne').close()");
                    PrimeFaces.current().executeScript("PF('panelrechargerlapageducourrier').toggle()");
                    referenceInterneCourrierTemp = null;
                    FacesContext.getCurrentInstance().addMessage("messagereferenceinterne",new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","La référence interne du courrier à bien été ajoutée"));
                } catch (SQLException e) {
                    PrimeFaces.current().executeScript("PF('panelloadingReferenceinterne').close()");
                    PrimeFaces.current().executeScript("PF('panelrechargerlapageducourrier').toggle()");
                    FacesContext.getCurrentInstance().addMessage("messagereferenceinterne",new FacesMessage(FacesMessage.SEVERITY_INFO,"Erreur","Une erreur s'est produite"));
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

        }else{
            PrimeFaces.current().executeScript("PF('panelloadingReferenceinterne').close()");
            PrimeFaces.current().executeScript("PF('panelboutonajouterreference').toggle()");
            FacesContext.getCurrentInstance().addMessage("messagereferenceinterne",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite"));

        }

    }

    public void archiverLeCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        Connection connection = DatabaseConnection.getConnexion();
        String mettreCourrierEnFavorisSQL = "update `recevoir_courrier` set `archive` = '"+ EtatCourrier.archiveActive +"' where id_courrier = '"+idCourrier+"' ; ";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierEnArchive+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(mettreCourrierEnFavorisSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            PrimeFaces.current().executeScript("swal('Validation','Le courrier à bien été ajouté à vos archives', 'success');");
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

    public void ajouterUneAnnotation(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        String ajouterAnnotationAuCourrierSQL = "INSERT INTO `annotation` (`texte`,`id_courrier`,`id_personne`) VALUES" +
                " ('"+annotation.getMessageNote().trim().replaceAll("'"," ") +"',"+"'"+ idCourrier+ "',"+"'"+idUser+"')";

        String ajouterEtapeCourrierSQL =  "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajouterUneNote+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = null;
        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        if(isCourrierTransferer != null){
            ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnTransfer+"')";
        }else{
            ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";
        }

        if(annotation.getMessageNote().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageAnnotation", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Erreur!!!", "Vous devez ecrire un message !!!"));
        }else{

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

    public void transmettreAUnAgent(){

        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute( "courrierId");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        String idDirectionUser = (String) session.getAttribute( "idDirectionUser");
        String ajouterEtapeCourrierSQL;
        String ajouterCorrespondanceEtapeCourrierSQL = null;

        if(isCourrierTransferer != null){
            ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnTransfer+"')";
        }else{
            ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";
        }

        if (idAgentAffecteAUneTache == null ){
            FacesContext.getCurrentInstance().addMessage("messageaffectation", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez selectionner un agent !!"));
        }else{

            if(etape.getMessage().isEmpty() ){
                FacesContext.getCurrentInstance().addMessage("messageaffectation", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez ecrire un message !!"));

            }else{

                if (dateFinalTemp == null){
                    ajouterEtapeCourrierSQL = "insert into `etape` ( `titre`, `etat`, `message` ) VALUES ('"+ ActionEtape.transmisPourTraitement+"',"+"'"+EtatEtape.enTraitement+"'," +"'"+etape.getMessage().trim().replaceAll("'", " ").replaceAll("<p>","").replaceAll("</p>","")+"')";
                }else{
                    etape.setDate_fin( DateUtils.convertirDateEnString(dateFinalTemp));
                    ajouterEtapeCourrierSQL = "insert into `etape` ( `titre`, `etat`, `date_fin`, `message` ) VALUES ('"+ ActionEtape.transmisPourTraitement+"',"+"'"+EtatEtape.enTraitement+"',"+"'"+ etape.getDate_fin()+"'," +"'"+etape.getMessage().trim().replaceAll("'", " ").replaceAll("<p>","").replaceAll("</p>","")+"')";
                }

                String ajouterCorrespondanceEtapePersonneAffecteurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`,`nature_etape`) VALUES" +
                        " ('" + idUser +"',"+"'"+TypeDePersonne.affecteurTache+"',"+"'"+EtatCourrier.courrierRecu+"')";

                String ajouterCorrespondanceEtapePersonneReceveurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`, `etat_tache`,`nature_etape`) VALUES" +
                        " ('" + idAgentAffecteAUneTache+"',"+"'"+TypeDePersonne.receveurTache+"',"+"'"+EtatEtape.PasRepondu +"',"+"'"+EtatCourrier.courrierRecu+"')";

                String idTypeDactivite = ActivitesQueries.recupererIdTypeDActivitesParSonTitre(TypeDActivites.tacheAjoutee);
                Connection connection = DatabaseConnection.getConnexion();
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    statement.addBatch(ajouterCorrespondanceEtapePersonneAffecteurSQL);
                    statement.addBatch(ajouterCorrespondanceEtapePersonneReceveurSQL);
                    statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                    statement.addBatch(ajouterEtapeCourrierSQL);
                    statement.addBatch(ActivitesQueries.ajouterUneActvitee(TitreActivites.tacheAjoutee, idCourrier ,idUser,idTypeDactivite,idDirectionUser));
                    statement.executeBatch();
                    connection.commit();
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage("messageaffectation", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Transmision effectuée avec succés"));
                    etape.setMessage(null);
                    etape.setDate_fin(null);
                    etape.setActeur(null);
                    idAgentAffecteAUneTache = null;
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("messageaffectation", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur avec la base de données s'est produite !!!"));
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

    public void clickExpansionToggleEtape(ToggleEvent toggleEvent){
        discussion.setIdEtape(((Etape)toggleEvent.getData()).getId());
        discussion.setListeDiscussion(DiscussionsQueries.recupererLesDiscussionsDUneEtape(discussion.getIdEtape()));
        PrimeFaces.current().executeScript("PF('panelloadingdiscussion').close()");
    }

    public void recupererLeDestinataireDUneTache(ActionEvent actionEvent){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idTache = String.valueOf(params.get("etapeId"));
        String requeteDestinataireTacheSQL = null;
        if(isResponsable){
            motAppercuDetailEtape = "Tache destinée à :";
            requeteDestinataireTacheSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' order by etape.id_etape desc";
        }else{
            motAppercuDetailEtape = "Tache crée par :";
            requeteDestinataireTacheSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.affecteurTache +"' order by etape.id_etape desc";

        }
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

    public void creerUneDiscussion(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute( "courrierId");
        String idDirectionUser = (String) session.getAttribute( "idDirectionUser");
        Connection connection = DatabaseConnection.getConnexion();
        if(etape.getReponseTache() == null || etape.getReponseTache().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageinfodiscussion", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez ecrire un message!!"));
        }else{
            String creerDiscussionSQL = "insert into `discussion_etape` (`etat_discussion`,`message_discussion`,`id_etape`,`id_personne`) VALUES" +
                    " ('" + EtatEtape.Ouvert +"',"+"'"+etape.getReponseTache().replaceAll("'", " ")+"',"+"'"+etape.getId()+"',"+"'"+idUser+"')";

            String updateIdAlfrescoDansDiscussionSQL = null;
            String updateNomFichierDansDiscussionSQL = null;

            if(fichierDiscussionAjouter){
                discussion.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(discussion.getCheminFichierDiscussionSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(discussion.getNomFichierDiscussion())), NomDesDossiers.fichierDiscussion));
                updateIdAlfrescoDansDiscussionSQL = "update `discussion_etape` set `identifiant_alfresco_discussion` = '"+discussion.getIdAlfresco()+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
                updateNomFichierDansDiscussionSQL = "update `discussion_etape` set `nom_fichier_discussion` = '"+discussion.getNomFichierDiscussion()+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
            }

            String changerCorrespondanceEtapePersonneReceveurSQL = "UPDATE `correspondance_personne_etape` set `etat_tache` = '"+EtatEtape.Repondu+"' where correspondance_personne_etape.id_etape = '"+etape.getId()+"'";

            Statement statement = null;
            try {
                String idTypeDactivite = ActivitesQueries.recupererIdTypeDActivitesParSonTitre(TypeDActivites.reponseAUneTache);
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
                statement.addBatch(ActivitesQueries.ajouterUneActvitee(TitreActivites.discussionAjoutee, idCourrier ,idUser,idTypeDactivite,idDirectionUser));
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

    public void recupererIdDossierAuClick(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDossier = params.get("idDossier");
        dossier.setIdDossier(idDossier);
    }

    public void ajouterCourrierDansDossier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");

        String dossierId = DossiersQueries.voirSiUnCourrierEstDejaDansUnDossier(idCourrier, dossier.getIdDossier());
        if(dossierId != null){

            if(dossierId.equals(dossier.getIdDossier())){
                FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Le courrier est déja dans ce dossier!!!"));
            }else{
                DossiersQueries.ajouterUnCourrierRecuDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
            }
        }else{
            DossiersQueries.ajouterUnCourrierRecuDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
        }
    }

    public void creerUnDossier(){

        if(dossier.getNomDossier().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Attention", "Vous devez renseigner le nom du dossier !!!"));
        }else{
            boolean trouve = false;
            for(int a =0; a < dossier.getDossierList().size(); a++){
                if(dossier.getNomDossier().equalsIgnoreCase(dossier.getDossierList().get(a).getNomDossier())){
                    trouve = true;
                    break;
                }
            }
            if(trouve){
                FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Vous avez déja un dossier de ce nom!!!"));
            }else{
                if(!dossier.getDescriptionDossier().isEmpty()){
                   dossier.setDescriptionDossier(dossier.getDescriptionDossier().replaceAll("'"," ").trim());
                }
                HttpSession session = SessionUtils.getSession();
                String idUser = (String) session.getAttribute( "idUser");
                recupererLesDossiers();
                DossiersQueries.creerUnDossier(idUser,dossier.getNomDossier().replaceAll("'"," ").trim(),dossier.getDescriptionDossier());
                dossier.setNomDossier(null);
                dossier.setDescriptionDossier(null);
            }
        }


    }

    public void recupererLesAutresDirections(){
        HttpSession session = SessionUtils.getSession();
        String directionUser = (String) session.getAttribute( "directionUser");
        if(emetteur.getDirection() != null){
            direction.setListeDirection(DataBaseQueries.recupererLaListeDesAutresDirections(directionUser,emetteur.getDirection()));
        }else{
            direction.setListeDirection(DataBaseQueries.recupererLaListeDesDirections());
            direction.getListeDirection().removeIf(e -> e.equals(directionUser));
        }

    }

    public void recupererIdDirectionParSonNomAuClick(){

        if(!direction.getTitreDirection().equalsIgnoreCase("rien")){
            idDirectionATransfererLeCourrier = DataBaseQueries.recupererIdDirectionParSonNom(direction.getTitreDirection());
        }
    }

    private void genererUniqueIDPourDestinataire(){
        UUID randomDestinataireID = UUID.randomUUID();
        finalUniqueIDDestinataire = "destinataire" +"_"+randomDestinataireID.toString();
    }

    public void checkDesDroitsPourTransfererUnCourrier(){
        HttpSession session = SessionUtils.getSession();
        String fonctionUser = session.getAttribute("fonctionUser").toString();
        switch (fonctionUser) {
            case FonctionsUtilisateurs.secretaireGeneral:
                droitTransfererCourrier = true;
                break;
            case FonctionsUtilisateurs.secretaireGeneralAdjoint:
                droitTransfererCourrier = true;
                break;
            case FonctionsUtilisateurs.directeurCabinet:
                droitTransfererCourrier = true;
                break;
            case FonctionsUtilisateurs.directeurGeneral:
                droitTransfererCourrier = true;
                break;
            case FonctionsUtilisateurs.directeurGeneralAdjoint:
                droitTransfererCourrier = true;
                break;
        }
        if(droitTransfererCourrier){
            PrimeFaces.current().executeScript("PF('dialogueTransferDuCourrier').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets pas de réaliser cette action', 'warning');");
        }
    }

    public void transfererLeCourrierAUneAutreDirection(){

        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute( "courrierId");
        String idDirectionUser = (String) session.getAttribute( "idDirectionUser");
        String ajouterEtapeCourrierSQL = null;
        genererUniqueIDPourDestinataire();
        String idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre("Agent du Ministere");
        String idFonctionDestinataire = DataBaseQueries.recupererIdFonctionParSonTitreEtSonType("Directeur", TypeDeFonctions.interne);
        String idEtablissementDestinataire = DataBaseQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);
        String ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" + " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" + idDirectionATransfererLeCourrier + "',"+ "'" +idEtablissementDestinataire+ "')";

        if (idAgentAffecteAUneTache == null ){
            FacesContext.getCurrentInstance().addMessage("messagesTransfererCourrier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez selectionner un agent !!"));
        }else{

            if(direction.getTitreDirection().equals("rien") ){
                FacesContext.getCurrentInstance().addMessage("messagesTransfererCourrier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez selectionner une direction !!"));
            }else{
                String idTypeDactivite = ActivitesQueries.recupererIdTypeDActivitesParSonTitre(TypeDActivites.transfertDuCourrier);

                ajouterEtapeCourrierSQL = "insert into `etape` ( `titre`, `etat`,`message`) VALUES ('"+ActionEtape.transmisPourTraitement+"',"+"'"+EtatEtape.enTraitement+"',"+"'"+ActionEtape.transfererA+" "+direction.getTitreDirection()+"')";

                String ajouterCorrespondanceEtapePersonneAffecteurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`) VALUES" +
                        " ('" + idUser +"',"+"'"+TypeDePersonne.affecteurTache+"')";

                String ajouterCorrespondanceEtapePersonneReceveurSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`, `etat_tache`) VALUES" +
                        " ('" + idAgentAffecteAUneTache+"',"+"'"+TypeDePersonne.receveurTache+"',"+"'"+EtatEtape.PasRepondu +"')";

                String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                        "('"+ idCourrier +"',"+"'"+ActionEtape.transfererAUneDirection+"')";

                String ajouterHistoriqueCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                            " ('" + EtatCourrier.actionSurCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.transfererAUneDirection+"')";
                String ajouterCorrespondanceEtapeCourrierHistoriqueSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                            "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";
                String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                        " ('" + idUser +"')";

                String recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`,`transfer`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+"'"+idCourrier+"',(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"'), '"+EtatCourrier.accuseDeReceptionNon+"',"+"'"+EtatCourrier.courrierTransferer+"');";

                Connection connection = DatabaseConnection.getConnexion();
                Statement statement = null;
                try {
                      connection.setAutoCommit(false);
                      statement = connection.createStatement();
                      statement.addBatch(ajouterDestinataireSQL);
                      statement.addBatch(recevoirCourrierSQL);
                      statement.addBatch(ajouterCorrespondanceEtapePersonneAffecteurSQL);
                      statement.addBatch(ajouterCorrespondanceEtapePersonneReceveurSQL);
                      statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                      statement.addBatch(ajouterEtapeCourrierSQL);

                      statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                      statement.addBatch(ajouterCorrespondanceEtapeCourrierHistoriqueSQL);
                      statement.addBatch(ajouterHistoriqueCourrierSQL);
                      statement.addBatch(ActivitesQueries.ajouterUneActvitee(TitreActivites.transfertDuCourrier, idCourrier ,idUser,idTypeDactivite,idDirectionUser));
                      int count[]  = statement.executeBatch();

                      connection.commit();
                      FacesContext context = FacesContext.getCurrentInstance();
                      context.getExternalContext().getFlash().setKeepMessages(true);
                      FacesContext.getCurrentInstance().addMessage("messagesTransfererCourrier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation", "Transfer effectuée avec succés"));
                      direction.setTitreDirection(null);
                      etape.setActeur(null);
                      idAgentAffecteAUneTache = null;
                } catch (SQLException e) {
                    FacesContext.getCurrentInstance().addMessage("messagesTransfererCourrier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur avec la base de données s'est produite !!!"));
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

    /***Getter and Setter***/
    public String getExistenceDestinataireDeTransfer() {
        return existenceDestinataireDeTransfer;
    }

    public void setExistenceDestinataireDeTransfer(String existenceDestinataireDeTransfer) {
        this.existenceDestinataireDeTransfer = existenceDestinataireDeTransfer;
    }

    public String getMessageEtape() {
        return messageEtape;
    }

    public void setMessageEtape(String messageEtape) {
        this.messageEtape = messageEtape;
    }

    public String getDateButoirEtape() {
        return dateButoirEtape;
    }

    public void setDateButoirEtape(String dateButoirEtape) {
        this.dateButoirEtape = dateButoirEtape;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public Etape getEtape() {
        return etape;
    }

    public void setEtape(Etape etape) {
        this.etape = etape;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annexe getAnnexe() {
        return annexe;
    }

    public void setAnnexe(Annexe annexe) {
        this.annexe = annexe;
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

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public Date getDateFinalTemp() {
        return dateFinalTemp;
    }

    public void setDateFinalTemp(Date dateFinalTemp) {
        this.dateFinalTemp = dateFinalTemp;
    }

    public String getNomActeurDuTransfer() {
        return nomActeurDuTransfer;
    }

    public void setNomActeurDuTransfer(String nomActeurDuTransfer) {
        this.nomActeurDuTransfer = nomActeurDuTransfer;
    }

    public ReponseCourrier getReponseCourrier() {
        return reponseCourrier;
    }

    public void setReponseCourrier(ReponseCourrier reponseCourrier) {
        this.reponseCourrier = reponseCourrier;
    }

    public String getReferenceInterneCourrierTemp() {
        return referenceInterneCourrierTemp;
    }

    public void setReferenceInterneCourrierTemp(String referenceInterneCourrierTemp) {
        this.referenceInterneCourrierTemp = referenceInterneCourrierTemp;
    }

    public String getMotAppercuDetailEtape() {
        return motAppercuDetailEtape;
    }

    public void setMotAppercuDetailEtape(String motAppercuDetailEtape) {
        this.motAppercuDetailEtape = motAppercuDetailEtape;
    }
}
