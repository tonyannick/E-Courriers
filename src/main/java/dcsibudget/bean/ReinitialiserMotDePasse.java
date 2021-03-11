package dcsibudget.bean;


import dcsibudget.databaseManager.UsersQueries;
import dcsibudget.dateAndTime.DateUtils;
import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.functions.paramsUtils;
import dcsibudget.logsManager.LoggerCreator;
import dcsibudget.messages.FacesMessages;
import dcsibudget.model.User;
import dcsibudget.securityManager.Cryptage;
import dcsibudget.sessionManager.SessionUtils;
import dcsibudget.variables.TypeDePersonne;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@SessionScoped
@Named
public class ReinitialiserMotDePasse implements Serializable {

    private static final long serialVersionUID = 985578534752653876L;
    private String motDePasse;
    private String retaperMotDePasse;
    private String valeurTokenURL;
    private String messageTokenExpire;
    private String tokenValid = "non";
    private String messageValidationMiseAJourMotDePasse;
    private static Logger reinitialiserMotDePasse;

    @PostConstruct
    public void initialisation(){
        valeurTokenURL = paramsUtils.recupererUnParametreParSonIdentifiant("token");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","pageReinitialisationtokenExpire");
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","messageValidationMiseAJourMotDePasse");

        messageTokenExpire = PropertiesFilesReader.mapMessageApplication.get("pageReinitialisationtokenExpire");
        messageValidationMiseAJourMotDePasse = PropertiesFilesReader.mapMessageApplication.get("messageValidationMiseAJourMotDePasse");
        reinitialiserMotDePasse = LoggerCreator.creerUnLog("ReinitialiserMotDePasse");
    }

    public void cliquerSurValider(){
        if(motDePasse.isEmpty() || motDePasse == null){
            FacesMessages.errorMessage("reinitialisermotdepasse","Erreur","Vous devez renseigner votre mot de passe");
        }else{
            if(!motDePasse.equals(retaperMotDePasse)){
                FacesMessages.errorMessage("reinitialisermotdepasse","Erreur","Vos deux saisies ne sont pas identiques");
            }else{
                try {
                    Thread.sleep(4 * 1000);
                    String motDePasseCrypter = Cryptage.crypterUnMot(motDePasse.trim());
                    UsersQueries.changerLeMotDePasseDUnUserParSonId(UsersQueries.mapDetailsToken.get("id_personne"),motDePasseCrypter);
                    PrimeFaces.current().executeScript("PF('dialogueSuccessMiseAJour').show()");
                    LoggerCreator.definirMessageInfo(reinitialiserMotDePasse,"Mot de passe de l'utilisateur  (d'id : "+UsersQueries.mapDetailsToken.get("id_personne")+") mise à jour");
                } catch (InterruptedException e) {
                    LoggerCreator.definirMessageInfo(reinitialiserMotDePasse,"Erreur sur le thread de mise à jour : "+e);
                    FacesMessages.errorMessage("reinitialisermotdepasse","Erreur","Une erreur s'est produite");
                    e.printStackTrace();
                }
            }
        }
    }


    public String retourALaPageDeConnexion(){
        return "login?faces-redirect=true";
    }

    public void verifierLaValiditeDuTokenDeLURL(){
        if(UsersQueries.recupererLeTokenDUnUserParLaValeurDuToken(valeurTokenURL).isEmpty()){
            PrimeFaces.current().executeScript("afficherMessageErreurToken()");
        }else{
            if(!DateUtils.recupererSimpleDateEnCours().equals(UsersQueries.mapDetailsToken.get("date_de_creation"))){
                PrimeFaces.current().executeScript("afficherMessageErreurToken()");
            }else{
                if(DateUtils.convertirStringEnHeure(DateUtils.recupererMiniHeuresEnCours()).after(
                        DateUtils.convertirStringEnHeure(UsersQueries.mapDetailsToken.get("heure_d_expiration")))){
                    PrimeFaces.current().executeScript("afficherMessageErreurToken()");
                }
            }
        }
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRetaperMotDePasse() {
        return retaperMotDePasse;
    }

    public void setRetaperMotDePasse(String retaperMotDePasse) {
        this.retaperMotDePasse = retaperMotDePasse;
    }

    public String getValeurTokenURL() {
        return valeurTokenURL;
    }

    public void setValeurTokenURL(String valeurTokenURL) {
        this.valeurTokenURL = valeurTokenURL;
    }

    public String getMessageTokenExpire() {
        return messageTokenExpire;
    }

    public void setMessageTokenExpire(String messageTokenExpire) {
        this.messageTokenExpire = messageTokenExpire;
    }

    public String getTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(String tokenValid) {
        this.tokenValid = tokenValid;
    }

    public String getMessageValidationMiseAJourMotDePasse() {
        return messageValidationMiseAJourMotDePasse;
    }

    public void setMessageValidationMiseAJourMotDePasse(String messageValidationMiseAJourMotDePasse) {
        this.messageValidationMiseAJourMotDePasse = messageValidationMiseAJourMotDePasse;
    }
}
