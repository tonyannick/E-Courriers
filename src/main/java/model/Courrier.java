package model;

import org.primefaces.model.StreamedContent;

import java.util.List;

public class Courrier {

    private String idCourrier;
    private String nomCourrier;
    private String cheminCourrierSurPC;
    private String heureDEnregistrement;
    private String genreCourrier;
    private String dateDEnregistrement;
    private String objetCourrier;
    private String referenceCourrier;
    private String commentairesCourrier;
    private String motsclesCourrier;
    private String typeCourrier;
    private String prioriteCourrier;
    private String idAlfresco;
    private String confidentiel;
    private String extensionCourrier;
    private String imageCourrier;
    private String idAjouterCourrier;
    private String isPDF;
    private String isFavoris;
    private String dossierAlfresco;
    private String idDossier;
    private String direction;
    private String accuseDeReception;
    private String transferer;
    private String referenceInterne;

    private StreamedContent streamedContentAlfresco;
    private List<String> listeTypeDeCourier;
    private List<Courrier> listeDesCouriersEnregistres;
    private List<Courrier> listeDesCouriersEnvoyes;
    private List<Courrier> listeDesCouriersRecus;
    private List<Courrier> listeDesCouriersFavoris;
    private List<Courrier> listeDesCouriersArchives;
    private List<Courrier> listeDesCourriersDansUnDossier;
    private String tempValeurAModifier;

    public Courrier() {
    }

    public Courrier(String referenceCourrier, String prioriteCourrier, String objetCourrier,
                    String dateDEnregistrement, String idCourrier, String confidentiel,
                    String extensionCourrier, String idAjouterCourrier,String idAlfresco,
                    String heureDEnregistrement) {

        this.referenceCourrier = referenceCourrier;
        this.prioriteCourrier = prioriteCourrier;
        this.objetCourrier = objetCourrier;
        this.dateDEnregistrement= dateDEnregistrement;
        this.idCourrier = idCourrier;
        this.confidentiel = confidentiel;
        this.extensionCourrier = extensionCourrier;
        this.idAjouterCourrier = idAjouterCourrier;
        this.idAlfresco = idAlfresco;
        this.heureDEnregistrement = heureDEnregistrement;

    }


    public Courrier(String referenceCourrier, String prioriteCourrier, String objetCourrier,
                    String dateDEnregistrement, String idCourrier, String confidentiel,
                    String extensionCourrier, String idAjouterCourrier,String idAlfresco,
                    String idDossier,String accuseDeReception, String transferer,String motsclesCourrier,String referenceInterne) {

        this.referenceCourrier = referenceCourrier;
        this.prioriteCourrier = prioriteCourrier;
        this.objetCourrier = objetCourrier;
        this.dateDEnregistrement= dateDEnregistrement;
        this.idCourrier = idCourrier;
        this.confidentiel = confidentiel;
        this.extensionCourrier = extensionCourrier;
        this.idAjouterCourrier = idAjouterCourrier;
        this.idAlfresco = idAlfresco;
        this.idDossier = idDossier;
        this.accuseDeReception = accuseDeReception;
        this.transferer = transferer;
        this.motsclesCourrier = motsclesCourrier;
        this.referenceInterne = referenceCourrier;

    }

    public Courrier(String referenceCourrier, String prioriteCourrier, String objetCourrier,
                    String dateDEnregistrement, String idCourrier, String confidentiel,
                    String extensionCourrier, String idAjouterCourrier,String idAlfresco,
                    String idDossier,String accuseDeReception) {

        this.referenceCourrier = referenceCourrier;
        this.prioriteCourrier = prioriteCourrier;
        this.objetCourrier = objetCourrier;
        this.dateDEnregistrement= dateDEnregistrement;
        this.idCourrier = idCourrier;
        this.confidentiel = confidentiel;
        this.extensionCourrier = extensionCourrier;
        this.idAjouterCourrier = idAjouterCourrier;
        this.idAlfresco = idAlfresco;
        this.idDossier = idDossier;
        this.accuseDeReception = accuseDeReception;

    }

    public Courrier(String referenceCourrier, String prioriteCourrier, String objetCourrier,
                    String dateDEnvoi, String idCourrier, String confidentiel,
                    String extensionCourrier, String idAjouterCourrier,String idAlfresco,
                    String idDossier,String accuseDeReception, String transferer) {

        this.referenceCourrier = referenceCourrier;
        this.prioriteCourrier = prioriteCourrier;
        this.objetCourrier = objetCourrier;
        this.dateDEnregistrement= dateDEnvoi;
        this.idCourrier = idCourrier;
        this.confidentiel = confidentiel;
        this.extensionCourrier = extensionCourrier;
        this.idAjouterCourrier = idAjouterCourrier;
        this.idAlfresco = idAlfresco;
        this.idDossier = idDossier;
        this.accuseDeReception = accuseDeReception;
        this.transferer = transferer;

    }


    public Courrier(String typeDeCourrier) {
        this.typeCourrier = typeDeCourrier;
    }

    public Courrier(String dateDEnregistrement, String typeDeCourrier) {
        this.dateDEnregistrement= dateDEnregistrement;
        this.typeCourrier = typeDeCourrier;
    }

    public Courrier(String dateDEnregistrement, String prioriteCourrier, String confidentiel, String idCourrier) {
        this.dateDEnregistrement= dateDEnregistrement;
        this.prioriteCourrier = prioriteCourrier;
        this.confidentiel = confidentiel;
        this.idCourrier= idCourrier;

    }
    public Courrier(String dateDEnregistrement, String prioriteCourrier, String confidentiel, String idCourrier, String genreCourrier) {
        this.dateDEnregistrement= dateDEnregistrement;
        this.prioriteCourrier = prioriteCourrier;
        this.confidentiel = confidentiel;
        this.idCourrier= idCourrier;
        this.genreCourrier = genreCourrier;

    }

    public Courrier(String referenceCourrier, String prioriteCourrier, String objetCourrier,
                    String dateDEnregistrement, String idCourrier, String genreCourrier,
                    String idAlfresco) {

        this.referenceCourrier = referenceCourrier;
        this.prioriteCourrier = prioriteCourrier;
        this.objetCourrier = objetCourrier;
        this.dateDEnregistrement= dateDEnregistrement;
        this.idCourrier = idCourrier;
        this.genreCourrier = genreCourrier;
        this.idAlfresco = idAlfresco;

    }


    public String getTransferer() {
        return transferer;
    }

    public void setTransferer(String transferer) {
        this.transferer = transferer;
    }

    public List<Courrier> getListeDesCourriersDansUnDossier() {
        return listeDesCourriersDansUnDossier;
    }

    public void setListeDesCourriersDansUnDossier(List<Courrier> listeDesCourriersDansUnDossier) {
        this.listeDesCourriersDansUnDossier = listeDesCourriersDansUnDossier;
    }

    public String getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    public List<Courrier> getListeDesCouriersRecus() {
        return listeDesCouriersRecus;
    }

    public void setListeDesCouriersRecus(List<Courrier> listeDesCouriersRecus) {
        this.listeDesCouriersRecus = listeDesCouriersRecus;
    }

    public String getTempValeurAModifier() {
        return tempValeurAModifier;
    }

    public void setTempValeurAModifier(String tempValeurAModifier) {
        this.tempValeurAModifier = tempValeurAModifier;
    }

    public String getIsFavoris() {
        return isFavoris;
    }

    public void setIsFavoris(String isFavoris) {
        this.isFavoris = isFavoris;
    }

    public String getHeureDEnregistrement() {
        return heureDEnregistrement;
    }

    public void setHeureDEnregistrement(String heureDEnregistrement) {
        this.heureDEnregistrement = heureDEnregistrement;
    }

    public List<Courrier> getListeDesCouriersFavoris() {
        return listeDesCouriersFavoris;
    }

    public void setListeDesCouriersFavoris(List<Courrier> listeDesCouriersFavoris) {
        this.listeDesCouriersFavoris = listeDesCouriersFavoris;
    }

    public String getDossierAlfresco() {
        return dossierAlfresco;
    }

    public void setDossierAlfresco(String dossierAlfresco) {
        this.dossierAlfresco = dossierAlfresco;
    }

    public String getIsPDF() {
        return isPDF;
    }

    public void setIsPDF(String isPDF) {
        this.isPDF = isPDF;
    }

    public StreamedContent getStreamedContentAlfresco() {
        return streamedContentAlfresco;
    }

    public void setStreamedContentAlfresco(StreamedContent streamedContentAlfresco) {
        this.streamedContentAlfresco = streamedContentAlfresco;
    }

    public List<Courrier> getListeDesCouriersEnvoyes() {
        return listeDesCouriersEnvoyes;
    }

    public void setListeDesCouriersEnvoyes(List<Courrier> listeDesCouriersEnvoyes) {
        this.listeDesCouriersEnvoyes = listeDesCouriersEnvoyes;
    }

    public String getImageCourrier() {
        return imageCourrier;
    }

    public void setImageCourrier(String imageCourrier) {
        this.imageCourrier = imageCourrier;
    }

    public String getIdAjouterCourrier() {
        return idAjouterCourrier;
    }

    public void setIdAjouterCourrier(String idAjouterCourrier) {
        this.idAjouterCourrier = idAjouterCourrier;
    }

    public List<Courrier> getListeDesCouriersEnregistres() {
        return listeDesCouriersEnregistres;
    }

    public void setListeDesCouriersEnregistres(List<Courrier> listeDesCouriersEnregistres) {
        this.listeDesCouriersEnregistres = listeDesCouriersEnregistres;
    }

    public String getExtensionCourrier() {
        return extensionCourrier;
    }

    public void setExtensionCourrier(String extensionCourrier) {
        this.extensionCourrier = extensionCourrier;
    }

    public String getConfidentiel() {
        return confidentiel;
    }

    public void setConfidentiel(String confidentiel) {
        this.confidentiel = confidentiel;
    }

    public String getIdAlfresco() {
        return idAlfresco;
    }

    public void setIdAlfresco(String idAlfresco) {
        this.idAlfresco = idAlfresco;
    }

    public String getIdCourrier() {
        return idCourrier;
    }

    public void setIdCourrier(String idCourrier) {
        this.idCourrier = idCourrier;
    }

    public String getNomCourrier() {
        return nomCourrier;
    }

    public void setNomCourrier(String nomCourrier) {
        this.nomCourrier = nomCourrier;
    }

    public String getCheminCourrierSurPC() {
        return cheminCourrierSurPC;
    }

    public void setCheminCourrierSurPC(String cheminCourrierSurPC) {
        this.cheminCourrierSurPC = cheminCourrierSurPC;
    }


    public String getGenreCourrier() {
        return genreCourrier;
    }

    public String getPrioriteCourrier() {
        return prioriteCourrier;
    }

    public void setPrioriteCourrier(String prioriteCourrier) {
        this.prioriteCourrier = prioriteCourrier;
    }

    public String getObjetCourrier() {
        return objetCourrier;
    }

    public void setObjetCourrier(String objetCourrier) {
        this.objetCourrier = objetCourrier;
    }

    public String getReferenceCourrier() {
        return referenceCourrier;
    }

    public void setReferenceCourrier(String referenceCourrier) {
        this.referenceCourrier = referenceCourrier;
    }

    public String getCommentairesCourrier() {
        return commentairesCourrier;
    }

    public void setCommentairesCourrier(String commentairesCourrier) {
        this.commentairesCourrier = commentairesCourrier;
    }

    public String getMotsclesCourrier() {
        return motsclesCourrier;
    }

    public void setMotsclesCourrier(String motsclesCourrier) {
        this.motsclesCourrier = motsclesCourrier;
    }

    public String getTypeCourrier() {
        return typeCourrier;
    }

    public void setTypeCourrier(String typeCourrier) {
        this.typeCourrier = typeCourrier;
    }

    public void setGenreCourrier(String genreCourrier) {
        this.genreCourrier = genreCourrier;
    }

    public String getDateDEnregistrement() {
        return dateDEnregistrement;
    }

    public void setDateDEnregistrement(String dateDEnregistrement) {
        this.dateDEnregistrement = dateDEnregistrement;
    }

    public List<String> getListeTypeDeCourier() {
        return listeTypeDeCourier;
    }

    public void setListeTypeDeCourier(List<String> listeTypeDeCourier) {
        this.listeTypeDeCourier = listeTypeDeCourier;
    }

    public List<Courrier> getListeDesCouriersArchives() {
        return listeDesCouriersArchives;
    }

    public void setListeDesCouriersArchives(List<Courrier> listeDesCouriersArchives) {
        this.listeDesCouriersArchives = listeDesCouriersArchives;
    }

    public String getReferenceInterne() {
        return referenceInterne;
    }

    public void setReferenceInterne(String referenceInterne) {
        this.referenceInterne = referenceInterne;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAccuseDeReception() {
        return accuseDeReception;
    }

    public void setAccuseDeReception(String accuseDeReception) {
        this.accuseDeReception = accuseDeReception;
    }

}
