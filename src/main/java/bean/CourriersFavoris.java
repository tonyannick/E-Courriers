package bean;

import databaseManager.CourriersQueries;
import dateAndTime.DateUtils;
import fileManager.PropertiesFilesReader;
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
public class CourriersFavoris implements Serializable {

    private static final long serialVersionUID = 8515072740410182566L;
    private Courrier courrier;
    private String favoris = "favoris";
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private String datePourRechercheAvancee;
    private String moisPourRechercheAvancee;
    private List<Courrier> courrierTempList = new ArrayList<>();
    private List<Courrier> courrierSauvegardeList = new ArrayList<>();
    private boolean isMoisSelectionne = false;
    private String titreDeLaPage;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
        titreDeLaPage = PropertiesFilesReader.mapTitreDesPages.get("favoris");
    }

    public void recupererLalisteDesCourriersEnFavoris(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersFavoris(CourriersQueries.recupererTousLesCourriersFavorisDUnUtilisateursParSonId(idPersonne));
        courrierSauvegardeList.clear();
        courrierSauvegardeList.addAll(courrier.getListeDesCouriersFavoris());
        Collections.unmodifiableList(courrierSauvegardeList);
    }


    public String voirLesDetailsDuCourrier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("courrierId",idCourrier);
        session.setAttribute("alfrescoId",alfrescoId);
        session.setAttribute("favorisouarchive",favoris);
        return "detailduncourrierfavorisouarchiver.xhtml?faces-redirect=true";
    }

    public List<String> recupererLesMoisDeLAnnee(){

        return Arrays.asList(DateUtils.recupererTousLesMoisDeLAnnee());
    }

    public void faireUneRechercheAvanceeParDate(){
        boolean trouve = false;
        if(datePourRechercheAvancee.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagecourrierpardate",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez renseigner une date"));
        }else{
            courrierTempList.clear();
            for(int a = 0; a < courrier.getListeDesCouriersFavoris().size(); a++){
                if(courrier.getListeDesCouriersFavoris().get(a).getDateDEnregistrement().equals(datePourRechercheAvancee)){
                    courrierTempList.add(courrier.getListeDesCouriersFavoris().get(a));
                    trouve = true;
                }
            }
            if(trouve){
                courrier.getListeDesCouriersFavoris().clear();
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
            for (int a = 0; a <  courrier.getListeDesCouriersFavoris().size(); a++) {
                try {
                    Date date = sdf.parse(courrier.getListeDesCouriersFavoris().get(a).getDateDEnregistrement());
                    if (date.after(DateUtils.premierJourDuMoisAPartirDUneDate) && date.before(DateUtils.dernierJourDuMoisAPartirDUneDate)) {
                        courrierTempList.add(courrier.getListeDesCouriersFavoris().get(a));
                        trouve = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(trouve){
                courrier.getListeDesCouriersFavoris().clear();
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

    public String getTitreDeLaPage() {
        return titreDeLaPage;
    }

    public void setTitreDeLaPage(String titreDeLaPage) {
        this.titreDeLaPage = titreDeLaPage;
    }
}
