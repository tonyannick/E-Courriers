package dcsibudget.model;

import java.util.List;

public class Statistiques {

    private String dateReception;
    private String dateDEnregistrement;
    private String heureReception;
    private String nombreDeCourrierRecusDuJour;
    private String nombreDeCourrierEnvoyesDuJour;
    private String nombreDeCourrierRecusDeLaSemaine;
    private String nombreDeCourrierEnvoyesDeLaSemaine;
    private String nombreDeCourrierUrgentDuJour;
    private String nombreDeCourrierConfidentielDuJour;
    private String nombreDeCourrierEnregistresDuJour;
    private String nombreDeCourrierEnvoyesDuMois;
    private String nombreDeCourrierRecusDuMois;
    private String nombreDeCourrierEnregistresDuMois;
    private String nombreDeCourrierUrgentDuMois;
    private String nombreDeCourrierPasUrgentDuMois;
    private String nombreDeCourrierConfidentielDuMois;
    private String nombreDeCourrierConfidentielDeLaSemaine;
    private String nombreDeCourrierUrgentDeLaSemaine;
    private String nomDirection;
    private String debutDeSemaine;
    private String finDeSemaine;
    private String typeDeCourrier;
    private String nombreTypeDeCourrier;
    private String nombreCourrierDirection;
    private List<Statistiques> nombreDeCourrierParDirection;

    public Statistiques() {
    }

    public Statistiques(String typeDeCourrier, String nombreTypeDeCourrier) {
        this.typeDeCourrier = typeDeCourrier;
        this.nombreTypeDeCourrier = nombreTypeDeCourrier;
    }

    public Statistiques(String typeDeCourrier, String nombreTypeDeCourrier,String dateDEnregistrement) {
        this.typeDeCourrier = typeDeCourrier;
        this.nombreTypeDeCourrier = nombreTypeDeCourrier;
        this.dateDEnregistrement = dateDEnregistrement;
    }


    public String getDateDEnregistrement() {
        return dateDEnregistrement;
    }

    public void setDateDEnregistrement(String dateDEnregistrement) {
        this.dateDEnregistrement = dateDEnregistrement;
    }

    public Statistiques(String dateReception) {
        this.dateReception = dateReception;
    }

    public String getTypeDeCourrier() {
        return typeDeCourrier;
    }

    public void setTypeDeCourrier(String typeDeCourrier) {
        this.typeDeCourrier = typeDeCourrier;
    }

    public String getNombreTypeDeCourrier() {
        return nombreTypeDeCourrier;
    }

    public void setNombreTypeDeCourrier(String nombreTypeDeCourrier) {
        this.nombreTypeDeCourrier = nombreTypeDeCourrier;
    }

    public String getNombreDeCourrierRecusDuMois() {
        return nombreDeCourrierRecusDuMois;
    }

    public void setNombreDeCourrierRecusDuMois(String nombreDeCourrierRecusDuMois) {
        this.nombreDeCourrierRecusDuMois = nombreDeCourrierRecusDuMois;
    }

    public String getNombreDeCourrierConfidentielDuJour() {
        return nombreDeCourrierConfidentielDuJour;
    }

    public void setNombreDeCourrierConfidentielDuJour(String nombreDeCourrierConfidentielDuJour) {
        this.nombreDeCourrierConfidentielDuJour = nombreDeCourrierConfidentielDuJour;
    }

    public String getNombreDeCourrierUrgentDuJour() {
        return nombreDeCourrierUrgentDuJour;
    }

    public void setNombreDeCourrierUrgentDuJour(String nombreDeCourrierUrgentDuJour) {
        this.nombreDeCourrierUrgentDuJour = nombreDeCourrierUrgentDuJour;
    }

    public String getNombreDeCourrierRecusDuJour() {
        return nombreDeCourrierRecusDuJour;
    }

    public void setNombreDeCourrierRecusDuJour(String nombreDeCourrierRecusDuJour) {
        this.nombreDeCourrierRecusDuJour = nombreDeCourrierRecusDuJour;
    }

    public String getNombreDeCourrierEnvoyesDuJour() {
        return nombreDeCourrierEnvoyesDuJour;
    }

    public void setNombreDeCourrierEnvoyesDuJour(String nombreDeCourrierEnvoyesDuJour) {
        this.nombreDeCourrierEnvoyesDuJour = nombreDeCourrierEnvoyesDuJour;
    }

    public String getNombreDeCourrierEnregistresDuJour() {
        return nombreDeCourrierEnregistresDuJour;
    }

    public void setNombreDeCourrierEnregistresDuJour(String nombreDeCourrierEnregistresDuJour) {
        this.nombreDeCourrierEnregistresDuJour = nombreDeCourrierEnregistresDuJour;
    }

    public String getNombreDeCourrierEnvoyesDuMois() {
        return nombreDeCourrierEnvoyesDuMois;
    }

    public void setNombreDeCourrierEnvoyesDuMois(String nombreDeCourrierEnvoyesDuMois) {
        this.nombreDeCourrierEnvoyesDuMois = nombreDeCourrierEnvoyesDuMois;
    }

    public String getNombreDeCourrierEnregistresDuMois() {
        return nombreDeCourrierEnregistresDuMois;
    }

    public void setNombreDeCourrierEnregistresDuMois(String nombreDeCourrierEnregistresDuMois) {
        this.nombreDeCourrierEnregistresDuMois = nombreDeCourrierEnregistresDuMois;
    }

    public String getDateReception() {
        return dateReception;
    }

    public void setDateReception(String dateReception) {
        this.dateReception = dateReception;
    }

    public String getHeureReception() {
        return heureReception;
    }

    public void setHeureReception(String heureReception) {
        this.heureReception = heureReception;
    }

    public String getNomDirection() {
        return nomDirection;
    }

    public void setNomDirection(String nomDirection) {
        this.nomDirection = nomDirection;
    }

    public String getNombreCourrierDirection() {
        return nombreCourrierDirection;
    }

    public void setNombreCourrierDirection(String nombreCourrierDirection) {
        this.nombreCourrierDirection = nombreCourrierDirection;
    }

    public List<Statistiques> getNombreDeCourrierParDirection() {
        return nombreDeCourrierParDirection;
    }

    public void setNombreDeCourrierParDirection(List<Statistiques> nombreDeCourrierParDirection) {
        this.nombreDeCourrierParDirection = nombreDeCourrierParDirection;
    }

    public String getNombreDeCourrierUrgentDuMois() {
        return nombreDeCourrierUrgentDuMois;
    }

    public void setNombreDeCourrierUrgentDuMois(String nombreDeCourrierUrgentDuMois) {
        this.nombreDeCourrierUrgentDuMois = nombreDeCourrierUrgentDuMois;
    }

    public String getNombreDeCourrierPasUrgentDuMois() {
        return nombreDeCourrierPasUrgentDuMois;
    }

    public void setNombreDeCourrierPasUrgentDuMois(String nombreDeCourrierPasUrgentDuMois) {
        this.nombreDeCourrierPasUrgentDuMois = nombreDeCourrierPasUrgentDuMois;
    }

    public String getNombreDeCourrierConfidentielDuMois() {
        return nombreDeCourrierConfidentielDuMois;
    }

    public void setNombreDeCourrierConfidentielDuMois(String nombreDeCourrierConfidentielDuMois) {
        this.nombreDeCourrierConfidentielDuMois = nombreDeCourrierConfidentielDuMois;
    }

    public String getDebutDeSemaine() {
        return debutDeSemaine;
    }

    public void setDebutDeSemaine(String debutDeSemaine) {
        this.debutDeSemaine = debutDeSemaine;
    }

    public String getFinDeSemaine() {
        return finDeSemaine;
    }

    public void setFinDeSemaine(String finDeSemaine) {
        this.finDeSemaine = finDeSemaine;
    }

    public String getNombreDeCourrierConfidentielDeLaSemaine() {
        return nombreDeCourrierConfidentielDeLaSemaine;
    }

    public void setNombreDeCourrierConfidentielDeLaSemaine(String nombreDeCourrierConfidentielDeLaSemaine) {
        this.nombreDeCourrierConfidentielDeLaSemaine = nombreDeCourrierConfidentielDeLaSemaine;
    }

    public String getNombreDeCourrierUrgentDeLaSemaine() {
        return nombreDeCourrierUrgentDeLaSemaine;
    }

    public void setNombreDeCourrierUrgentDeLaSemaine(String nombreDeCourrierUrgentDeLaSemaine) {
        this.nombreDeCourrierUrgentDeLaSemaine = nombreDeCourrierUrgentDeLaSemaine;
    }

    public String getNombreDeCourrierRecusDeLaSemaine() {
        return nombreDeCourrierRecusDeLaSemaine;
    }

    public void setNombreDeCourrierRecusDeLaSemaine(String nombreDeCourrierRecusDeLaSemaine) {
        this.nombreDeCourrierRecusDeLaSemaine = nombreDeCourrierRecusDeLaSemaine;
    }

    public String getNombreDeCourrierEnvoyesDeLaSemaine() {
        return nombreDeCourrierEnvoyesDeLaSemaine;
    }

    public void setNombreDeCourrierEnvoyesDeLaSemaine(String nombreDeCourrierEnvoyesDeLaSemaine) {
        this.nombreDeCourrierEnvoyesDeLaSemaine = nombreDeCourrierEnvoyesDeLaSemaine;
    }
}
