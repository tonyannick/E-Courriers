package bean;

import databaseManager.DossiersQueries;
import model.Dossier;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;

@Named
@SessionScoped
public class MesDossiers implements Serializable {

    private static final long serialVersionUID = 5160498496961132417L;
    private Dossier dossier;

    @PostConstruct
    public void initialisation(){
        dossier = new Dossier();
    }

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DossiersQueries.recupererLesDossiersDUnUtilisateur(idUser));
    }

    public void creerUnDossier(){

        if(dossier.getNomDossier().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Attention", "Vous devez renseigner le nom du dossier !!!"));
        }else{
            boolean trouve = false;
            for(int a =0; a < dossier.getDossierList().size(); a++){
                if(dossier.getNomDossier().equalsIgnoreCase(dossier.getDossierList().get(a).getNomDossier())){
                    trouve = true;
                    break;
                }
            }
            if(trouve){
                FacesContext.getCurrentInstance().addMessage("messagedossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Vous avez déja un dossier de ce nom!!!"));
            }else{
                if(!dossier.getDescriptionDossier().isEmpty()){
                    dossier.setDescriptionDossier(dossier.getDescriptionDossier().replaceAll("'"," ").trim());
                }
                HttpSession session = SessionUtils.getSession();
                String idUser = (String) session.getAttribute( "idUser");
                DossiersQueries.creerUnDossier(idUser,dossier.getNomDossier().replaceAll("'"," ").trim(),dossier.getDescriptionDossier());
                dossier.setNomDossier(null);
                dossier.setDescriptionDossier(null);
            }
        }


    }

    public String voirLesCourriersDuDossier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idDossier = (params.get("idDossier"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("idDossier",idDossier);
        return "detaildundossier.xhtml?faces-redirect=true";
    }

    /***getter and setter***/
    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }
}
