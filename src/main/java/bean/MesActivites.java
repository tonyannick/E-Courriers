package bean;

import databaseManager.ActivitesQueries;
import dateAndTime.DateUtils;
import model.Activites;
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


@SessionScoped
@Named
public class MesActivites implements Serializable {

    private static final long serialVersionUID = -832586958164004116L;
    private Activites activites;
    private Integer firstRow = 0;
    private Integer rowsPerPage = 10;
    private List<Activites> activitesDuJour = new ArrayList<>();

    @PostConstruct
    public void initialisation(){
        activites = new Activites();
    }

    public void recupererLaListeDesActivites(){
        HttpSession session = SessionUtils.getSession();
        String idDirectionUser = (String) session.getAttribute("idDirectionUser");
        activites.setActivitesList(ActivitesQueries.recupererLesActivitesDUneDirectionParSonId(idDirectionUser));
        remplirActivitesDuJour();
    }

    public void remplirActivitesDuJour(){
        String dateDuJour = DateUtils.recupererSimpleDateEnCoursAuFormatFrancais();
        activitesDuJour.clear();
        for(int a = 0; a < activites.getActivitesList().size(); a++){
            if ( activites.getActivitesList().get(a).getDateActivites().equals(dateDuJour)) {
                activitesDuJour.add(activites.getActivitesList().get(a));
            }
        }
        Collections.reverse(activitesDuJour);
        activites.getActivitesList().removeIf(e -> e.getDateActivites().equals(dateDuJour));
    }

    /*public String voirLesDetailsDuCourrier(){

        HttpSession session = SessionUtils.getSession();
        boolean isResponsable = (boolean)session.getAttribute("isResponsable");

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        String etatCourrier = (params.get("etatCourrier"));

        session.setAttribute("courrierId", idCourrier);
        session.setAttribute("alfrescoId", alfrescoId);
        session.setAttribute("etatCourrier", etatCourrier);

        if(isResponsable){
            if(etatCourrier.equals(EtatCourrier.courrierEnvoye)){
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "detailduncourrierenregistre?faces-redirect=true";
            }
        }else{
            if(etatCourrier.equals(EtatCourrier.courrierEnvoye)){
                return "detailduncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "detailduncourrierenregistre?faces-redirect=true";
            }

            if(etatCourrier.equals("Courrier")){
                return "repondreauncourrierrecus.xhtml?faces-redirect=true";
            }else{
                return "repondreaunetache.xhtml?faces-redirect=true";
            }
        }

    }*/


    public List<Activites> getActivitesDuJour() {
        return activitesDuJour;
    }

    public void setActivitesDuJour(List<Activites> activitesDuJour) {
        this.activitesDuJour = activitesDuJour;
    }

    public Activites getActivites() {
        return activites;
    }

    public void setActivites(Activites activites) {
        this.activites = activites;
    }

    public Integer getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(Integer firstRow) {
        this.firstRow = firstRow;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
}