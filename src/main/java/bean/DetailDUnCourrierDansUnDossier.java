package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.DataBaseQueries;
import databaseManager.DatabasConnection;
import databaseManager.DossiersQueries;
import model.*;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;

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
public class DetailDUnCourrierDansUnDossier implements Serializable {

    private static final long serialVersionUID = -9197083489401135221L;
    private Courrier courrier;
    private Emetteur emetteur;
    private Destinataire destinataire;
    private Annexe annexe;
    private Dossier dossier;
    private Annotation annotation;
    private Etape etape;
    private List<String> listeIdAnnexeAlfresco = new ArrayList<>();


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
        String jour = courrier.getDateDeReception().substring(courrier.getDateDeReception().lastIndexOf("-") +1);
        String mois = courrier.getDateDeReception().substring(courrier.getDateDeReception().indexOf("-")+1,courrier.getDateDeReception().indexOf("-")+3);
        String annee = courrier.getDateDeReception().substring(0,4);
        courrier.setDateDeReception(jour+"-"+mois+"-"+annee);

        String jourEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().lastIndexOf("-") +1);
        String moisEnregistrement = courrier.getDateDEnregistrement().substring(courrier.getDateDEnregistrement().indexOf("-")+1,courrier.getDateDEnregistrement().indexOf("-")+3);
        String anneeEnregistrement = courrier.getDateDEnregistrement().substring(0,4);
        courrier.setDateDEnregistrement(jourEnregistrement+"-"+moisEnregistrement+"-"+anneeEnregistrement);

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
        String sessionID = session.getAttribute("uniqueUserID").toString();
        courrier.setIdCourrier((String) session.getAttribute( "courrierId"));
        annexe.setNombreDAnnexe(DataBaseQueries.recupererLeNombreDAnnexeDUnCourrier(courrier.getIdCourrier()));

        PrimeFaces.current().executeScript("affichageGridAnnexe()");
        if ( Integer.parseInt(annexe.getNombreDAnnexe()) > 0){
            String voirListeAnnexeSQL = "Select * from `annexe` where id_courrier = "+courrier.getIdCourrier()+";";
            Connection connection = DatabasConnection.getConnexion();
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
        annotation.setListeDesAnnotations(DataBaseQueries.recupererLesAnnotationsDUnCourrier(idCourrier));
    }

    public void recupererLesDestinatairesDuCourrier(){
        HttpSession session = SessionUtils.getSession();
        String idCourrier = (String) session.getAttribute("courrierId");
        destinataire.setListeDestinataire(DataBaseQueries.recupererLesDestinatairesDUnCourrier(idCourrier));
        destinataire.setNombreDeDestinataire(String.valueOf(DataBaseQueries.nombreDeDestinataireDuCourrier));
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

        Connection connection = DatabasConnection.getConnexion();
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


    public void retirerUnCourrierDUnDossier(){
        HttpSession session = SessionUtils.getSession();
        String courrierId = (String) session.getAttribute("courrierId");
        String idDossier = (String) session.getAttribute("idDossier");
        Connection connection = DatabasConnection.getConnexion();
        String retirerCourrierDuDossierSQL = "DELETE FROM `correspondance_dossier_courrier` WHERE id_dossier = '"+idDossier+"' and id_courrier='"+courrierId+"';";

        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(retirerCourrierDuDossierSQL);
            statement.executeBatch();
            connection.commit();
            PrimeFaces.current().executeScript("PF('dialogueConfirmationSuppression').hide()");
            PrimeFaces.current().executeScript("PF('dialogueRetourAuxCourriersDuDossiers').show()");
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite!!!"));
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


    public String retourALaListeDesCourriersDuDossiers(){
        return "detaildundossier?faces-redirect=true";
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

}
