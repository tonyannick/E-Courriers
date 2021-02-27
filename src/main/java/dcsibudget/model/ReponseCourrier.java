package dcsibudget.model;

import org.primefaces.model.StreamedContent;

import java.util.List;

public class ReponseCourrier {

    private String text;
    private String idReponseCourrier;
    private String createurReponse;
    private String receveurReponse;
    private String extensionFichier;
    private String identifiantAlfresco;
    private String dossierAlfresco;
    private String nomFichier;
    private String cheminFichierSurPC;
    private String date;
    private String heure;
    private String role;
    private List<ReponseCourrier> reponseCourrierList;
    private StreamedContent streamedContentAlfresco;

    public ReponseCourrier(){

    }

    public ReponseCourrier(String text, String idReponseCourrier, String createurReponse, String receveurReponse, String extensionFichier,
                           String identifiantAlfresco, String dossierAlfresco) {
        this.text = text;
        this.idReponseCourrier = idReponseCourrier;
        this.createurReponse = createurReponse;
        this.receveurReponse = receveurReponse;
        this.extensionFichier = extensionFichier;
        this.identifiantAlfresco = identifiantAlfresco;
        this.dossierAlfresco = dossierAlfresco;
    }

    public ReponseCourrier( String text, String createurReponse, String date, String heure,String identifiantAlfresco,String role) {
        this.text = text;
        this.createurReponse = createurReponse;
        this.date = date;
        this.heure = heure;
        this.identifiantAlfresco = identifiantAlfresco;
        this.role = role;
    }


    public List<ReponseCourrier> getReponseCourrierList() {
        return reponseCourrierList;
    }

    public void setReponseCourrierList(List<ReponseCourrier> reponseCourrierList) {
        this.reponseCourrierList = reponseCourrierList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdReponseCourrier() {
        return idReponseCourrier;
    }

    public void setIdReponseCourrier(String idReponseCourrier) {
        this.idReponseCourrier = idReponseCourrier;
    }

    public String getCreateurReponse() {
        return createurReponse;
    }

    public void setCreateurReponse(String createurReponse) {
        this.createurReponse = createurReponse;
    }

    public String getReceveurReponse() {
        return receveurReponse;
    }

    public void setReceveurReponse(String receveurReponse) {
        this.receveurReponse = receveurReponse;
    }

    public String getExtensionFichier() {
        return extensionFichier;
    }

    public void setExtensionFichier(String extensionFichier) {
        this.extensionFichier = extensionFichier;
    }

    public String getIdentifiantAlfresco() {
        return identifiantAlfresco;
    }

    public void setIdentifiantAlfresco(String identifiantAlfresco) {
        this.identifiantAlfresco = identifiantAlfresco;
    }

    public String getDossierAlfresco() {
        return dossierAlfresco;
    }

    public void setDossierAlfresco(String dossierAlfresco) {
        this.dossierAlfresco = dossierAlfresco;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichierSurPC() {
        return cheminFichierSurPC;
    }

    public void setCheminFichierSurPC(String cheminFichierSurPC) {
        this.cheminFichierSurPC = cheminFichierSurPC;
    }

    public StreamedContent getStreamedContentAlfresco() {
        return streamedContentAlfresco;
    }

    public void setStreamedContentAlfresco(StreamedContent streamedContentAlfresco) {
        this.streamedContentAlfresco = streamedContentAlfresco;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
