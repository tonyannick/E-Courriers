package dcsibudget.model;

import java.util.List;

public class Activites {

    private String titreActivites;
    private String heureActivites;
    private String dateActivites;
    private String personneActivites;
    private String idCourrier;
    private String objetCourrier;
    private String etatCourrier;
    private List<Activites> activitesList;

    public Activites(String titreActivites, String heureActivites, String dateActivites, String personneActivites,
                     String idCourrier, String objetCourrier, String etatCourrier) {
        this.titreActivites = titreActivites;
        this.heureActivites = heureActivites;
        this.dateActivites = dateActivites;
        this.personneActivites = personneActivites;
        this.idCourrier = idCourrier;
        this.objetCourrier = objetCourrier;
        this.etatCourrier = etatCourrier;
    }

    public Activites() {
    }

    public List<Activites> getActivitesList() {
        return activitesList;
    }

    public void setActivitesList(List<Activites> activitesList) {
        this.activitesList = activitesList;
    }

    public String getTitreActivites() {
        return titreActivites;
    }

    public void setTitreActivites(String titreActivites) {
        this.titreActivites = titreActivites;
    }

    public String getHeureActivites() {
        return heureActivites;
    }

    public void setHeureActivites(String heureActivites) {
        this.heureActivites = heureActivites;
    }

    public String getDateActivites() {
        return dateActivites;
    }

    public void setDateActivites(String dateActivites) {
        this.dateActivites = dateActivites;
    }

    public String getPersonneActivites() {
        return personneActivites;
    }

    public void setPersonneActivites(String personneActivites) {
        this.personneActivites = personneActivites;
    }

    public String getIdCourrier() {
        return idCourrier;
    }

    public void setIdCourrier(String idCourrier) {
        this.idCourrier = idCourrier;
    }

    public String getObjetCourrier() {
        return objetCourrier;
    }

    public void setObjetCourrier(String objetCourrier) {
        this.objetCourrier = objetCourrier;
    }

    public String getEtatCourrier() {
        return etatCourrier;
    }

    public void setEtatCourrier(String etatCourrier) {
        this.etatCourrier = etatCourrier;
    }
}
