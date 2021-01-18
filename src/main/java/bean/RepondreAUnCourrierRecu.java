package bean;

import alfresco.ConnexionAlfresco;
import alfresco.NomDesDossiers;
import databaseManager.*;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SessionScoped
@Named
public class RepondreAUnCourrierRecu implements Serializable {

    private static final long serialVersionUID = 6721136176890131504L;

    private Courrier courrier;
    private Emetteur emetteur;
    private Direction direction;
    private Destinataire destinataire;
    private Annexe annexe;
    private Annotation annotation;
    private Discussion discussion;
    private Etape etape;
    private Dossier dossier;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private List<String> mesAgentsListe = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private String idAgentAffecteAUneTache = null;
    private boolean fichierDiscussionAjouter = false;
    private String premiereEtapeActeur;
    private String premiereEtapeMessage;
    private String premiereEtapeDate;
    private String messageEtape;
    private String dateButoirEtape;
    private String idDirectionATransfererLeCourrier;
    private ScheduleModel eventModel;
    private Date dateFinalTemp = null;
    private String existenceDestinataireDeTransfer = "non";
    private String nomActeurDuTransfer;
    private String nomAgentAccuseDeReception;
    private String dateAccusseDeReception;
    private String messageAccuseDeReception;
    private boolean isResponsable = false;


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
    }


    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
        }
    }

    public void recupererLesActionsParEtapeDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String directionUser = (String) session.getAttribute("directionUser");
        etape.setListeDesActionsSurLeCourrier(DataBaseQueries.recupererLesActionsEffectueesSurUnCourrierPourLaTimeLine(idCourrier));
        etape.setNombreDeTache(String.valueOf(DataBaseQueries.nombreDActionEnCoursDuCourrier));

        String annee = null; String mois = null;String jour = null;
        String anneeAccusseDeReception = null; String moisAccusseDeReception = null;String jourAccusseDeReception = null;

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

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DossiersQueries.recupererLesDossiersDUnUtilisateur(idUser));
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

        courrier.setObjetCourrier(CourriersQueries.objetCourrier);
        courrier.setReferenceCourrier(CourriersQueries.referenceCourrier);
        courrier.setPrioriteCourrier(CourriersQueries.prioriteCourrier);
        courrier.setTypeCourrier(CourriersQueries.typeCourrier);
        courrier.setAccuseDeReception(CourriersQueries.accuseDeReception);

        courrier.setDossierAlfresco(CourriersQueries.dossierAlfresco);
        courrier.setConfidentiel(CourriersQueries.confidentiel);
        courrier.setHeureDeReception(CourriersQueries.heureDeReception);
        courrier.setDateDEnregistrement(CourriersQueries.dateDEnregistrement);
        courrier.setDateDeReception(CourriersQueries.dateDeReception);
        courrier.setReferenceInterne(CourriersQueries.referenceInterne);

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

    public void recupererLesTachesAffecteesAUnAgent(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        String idUser = (String) session.getAttribute( "idUser" );
        etape.setListeDeMesTaches(DataBaseQueries.recupererLesActionsAffecteesAUnAgents(idUser,idCourrier));
        etape.setNombreDeTacheDUnAgent(String.valueOf(DataBaseQueries.nombreDeTacheDunAgentSurCourrier));
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

               /* for(int a = 0; a <  listeIdAnnexeAlfresco.size(); a++){
                    System.out.println(" listeIdAnnexeAlfresco.get(0) = " +  listeIdAnnexeAlfresco.get(0));
                }*/
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

    public void clickExpansionToggleEtape(ToggleEvent toggleEvent){
        discussion.setIdEtape(((Etape)toggleEvent.getData()).getId());
        discussion.setListeDiscussion(DiscussionsQueries.recupererLesDiscussionsDUneEtape(discussion.getIdEtape()));
        PrimeFaces.current().executeScript("PF('panelloadingdiscussion').close()");
    }

    public void recupererLeCreateurDUneTache(ActionEvent actionEvent){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idTache = String.valueOf(params.get("etapeId"));

        String requeteDestinataireTacheSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.affecteurTache +"' order by etape.id_etape desc";


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

    public void creerUneDiscussion(){/*TODO bloque un message vide*/
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        String idCourrier = (String) session.getAttribute("courrierId");
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



    /****Getter and setter***/


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


}
