package model;

import java.util.List;

public class Dossier {

    private String idDossier;
    private String nomDossier;
    private String descriptionDossier;
    private String createurDossier;
    private String dateDeCreation;
    private String heureDeCreation;
    private List<Dossier> dossierList;

    public Dossier() {
    }

    public Dossier(String idDossier, String nomDossier, String createurDossier) {
        this.idDossier = idDossier;
        this.nomDossier = nomDossier;
        this.createurDossier = createurDossier;
    }

    public Dossier(String idDossier, String nomDossier, String createurDossier, String dateDeCreation, String heureDeCreation) {
        this.idDossier = idDossier;
        this.nomDossier = nomDossier;
        this.createurDossier = createurDossier;
        this.dateDeCreation = dateDeCreation;
        this.heureDeCreation = heureDeCreation;
    }


    public String getDescriptionDossier() {
        return descriptionDossier;
    }

    public void setDescriptionDossier(String descriptionDossier) {
        this.descriptionDossier = descriptionDossier;
    }

    public List<Dossier> getDossierList() {
        return dossierList;
    }

    public void setDossierList(List<Dossier> dossierList) {
        this.dossierList = dossierList;
    }

    public String getDateDeCreation() {
        return dateDeCreation;
    }

    public void setDateDeCreation(String dateDeCreation) {
        this.dateDeCreation = dateDeCreation;
    }

    public String getHeureDeCreation() {
        return heureDeCreation;
    }

    public void setHeureDeCreation(String heureDeCreation) {
        this.heureDeCreation = heureDeCreation;
    }

    public String getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    public String getNomDossier() {
        return nomDossier;
    }

    public void setNomDossier(String nomDossier) {
        this.nomDossier = nomDossier;
    }

    public String getCreateurDossier() {
        return createurDossier;
    }

    public void setCreateurDossier(String createurDossier) {
        this.createurDossier = createurDossier;
    }
}
