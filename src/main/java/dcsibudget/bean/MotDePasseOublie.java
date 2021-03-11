package dcsibudget.bean;

import dcsibudget.databaseManager.UsersQueries;
import dcsibudget.dateAndTime.DateUtils;
import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import dcsibudget.mailManager.EmailValidation;
import dcsibudget.mailManager.MailUtils;
import dcsibudget.messages.FacesMessages;
import dcsibudget.securityManager.Captcha;
import dcsibudget.sessionManager.SessionUtils;
import dcsibudget.variables.TypeDePersonne;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SessionScoped
@Named
public class MotDePasseOublie implements Serializable {

    private static final long serialVersionUID = -975644351804199257L;
    private String indicationMessageMailPourMotDePasse;
    private String adresseMail;
    private String valeurSaisieCaptcha;
    private String captcha;
    private String messageValidationReinitialisation;
    private static Logger motDePasseOublieLogger;
    private StreamedContent imageCaptcha;
    private StringBuilder messageDuMail = new StringBuilder();
    private String tokenReinitialisation;

    @PostConstruct
    public void initialisation(){
        motDePasseOublieLogger = LoggerCreator.creerUnLog("MotDePasseOublie");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","renseignementadressemailpourchangermotdepasse");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","emailReinitialisationMotDePasse");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","cliquerSurLeLienPourReinitialiserLeMotDePasse");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","sujetReinitialisationMotDePasse");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","directionCentraleDesSystemesDInformation");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","emailReinitialisationMotDePasse");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","ministereDuBudgetEtDesComptesPublics");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","ceLienExpiraDansUneHeure");
        messageValidationReinitialisation = PropertiesFilesReader.mapMessageApplication.get("emailReinitialisationMotDePasse");
        indicationMessageMailPourMotDePasse = PropertiesFilesReader.mapMessageApplication.get("renseignementadressemailpourchangermotdepasse");
        tokenReinitialisation = UUID.randomUUID().toString()+"-"+UUID.randomUUID().toString();
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

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            } catch (IOException e) {
                LoggerCreator.definirMessageErreur(motDePasseOublieLogger,"Probleme lors de la génération de l'image Captcha dans la page mot de passe oublié : "+e);
                e.printStackTrace();
                return null;
            }
        }).build();

    }

    private Date recupererHeureEnCoursEtAjouterUneHeure(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,1);
        return calendar.getTime();
    }

    public void creerUneSessionTemporairePourReinitialiserLeMotDePasse(){
        HttpSession httpSession = SessionUtils.getSession();
        httpSession.setAttribute("userName", TypeDePersonne.sessionTemporaireReinitiliaserMotDePasse);
        httpSession.setMaxInactiveInterval(60*60);
    }

    public void envoyerMailPourReinitialiserLeMotDePasse(){
        if(verifierCaptcha()){
            if(EmailValidation.voirSiEmailEstValide(adresseMail.trim())){
                if(UsersQueries.verifierSiUnUserExisteParSonEmail(adresseMail.trim()) != null){
                    creerUneSessionTemporairePourReinitialiserLeMotDePasse();
                    UsersQueries.creerUnTokenPourUnUserParSonId(UsersQueries.verifierSiUnUserExisteParSonEmail(adresseMail.trim()),
                            tokenReinitialisation,DateUtils.recupererSimpleDateEnCours(),DateUtils.recupererMiniHeuresEnCours(),
                            DateUtils.convertirHeureEnCoursEnString(recupererHeureEnCoursEtAjouterUneHeure()));
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    String serverInfos = request.getServerName() + ":" + request.getServerPort();
                    messageDuMail.append(PropertiesFilesReader.mapMessageApplication.get("cliquerSurLeLienPourReinitialiserLeMotDePasse"));
                    messageDuMail.append("\n");
                    messageDuMail.append("\n----------------------\n");
                    messageDuMail.append("http://"+serverInfos+"/E_Courriers/reinitialisermotdepasse?token="+tokenReinitialisation);
                    messageDuMail.append("\n----------------------\n");
                    messageDuMail.append("\n");
                    messageDuMail.append("\n");
                    messageDuMail.append(PropertiesFilesReader.mapMessageApplication.get("ceLienExpiraDansUneHeure"));
                    messageDuMail.append("\n");
                    messageDuMail.append(PropertiesFilesReader.mapMessageApplication.get("ministereDuBudgetEtDesComptesPublics"));
                    MailUtils.envoyerUnMailSimple(adresseMail.trim(),PropertiesFilesReader.mapMessageApplication.get("sujetReinitialisationMotDePasse"),messageDuMail.toString());
                    PrimeFaces.current().executeScript("PF('dialogueSuccessReinitialisation').show()");
                    setAdresseMail(null);
                    setValeurSaisieCaptcha(null);
                    LoggerCreator.definirMessageInfo(motDePasseOublieLogger,"Mail de réinitialisation du mot de passe envoyé");
                }else{
                    FacesMessages.warningMessage("messagesMotDePasseOublie","Oups!!!","Nous n'avons trouvé aucun utilisateur avec cette email");
                }
            }else{
                FacesMessages.errorMessage("messagesMotDePasseOublie","Erreur","L'email ne semble pas correcte");
            }
        }else{
           // PrimeFaces.current().executeScript("cacherloadingEnvoyerMail()");
            FacesMessages.errorMessage("messagesMotDePasseOublie","Erreur","Le texte du captcha n'est pas correct");
            LoggerCreator.definirMessageErreur(motDePasseOublieLogger,"Texte du captcha incorrect pour reinitailisation du mot de passe");
        }

    }

    private boolean verifierCaptcha(){
        boolean isOk = false;
        if(valeurSaisieCaptcha.equals(captcha)){
            isOk = true;
        }
        return isOk;
    }

    public String retourALaPageDeConnexion(){
        return "login?faces-redirect=true";
    }

    public String getIndicationMessageMailPourMotDePasse() {
        return indicationMessageMailPourMotDePasse;
    }

    public void setIndicationMessageMailPourMotDePasse(String indicationMessageMailPourMotDePasse) {
        this.indicationMessageMailPourMotDePasse = indicationMessageMailPourMotDePasse;
    }

    public String getAdresseMail() {
        return adresseMail;
    }

    public void setAdresseMail(String adresseMail) {
        this.adresseMail = adresseMail;
    }

    public StreamedContent getImageCaptcha() {
        return imageCaptcha;
    }

    public void setImageCaptcha(StreamedContent imageCaptcha) {
        this.imageCaptcha = imageCaptcha;
    }

    public String getValeurSaisieCaptcha() {
        return valeurSaisieCaptcha;
    }

    public void setValeurSaisieCaptcha(String valeurSaisieCaptcha) {
        this.valeurSaisieCaptcha = valeurSaisieCaptcha;
    }

    public String getMessageValidationReinitialisation() {
        return messageValidationReinitialisation;
    }

    public void setMessageValidationReinitialisation(String messageValidationReinitialisation) {
        this.messageValidationReinitialisation = messageValidationReinitialisation;
    }

}


