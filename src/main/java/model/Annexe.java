package model;

import org.primefaces.model.StreamedContent;

import java.util.List;


public class Annexe {


    private String idAnnexe;
    private String nomAnnexe;
    private String typeDeFichierAnnexe;
    private String cheminAnnexeSurPC;
    private String fkCourrier;
    private String idAlfresco;
    private String nombreDAnnexe;
    private StreamedContent streamAnnexeUn;
    private StreamedContent streamAnnexeDeux;
    private StreamedContent streamAnnexeTrois;
    private StreamedContent streamAnnexeQuatre;

    private List<String> listeDesAnnexes;

    public Annexe(String cheminAnnexe, String nomAnnexe, String typeDeFichierAnnexe) {
        this.nomAnnexe = nomAnnexe;
        this.cheminAnnexeSurPC = cheminAnnexe;
        this.typeDeFichierAnnexe = typeDeFichierAnnexe;
    }

    public Annexe() {
    }


    public StreamedContent getStreamAnnexeUn() {
        return streamAnnexeUn;
    }

    public void setStreamAnnexeUn(StreamedContent streamAnnexeUn) {
        this.streamAnnexeUn = streamAnnexeUn;
    }

    public StreamedContent getStreamAnnexeDeux() {
        return streamAnnexeDeux;
    }

    public void setStreamAnnexeDeux(StreamedContent streamAnnexeDeux) {
        this.streamAnnexeDeux = streamAnnexeDeux;
    }

    public StreamedContent getStreamAnnexeTrois() {
        return streamAnnexeTrois;
    }

    public void setStreamAnnexeTrois(StreamedContent streamAnnexeTrois) {
        this.streamAnnexeTrois = streamAnnexeTrois;
    }

    public StreamedContent getStreamAnnexeQuatre() {
        return streamAnnexeQuatre;
    }

    public void setStreamAnnexeQuatre(StreamedContent streamAnnexeQuatre) {
        this.streamAnnexeQuatre = streamAnnexeQuatre;
    }

    public String getNombreDAnnexe() {
        return nombreDAnnexe;
    }

    public void setNombreDAnnexe(String nombreDAnnexe) {
        this.nombreDAnnexe = nombreDAnnexe;
    }

    public String getIdAnnexe() {
        return idAnnexe;
    }

    public void setIdAnnexe(String idAnnexe) {
        this.idAnnexe = idAnnexe;
    }

    public String getNomAnnexe() {
        return nomAnnexe;
    }

    public void setNomAnnexe(String nomAnnexe) {
        this.nomAnnexe = nomAnnexe;
    }

    public String getTypeDeFichierAnnexe() {
        return typeDeFichierAnnexe;
    }

    public void setTypeDeFichierAnnexe(String typeDeFichierAnnexe) {
        this.typeDeFichierAnnexe = typeDeFichierAnnexe;
    }

    public String getCheminAnnexeSurPC() {
        return cheminAnnexeSurPC;
    }

    public void setCheminAnnexeSurPC(String cheminAnnexeSurPC) {
        this.cheminAnnexeSurPC = cheminAnnexeSurPC;
    }

    public String getFkCourrier() {
        return fkCourrier;
    }

    public void setFkCourrier(String fkCourrier) {
        this.fkCourrier = fkCourrier;
    }

    public String getIdAlfresco() {
        return idAlfresco;
    }

    public void setIdAlfresco(String idAlfresco) {
        this.idAlfresco = idAlfresco;
    }

    public List<String> getListeDesAnnexes() {
        return listeDesAnnexes;
    }

    public void setListeDesAnnexes(List<String> listeDesAnnexes) {
        this.listeDesAnnexes = listeDesAnnexes;
    }
}
