package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.CourriersQueries;
import databaseManager.DataBaseQueries;
import databaseManager.DatabaseConnection;
import databaseManager.DiscussionsQueries;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class RepondreAUneTache implements Serializable {

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
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private String idRecevoirCourrier;
    private String elementDuCourrierAModifier;
    private boolean fichierDiscussionAjouter = false;
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
        recupererListeTypeDeCourrier();

    }

    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
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

    public void recupererListeTypeDeCourrier(){
        courrier.setListeTypeDeCourier(CourriersQueries.recupererLaListeDeTypesDeCourrier());
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
        emetteur.setTypeDEmetteur(CourriersQueries.typeDemetteur);
        emetteur.setMinistere(CourriersQueries.ministereEmetteur);
        emetteur.setDirection(CourriersQueries.directeurEmetteur);
        emetteur.setFonction(CourriersQueries.fonctionEmetteur);
        CourriersQueries.recupererLeDestinataireDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLesDetailsDUnCourrierEnregistre(idCourrier);
        courrier.setObjetCourrier(CourriersQueries.objetCourrier);
        courrier.setReferenceCourrier(CourriersQueries.referenceCourrier);
        courrier.setPrioriteCourrier(CourriersQueries.prioriteCourrier);
        courrier.setTypeCourrier(CourriersQueries.typeCourrier);

        courrier.setDossierAlfresco(CourriersQueries.dossierAlfresco);
        courrier.setConfidentiel(CourriersQueries.confidentiel);
        courrier.setDateDEnregistrement(CourriersQueries.dateDEnregistrement);

        nomEtPrenomAjouteurCourrier = CourriersQueries.nomEtPrenomPersonneAjouteurDuCourrier;
        fonctionAjouteurCourrier = CourriersQueries.fonctionPersonneAjouteurDuCourrier;

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);
        nomEtPrenomAjouteurCourrier = CourriersQueries.nomEtPrenomPersonneAjouteurDuCourrier;
        fonctionAjouteurCourrier = CourriersQueries.fonctionPersonneAjouteurDuCourrier;

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

    public void recupererLesTachesAffescteesAUnAgent(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("idCourrier");
        String idUser = (String) session.getAttribute( "idUser" );
        etape.setListeDeMesTaches(DataBaseQueries.recupererLesActionsAffecteesAUnAgents(idUser,idCourrier));
        etape.setNombreDeTacheDUnAgent(String.valueOf(DataBaseQueries.nombreDeTacheDunAgentSurCourrier));
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

    public void recupererLeCreateurDUneTache(ActionEvent actionEvent){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idTache = String.valueOf(params.get("etapeId"));
        String  requeteDestinataireTacheSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.affecteurTache +"' order by etape.id_etape desc";
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
        for (int i = 0; i < etape.getListeDeMesTaches().size(); i++) {
            if ( etape.getListeDeMesTaches().get(i).getId().equals(idEtape)){
                if (etape.getListeDeMesTaches().get(i).getEtat().equals(EtatEtape.termine)){
                    PrimeFaces.current().executeScript("swal('Oups','Cette discussion à déjé été cloturée', 'warning');");
                }else{
                    PrimeFaces.current().executeScript("openFormDiscussion()");
                }
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

    public void creerUneDiscussion(){/*TODO bloque un message vide*/
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser" );
        Connection connection = DatabaseConnection.getConnexion();
        String courrierMinistre = "courrier_ministre";
        if(etape.getReponseTache() == null || etape.getReponseTache().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageerreurdiscussion", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez ecrire un message!!"));
        }else{
            String creerDiscussionSQL = "insert into `discussion_etape` (`etat_discussion`,`message_discussion`,`id_etape`,`id_personne`) VALUES" +
                    " ('" + EtatEtape.Ouvert +"',"+"'"+etape.getReponseTache().replaceAll("'", " ")+"',"+"'"+etape.getId()+"',"+"'"+idUser+"')";

            String updateIdAlfrescoDansDiscussionSQL = null;
            String updateNomFichierDansDiscussionSQL = null;

            if(fichierDiscussionAjouter){
                discussion.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(discussion.getCheminFichierDiscussionSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(discussion.getNomFichierDiscussion())), courrierMinistre));
                updateIdAlfrescoDansDiscussionSQL = "update `discussion_etape` set `identifiant_alfresco_discussion` = '"+discussion.getIdAlfresco()+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
                updateNomFichierDansDiscussionSQL = "update `discussion_etape` set `nom_fichier_discussion` = '"+discussion.getNomFichierDiscussion().trim().replaceAll("'"," ")+"' where id_discussion_etape  = (select id_discussion_etape from (select id_discussion_etape from discussion_etape where etat_discussion = '"+EtatEtape.Ouvert+"' and message_discussion = '"+etape.getReponseTache().replaceAll("'", " ")+"' and id_etape = '"+etape.getId()+"' and id_personne = '"+idUser+"' ) as temp)";
                System.out.println("updateNomFichierDansDiscussionSQL = " + updateNomFichierDansDiscussionSQL);
                System.out.println("updateIdAlfrescoDansDiscussionSQL = " + updateIdAlfrescoDansDiscussionSQL);
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

    public void voirLeFichierDUneDiscussion(ActionEvent actionEvent){
        discussion.setStreamedContentAlfresco(null);
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDiscussionEtape = String.valueOf(params.get("discussionEtapeId"));
        String requeteDiscussionEtapeSQL = "select identifiant_alfresco_discussion from `discussion_etape` where id_discussion_etape = '"+idDiscussionEtape+"' ; ";
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
                }else{
                    PrimeFaces.current().executeScript("swal('Aucun fichier','Pas de fichier joint à ce message', 'warning');");
                }

            }

        } catch (SQLException e) {
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite avec la base de données', 'warning');");
            e.printStackTrace();
        }

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
