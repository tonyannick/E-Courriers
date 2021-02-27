package dcsibudget.model;

import java.util.List;

public class Emetteur {

    private String typeDEmetteur;
    private String idTypeDEmetteur;
    private String direction;
    private String fonction;
    private String ministere;
    private String idEmetteur;
    private String directionAutreMinistere;
    private String fonctionAutreMinistere;
    private String ministereAutreMinistere;
    private String raisonSocial;
    private String directionEntreprise;
    private String fonctionEntreprise;
    private String telephoneEntreprise;
    private String emailEntreprise;
    private String adresseEntreprise;
    private String nomParticulier;
    private String prenomParticulier;
    private String telephoneParticulier;
    private String emailParticulier;
    private String heureEnvoi;
    private String dateEnvoi;
    private List<Emetteur> listeDetailsDunEmetteur;

    public Emetteur() {
    }

    public Emetteur(String idTypeDEmetteur, String directionAutreMinistere, String fonctionAutreMinistere, String ministereAutreMinistere) {
        this.idTypeDEmetteur = idTypeDEmetteur;
        this.directionAutreMinistere = directionAutreMinistere;
        this.fonctionAutreMinistere = fonctionAutreMinistere;
        this.ministereAutreMinistere = ministereAutreMinistere;
    }

    public List<Emetteur> getListeDetailsDunEmetteur() {
        return listeDetailsDunEmetteur;
    }

    public void setListeDetailsDunEmetteur(List<Emetteur> listeDetailsDunEmetteur) {
        this.listeDetailsDunEmetteur = listeDetailsDunEmetteur;
    }

    public String getNomParticulier() {
        return nomParticulier;
    }

    public void setNomParticulier(String nomParticulier) {
        this.nomParticulier = nomParticulier;
    }

    public String getPrenomParticulier() {
        return prenomParticulier;
    }

    public void setPrenomParticulier(String prenomParticulier) {
        this.prenomParticulier = prenomParticulier;
    }

    public String getTelephoneParticulier() {
        return telephoneParticulier;
    }

    public void setTelephoneParticulier(String telephoneParticulier) {
        this.telephoneParticulier = telephoneParticulier;
    }

    public String getEmailParticulier() {
        return emailParticulier;
    }

    public void setEmailParticulier(String emailParticulier) {
        this.emailParticulier = emailParticulier;
    }

    public String getRaisonSocial() {
        return raisonSocial;
    }

    public void setRaisonSocial(String raisonSocial) {
        this.raisonSocial = raisonSocial;
    }

    public String getDirectionEntreprise() {
        return directionEntreprise;
    }

    public void setDirectionEntreprise(String directionEntreprise) {
        this.directionEntreprise = directionEntreprise;
    }

    public String getFonctionEntreprise() {
        return fonctionEntreprise;
    }

    public void setFonctionEntreprise(String fonctionEntreprise) {
        this.fonctionEntreprise = fonctionEntreprise;
    }

    public String getTelephoneEntreprise() {
        return telephoneEntreprise;
    }

    public void setTelephoneEntreprise(String telephoneEntreprise) {
        this.telephoneEntreprise = telephoneEntreprise;
    }

    public String getEmailEntreprise() {
        return emailEntreprise;
    }

    public void setEmailEntreprise(String emailEntreprise) {
        this.emailEntreprise = emailEntreprise;
    }

    public String getAdresseEntreprise() {
        return adresseEntreprise;
    }

    public void setAdresseEntreprise(String adresseEntreprise) {
        this.adresseEntreprise = adresseEntreprise;
    }

    public String getDirectionAutreMinistere() {
        return directionAutreMinistere;
    }

    public void setDirectionAutreMinistere(String directionAutreMinistere) {
        this.directionAutreMinistere = directionAutreMinistere;
    }

    public String getFonctionAutreMinistere() {
        return fonctionAutreMinistere;
    }

    public void setFonctionAutreMinistere(String fonctionAutreMinistere) {
        this.fonctionAutreMinistere = fonctionAutreMinistere;
    }

    public String getMinistereAutreMinistere() {
        return ministereAutreMinistere;
    }

    public void setMinistereAutreMinistere(String ministereAutreMinistere) {
        this.ministereAutreMinistere = ministereAutreMinistere;
    }

    public Emetteur(String typeDEmetteur, String idTypeDEmetteur) {
        this.typeDEmetteur = typeDEmetteur;
        this.idTypeDEmetteur = idTypeDEmetteur;
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getTypeDEmetteur() {
        return typeDEmetteur;
    }

    public void setTypeDEmetteur(String typeDEmetteur) {
        this.typeDEmetteur = typeDEmetteur;
    }

    public String getIdTypeDEmetteur() {
        return idTypeDEmetteur;
    }

    public void setIdTypeDEmetteur(String idTypeDEmetteur) {
        this.idTypeDEmetteur = idTypeDEmetteur;
    }

    public String getIdEmetteur() {
        return idEmetteur;
    }

    public void setIdEmetteur(String idEmetteur) {
        this.idEmetteur = idEmetteur;
    }

    public String getHeureEnvoi() {
        return heureEnvoi;
    }

    public void setHeureEnvoi(String heureEnvoi) {
        this.heureEnvoi = heureEnvoi;
    }

    public String getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(String dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
