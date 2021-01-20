package bean;

import cookieManager.CookiesUtils;
import databaseManager.CourriersQueries;
import databaseManager.DataBaseQueries;
import databaseManager.UsersQueries;
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
        if(UsersQueries.verifierUserLogin(user.getUserlogin(),user.getUserPassword())){
            userSessionUniqueId = UUID.randomUUID().toString();
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("uniqueUserID",userSessionUniqueId);
            session.setAttribute("userName", UsersQueries.nomCompletUser);
            session.setAttribute("idUser",UsersQueries.idPersonne);
            session.setAttribute("id_etablissement",UsersQueries.idEtablissement);
            session.setAttribute("directionUser", UsersQueries.directionUser);
            session.setAttribute("fonctionUser", UsersQueries.fonctionUser);
            session.setAttribute("profilUser", UsersQueries.profilUser);
            session.setAttribute("premiereConnexion", "oui");
            session.setAttribute("idDirectionUser", UsersQueries.idDirectionUser);
            if(user.isSeSouvenir()) {
                CookiesUtils.creerUnCookie("cookieIdentifiant",user.getUserlogin());
                CookiesUtils.creerUnCookie("cookieMotDePasse",user.getUserPassword());
                CookiesUtils.creerUnCookie("seSouvenirDeMoi","oui");
            }
            UsersQueries.recupererInfosDeSession();
            session.setAttribute("isResponsable", UsersQueries.isResponsable);
            session.setAttribute("isSecretaire", UsersQueries.isSecretaire);
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

    public String clic(){
        return "sessionexpiree?faces-redirect=true";
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
