package bean;

import alfresco.ConnexionAlfresco;
import alfresco.URLAlfresco;
import database.DataBaseQueries;
import database.DatabaseManager;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import sessionManager.SessionUtils;
import variables.ActionEtape;
import variables.EtatCourrier;
import variables.EtatEtape;
import variables.RoleEtape;

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
import java.util.UUID;


@Named
@SessionScoped
public class DetailDUnCourrierEnvoye implements Serializable {

    private static final long serialVersionUID = 9112784637138639983L;
    private Courrier courrier;
    private Emetteur emetteur;
    private Destinataire destinataire;
    private Annexe annexe;
    private Dossier dossier;
    private Annotation annotation;
    private Etape etape;
    private ReponseCourrier reponseCourrier;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private String existenceDestinataireDeTransfer = "non";
    private boolean fichierReponseCourrierAjouter = false;


    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
        emetteur = new Emetteur();
        annexe = new Annexe();
        etape = new Etape();
        dossier = new Dossier();
        annotation = new Annotation();
        destinataire = new Destinataire();
        reponseCourrier = new ReponseCourrier();
    }

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DataBaseQueries.recupererLesDossiersDUnUtilisateur(idUser));
    }

    public void recupererToutesLesInformationsDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        DataBaseQueries.recupererLEmetteurDUnCourrierParIdCourrier(idCourrier);
        DataBaseQueries.recupererLeDestinataireDUnCourrierParIdCourrier(idCourrier);
        DataBaseQueries.recupererLesDetailsDUnCourrierEnregistre(idCourrier);

        emetteur.setTypeDEmetteur(DataBaseQueries.typeDemetteur);
        emetteur.setMinistere(DataBaseQueries.ministereEmetteur);
        emetteur.setDirection(DataBaseQueries.directeurEmetteur);
        emetteur.setFonction(DataBaseQueries.fonctionEmetteur);
        PrimeFaces.current().executeScript("affichageEnFonctionDuTypeEmetteur()");

        courrier.setObjetCourrier(DataBaseQueries.objetCourrier);
        courrier.setReferenceCourrier(DataBaseQueries.referenceCourrier);
        courrier.setPrioriteCourrier(DataBaseQueries.prioriteCourrier);
        courrier.setTypeCourrier(DataBaseQueries.typeCourrier);

        courrier.setDossierAlfresco(DataBaseQueries.dossierAlfresco);
        courrier.setConfidentiel(DataBaseQueries.confidentiel);
        courrier.setHeureDeReception(DataBaseQueries.heureDeReception);
        courrier.setDateDEnregistrement(DataBaseQueries.dateDEnregistrement);
        courrier.setDateDeReception(DataBaseQueries.dateDeReception);
        courrier.setCommentairesCourrier(DataBaseQueries.commentairesCourrier);
        String jour = courrier.getDateDeReception().substring(courrier.getDateDeReception().lastIndexOf("-") +1);
        String mois = courrier.getDateDeReception().substring(courrier.getDateDeReception().indexOf("-")+1,courrier.getDateDeReception().indexOf("-")+3);
        String annee = courrier.getDateDeReception().substring(0,4);

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);

        courrier.setDateDeReception(jour+"-"+mois+"-"+annee);
        courrier.setHeureDEnregistrement(DataBaseQueries.heureDEnregistrement);
    }

    public void afficherLeFichierDuCourier(){
        HttpSession session = SessionUtils.getSession();
        String idDocumementAlfresco = (String) session.getAttribute("alfrescoId");
        courrier.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(idDocumementAlfresco));
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
            Connection connection = DatabaseManager.getConnexion();
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

    public void recupererLesDestinatairesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String isCourrierTransferer = (String) session.getAttribute( "courrierTransferer");
        destinataire.setListeDestinataire(DataBaseQueries.recupererLesDestinatairesDUnCourrier(idCourrier));
        destinataire.setNombreDeDestinataire(String.valueOf(DataBaseQueries.nombreDeDestinataireDuCourrier));
        destinataire.setListeDestinataireParTransfer(DataBaseQueries.recupererLesDestinatairesParTransferDUnCourrier(idCourrier));
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
            case "Entreprise"  :
                choixTypeDestinataire = "Entreprise";
                requeteDetailDestinataireSQL = "select nom_etablissement,tel_etablissement,mail_etablissement,adresse_etablissement,titre_fonction,nom_direction from `etablissement` inner join `personne` on etablissement.id_etablissement = personne.id_etablissement inner join `direction` on personne.id_direction = direction.id_direction inner join `fonction` on personne.id_fonction = fonction.id_fonction where  personne.id_personne = '"+idDestinataire+"' ; ";
                break;
            case "Association"  :
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

        Connection connection = DatabaseManager.getConnexion();
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

    public void checkIfAlfrescoIsOnline(){
        if(!ConnexionAlfresco.voirSiLeServeurEstEnLigne(URLAlfresco.alfrescoURLDuServeur)){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
        }
    }

    public void recupererLesReponsesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        reponseCourrier.setReponseCourrierList(DataBaseQueries.recupererLesReponsesDUnCourrier(idCourrier));
    }

    public void recupererLHistoriqueDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        etape.setListeHistoriques(DataBaseQueries.recupererLHistoriqueDesActionsSurUnCourrierAPartirDuMomentDeSonEnvoi(idCourrier));
    }

    public void ajouterUneReponseAuCourrier(){

        String uniqueID = UUID.randomUUID().toString();
        if(reponseCourrier.getText().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez ecrire un message"));
        }else{
            System.out.println("ici 1");
            HttpSession session = SessionUtils.getSession();
            String idCourrier = (String) session.getAttribute("courrierId");
            String idUser = (String) session.getAttribute( "idUser");
            String repondreSQL = "INSERT INTO `reponse_courrier` (`message_reponse_courrier`, `identifiant_unique_reponse`,`fk_courrier`, `fk_personne`) " +
                    "VALUES ( '"+reponseCourrier.getText().replaceAll("'", " ")+"','"+uniqueID+"' ,'"+idCourrier+"', '"+idUser+"');";

            String updateIdAlfrescoSQL = null;
            String updateNomFichierSQL = null;

            if(fichierReponseCourrierAjouter){
                reponseCourrier.setIdentifiantAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(reponseCourrier.getCheminFichierSurPC()), FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(reponseCourrier.getNomFichier())), "courrier_ministre"));
                updateIdAlfrescoSQL = "update `reponse_courrier` set `identifiant_alfresco_reponse_courrier` = '"+reponseCourrier.getIdentifiantAlfresco()+"' where id_reponse_courrier  = (select id_reponse_courrier from (select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' ) as temp)";
                updateNomFichierSQL = "update `reponse_courrier` set `nom_fichier_reponse_courrier` = '"+reponseCourrier.getNomFichier()+"' where id_reponse_courrier  = (select id_reponse_courrier from (select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' ) as temp)";
            }

            String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                    " ('" + EtatCourrier.ajoutDUneReponseAuCourrier+"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.ajoutDUneReponseAuCourrier+"')";

            String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                    "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierRecu+"')";

            String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`,`role_agent`) VALUES" +
                    " ('" + idUser +"',"+"'"+ RoleEtape.receveurReponseAuCourrier+"')";
            String ajouterCorrespondancePersonneReponseCourrierSQL = "INSERT INTO `correspondance_personne_reponse_courrier` (`id_personne`,`role`,`id_reponse_courrier`) VALUES" +
                    "('"+ idUser +"',"+"'"+RoleEtape.receveurReponseAuCourrier+"',"+"(select id_reponse_courrier from reponse_courrier where identifiant_unique_reponse = '"+uniqueID+"' )"+")";

            Connection connection = DatabaseManager.getConnexion();
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
                statement.executeBatch();
                connection.commit();
                System.out.println("ici 3 ");
                reponseCourrier.setText(null);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","Votre réponse à bien été ajoutée"));
                recupererLesReponsesDuCourrier();
            } catch (SQLException e) {
                //PrimeFaces.current().executeScript("swal(Erreur','Une erreur s'est produite sur le réseau', 'error');");
                FacesContext.getCurrentInstance().addMessage("messagereponse",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite sur le réseau"));
                e.printStackTrace();
            }


        }
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

    public void voirLeFichierAttacheAUneReponse(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String reponseIdentifiantAlfresco = null;
        reponseIdentifiantAlfresco= String.valueOf(params.get("reponseIdentifiantAlfresco"));
        System.out.println("reponseIdentifiantAlfresco = " + reponseIdentifiantAlfresco);
        if(reponseIdentifiantAlfresco.contains("SpacesStore")) {
            reponseCourrier.setStreamedContentAlfresco(ConnexionAlfresco.telechargerDocumentDansAlfresco(reponseIdentifiantAlfresco));
            System.out.println("reponseCourrier.getStreamedContentAlfresco() = " + reponseCourrier.getStreamedContentAlfresco());
            PrimeFaces.current().executeScript("PF('dlgFichierReponseCourrier').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Aucun fichier','Pas de fichier joint à ce message', 'warning');");
        }
    }


    public void mettreLeCourrierEnFavoris(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        Connection connection = DatabaseManager.getConnexion();
        String mettreCourrierEnFavorisSQL = "update `envoyer_courrier` set `favoris` = '"+ EtatCourrier.favoris +"' where id_courrier = '"+idCourrier+"' ;";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierEnFavoris+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnvoye+"')";

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



    }

    public void archiverLeCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        Connection connection = DatabaseManager.getConnexion();
        String mettreCourrierEnFavorisSQL = "update `envoyer_courrier` set `archive` = '"+ EtatCourrier.archiveActive +"' where id_courrier = '"+idCourrier+"' ; ";

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierEnArchive+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnvoye+"')";

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

    public void recupererIdDossierAuClick(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDossier = params.get("idDossier");
        dossier.setIdDossier(idDossier);
    }

    public void ajouterCourrierDansDossier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");

        String dossierId = DataBaseQueries.voirSiUnCourrierEstDejaDansUnDossier(idCourrier,dossier.getIdDossier());
        if(dossierId != null){

            if(dossierId.equals(dossier.getIdDossier())){
                FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Le courrier est déja dans ce dossier!!!"));
            }else{
                DataBaseQueries.ajouterUnCourrierDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
            }
        }else{
            DataBaseQueries.ajouterUnCourrierDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
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
                DataBaseQueries.creerUnDossier(idUser,dossier.getNomDossier().replaceAll("'"," ").trim(),dossier.getDescriptionDossier());
                dossier.setNomDossier(null);
                dossier.setDescriptionDossier(null);
            }
        }


    }


    /***Getter and Setter***/

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

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public String getExistenceDestinataireDeTransfer() {
        return existenceDestinataireDeTransfer;
    }

    public void setExistenceDestinataireDeTransfer(String existenceDestinataireDeTransfer) {
        this.existenceDestinataireDeTransfer = existenceDestinataireDeTransfer;
    }

    public ReponseCourrier getReponseCourrier() {
        return reponseCourrier;
    }

    public void setReponseCourrier(ReponseCourrier reponseCourrier) {
        this.reponseCourrier = reponseCourrier;
    }
}
