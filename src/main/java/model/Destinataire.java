package model;

import java.util.List;

public class Destinataire {

    private String idDestinataire;
    private String idRecevoirCourrier;
    private String typeDestinataire;
    private String idTypeDestinataire;
    private String direction;
    private String fonction;
    private String ministere;
    private String dateReception;
    private String heureReception;
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
    private String nombreDeDestinataire;
    private String accuseDeReception;
    private String etatCourrierTransferer;
    private List<Destinataire> listeDestinataire;
    private List<Destinataire> listeDestinataireParTransfer;


    public Destinataire() {
    }

    public Destinataire ( String idRecevoirCourrier, String typeDestinataire, String ministere, String direction,
                          String fonction, String idDestinataire, String accuseDeReception, String etatCourrierTransferer) {
        this.idRecevoirCourrier = idRecevoirCourrier;
        this.fonction = fonction;
        this.typeDestinataire = typeDestinataire;
        this.ministere = ministere;
        this.direction = direction;
        this.idDestinataire = idDestinataire;
        this.accuseDeReception = accuseDeReception;
        this.etatCourrierTransferer = etatCourrierTransferer;
    }

    public Destinataire ( String idRecevoirCourrier, String typeDestinataire, String ministere, String direction,
                          String fonction, String idDestinataire, String accuseDeReception, String etatCourrierTransferer,
                          String dateReception, String heureReception) {
        this.idRecevoirCourrier = idRecevoirCourrier;
        this.fonction = fonction;
        this.typeDestinataire = typeDestinataire;
        this.ministere = ministere;
        this.direction = direction;
        this.idDestinataire = idDestinataire;
        this.accuseDeReception = accuseDeReception;
        this.etatCourrierTransferer = etatCourrierTransferer;
        this.dateReception = dateReception;
        this.heureReception = heureReception;
    }

    public List<Destinataire> getListeDestinataireParTransfer() {
        return listeDestinataireParTransfer;
    }

    public void setListeDestinataireParTransfer(List<Destinataire> listeDestinataireParTransfer) {
        this.listeDestinataireParTransfer = listeDestinataireParTransfer;
    }

    public String getAccuseDeReception() {
        return accuseDeReception;
    }

    public void setAccuseDeReception(String accuseDeReception) {
        this.accuseDeReception = accuseDeReception;
    }

    public String getNombreDeDestinataire() {
        return nombreDeDestinataire;
    }

    public void setNombreDeDestinataire(String nombreDeDestinataire) {
        this.nombreDeDestinataire = nombreDeDestinataire;
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

    public String getIdRecevoirCourrier() {
        return idRecevoirCourrier;
    }

    public void setIdRecevoirCourrier(String idRecevoirCourrier) {
        this.idRecevoirCourrier = idRecevoirCourrier;
    }

    public String getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(String idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    public List<Destinataire> getListeDestinataire() {
        return listeDestinataire;
    }

    public void setListeDestinataire(List<Destinataire> listeDestinataire) {
        this.listeDestinataire = listeDestinataire;
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

    public String getEtatCourrierTransferer() {
        return etatCourrierTransferer;
    }

    public void setEtatCourrierTransferer(String etatCourrierTransferer) {
        this.etatCourrierTransferer = etatCourrierTransferer;
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

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTypeDestinataire() {
        return typeDestinataire;
    }

    public void setTypeDestinataire(String typeDestinataire) {
        this.typeDestinataire = typeDestinataire;
    }

    public String getIdTypeDestinataire() {
        return idTypeDestinataire;
    }

    public void setIdTypeDestinataire(String idTypeDestinataire) {
        this.idTypeDestinataire = idTypeDestinataire;
    }
}
