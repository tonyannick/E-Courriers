package model;

import java.util.List;

public class Etablissement {

    private String idetablissement;
    private String titreEtablissement;
    private String adresseEtablissement;
    private String abreviationEtablissement;
    private String telEtablissement;
    private String mailEtablissement;
    private List<String> listeEtablissement;
    private List<Etablissement> listeDesEtablissements;

    public Etablissement() {
    }

    public Etablissement(String idetablissement, String titreEtablissement, String abreviationEtablissement, String telEtablissement, String mailEtablissement) {
        this.idetablissement = idetablissement;
        this.titreEtablissement = titreEtablissement;
        this.abreviationEtablissement = abreviationEtablissement;
        this.telEtablissement = telEtablissement;
        this.mailEtablissement = mailEtablissement;
    }

    public String getMailEtablissement() {
        return mailEtablissement;
    }

    public void setMailEtablissement(String mailEtablissement) {
        this.mailEtablissement = mailEtablissement;
    }

    public String getTelEtablissement() {
        return telEtablissement;
    }

    public void setTelEtablissement(String telEtablissement) {
        this.telEtablissement = telEtablissement;
    }

    public String getAbreviationEtablissement() {
        return abreviationEtablissement;
    }

    public void setAbreviationEtablissement(String abreviationEtablissement) {
        this.abreviationEtablissement = abreviationEtablissement;
    }

    public List<Etablissement> getListeDesEtablissements() {
        return listeDesEtablissements;
    }

    public void setListeDesEtablissements(List<Etablissement> listeDesEtablissements) {
        this.listeDesEtablissements = listeDesEtablissements;
    }

    public String getIdetablissement() {
        return idetablissement;
    }

    public void setIdetablissement(String idetablissement) {
        this.idetablissement = idetablissement;
    }

    public String getTitreEtablissement() {
        return titreEtablissement;
    }

    public void setTitreEtablissement(String titreEtablissement) {
        this.titreEtablissement = titreEtablissement;
    }

    public List<String> getListeEtablissement() {
        return listeEtablissement;
    }

    public void setListeEtablissement(List<String> listeEtablissement) {
        this.listeEtablissement = listeEtablissement;
    }

    public String getAdresseEtablissement() {
        return adresseEtablissement;
    }

    public void setAdresseEtablissement(String adresseEtablissement) {
        this.adresseEtablissement = adresseEtablissement;
    }
}
