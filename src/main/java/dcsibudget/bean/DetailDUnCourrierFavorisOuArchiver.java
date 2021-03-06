package dcsibudget.bean;

import dcsibudget.alfresco.ConnexionAlfresco;
import dcsibudget.databaseManager.CourriersQueries;
import dcsibudget.databaseManager.DataBaseQueries;
import dcsibudget.databaseManager.DatabaseConnection;
import dcsibudget.databaseManager.DossiersQueries;
import dcsibudget.model.*;
import org.primefaces.PrimeFaces;
import dcsibudget.sessionManager.SessionUtils;
import dcsibudget.variables.ActionEtape;
import dcsibudget.variables.EtatCourrier;
import dcsibudget.variables.EtatEtape;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class DetailDUnCourrierFavorisOuArchiver implements Serializable {

    private static final long serialVersionUID = -1619928909603281338L;
    private Courrier courrier;
    private Emetteur emetteur;
    private Destinataire destinataire;
    private Annexe annexe;
    private Dossier dossier;
    private Annotation annotation;
    private Etape etape;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();
    private String favorisOuArchive = null;
    private String urlDeRetour = null;
    private String indicateurFavorisOuArchive;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
        emetteur = new Emetteur();
        annexe = new Annexe();
        etape = new Etape();
        dossier = new Dossier();
        annotation = new Annotation();
        destinataire = new Destinataire();
    }

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DossiersQueries.recupererLesDossiersDUnUtilisateur(idUser));
    }

    public void recupererToutesLesInformationsDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        favorisOuArchive = (String) session.getAttribute("favorisouarchive");
        CourriersQueries.recupererLEmetteurDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLeDestinataireDUnCourrierParIdCourrier(idCourrier);
        CourriersQueries.recupererLesDetailsDUnCourrierEnregistre(idCourrier);

        emetteur.setTypeDEmetteur(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("type_personne"));
        emetteur.setMinistere(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_etablissement"));
        emetteur.setDirection(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_direction"));
        emetteur.setFonction(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("titre_fonction"));

        emetteur.setMinistereAutreMinistere(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_etablissement"));
        emetteur.setDirectionAutreMinistere(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_direction"));
        emetteur.setFonctionAutreMinistere(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("titre_fonction"));

        emetteur.setRaisonSocial(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_etablissement"));
        emetteur.setDirectionEntreprise(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nom_direction"));
        emetteur.setFonctionEntreprise(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("titre_fonction"));
        emetteur.setTelephoneEntreprise(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("tel_etablissement"));
        emetteur.setEmailEntreprise(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("mail_etablissement"));
        emetteur.setAdresseEntreprise(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("adresse_etablissement"));

        emetteur.setNomParticulier(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("nomEtPrenomEmetteurDuCourrier"));
        emetteur.setTelephoneParticulier(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("tel"));
        emetteur.setEmailParticulier(CourriersQueries.mapDetailsEmetteurDUnCourrier.get("email"));

        PrimeFaces.current().executeScript("affichageEnFonctionDuTypeEmetteur()");

        courrier.setObjetCourrier(CourriersQueries.mapDetailsCourrierEnregistre.get("objet"));
        courrier.setReferenceCourrier(CourriersQueries.mapDetailsCourrierEnregistre.get("reference"));
        courrier.setPrioriteCourrier(CourriersQueries.mapDetailsCourrierEnregistre.get("priorite"));
        courrier.setTypeCourrier(CourriersQueries.mapDetailsCourrierEnregistre.get("type_courrier"));
        courrier.setAccuseDeReception(CourriersQueries.mapDetailsCourrierEnregistre.get("accuse_reception"));
        courrier.setCommentairesCourrier(CourriersQueries.mapDetailsCourrierEnregistre.get("commentaires"));
        courrier.setDossierAlfresco(CourriersQueries.mapDetailsCourrierEnregistre.get("dossier_alfresco_emetteur"));
        courrier.setConfidentiel(CourriersQueries.mapDetailsCourrierEnregistre.get("confidentiel"));
        courrier.setDateDEnregistrement(CourriersQueries.mapDetailsCourrierEnregistre.get("date_enregistrement"));
        courrier.setHeureDEnregistrement(CourriersQueries.mapDetailsCourrierEnregistre.get("heure_enregistrement"));

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);
        System.out.println("favorisOuArchive = " + favorisOuArchive);
        if(favorisOuArchive.equals("favoris")){
            indicateurFavorisOuArchive = "favoris";
            urlDeRetour = "courriersfavoris.xhtml";
        }else{
            indicateurFavorisOuArchive = "archives";
            urlDeRetour = "courriersarchives.xhtml";
        }

        PrimeFaces.current().executeScript("affichageFavorisOuPDF()");
    }

    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("toastErreurAlfresco()");
        }
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

            FacesContext.getCurrentInstance().addMessage("message", new FacesMessage(FacesMessage.SEVERITY_INFO,"Annexe 1" ,"Fichier Annexe 1"));
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
        System.out.println("annexeNumber = " + annexeNumber);
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
        destinataire.setListeDestinataire(CourriersQueries.recupererLesDestinatairesDUnCourrier(idCourrier));
        destinataire.setNombreDeDestinataire(String.valueOf(CourriersQueries.nombreDeDestinataireDuCourrier));
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


    public void recupererIdDossierAuClick(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDossier = params.get("idDossier");
        dossier.setIdDossier(idDossier);
    }

    public void ajouterCourrierDansDossier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");

        String dossierId = DossiersQueries.voirSiUnCourrierEstDejaDansUnDossier(idCourrier,dossier.getIdDossier());
        if(dossierId != null){

            if(dossierId.equals(dossier.getIdDossier())){
                FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Le courrier est déja dans ce dossier!!!"));
            }else{
                DossiersQueries.ajouterUnCourrierDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
            }
        }else{
            DossiersQueries.ajouterUnCourrierDansUnDossier(dossier.getIdDossier(),idCourrier,idUser);
        }
    }

    public void creerUnDossier(){

        if(dossier.getNomDossier().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Attention", "Vous devez renseigner le nom du dossier !!!"));
        }else{
            boolean trouve = false;
            for(int a =0; a < dossier.getDossierList().size(); a++){
                if(dossier.getNomDossier().equals(dossier.getDossierList().get(a).getNomDossier())){
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
                DossiersQueries.creerUnDossier(idUser,dossier.getNomDossier().replaceAll("'"," ").trim(),dossier.getDescriptionDossier());
                dossier.setNomDossier(null);
                dossier.setDescriptionDossier(null);
            }
        }


    }

    public String determinerUserParRapportAuCourrier(){
        String natureUserParRapportAuCourrier= null;
        HttpSession session = SessionUtils.getSession();
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        if(directionUser.equals(emetteur.getDirection()) && fonctionUser.equals(emetteur.getFonction())){
            natureUserParRapportAuCourrier = "Emetteur";
        }else{
            natureUserParRapportAuCourrier = "Destinataire";
        }

        System.out.println("natureUserParRapportAuCourrier = " + natureUserParRapportAuCourrier);
        return natureUserParRapportAuCourrier;
    }

    public void restaurerLeCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        String idUserAccuseDeReception = null;
        String retirerCourrierEnArchiveSQL = null;

        if(determinerUserParRapportAuCourrier().equals("Destinataire")){
            for(int a = 0; a < destinataire.getListeDestinataire().size(); a++ ){
                if(destinataire.getListeDestinataire().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataire().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataire().get(a).getIdDestinataire();
                }
            }
            retirerCourrierEnArchiveSQL = "update `recevoir_courrier` set `archive` = '"+ EtatCourrier.archiveNonActive+"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ idUserAccuseDeReception+"'; ";

        }else{
            retirerCourrierEnArchiveSQL = "update `envoyer_courrier` set `archive` = '"+ EtatCourrier.archiveNonActive+"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ emetteur.getIdEmetteur()+"'; ";

        }

        Connection connection = DatabaseConnection.getConnexion();

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierRetirerDesArchives+"')";

        String ajouterCorrespondanceEtapeCourrierSQL = "INSERT INTO `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                "('"+ idCourrier +"',"+"'"+EtatCourrier.courrierEnvoye+"')";

        String ajouterCorrespondanceEtapePersonneSQL = "INSERT INTO `correspondance_personne_etape` (`id_personne`) VALUES" +
                " ('" + idUser +"')";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(retirerCourrierEnArchiveSQL);
            statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
            statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
            statement.addBatch(ajouterEtapeCourrierSQL);
            statement.executeBatch();
            connection.commit();
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            PrimeFaces.current().executeScript("swal('Validation','Le courrier à bien été restauré', 'success');");
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

    public void retirerLeCourrierDesFavoris(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        String idUser = (String) session.getAttribute( "idUser");
        String directionUser = (String) session.getAttribute( "directionUser");
        String fonctionUser = (String) session.getAttribute( "fonctionUser");
        String idUserAccuseDeReception = null;
        String mettreCourrierEnFavorisSQL = null;
        if(determinerUserParRapportAuCourrier().equals("Destinataire")){
            for(int a = 0; a < destinataire.getListeDestinataire().size(); a++ ){
                if(destinataire.getListeDestinataire().get(a).getDirection().equalsIgnoreCase(directionUser) && destinataire.getListeDestinataire().get(a).getFonction().equalsIgnoreCase(fonctionUser)){
                    idUserAccuseDeReception = destinataire.getListeDestinataire().get(a).getIdDestinataire();
                }
            }
            mettreCourrierEnFavorisSQL = "update `recevoir_courrier` set `favoris` = '"+ EtatCourrier.pasfavoris +"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ idUserAccuseDeReception+"'; ";

        }else{
            mettreCourrierEnFavorisSQL = "update `envoyer_courrier` set `favoris` = '"+ EtatCourrier.pasfavoris +"' where id_courrier = '"+idCourrier+"' and id_personne = '"+ emetteur.getIdEmetteur()+"'; ";

        }

        Connection connection = DatabaseConnection.getConnexion();

        String ajouterEtapeCourrierSQL = "INSERT INTO `etape` (`titre`, `etat`, `message`) VALUES" +
                " ('" + EtatCourrier.modificationCourrier +"',"+"'"+ EtatEtape.termine+"',"+"'"+ ActionEtape.courrierRetirerDesFavoris+"')";

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
            PrimeFaces.current().executeScript("PF('dialogueCourrierRetirer').show()");
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

    public String retourALaListeDesCourriersFavoris(){
        PrimeFaces.current().executeScript("PF('dialoguecourrierbienenvoye').hide()");
        return "courriersfavoris?faces-redirect=true";
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

    public String getFavorisOuArchive() {
        return favorisOuArchive;
    }

    public void setFavorisOuArchive(String favorisOuArchive) {
        this.favorisOuArchive = favorisOuArchive;
    }

    public String getUrlDeRetour() {
        return urlDeRetour;
    }

    public void setUrlDeRetour(String urlDeRetour) {
        this.urlDeRetour = urlDeRetour;
    }

    public String getIndicateurFavorisOuArchive() {
        return indicateurFavorisOuArchive;
    }

    public void setIndicateurFavorisOuArchive(String indicateurFavorisOuArchive) {
        this.indicateurFavorisOuArchive = indicateurFavorisOuArchive;
    }
}
