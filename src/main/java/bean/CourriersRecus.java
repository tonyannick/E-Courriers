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
public class CourriersRecus implements Serializable {

    private static final long serialVersionUID = 4919715941602670728L;
    private Courrier courrier;
    private Integer first = 0;
    private Integer rowsPerPage = 15;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
    }

    public void recupererLalisteDesCourriersRecus(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersRecus(DataBaseQueries.recupererTousLesCourriersReçusParUnUtilisateurParSonId(idPersonne));
    }

    public String voirLesDetailsDuCourrier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        String dossierId = (params.get("dossierId"));
        String etatTransfer = (params.get("etatTransfer"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("courrierId",idCourrier);
        session.setAttribute("alfrescoId",alfrescoId);
        session.setAttribute("dossierId",dossierId);

        if(etatTransfer.equalsIgnoreCase("oui")){
            session.setAttribute("courrierTransferer","courrierTransferer");
            return "detailduncourriertransferer.xhtml?faces-redirect=true";
        }else{
            return "detailduncourrierrecus.xhtml?faces-redirect=true";
        }
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
}
