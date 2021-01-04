package model;

import java.util.List;

public class Etape {

    private String message;
    private String date_debut;
    private String date_fin;
    private String titre;
    private String etape;
    private String reponseTache;
    private String acteur;
    private String destinataire;
    private String fkcourrier;
    private String id;
    private String createurDUneTache;
    private String etat;
    private String nature;
    private String nombreDeTache;
    private String nombreDeTacheDUnAgent;
    private List<Etape> listeHistoriques;
    private List<Etape> listeDesActionsSurLeCourrier;
    private List<Etape> listeDeMesTaches;
    private List<Etape> listeDeMesTachesEnTraitement;

    public Etape() {
    }



    /***Historique et timeline**/
    public Etape(String id_etape,String date_debut, String acteur, String titre, String message) {
        this.id = id_etape;
        this.date_debut = date_debut;
        this.acteur = acteur;
        this.titre = titre;
        this.message = message;
    }



    /***Actions sur le courrier***/
    public Etape(String id_etape, String date_debut, String date_fin, String acteur, String etat, String message) {
        this.id = id_etape;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.acteur = acteur;
        this.etat = etat;
        this.message = message;
    }

    /***Cinq dernieres actions sur le courrier***/
    public Etape(String id_etape, String date_debut, String nature, String message) {
        this.id = id_etape;
        this.date_debut = date_debut;
        this.nature = nature;
        this.message = message;
    }


    /***Actions sur le courrier pour la timeline***/
    public Etape(String id_etape, String date_debut, String date_fin, String acteur, String etat, String titre, String message) {
        this.id = id_etape;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.acteur = acteur;
        this.etat = etat;
        this.titre = titre;
        this.message = message;
    }


    public String getNombreDeTache() {
        return nombreDeTache;
    }

    public void setNombreDeTache(String nombreDeTache) {
        this.nombreDeTache = nombreDeTache;
    }

    public String getNombreDeTacheDUnAgent() {
        return nombreDeTacheDUnAgent;
    }

    public void setNombreDeTacheDUnAgent(String nombreDeTacheDUnAgent) {
        this.nombreDeTacheDUnAgent = nombreDeTacheDUnAgent;
    }

    public String getCreateurDUneTache() {
        return createurDUneTache;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setCreateurDUneTache(String createurDUneTache) {
        this.createurDUneTache = createurDUneTache;
    }

    public List<Etape> getListeDeMesTaches() {
        return listeDeMesTaches;
    }

    public void setListeDeMesTaches(List<Etape> listeDeMesTaches) {
        this.listeDeMesTaches = listeDeMesTaches;
    }

    public String getReponseTache() {
        return reponseTache;
    }

    public void setReponseTache(String reponseTache) {
        this.reponseTache = reponseTache;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public List<Etape> getListeDesActionsSurLeCourrier() {
        return listeDesActionsSurLeCourrier;
    }

    public void setListeDesActionsSurLeCourrier(List<Etape> listeDesActionsSurLeCourrier) {
        this.listeDesActionsSurLeCourrier = listeDesActionsSurLeCourrier;
    }

    public List<Etape> getListeHistoriques() {
        return listeHistoriques;
    }

    public void setListeHistoriques(List<Etape> listeHistoriques) {
        this.listeHistoriques = listeHistoriques;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(String date_debut) {
        this.date_debut = date_debut;
    }

    public String getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(String date_fin) {
        this.date_fin = date_fin;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getEtape() {
        return etape;
    }

    public void setEtape(String etape) {
        this.etape = etape;
    }

    public String getActeur() {
        return acteur;
    }

    public void setActeur(String acteur) {
        this.acteur = acteur;
    }

    public String getFkcourrier() {
        return fkcourrier;
    }

    public void setFkcourrier(String fkcourrier) {
        this.fkcourrier = fkcourrier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public List<Etape> getListeDeMesTachesEnTraitement() {
        return listeDeMesTachesEnTraitement;
    }

    public void setListeDeMesTachesEnTraitement(List<Etape> listeDeMesTachesEnTraitement) {
        this.listeDeMesTachesEnTraitement = listeDeMesTachesEnTraitement;
    }
}