package model;

import java.util.List;

public class Annotation {

    private String idNote;
    private String auteur;
    private String messageNote;
    private String dateNote;
    private String heureNote;
    private String fkCourrier;
    private List<Annotation> listeDesAnnotations;

    public Annotation() {
    }

    public Annotation(String idNote, String messageNote, String dateNote, String heureNote, String fkCourrier,String auteur) {
        this.idNote = idNote;
        this.messageNote = messageNote;
        this.dateNote = dateNote;
        this.heureNote = heureNote;
        this.fkCourrier = fkCourrier;
        this.auteur = auteur;
    }


    public List<Annotation> getListeDesAnnotations() {
        return listeDesAnnotations;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setListeDesAnnotations(List<Annotation> listeDesAnnotations) {
        this.listeDesAnnotations = listeDesAnnotations;
    }

    public String getIdNote() {
        return idNote;
    }

    public void setIdNote(String idNote) {
        this.idNote = idNote;
    }

    public String getMessageNote() {
        return messageNote;
    }

    public void setMessageNote(String messageNote) {
        this.messageNote = messageNote;
    }

    public String getDateNote() {
        return dateNote;
    }

    public void setDateNote(String dateNote) {
        this.dateNote = dateNote;
    }

    public String getHeureNote() {
        return heureNote;
    }

    public void setHeureNote(String heureNote) {
        this.heureNote = heureNote;
    }

    public String getFkCourrier() {
        return fkCourrier;
    }

    public void setFkCourrier(String fkCourrier) {
        this.fkCourrier = fkCourrier;
    }
}
