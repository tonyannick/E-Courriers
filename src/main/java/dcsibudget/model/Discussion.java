package dcsibudget.model;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.util.List;

public class Discussion {

    private String idDiscussion;
    private String dateDiscussion;
    private String messageDiscussion;
    private String fichierDiscussion;
    private String extensionFichierDiscussion;
    private String idAlfresco;
    private String nomPersonne;
    private String etatDiscussion;
    private String idEtape;
    private String idCourrier;
    private String etatCorrespondanceEtape;
    private StreamedContent streamedContentAlfresco;
    private String cheminFichierDiscussionSurPC;
    private List<Discussion> listeDiscussion;
    private List<Discussion> listeDiscussionsOuvertes;
    private String nomFichierDiscussion;
    private String isFichierDiscussionPDF;

    public Discussion() {
    }

    public Discussion(String idDiscussion,String dateDiscussion, String messageDiscussion, String nomPersonne,String fichierDiscussion) {
        this.idDiscussion = idDiscussion;
        this.dateDiscussion = dateDiscussion;
        this.messageDiscussion = messageDiscussion;
        this.nomPersonne = nomPersonne;
        this.fichierDiscussion = fichierDiscussion;
    }


    public Discussion(String idDiscussion,String dateDiscussion, String messageDiscussion) {
        this.idDiscussion = idDiscussion;
        this.dateDiscussion = dateDiscussion;
        this.messageDiscussion = messageDiscussion;
    }

    public Discussion(String idDiscussion,String dateDiscussion, String messageDiscussion,String etatCorrespondanceEtape) {
        this.idDiscussion = idDiscussion;
        this.dateDiscussion = dateDiscussion;
        this.messageDiscussion = messageDiscussion;
        this.etatCorrespondanceEtape = etatCorrespondanceEtape;
    }


    public String getIsFichierDiscussionPDF() {
        return isFichierDiscussionPDF;
    }

    public void setIsFichierDiscussionPDF(String isFichierDiscussionPDF) {
        this.isFichierDiscussionPDF = isFichierDiscussionPDF;
    }

    public StreamedContent getStreamedContentAlfresco() {

        if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }else{
            return streamedContentAlfresco;
        }
    }

    public void setStreamedContentAlfresco(StreamedContent streamedContentAlfresco) {
        this.streamedContentAlfresco = streamedContentAlfresco;
    }

    public String getNomFichierDiscussion() {
        return nomFichierDiscussion;
    }

    public void setNomFichierDiscussion(String nomFichierDiscussion) {
        this.nomFichierDiscussion = nomFichierDiscussion;
    }

    public String getCheminFichierDiscussionSurPC() {
        return cheminFichierDiscussionSurPC;
    }

    public void setCheminFichierDiscussionSurPC(String cheminFichierDiscussionSurPC) {
        this.cheminFichierDiscussionSurPC = cheminFichierDiscussionSurPC;
    }

    public String getExtensionFichierDiscussion() {
        return extensionFichierDiscussion;
    }

    public void setExtensionFichierDiscussion(String extensionFichierDiscussion) {
        this.extensionFichierDiscussion = extensionFichierDiscussion;
    }

    public String getIdAlfresco() {
        return idAlfresco;
    }

    public void setIdAlfresco(String idAlfresco) {
        this.idAlfresco = idAlfresco;
    }

    public List<Discussion> getListeDiscussion() {
        return listeDiscussion;
    }

    public void setListeDiscussion(List<Discussion> listeDiscussion) {
        this.listeDiscussion = listeDiscussion;
    }

    public String getIdDiscussion() {
        return idDiscussion;
    }

    public void setIdDiscussion(String idDiscussion) {
        this.idDiscussion = idDiscussion;
    }

    public String getDateDiscussion() {
        return dateDiscussion;
    }

    public void setDateDiscussion(String dateDiscussion) {
        this.dateDiscussion = dateDiscussion;
    }

    public String getMessageDiscussion() {
        return messageDiscussion;
    }

    public void setMessageDiscussion(String messageDiscussion) {
        this.messageDiscussion = messageDiscussion;
    }

    public String getFichierDiscussion() {
        return fichierDiscussion;
    }

    public void setFichierDiscussion(String fichierDiscussion) {
        this.fichierDiscussion = fichierDiscussion;
    }

    public String getNomPersonne() {
        return nomPersonne;
    }

    public void setNomPersonne(String nomPersonne) {
        this.nomPersonne = nomPersonne;
    }

    public String getEtatDiscussion() {
        return etatDiscussion;
    }

    public void setEtatDiscussion(String etatDiscussion) {
        this.etatDiscussion = etatDiscussion;
    }

    public String getIdEtape() {
        return idEtape;
    }

    public void setIdEtape(String idEtape) {
        this.idEtape = idEtape;
    }

    public String getIdCourrier() {
        return idCourrier;
    }

    public void setIdCourrier(String idCourrier) {
        this.idCourrier = idCourrier;
    }

    public List<Discussion> getListeDiscussionsOuvertes() {
        return listeDiscussionsOuvertes;
    }

    public void setListeDiscussionsOuvertes(List<Discussion> listeDiscussionsOuvertes) {
        this.listeDiscussionsOuvertes = listeDiscussionsOuvertes;
    }

    public String getEtatCorrespondanceEtape() {
        return etatCorrespondanceEtape;
    }

    public void setEtatCorrespondanceEtape(String etatCorrespondanceEtape) {
        this.etatCorrespondanceEtape = etatCorrespondanceEtape;
    }
}
