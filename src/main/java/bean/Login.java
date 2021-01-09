package bean;

import cookieManager.CookiesUtils;
import database.DataBaseQueries;
import model.User;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

@Named
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = -486667257950195964L;
    private User user;
    private String userSessionUniqueId;

    @PostConstruct
    public void initialisation(){
        user = new User();
        user.setSeSouvenir(false);
    }

    /**Methode de clic sur le bouton valider**/
    public String cliquerSurValider(){
        if(DataBaseQueries.verifierUserLogin(user.getUserlogin(),user.getUserPassword())){
            userSessionUniqueId = UUID.randomUUID().toString();
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("uniqueUserID",userSessionUniqueId);
            session.setAttribute("userName", DataBaseQueries.nomCompletUser);
            session.setAttribute("idUser",DataBaseQueries.idPersonne);
            session.setAttribute("id_etablissement",DataBaseQueries.idEtablissement);
            session.setAttribute("directionUser", DataBaseQueries.directionUser);
            session.setAttribute("fonctionUser", DataBaseQueries.fonctionUser);
            session.setAttribute("profilUser", DataBaseQueries.profilUser);
            session.setAttribute("premiereConnexion", "oui");
            session.setAttribute("idDirectionUser", DataBaseQueries.idDirectionUser);
            if(user.isSeSouvenir()) {
                CookiesUtils.creerUnCookie("cookieIdentifiant",user.getUserlogin());
                CookiesUtils.creerUnCookie("cookieMotDePasse",user.getUserPassword());
                CookiesUtils.creerUnCookie("seSouvenirDeMoi","oui");
            }
            DataBaseQueries.recupererInfosDeSession();
            session.setAttribute("isResponsable", DataBaseQueries.isResponsable);
            session.setAttribute("isSecretaire", DataBaseQueries.isSecretaire);
            return "tableaudebord?faces-redirect=true";
        }else{
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage("messagesLogin", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur!!!",
                    "Identifiant ou mot de passe invalide"));
            return "login?faces-redirect=true";
        }

    }


    public String clicSurVersMotDePasseOublie(){
        return "motdepasseoublie?faces-redirect=true";
    }

    /**Methode de click sur se souvenir**/
    public void cliquerSurSeSouvenir(){

        if(CookiesUtils.recupererUnCookie("cookieIdentifiant") != null){
            user.setUserlogin(CookiesUtils.recupererUnCookie("cookieIdentifiant").getValue());
        }
        if(CookiesUtils.recupererUnCookie("cookieMotDePasse") != null){
            user.setUserPassword(CookiesUtils.recupererUnCookie("cookieMotDePasse").getValue());
        }
        if(CookiesUtils.recupererUnCookie("seSouvenirDeMoi") != null){
            user.setSeSouvenir(true);
        }else{
            user.setSeSouvenir(false);
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
