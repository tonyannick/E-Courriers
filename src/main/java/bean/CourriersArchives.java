package bean;

import databaseManager.CourriersQueries;
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
public class CourriersArchives implements Serializable {

    private static final long serialVersionUID = -1253920046630977484L;
    private Courrier courrier;
    private String archive = "archive";
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private String datePourRechercheAvancee;
    private String moisPourRechercheAvancee;
    private List<Courrier> courrierTempList = new ArrayList<>();
    private List<Courrier> courrierSauvegardeList = new ArrayList<>();
    private boolean isMoisSelectionne = false;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersArchives(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersArchives(CourriersQueries.recupererTousLesCourriersArchivesDUnUtilisateursParSonId(idPersonne));
        courrierSauvegardeList.clear();
        courrierSauvegardeList.addAll(courrier.getListeDesCouriersArchives());
        Collections.unmodifiableList(courrierSauvegardeList);
    }



    public void faireUneRechercheAvanceeParDate(){
        boolean trouve = false;
        if(datePourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une date"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersArchives().size(); a++){
                if(courrier.getListeDesCouriersArchives().get(a).getDateDeReception().equals(datePourRechercheAvancee)){
                    courrierTempList.add(courrier.getListeDesCouriersArchives().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersArchives().clear();
                courrier.setListeDesCouriersFavoris(courrierTempList);
                setDatePourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier pour cette date"));

            }
        }

    }

    public void faireUneRechercheAvanceeParMois(){
        boolean trouve = false;
        if(isMoisSelectionne){
            courrierTempList.clear();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int a = 0; a <  courrier.getListeDesCouriersArchives().size(); a++) {
                try {
                    Date date = sdf.parse(courrier.getListeDesCouriersArchives().get(a).getDateDeReception());
                    if (date.after(DateUtils.premierJourDuMoisAPartirDUneDate) && date.before(DateUtils.dernierJourDuMoisAPartirDUneDate)) {
                        courrierTempList.add(courrier.getListeDesCouriersArchives().get(a));
                        trouve = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(trouve){
                courrier.getListeDesCouriersArchives().clear();
                courrier.setListeDesCouriersFavoris(courrierTempList);
                setMoisPourRechercheAvancee(null);
                gestionDeLAffichageDesBoutonsDeRecherche();
            }else{
                FacesContext.getCurrentInstance().addMessage("messagecourrierparmois",new FacesMessage(FacesMessage.SEVERITY_WARN,"Aucun resultat","Pas de courrier dans ce mois"));
            }
        }else{
            FacesContext.getCurrentInstance().addMessage("messagecourrierparmois",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner un mois"));
        }

    }

    public void avoirDateEnFonctionDuMoisAuClick(){
        if(moisPourRechercheAvancee != null){
            DateUtils.transformerNomDuMoisEnDatePourLAnneeEnCours(moisPourRechercheAvancee);
            isMoisSelectionne = true;
        }else{
            isMoisSelectionne = false;
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

    public String voirLesDetailsDuCourrier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("courrierId",idCourrier);
        session.setAttribute("alfrescoId",alfrescoId);
        session.setAttribute("favorisouarchive",archive);
        return "detailduncourrierfavorisouarchiver.xhtml?faces-redirect=true";
    }

    public List<String> recupererLesMoisDeLAnnee(){

        return Arrays.asList(DateUtils.recupererTousLesMoisDeLAnnee());
    }


    /***Getter and setter**/

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

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
}
