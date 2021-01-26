package bean;

import databaseManager.CourriersQueries;
import databaseManager.DataBaseQueries;
import databaseManager.DatabaseConnection;
import databaseManager.DirectionQueries;
import dateAndTime.DateUtils;
import model.Courrier;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;
import variables.EtatCourrier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@SessionScoped
@Named
public class CourriersRecus implements Serializable {

    private static final long serialVersionUID = 4919715941602670728L;
    private Courrier courrier;
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private String datePourRechercheAvancee;
    private String moisPourRechercheAvancee;
    private String directionPourRechercheAvancee;
    private String typeDeCourrierPourRechercheAvancee;
    private String motClesPourRechercheAvancee;
    private String referenceInternePourRechercheAvancee;
    private boolean isMoisSelectionne = false;
    private List<Courrier> courrierTempList = new ArrayList<>();
    private List<Courrier> courrierSauvegardeList = new ArrayList<>();


    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersRecus(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersRecus(CourriersQueries.recupererTousLesCourriersReçusParUnUtilisateurParSonId(idPersonne));
        courrierSauvegardeList.clear();
        courrierSauvegardeList.addAll(courrier.getListeDesCouriersRecus());
        Collections.unmodifiableList(courrierSauvegardeList);
    }

    public String voirLesDetailsDuCourrier(){

        HttpSession session = SessionUtils.getSession();
        boolean isResponsable = (boolean)session.getAttribute("isResponsable");

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        String confidentiel = (params.get("confidentiel"));
        String etatTransfer = (params.get("etatTransfer"));
        String dossierId = (params.get("dossierId"));

        if(confidentiel.equals(EtatCourrier.confidentiel)){
            if(!isResponsable){
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de consulter ce courrier confidentiel', 'warning');");
                return null;
            }else{
                session.setAttribute("courrierId", idCourrier);
                session.setAttribute("alfrescoId", alfrescoId);
                session.setAttribute("dossierId", dossierId);

                if (etatTransfer.equalsIgnoreCase("oui")) {
                    session.setAttribute("courrierTransferer", "courrierTransferer");
                    return "detailduncourriertransferer.xhtml?faces-redirect=true";
                } else {
                    return "detailduncourrierrecus.xhtml?faces-redirect=true";
                }
            }
        }else{
            session.setAttribute("courrierId", idCourrier);
            session.setAttribute("alfrescoId", alfrescoId);
            session.setAttribute("dossierId", dossierId);

            if (etatTransfer.equalsIgnoreCase("oui")) {
                session.setAttribute("courrierTransferer", "courrierTransferer");
                return "detailduncourriertransferer.xhtml?faces-redirect=true";
            } else {
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }
        }

    }

    public void faireUneRechercheAvanceeParDate(){
        boolean trouve = false;
        if(datePourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une date"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersRecus().size(); a++){
                if(courrier.getListeDesCouriersRecus().get(a).getDateDEnregistrement().equals(datePourRechercheAvancee)){
                    courrierTempList.add(courrier.getListeDesCouriersRecus().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersRecus().clear();
                courrier.setListeDesCouriersRecus(courrierTempList);
                setDatePourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier pour cette date"));

            }
        }

    }

    public List<String> recupererLesMoisDeLAnnee(){
        return Arrays.asList(DateUtils.recupererTousLesMoisDeLAnnee());
    }

    public void faireUneRechercheAvanceeParMotsCles(){
        boolean trouve = false;
        if(motClesPourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messageparmotscles",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner un mot clé"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersRecus().size(); a++){
                if(courrier.getListeDesCouriersRecus().get(a).getMotsclesCourrier().contains(motClesPourRechercheAvancee)){
                    courrierTempList.add(courrier.getListeDesCouriersRecus().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersRecus().clear();
                courrier.setListeDesCouriersRecus(courrierTempList);
                setMotClesPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messageparmotscles",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier trouvé"));

            }
        }
    }

    public void faireUneRechercheAvanceeParReferenceInterne(){
        boolean trouve = false;
        if(referenceInternePourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagepourreferenceinterne",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une référence interne"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersRecus().size(); a++){
                if(courrier.getListeDesCouriersRecus().get(a).getReferenceInterne().contains(referenceInternePourRechercheAvancee.trim())){
                    courrierTempList.add(courrier.getListeDesCouriersRecus().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersRecus().clear();
                courrier.setListeDesCouriersRecus(courrierTempList);
                setMotClesPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagepourreferenceinterne",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier trouvé"));

            }
        }

    }

    public void annulerUneRechercheAvancee(){
        courrier.getListeDesCouriersRecus().clear();
        courrier.setListeDesCouriersRecus(courrierSauvegardeList);
        PrimeFaces.current().executeScript("afficherBoutonFaireUneRecherche()");
    }

    public void avoirDateEnFonctionDuMoisAuClick(){
        if(moisPourRechercheAvancee != null){
            DateUtils.transformerNomDuMoisEnDatePourLAnneeEnCours(moisPourRechercheAvancee);
            isMoisSelectionne = true;
        }else{
            isMoisSelectionne = false;
        }
    }

    public void faireUneRechercheAvanceeParMois(){
        boolean trouve = false;
        if(isMoisSelectionne){
            courrierTempList.clear();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int a = 0; a <  courrier.getListeDesCouriersRecus().size(); a++) {
                try {

                    Date date = sdf.parse(courrier.getListeDesCouriersRecus().get(a).getDateDEnregistrement());
                    if (date.after(DateUtils.premierJourDuMoisAPartirDUneDate) && date.before(DateUtils.dernierJourDuMoisAPartirDUneDate)) {
                        courrierTempList.add(courrier.getListeDesCouriersRecus().get(a));
                        trouve = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(trouve){
                courrier.getListeDesCouriersRecus().clear();
                courrier.setListeDesCouriersRecus(courrierTempList);
                setMoisPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagecourrierparmois",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier dans ce mois"));
            }
        }else{
            FacesContext.getCurrentInstance().addMessage("messagecourrierparmois",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner un mois"));
        }

    }

    public List<String> avoirListeDesTypesDeCourrier(){
        return CourriersQueries.recupererLaListeDeTypesDeCourrier();
    }

    public List<String> avoirListeDesDirections(){
        return DirectionQueries.recupererLaListeDesDirections();
    }

    public void faireUneRechercheAvanceeParTypeDeCourrier(){
        boolean trouve = false;
        if(typeDeCourrierPourRechercheAvancee == null){
            FacesContext.getCurrentInstance().addMessage("messagetypedecourrier",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner un type de courrier"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersRecus().size(); a++) {
                if (courrier.getListeDesCouriersRecus().get(a).getExtensionCourrier().equals(typeDeCourrierPourRechercheAvancee)) {
                    courrierTempList.add(courrier.getListeDesCouriersRecus().get(a));
                    trouve = true;
                }
            }

            if(trouve){
                courrier.getListeDesCouriersRecus().clear();
                courrier.setListeDesCouriersRecus(courrierTempList);
                setTypeDeCourrierPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagetypedecourrier",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier dans ce type"));
            }
        }
    }

    public void faireUneRechercheAvanceePaDirection(){
        if(directionPourRechercheAvancee == null){
            FacesContext.getCurrentInstance().addMessage("messagedirection",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une direction"));
        }else{
            courrierTempList.clear();
            List<String> idDesCourriersRecus = new ArrayList<>();
            List<String> idDesCourriersCorrespondants = new ArrayList<>();
            for(int a = 0; a < courrier.getListeDesCouriersRecus().size(); a++) {
                idDesCourriersRecus.add(courrier.getListeDesCouriersRecus().get(a).getIdCourrier());
            }
            ResultSet resultSet = null;
            ResultSet resultSet1 = null;
            Connection connection = DatabaseConnection.getConnexion();
            String requeteSQL = null;
            try {
                Statement statement = connection.createStatement();
                for(int a = 0; a < idDesCourriersRecus.size(); a++){
                    requeteSQL = "select * from (`recevoir_courrier` inner join `personne` on recevoir_courrier.id_personne = personne.id_personne inner join type_de_personne on personne.fk_type_personne = type_de_personne.id_type_de_personne left join `fonction` on personne.id_fonction = fonction.id_fonction left join `direction` on personne.id_direction = direction.id_direction ) where id_courrier = " + idDesCourriersRecus.get(a) + " and recevoir_courrier.transfer is NULL;";
                    resultSet = statement.executeQuery(requeteSQL);
                    while (resultSet.next()){
                        if( resultSet.getString("nom_direction").equals(directionPourRechercheAvancee.trim())){
                            idDesCourriersCorrespondants.add(idDesCourriersRecus.get(a));
                        }
                    }
                }

                if(idDesCourriersCorrespondants.size() > 0){
                    String requeteMesCourriersSQL = null;
                    Statement statement1 = connection.createStatement();
                    for(int a = 0; a < idDesCourriersCorrespondants.size(); a++){
                        requeteMesCourriersSQL = "select * from `envoyer_courrier` inner join `courrier` on envoyer_courrier.id_courrier = courrier.id_courrier left join correspondance_dossier_courrier on correspondance_dossier_courrier.id_courrier = courrier.id_courrier left join dossier on correspondance_dossier_courrier.id_dossier = dossier.id_dossier inner join personne on envoyer_courrier.id_personne = personne.id_personne inner join direction on personne.id_direction = direction.id_direction inner join type_courrier on courrier.fk_type_courrier = type_courrier.id_type_courrier where courrier.id_courrier = '"+idDesCourriersCorrespondants.get(a)+"' and courrier.etat = '"+ EtatCourrier.courrierEnvoye+"' and envoyer_courrier.favoris = '"+EtatCourrier.pasfavoris+"' and envoyer_courrier.archive =  '"+ EtatCourrier.archiveNonActive +"' order by courrier.id_courrier desc;";
                        resultSet1 = statement1.executeQuery(requeteMesCourriersSQL);
                        while (resultSet1.next()){
                            courrierTempList.add(new Courrier(
                                    resultSet1.getString("reference"),
                                    resultSet1.getString("priorite"),
                                    resultSet1.getString("objet"),
                                    resultSet1.getString("courrier.date_enregistrement"),
                                    resultSet1.getString("courrier.id_courrier"),
                                    resultSet1.getString("confidentiel"),
                                    resultSet1.getString("titre_type_courrier"),
                                    resultSet1.getString("id_envoyer"),
                                    resultSet1.getString("identifiant_alfresco"),
                                    resultSet1.getString("dossier.id_dossier"),
                                    resultSet1.getString("confirmation_reception")));
                        }
                    }

                    courrier.getListeDesCouriersRecus().clear();
                    courrier.setListeDesCouriersRecus(courrierTempList);
                    setDirectionPourRechercheAvancee(null);
                    gestionDeLAffichageDesBoutonsDeRecherche();
                }else{
                    FacesContext.getCurrentInstance().addMessage("messagedirection",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier de cette direction"));

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }



        }
    }

    private void gestionDeLAffichageDesBoutonsDeRecherche(){
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParDate').hide()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParMois').hide()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParMotsCles').hide()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParTypeDeCourrier').hide()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParDirection').hide()");
        PrimeFaces.current().executeScript("afficherBoutonAnnulerRecherche()");
    }

    /***Getter and setter**/
    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getDatePourRechercheAvancee() {
        return datePourRechercheAvancee;
    }

    public void setDatePourRechercheAvancee(String datePourRechercheAvancee) {
        this.datePourRechercheAvancee = datePourRechercheAvancee;
    }

    public String getMoisPourRechercheAvancee() {
        return moisPourRechercheAvancee;
    }

    public void setMoisPourRechercheAvancee(String moisPourRechercheAvancee) {
        this.moisPourRechercheAvancee = moisPourRechercheAvancee;
    }

    public String getDirectionPourRechercheAvancee() {
        return directionPourRechercheAvancee;
    }

    public void setDirectionPourRechercheAvancee(String directionPourRechercheAvancee) {
        this.directionPourRechercheAvancee = directionPourRechercheAvancee;
    }

    public String getTypeDeCourrierPourRechercheAvancee() {
        return typeDeCourrierPourRechercheAvancee;
    }

    public void setTypeDeCourrierPourRechercheAvancee(String typeDeCourrierPourRechercheAvancee) {
        this.typeDeCourrierPourRechercheAvancee = typeDeCourrierPourRechercheAvancee;
    }

    public String getMotClesPourRechercheAvancee() {
        return motClesPourRechercheAvancee;
    }

    public void setMotClesPourRechercheAvancee(String motClesPourRechercheAvancee) {
        this.motClesPourRechercheAvancee = motClesPourRechercheAvancee;
    }

    public String getReferenceInternePourRechercheAvancee() {
        return referenceInternePourRechercheAvancee;
    }

    public void setReferenceInternePourRechercheAvancee(String referenceInternePourRechercheAvancee) {
        this.referenceInternePourRechercheAvancee = referenceInternePourRechercheAvancee;
    }
}
