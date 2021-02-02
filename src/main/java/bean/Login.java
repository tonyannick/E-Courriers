package bean;

import cookieManager.CookiesUtils;
import databaseManager.UsersQueries;
import messages.FacesMessages;
import model.User;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import securityManager.Captcha;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Named
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = -486667257950195964L;
    private User user;
    private String userSessionUniqueId;
    private StreamedContent imageCaptcha;
    private String valeurCaptcha;
    private String captcha;

    @PostConstruct
    public void initialisation(){
        user = new User();
        user.setSeSouvenir(false);
    }

    public void actualiserImageCaptcha(){

        captcha = Captcha.captchaGenerator(9);
        imageCaptcha = DefaultStreamedContent.builder().contentType("image/png").stream(() -> {

            int width = 140, height = 30;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.OPAQUE);
            Graphics graphics = bufferedImage.createGraphics();
            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            graphics.setColor(new Color(130, 130, 130));
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(new Color(255, 255, 255));
            graphics.drawString(captcha, 20, 25);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "png", os);
                return new ByteArrayInputStream(os.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).build();

    }

    /**Methode de clic sur le bouton valider**/
    public String cliquerSurValider(){

        if(verifierCaptcha()){
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
                FacesMessages.errorMessage("messagesLogin","Erreur!!!","Identifiant ou mot de passe invalide");
                return "login?faces-redirect=true";
            }
        }else{
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessages.errorMessage("messagesLogin","Erreur!!!","Le texte du captcha n'est pas correct");
            return "login?faces-redirect=true";
        }
    }

    private boolean verifierCaptcha(){
        boolean isOk = false;
        if(valeurCaptcha.equals(captcha)){
            isOk = true;
        }
        return isOk;
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

    public StreamedContent getImageCaptcha() {
        return imageCaptcha;
    }

    public void setImageCaptcha(StreamedContent imageCaptcha) {
        this.imageCaptcha = imageCaptcha;
    }

    public String getValeurCaptcha() {
        return valeurCaptcha;
    }

    public void setValeurCaptcha(String valeurCaptcha) {
        this.valeurCaptcha = valeurCaptcha;
    }
}
