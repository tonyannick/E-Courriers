package bean;

import database.DataBaseQueries;
import dateAndTime.DateUtils;
import model.Courrier;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@SessionScoped
@Named
public class CourriersEnvoyes implements Serializable {

    private static final long serialVersionUID = -8408333902877843429L;
    private Courrier courrier;
    private List<Courrier> courrierList;
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private List<Courrier> courrierTempList = new ArrayList<>();
    private List<Courrier> courrierSauvegardeList = new ArrayList<>();
    private String datePourRechercheAvancee;
    private String moisPourRechercheAvancee;
    private String typeDeCourrierPourRechercheAvancee;
    private boolean isMoisSelectionne = false;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersEnvoyes(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersEnvoyes(DataBaseQueries.recupererTousLesCourriersEnvoyesDUnUtilisateursParSonId(idPersonne));
        courrierSauvegardeList.clear();
        courrierSauvegardeList.addAll(courrier.getListeDesCouriersEnvoyes());
        Collections.unmodifiableList(courrierSauvegardeList);
    }

    public String voirLesDetailsDuCourrier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("courrierId",idCourrier);
        session.setAttribute("alfrescoId",alfrescoId);
        session.setAttribute("courrierEnvoye","courrierEnvoye");
        return "detailduncourrierenvoye.xhtml?faces-redirect=true";
    }

    public void faireUneRechercheAvanceeParDate(){
        boolean trouve = false;
        if(datePourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une date"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersEnvoyes().size(); a++){
                if(courrier.getListeDesCouriersEnvoyes().get(a).getDateDEnregistrement().equals(datePourRechercheAvancee)){
                    courrierTempList.add(courrier.getListeDesCouriersEnvoyes().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersEnvoyes().clear();
                courrier.setListeDesCouriersEnvoyes(courrierTempList);
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

    public void annulerUneRechercheAvancee(){
        courrier.getListeDesCouriersEnvoyes().clear();
        courrier.setListeDesCouriersEnvoyes(courrierSauvegardeList);
        PrimeFaces.current().executeScript("afficherBoutonFaireUneRecherche()");
    }

    private void gestionDeLAffichageDesBoutonsDeRecherche(){
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParDate').hide()");
        PrimeFaces.current().executeScript("afficherBoutonAnnulerRecherche()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParMois').hide()");
        PrimeFaces.current().executeScript("PF('dialogueRechercherCourrierParTypeDeCourrier').hide()");
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
            for (int a = 0; a <  courrier.getListeDesCouriersEnvoyes().size(); a++) {
                try {

                    Date date = sdf.parse(courrier.getListeDesCouriersEnvoyes().get(a).getDateDEnregistrement());
                    if (date.after(DateUtils.premierJourDuMoisAPartirDUneDate) && date.before(DateUtils.dernierJourDuMoisAPartirDUneDate)) {
                        courrierTempList.add(courrier.getListeDesCouriersEnvoyes().get(a));
                        trouve = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(trouve){
                courrier.getListeDesCouriersEnvoyes().clear();
                courrier.setListeDesCouriersEnvoyes(courrierTempList);
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
        return DataBaseQueries.recupererLaListeDeTypesDeCourrier();
    }

    public void faireUneRechercheAvanceeParTypeDeCourrier(){
        boolean trouve = false;
        if(typeDeCourrierPourRechercheAvancee == null){
            FacesContext.getCurrentInstance().addMessage("messagetypedecourrier",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner un type de courrier"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersEnvoyes().size(); a++) {
                if (courrier.getListeDesCouriersEnvoyes().get(a).getExtensionCourrier().equals(typeDeCourrierPourRechercheAvancee)) {
                    courrierTempList.add(courrier.getListeDesCouriersEnvoyes().get(a));
                    trouve = true;
                }
            }

            if(trouve){
                courrier.getListeDesCouriersEnvoyes().clear();
                courrier.setListeDesCouriersEnvoyes(courrierTempList);
                setTypeDeCourrierPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagetypedecourrier",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier dans ce type"));
            }
        }
    }

    /***Getter and setter**/

    public String getDatePourRechercheAvancee() {
        return datePourRechercheAvancee;
    }

    public void setDatePourRechercheAvancee(String datePourRechercheAvancee) {
        this.datePourRechercheAvancee = datePourRechercheAvancee;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public List<Courrier> getCourrierList() {
        return courrierList;
    }

    public void setCourrierList(List<Courrier> courrierList) {
        this.courrierList = courrierList;
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

    public String getMoisPourRechercheAvancee() {
        return moisPourRechercheAvancee;
    }

    public void setMoisPourRechercheAvancee(String moisPourRechercheAvancee) {
        this.moisPourRechercheAvancee = moisPourRechercheAvancee;
    }

    public String getTypeDeCourrierPourRechercheAvancee() {
        return typeDeCourrierPourRechercheAvancee;
    }

    public void setTypeDeCourrierPourRechercheAvancee(String typeDeCourrierPourRechercheAvancee) {
        this.typeDeCourrierPourRechercheAvancee = typeDeCourrierPourRechercheAvancee;
    }
}
