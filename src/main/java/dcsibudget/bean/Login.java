package dcsibudget.bean;

import dcsibudget.cookieManager.CookiesUtils;
import dcsibudget.databaseManager.UsersQueries;
import dcsibudget.fileManager.FileManager;
import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import dcsibudget.messages.FacesMessages;
import dcsibudget.model.User;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import dcsibudget.securityManager.Captcha;
import dcsibudget.sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Named
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = -486667257950195964L;
    private User user;
    private StreamedContent imageCaptcha;
    private String valeurCaptcha;
    private String captcha;
    private static Logger loginLogger;

    @PostConstruct
    public void initialisation(){
        user = new User();
        user.setSeSouvenir(false);
        loginLogger = LoggerCreator.creerUnLog("Login");
        checkDesFichiersLogs();
    }


    public void checkDesFichiersLogs(){
        double value = FileManager.calculerLaTailleDUnFichierEnMegaBytes(new File(FileManager.logFilePathOnSystem));
        if(value > 2){
            System.out.println("trop grand");
        }
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
        FileManager.creerDossierECourrier();
        if(verifierCaptcha()){
            if(UsersQueries.verifierUserLogin(user.getUserlogin(),user.getUserPassword())){
                HttpSession session = SessionUtils.getSession();
                session.setAttribute("userName", UsersQueries.mapDetailsUser.get("nom_complet"));
                session.setAttribute("idUser",UsersQueries.mapDetailsUser.get("id_personne"));
                session.setAttribute("id_etablissement",UsersQueries.mapDetailsUser.get("id_etablissement"));
                session.setAttribute("directionUser", UsersQueries.mapDetailsUser.get("nom_direction"));
                session.setAttribute("fonctionUser", UsersQueries.mapDetailsUser.get("titre_fonction"));
                session.setAttribute("profilUser", UsersQueries.mapDetailsUser.get("titre_profil"));
                session.setAttribute("idDirectionUser",UsersQueries.mapDetailsUser.get("id_direction"));

                if(user.isSeSouvenir()) {
                    CookiesUtils.creerUnCookie("cookieIdentifiant",user.getUserlogin());
                    CookiesUtils.creerUnCookie("cookieMotDePasse",user.getUserPassword());
                    CookiesUtils.creerUnCookie("seSouvenirDeMoi","oui");
                }

                UsersQueries.recupererInfosFonctionDuUser();
                session.setAttribute("isResponsable", UsersQueries.isResponsable);
                session.setAttribute("isSecretaire", UsersQueries.isSecretaire);
                chargerLesTitresDesPages();

                LoggerCreator.definirMessageInfo(loginLogger,"Connexion reussie de : "+user.getUserlogin());

                if(UsersQueries.isResponsable || UsersQueries.isSecretaire){
                    return "tableaudebord?faces-redirect=true";
                }else{
                    return "tableaudebordagent?faces-redirect=true";
                }
            }else{
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                FacesMessages.errorMessage("messagesLogin","Erreur!!!","Identifiant ou mot de passe invalide");
                LoggerCreator.definirMessageErreur(loginLogger,"Identifiant "+user.getUserName()+" ou mot de passe invalide "+user.getUserPassword());
                return "login?faces-redirect=true";
            }
        }else{
            LoggerCreator.definirMessageErreur(loginLogger,"Texte du captcha incorrect ");
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessages.errorMessage("messagesLogin","Erreur!!!","Le texte du captcha n'est pas correct");
            return "login?faces-redirect=true";
        }
    }

    private void chargerLesTitresDesPages(){
        PropertiesFilesReader.lireLeFichierDesTitresDesPages("titresDesPages.properties");
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
