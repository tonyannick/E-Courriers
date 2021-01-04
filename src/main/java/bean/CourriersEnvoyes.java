package bean;

import database.DataBaseQueries;
import model.Courrier;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@SessionScoped
@Named
public class CourriersEnvoyes implements Serializable {

    private static final long serialVersionUID = -8408333902877843429L;
    private Courrier courrier;
    private List<Courrier> courrierList;
    private Integer first = 0;
    private Integer rowsPerPage = 15;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersEnvoyes(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersEnvoyes(DataBaseQueries.recupererTousLesCourriersEnvoyesDUnUtilisateursParSonId(idPersonne));
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

    /***Getter and setter**/

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
}
