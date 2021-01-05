package bean;

import database.DataBaseQueries;
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
import java.util.Map;


@Named
@SessionScoped
public class MesCourriers implements Serializable {

    private static final long serialVersionUID = 1261200135886356695L;
    private Courrier courrier;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
       // recupererLalisteDesCourriersEnregistres();
    }

    public void recupererLalisteDesCourriersEnregistres(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        courrier.setListeDesCouriersEnregistres(DataBaseQueries.recupererTousLesCourriersEnregistresDUnUtilisateursParSonId(idPersonne));
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
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de consulter ce courrier confidentiel', 'warning');");
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

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }
}
