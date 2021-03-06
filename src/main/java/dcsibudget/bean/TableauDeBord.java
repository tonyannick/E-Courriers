package dcsibudget.bean;

import dcsibudget.alfresco.ConnexionAlfresco;
import dcsibudget.databaseManager.DataBaseQueries;
import dcsibudget.databaseManager.StatistiquesQueries;
import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import dcsibudget.model.Discussion;
import dcsibudget.model.Etape;
import dcsibudget.model.Statistiques;
import dcsibudget.model.User;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import dcsibudget.sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;

@Named
@SessionScoped
public class TableauDeBord implements Serializable {

    private static final long serialVersionUID = -1156993060771162608L;
    private User user;
    private Statistiques statistiques;
    private Discussion discussion;
    private Etape etape;
    private boolean isResponsable = false;
    private int courrierConfidentiel;
    private int courrierPasConfidentiel;
    private String titreDeLaPage;
    private static Logger TableauDeBordLogger;

    @PostConstruct
    public void initialisation(){
        user = new User();
        statistiques = new Statistiques();
        discussion = new Discussion();
        etape = new Etape();
        recupererInfosDeSessionUser();
        checkIfAlfrescoIsOnline();
        titreDeLaPage = PropertiesFilesReader.mapTitreDesPages.get("tableauDeBord");
        TableauDeBordLogger = LoggerCreator.creerUnLog("TableauDeBord");
    }

    /***Recuperer les infos de session du user connecté***/
    public void recupererInfosDeSessionUser(){
        HttpSession httpSession = SessionUtils.getSession();
        user.setUserDirection((String)httpSession.getAttribute("directionUser"));

    }

    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("PF('dialogueAlfrescoMessage').show()");
        }
    }

    /**Déconnexion de l'application**/
    public String deconnexion() {
        HttpSession session = SessionUtils.getSession();
        session.removeAttribute("fonctionUser");
        session.removeAttribute("directionUser");
        session.removeAttribute("profilUser");
        session.removeAttribute("isResponsable");
        session.removeAttribute("isSecretaire");
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
        LoggerCreator.definirMessageInfo(TableauDeBordLogger,"Déconnexion reussie de : "+user.getUserlogin());
        return "login?faces-redirect=true";
    }

    public String voirLesCourriersEnregistres(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        String fonction = session.getAttribute("fonctionUser").toString();
        if(isResponsable){
            return "mescourriers?faces-redirect=true";
        }else{
            if(fonction.equalsIgnoreCase("Secrétaire") || fonction.equalsIgnoreCase("Secrétaire Particuliere")){
                return "mescourriers?faces-redirect=true";
            }else{
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
                return null;
            }

        }
    }

    public String afficherPagePourAjouterUnNouveauCourrier(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        String fonction = session.getAttribute("fonctionUser").toString();
        if(isResponsable){
            return "nouveaucourrier?faces-redirect=true";
        }else{
            if(fonction.equalsIgnoreCase("Secrétaire") || fonction.equalsIgnoreCase("Secrétaire Particuliere")){
                return "nouveaucourrier?faces-redirect=true";
            }else{
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
                return null;
            }

        }
    }

    public String naviguerVersUneListeDesCourriers(){
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        String fonction = session.getAttribute("fonctionUser").toString();
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String url = params.get("url");
        if(isResponsable){
            return url+"?faces-redirect=true";
        }else{
            if(fonction.equalsIgnoreCase("Secrétaire") || fonction.equalsIgnoreCase("Secrétaire Particuliere")){
                return url+"?faces-redirect=true";
            }else{
                PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
                return null;
            }

        }
    }

    /**Acceder aux parametre**/
    public String accederAuxParametreDeLapplication() {
        HttpSession httpSession = SessionUtils.getSession();
        String profilUtilisateur = (String) httpSession.getAttribute("profilUser");
        boolean avoirAccesAuxParametres = false;
        switch (profilUtilisateur) {

            case "Super Administrateur" :
                avoirAccesAuxParametres = true;
                break;
            case "Administrateur" :
                avoirAccesAuxParametres = true;
                break;
            case "Technicien" :
                avoirAccesAuxParametres = true;
                break;
            case "Utilisateur" :
                avoirAccesAuxParametres = true;
                break;
            case "Observateur" :
                avoirAccesAuxParametres = false;
                break;
            case "Super Utilisateur" :
                avoirAccesAuxParametres = true;
                break;
            default:
                avoirAccesAuxParametres = false;
                break;
        }

        if(avoirAccesAuxParametres){
            return "parametres?faces-redirect=true";
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets pas d'acceder aux parametres', 'warning');");
            return null;
        }
    }

    public String voirUneTache(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int etapeId = Integer.valueOf(params.get("etapeId"));
        String natureEtape = params.get("natureEtape");
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("natureEtape",natureEtape);
        return DataBaseQueries.repondreAUneTache(String.valueOf(etapeId));
    }

    public String accederALEtapeDUneDiscussion(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int discussionId = Integer.valueOf(params.get("idDiscussion"));
        String etatCorrespondanceEtape = params.get("etatCorrespondanceEtape");
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("etatCorrespondanceEtape",etatCorrespondanceEtape);
        return DataBaseQueries.allerAUneEtapeAPartirDUneDiscussion(String.valueOf(discussionId));
    }

    /***Statistiques de la page d'accueil***/
    public void recupererDataPourStatistiques(){
        HttpSession httpSession = SessionUtils.getSession();
        String idUser = (String)httpSession.getAttribute("idUser");
        String idDirection = httpSession.getAttribute("idDirectionUser").toString();
        StatistiquesQueries.recupererLesStatistiquesDuJour();
        StatistiquesQueries.calculerLesStatistiquesDuMoisEnCoursPourUneDirection(idDirection);
        courrierConfidentiel = StatistiquesQueries.nombreCourrierConfidentielDuMois;
        courrierPasConfidentiel = StatistiquesQueries.nombreCourrierPasConfidentielDuMois;

        statistiques.setNombreDeCourrierRecusDuJour(String.valueOf( StatistiquesQueries.nombreCourrierRecusDuJour));
        statistiques.setNombreDeCourrierEnvoyesDuJour(String.valueOf( StatistiquesQueries.nombreCourrierEnvoyesDuJour));
        statistiques.setNombreDeCourrierUrgentDuJour(String.valueOf( StatistiquesQueries.nombreCourrierUrgentDuJour));
        statistiques.setNombreDeCourrierConfidentielDuJour(String.valueOf( StatistiquesQueries.nombreCourrierConfidentielDuJour));

        statistiques.setNombreDeCourrierEnvoyesDuMois(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDuMois));
        statistiques.setNombreDeCourrierRecusDuMois(String.valueOf(StatistiquesQueries.nombreCourrierRecusDuMois));
        statistiques.setNombreDeCourrierUrgentDuMois(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDuMois));
        statistiques.setNombreDeCourrierPasUrgentDuMois(String.valueOf(StatistiquesQueries.nombreCourrierPasUrgentDuMois));
        statistiques.setNombreDeCourrierConfidentielDuMois(String.valueOf(StatistiquesQueries.nombreCourrierConfidentielDuMois));

        discussion.setListeDiscussionsOuvertes(DataBaseQueries.recupererLesDiscusssionsDUnUserEnCours(idUser));
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            etape.setListeDeMesTachesEnTraitement(DataBaseQueries.recupererLesCinqsDernieresTachesEnCoursDeTraitementCreesParUnUser(idUser));
        }else{
            etape.setListeDeMesTachesEnTraitement(DataBaseQueries.recupererLesCinqDernieresTachesEnTraitementDUnAgent(idUser));
        }
    }

    public Etape getEtape() {
        return etape;
    }

    public void setEtape(Etape etape) {
        this.etape = etape;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Statistiques getStatistiques() {
        return statistiques;
    }

    public void setStatistiques(Statistiques statistiques) {
        this.statistiques = statistiques;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public int getCourrierConfidentiel() {
        return courrierConfidentiel;
    }

    public void setCourrierConfidentiel(int courrierConfidentiel) {
        this.courrierConfidentiel = courrierConfidentiel;
    }

    public int getCourrierPasConfidentiel() {
        return courrierPasConfidentiel;
    }

    public void setCourrierPasConfidentiel(int courrierPasConfidentiel) {
        this.courrierPasConfidentiel = courrierPasConfidentiel;
    }

    public String getTitreDeLaPage() {
        return titreDeLaPage;
    }

    public void setTitreDeLaPage(String titreDeLaPage) {
        this.titreDeLaPage = titreDeLaPage;
    }
}

