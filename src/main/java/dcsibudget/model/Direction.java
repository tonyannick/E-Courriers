package dcsibudget.model;

import java.util.List;

public class Direction {

    private String idDirection;
    private String titreDirection;
    private String fkEtablissement;
    private List<String> listeDirection;
    private List<Direction> listeObjetsDirection;
    private List<String> listeDirectionEmetteur;
    private List<String> listeDirectionDestinataire;


    public Direction() {
    }

    public Direction(String idDirection, String titreDirection, String fkEtablissement) {
        this.idDirection = idDirection;
        this.titreDirection = titreDirection;
        this.fkEtablissement = fkEtablissement;
    }

    public Direction(String titreDirection) {
        this.titreDirection = titreDirection;
    }

    public List<String> getListeDirection() {
        return listeDirection;
    }

    public void setListeDirection(List<String> listeDirection) {
        this.listeDirection = listeDirection;
    }

    public String getIdDirection() {
        return idDirection;
    }

    public void setIdDirection(String idDirection) {
        this.idDirection = idDirection;
    }

    public String getTitreDirection() {
        return titreDirection;
    }

    public void setTitreDirection(String titreDirection) {
        this.titreDirection = titreDirection;
    }

    public String getFkEtablissement() {
        return fkEtablissement;
    }

    public void setFkEtablissement(String fkEtablissement) {
        this.fkEtablissement = fkEtablissement;
    }

    public List<String> getListeDirectionEmetteur() {
        return listeDirectionEmetteur;
    }

    public void setListeDirectionEmetteur(List<String> listeDirectionEmetteur) {
        this.listeDirectionEmetteur = listeDirectionEmetteur;
    }

    public List<String> getListeDirectionDestinataire() {
        return listeDirectionDestinataire;
    }

    public void setListeDirectionDestinataire(List<String> listeDirectionDestinataire) {
        this.listeDirectionDestinataire = listeDirectionDestinataire;
    }

    public List<Direction> getListeObjetsDirection() {
        return listeObjetsDirection;
    }

    public void setListeObjetsDirection(List<Direction> listeObjetsDirection) {
        this.listeObjetsDirection = listeObjetsDirection;
    }
}
