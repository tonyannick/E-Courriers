package bean;

import databaseManager.CourriersQueries;
import dateAndTime.DateUtils;
import model.Courrier;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;
import variables.EtatCourrier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class MesCourriers implements Serializable {

    private static final long serialVersionUID = 1261200135886356695L;
    private Courrier courrier;
    private Integer first = 0;
    private Integer rowsPerPage = 5;
    private Integer firstAncienCourrier = 0;
    private Integer rowsPerPageAncienCourrier = 5;
    private List<Courrier> mesCourriersDuJour = new ArrayList<>();

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
       //recupererLalisteDesCourriersEnregistres();
    }

    public void recupererLalisteDesCourriersEnregistres(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersEnregistres(CourriersQueries.recupererTousLesCourriersEnregistresDUnUtilisateursParSonId(idPersonne));
        remplirActivitesDuJour();
    }

    public String voirLesDetailsDunCourrierEnregistre(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int courrierId = Integer.valueOf(params.get("idCourrier"));
        String idCourrier = String.valueOf(courrierId);
        String idAjouterCourrier = String.valueOf(Integer.valueOf(params.get("idAjouterCourrier")));
        String idDocumementAlfresco = String.valueOf(params.get("idAlfresco"));
        String confidentiel = (params.get("confidentiel"));
        HttpSession session = SessionUtils.getSession();
        boolean isResponsable = (boolean)session.getAttribute("isResponsable");

        if(confidentiel.equals(EtatCourrier.confidentiel)) {
            if (!isResponsable) {
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets pas de consulter ce courrier confidentiel', 'warning');");
                return null;
            }else{
                session.setAttribute("idAjouterCourrier",idAjouterCourrier);
                session.setAttribute("idAlfresco",idDocumementAlfresco);
                session.setAttribute("idCourrier",idCourrier);
                return "detailduncourrierenregistre?faces-redirect=true";
            }
        }else{
            session.setAttribute("idAjouterCourrier",idAjouterCourrier);
            session.setAttribute("idAlfresco",idDocumementAlfresco);
            session.setAttribute("idCourrier",idCourrier);
            return "detailduncourrierenregistre?faces-redirect=true";
        }
    }

    public void remplirActivitesDuJour(){
        String dateDuJour = DateUtils.recupererSimpleDateEnCoursAuFormatFrancais();
        mesCourriersDuJour.clear();
        for(int a = 0; a < courrier.getListeDesCouriersEnregistres().size(); a++){
            if ( courrier.getListeDesCouriersEnregistres().get(a).getDateDEnregistrement().equals(dateDuJour)) {
                mesCourriersDuJour.add(courrier.getListeDesCouriersEnregistres().get(a));
            }
        }
       // Collections.reverse(mesCourriersDuJour);
        courrier.getListeDesCouriersEnregistres().removeIf(e -> e.getDateDEnregistrement().equals(dateDuJour));
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public List<Courrier> getMesCourriersDuJour() {
        return mesCourriersDuJour;
    }

    public void setMesCourriersDuJour(List<Courrier> mesCourriersDuJour) {
        this.mesCourriersDuJour = mesCourriersDuJour;
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

    public Integer getFirstAncienCourrier() {
        return firstAncienCourrier;
    }

    public void setFirstAncienCourrier(Integer firstAncienCourrier) {
        this.firstAncienCourrier = firstAncienCourrier;
    }

    public Integer getRowsPerPageAncienCourrier() {
        return rowsPerPageAncienCourrier;
    }

    public void setRowsPerPageAncienCourrier(Integer rowsPerPageAncienCourrier) {
        this.rowsPerPageAncienCourrier = rowsPerPageAncienCourrier;
    }
}
