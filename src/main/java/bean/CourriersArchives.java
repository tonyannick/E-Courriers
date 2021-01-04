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
import java.util.Map;

@SessionScoped
@Named
public class CourriersArchives implements Serializable {

    private static final long serialVersionUID = -1253920046630977484L;
    private Courrier courrier;
    private String archive = "archive";
    private Integer first = 0;
    private Integer rowsPerPage = 15;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersArchives(){
        HttpSession session = SessionUtils.getSession();
        String sessionID = session.getAttribute("uniqueUserID").toString();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersArchives(DataBaseQueries.recupererTousLesCourriersArchivesDUnUtilisateursParSonId(idPersonne));
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

    /***Getter and setter**/

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
