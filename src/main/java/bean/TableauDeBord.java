package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.DataBaseQueries;
import model.Discussion;
import model.Etape;
import model.Statistiques;
import model.User;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<Statistiques> statistiquesList = new ArrayList<>();
    private String typeCourrierUn;
    private String typeCourrierDeux;
    private String typeCourrierTrois;
    private String typeCourrierQuatre;
    private String typeCourrierCinq;
    private String typeCourrierSix;
    private String valeurTypeCourrierUn;
    private String valeurTypeCourrierDeux;
    private String valeurTypeCourrierTrois;
    private String valeurTypeCourrierQuatre;
    private String valeurTypeCourrierCinq;
    private String valeurTypeCourrierSix;
    private String valeurTypeCourrierSept;


    @PostConstruct
    public void initialisation(){
        user = new User();
        statistiques = new Statistiques();
        discussion = new Discussion();
        etape = new Etape();
        recupererInfosDeSessionUser();
        checkIfAlfrescoIsOnline();
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
        DataBaseQueries.recupererLesStatistiquesPourLaPageDAccueil();
        statistiques.setNombreDeCourrierRecusDuJour(String.valueOf(DataBaseQueries.nombreCourrierRecusDuJour));
        statistiques.setNombreDeCourrierEnvoyesDuJour(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDuJour));
        statistiques.setNombreDeCourrierUrgentDuJour(String.valueOf(DataBaseQueries.nombreCourrierUrgentDuJour));
        statistiques.setNombreDeCourrierConfidentielDuJour(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDuJour));
        statistiques.setNombreDeCourrierEnvoyesDuMois(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDuMois));
        statistiques.setNombreDeCourrierRecusDuMois(String.valueOf(DataBaseQueries.nombreCourrierRecusDuMois));
        statistiques.setNombreDeCourrierUrgentDuMois(String.valueOf(DataBaseQueries.nombreCourrierUrgentDuMois));
        statistiques.setNombreDeCourrierPasUrgentDuMois(String.valueOf(DataBaseQueries.nombreCourrierPasUrgentDuMois));
        statistiques.setNombreDeCourrierConfidentielDuMois(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDuMois));

        DataBaseQueries.mapNombreCourrierParType.forEach((key,value) -> statistiquesList.add(new Statistiques(key,String.valueOf(value))));
        discussion.setListeDiscussionsOuvertes(DataBaseQueries.recupererLesDiscusssionsDUnUserEnCours(idUser));
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            etape.setListeDeMesTachesEnTraitement(DataBaseQueries.recupererLesCinqsDernieresTachesEnCoursDeTraitementCreesParUnUser(idUser));
        }else{
            etape.setListeDeMesTachesEnTraitement(DataBaseQueries.recupererLesCinqDernieresTachesEnTraitementDUnAgent(idUser));
        }

        if (statistiquesList.size() > 0){
           typeCourrierUn = statistiquesList.get(0).getTypeDeCourrier();
           valeurTypeCourrierUn = statistiquesList.get(0).getNombreTypeDeCourrier();
        } if (statistiquesList.size() > 1){
            typeCourrierDeux = statistiquesList.get(1).getTypeDeCourrier();
            valeurTypeCourrierDeux = statistiquesList.get(1).getNombreTypeDeCourrier();
        } if (statistiquesList.size() > 2){
            typeCourrierTrois = statistiquesList.get(2).getTypeDeCourrier();
            valeurTypeCourrierTrois = statistiquesList.get(2).getNombreTypeDeCourrier();
        } if (statistiquesList.size() > 3){
            typeCourrierQuatre = statistiquesList.get(3).getTypeDeCourrier();
            valeurTypeCourrierQuatre = statistiquesList.get(3).getNombreTypeDeCourrier();
        } if (statistiquesList.size() > 4){
            typeCourrierCinq = statistiquesList.get(4).getTypeDeCourrier();
            valeurTypeCourrierCinq = statistiquesList.get(4).getNombreTypeDeCourrier();
        } if (statistiquesList.size() > 5){
            typeCourrierSix = statistiquesList.get(5).getTypeDeCourrier();
            valeurTypeCourrierSix = statistiquesList.get(5).getNombreTypeDeCourrier();
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

    public String getTypeCourrierUn() {
        return typeCourrierUn;
    }

    public void setTypeCourrierUn(String typeCourrierUn) {
        this.typeCourrierUn = typeCourrierUn;
    }

    public String getTypeCourrierDeux() {
        return typeCourrierDeux;
    }

    public void setTypeCourrierDeux(String typeCourrierDeux) {
        this.typeCourrierDeux = typeCourrierDeux;
    }

    public String getTypeCourrierTrois() {
        return typeCourrierTrois;
    }

    public void setTypeCourrierTrois(String typeCourrierTrois) {
        this.typeCourrierTrois = typeCourrierTrois;
    }

    public String getTypeCourrierQuatre() {
        return typeCourrierQuatre;
    }

    public void setTypeCourrierQuatre(String typeCourrierQuatre) {
        this.typeCourrierQuatre = typeCourrierQuatre;
    }

    public String getTypeCourrierCinq() {
        return typeCourrierCinq;
    }

    public void setTypeCourrierCinq(String typeCourrierCinq) {
        this.typeCourrierCinq = typeCourrierCinq;
    }

    public String getTypeCourrierSix() {
        return typeCourrierSix;
    }

    public void setTypeCourrierSix(String typeCourrierSix) {
        this.typeCourrierSix = typeCourrierSix;
    }

    public String getValeurTypeCourrierUn() {
        return valeurTypeCourrierUn;
    }

    public void setValeurTypeCourrierUn(String valeurTypeCourrierUn) {
        this.valeurTypeCourrierUn = valeurTypeCourrierUn;
    }

    public String getValeurTypeCourrierDeux() {
        return valeurTypeCourrierDeux;
    }

    public void setValeurTypeCourrierDeux(String valeurTypeCourrierDeux) {
        this.valeurTypeCourrierDeux = valeurTypeCourrierDeux;
    }

    public String getValeurTypeCourrierTrois() {
        return valeurTypeCourrierTrois;
    }

    public void setValeurTypeCourrierTrois(String valeurTypeCourrierTrois) {
        this.valeurTypeCourrierTrois = valeurTypeCourrierTrois;
    }

    public String getValeurTypeCourrierQuatre() {
        return valeurTypeCourrierQuatre;
    }

    public void setValeurTypeCourrierQuatre(String valeurTypeCourrierQuatre) {
        this.valeurTypeCourrierQuatre = valeurTypeCourrierQuatre;
    }

    public String getValeurTypeCourrierCinq() {
        return valeurTypeCourrierCinq;
    }

    public void setValeurTypeCourrierCinq(String valeurTypeCourrierCinq) {
        this.valeurTypeCourrierCinq = valeurTypeCourrierCinq;
    }

    public String getValeurTypeCourrierSix() {
        return valeurTypeCourrierSix;
    }

    public void setValeurTypeCourrierSix(String valeurTypeCourrierSix) {
        this.valeurTypeCourrierSix = valeurTypeCourrierSix;
    }

    public String getValeurTypeCourrierSept() {
        return valeurTypeCourrierSept;
    }

    public void setValeurTypeCourrierSept(String valeurTypeCourrierSept) {
        this.valeurTypeCourrierSept = valeurTypeCourrierSept;
    }
}

