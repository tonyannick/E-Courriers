package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.CourriersQueries;
import databaseManager.DataBaseQueries;
import databaseManager.DatabasConnection;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import sessionManager.SessionUtils;
import variables.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Named
@SessionScoped
public class NouveauCourrier implements Serializable {

    private static final long serialVersionUID = 7592103724942266914L;
    private Emetteur typeDEmetteur;
    private Direction direction;
    private Fonction fonction;
    private Etablissement etablissement;
    private Courrier courrier;
    private Emetteur emetteur;
    private Destinataire destinataire;
    private Annexe annexe;
    private User user;
    private String finalUniqueIDEmetteur,finalUniqueIDDestinataire;
    private String idUser;
    private List<Annexe> listeAnnexe = new ArrayList<>();
    private List<String> listeIdAlfrescoAnnexe = new ArrayList<>();
    private final String property = "java.io.tmpdir";
    private final String tempDirectoryPath = System.getProperty(property);
    private boolean isAnnexe = false;
    private Date dateTemp = null;
    private String isResponsable = "0";
    private String isResponsableDestinataire = "0";
    private boolean thisIsConfidentiel;
    private String fichierConfidentiel = "Fichier_Confidentiel";
    private List<String> tempListEmetteur;
    private List<String> tempListDestinataire = new ArrayList<>();

    @PostConstruct
    public void initialisation(){

        typeDEmetteur = new Emetteur();
        fonction = new Fonction();
        direction = new Direction();
        etablissement = new Etablissement();
        courrier = new Courrier();
        emetteur = new Emetteur();
        destinataire = new Destinataire();
        annexe = new Annexe();
        user = new User();
        recupererListeDirection();
        recupererListeMinisteres();
        recupererListeTypeDeCourrier();
        courrier.setConfidentiel("Non");
        courrier.setGenreCourrier(GenreDeCourrier.courrierInterne);
        HttpSession httpSession = SessionUtils.getSession();

        user.setUserDirection((httpSession.getAttribute("directionUser").toString()));
        Iterator<String> stringIterator = direction.getListeDirectionEmetteur().iterator();
        while(stringIterator.hasNext()){
           String element = stringIterator.next();
           if(!element.equals(user.getUserDirection())){
                stringIterator.remove();
           }
        }

        direction.getListeDirectionDestinataire().removeIf(e -> e.equals(user.getUserDirection()));
        tempListDestinataire.removeIf(e -> e.equals(user.getUserDirection()));

    }
    public void checkIfAlfrescoIsOnline(){
        if(ConnexionAlfresco.getAlfticket() == null){
            PrimeFaces.current().executeScript("PF('dialogueAlfrescoErreurMessage').show()");
            PrimeFaces.current().executeScript("desactiverLeFormulaire()");
        }
    }


    public void actionAuRechargementDeLaPage(){
        reinitialiserLeFormulaire();
        HttpSession httpSession = SessionUtils.getSession();
        user.setUserFonction(httpSession.getAttribute("fonctionUser").toString());
        if(user.getUserFonction().equals("Secrétaire Général Adjoint")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }else if(user.getUserFonction().equals("Secrétaire Général")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }else if(user.getUserFonction().equals("Ministre Délégué")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }else if(user.getUserFonction().equals("Ministre")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }else if(user.getUserFonction().equals("Directeur Général")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }else if(user.getUserFonction().equals("Directeur de Cabinet")){
            isResponsable = "1";
              isResponsableDestinataire = "1";
        }
    }

    public void rechargerLaPage(){
        PrimeFaces.current().executeScript("rechargerLaPageDuFormulaire()");
    }


    private void genererUniqueIDPourEmetteurEtDestinataire(){
        UUID randomEmetteurID = UUID.randomUUID();
        UUID randomDestinataireID = UUID.randomUUID();
        finalUniqueIDEmetteur = "emetteur" +"_"+randomEmetteurID.toString();
        finalUniqueIDDestinataire = "destinataire" +"_"+randomDestinataireID.toString();
    }

    public void recupererListeDirection(){
        direction.setListeDirection(DataBaseQueries.recupererLaListeDesDirections());
        direction.setListeDirectionEmetteur(DataBaseQueries.recupererLaListeDesDirections());
        direction.setListeDirectionDestinataire(DataBaseQueries.recupererLaListeDesDirections());
        tempListDestinataire.addAll(DataBaseQueries.recupererLaListeDesDirections());
       // tempListEmetteur.addAll(DataBaseQueries.recupererLaListeDesDirections());
    }


    public List<String> recupererListeFonctionsParDirectionDeLEmetteur(){
        fonction.setListeFonction(DataBaseQueries.recupererLaListeDesFonctionsParDirection(emetteur.getDirection()));
        return fonction.getListeFonction();
    }

    public List<String> recupererListeFonctionsParDirectionDuDestinataire(){
        fonction.setListeFonction(DataBaseQueries.recupererLaListeDesFonctionsParDirection(destinataire.getDirection()));
        return fonction.getListeFonction();
    }

    public void recupererListeMinisteres(){
        etablissement.setListeEtablissement(DataBaseQueries.recupererLaListeDesMinisteres());
    }

    public void recupererListeTypeDeCourrier(){
        courrier.setListeTypeDeCourier(CourriersQueries.recupererLaListeDeTypesDeCourrier());
    }

    public void ajoutDuFichierDuCourrier(FileUploadEvent fileUploadEvent){
        byte[] bytes = null;
        courrier.setNomCourrier(DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName());
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        courrier.setCheminCourrierSurPC(tempDirectoryPath+"courrier_"+courrier.getNomCourrier());
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(courrier.getCheminCourrierSurPC())));
            stream.write(bytes);
            stream.flush();
            stream.close();
            PrimeFaces.current().executeScript("validerAjouterFichier()");
            FacesContext.getCurrentInstance().addMessage("messageFichierCourrier",new FacesMessage(FacesMessage.SEVERITY_INFO,"Info","Le fichier du courrier à bien été ajouté"));
        } catch (IOException e) {
            PrimeFaces.current().executeScript("swal('Erreur!', 'Une erreur s'est produite lors de l'ajout du fichier', 'error')");
            e.printStackTrace();
        }finally {
            try {
                if(stream != null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ajoutDesFichierAnnexes(FileUploadEvent event) {

        byte[] bytes=null;
        if(null!= event.getFile()){
            annexe.setNomAnnexe(DateUtils.recupererSimpleHeuresEnCours()+"_"+event.getFile().getFileName());
            annexe.setCheminAnnexeSurPC(tempDirectoryPath+"annexe_"+annexe.getNomAnnexe());
            bytes = event.getFile().getContent();
            BufferedOutputStream stream = null;
            try {
                stream = new BufferedOutputStream(new FileOutputStream(new File(annexe.getCheminAnnexeSurPC())));
                stream.write(bytes);
                stream.flush();
                stream.close();
                listeAnnexe.add(new Annexe(annexe.getCheminAnnexeSurPC(), annexe.getNomAnnexe(), FileManager.recupererExtensionDUnFichierParSonNom(annexe.getNomAnnexe())));
                isAnnexe = true;
                FacesContext.getCurrentInstance().addMessage("messagesAnnexes", new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation !!!", "Le/Les fichier(s) annexe(s) a/ont bien été(s) ajouté(s) !!!"));
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage("messagesAnnexes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur!!!", "Une erreur s'est produite lors de l'ajour des fichiers annexes, veuillez réessayer !!!"));
                e.printStackTrace();
            }finally {
                try {
                    if(stream != null){
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public void voirSiLeCourrierEstConfidentiel(){
        if(courrier.getConfidentiel().equalsIgnoreCase("Oui")){
            FacesContext.getCurrentInstance().addMessage("messageconfidentiel", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Le courrier à été marqué comme confidentiel"));
        }else if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
            FacesContext.getCurrentInstance().addMessage("messageconfidentiel", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Le courrier à été marqué comme non confidentiel"));
        }
    }

    public void voirSiLeCourrierEstExterne(){
        if(emetteur.getTypeDEmetteur().equalsIgnoreCase("Entreprise")){
            courrier.setGenreCourrier(GenreDeCourrier.courrierExterne);
            direction.setListeDirectionDestinataire(direction.getListeDirectionEmetteur());
            afficherIndicationGenreCourrierAuClickSurLeTypeDEmetteur();
        }else if(emetteur.getTypeDEmetteur().equalsIgnoreCase("Association")){
            courrier.setGenreCourrier(GenreDeCourrier.courrierExterne);
            afficherIndicationGenreCourrierAuClickSurLeTypeDEmetteur();
        }else if(emetteur.getTypeDEmetteur().equalsIgnoreCase("Particulier")){
            courrier.setGenreCourrier(GenreDeCourrier.courrierExterne);
            afficherIndicationGenreCourrierAuClickSurLeTypeDEmetteur();
        }else if(emetteur.getTypeDEmetteur().equalsIgnoreCase("Agent Autre Ministere")){
            courrier.setGenreCourrier(GenreDeCourrier.courrierExterne);
            direction.setListeDirectionDestinataire(direction.getListeDirectionEmetteur());
            afficherIndicationGenreCourrierAuClickSurLeTypeDEmetteur();
        }else if(emetteur.getTypeDEmetteur().equalsIgnoreCase("Agent du Ministere")){
            courrier.setGenreCourrier(GenreDeCourrier.courrierInterne);
            retirerIndicationGenreCourrierAuClickSurLeTypeDEmetteur();
            direction.setListeDirectionDestinataire(tempListDestinataire);
        }
    }

    public void afficherIndicationGenreCourrierAuClickSurLeTypeDEmetteur(){
        PrimeFaces.current().executeScript("afficherMessageEnFonctionDuTypeDEmetteur()");
    }
    public void retirerIndicationGenreCourrierAuClickSurLeTypeDEmetteur(){
        PrimeFaces.current().executeScript("retirerMessageEnFonctionDuTypeDEmetteur()");
    }

    public void validerLeFormulaire(){
        HttpSession session = SessionUtils.getSession();
        idUser = (String) session.getAttribute("idUser");

        String courrierMinistre = "courrier_ministre";
        String idTypeDeCourrier = null;
        String idTypeDeDestinataire = null;
        String idTypeDetablissement = null;
        String idTypeDEmetteur = null;
        String idFonctionEmetteur = null;
        String idFonctionDestinataire = null;
        String idFonctionDestinataireAutreMinistere = null;
        String idDirectionEmetteur = null;
        String idDirectionDestinataire = null;
        String idEtablissementEmetteur = null;
        String idEtablissementDestinataire = null;
        String idDirectionDestinataireAutreMinistere = null;
        String idEtablissementDestinataireAutreMinistere = null;
        String idEtablissementEmetteurAutreMinistere = null;
        String idEtablissementDestinataireEntreprise = null;
        String updateCourrierAlfrescoSQL = null;
        String ajouterAnnexeAlfrescoSQL = null;

        if(dateTemp!= null && courrier.getHeureDeReception() != null){
            genererUniqueIDPourEmetteurEtDestinataire();
            courrier.setDateDeReception( DateUtils.convertirDateEnString(dateTemp));
            String envoyerCourrierSQL = null;
            String recevoirCourrierSQL = null;
            String ajouterEmetteurSQL=null;
            String ajouterDestinataireSQL=null;
            String ajouterDirectionSQL = null;
            String ajouterFonctionSQL = null;
            String ajouterEtablissementSQL = null;

            idTypeDeCourrier = CourriersQueries.recupererIdTypeDeCourrierParTitre(courrier.getTypeCourrier().replaceAll("'"," "));
            idTypeDEmetteur = DataBaseQueries.recupererIdTypeDePersonneParTitre(emetteur.getTypeDEmetteur());
            idTypeDeDestinataire = DataBaseQueries.recupererIdTypeDePersonneParTitre(destinataire.getTypeDestinataire());
            idDirectionEmetteur = DataBaseQueries.recupererIdDirectionParSonNom(emetteur.getDirection());
            idDirectionDestinataire = DataBaseQueries.recupererIdDirectionParSonNom(destinataire.getDirection());
            idEtablissementDestinataireAutreMinistere = DataBaseQueries.recupererIdEtablissementParSonAbreviation(destinataire.getMinistere());
            idEtablissementEmetteur = DataBaseQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);
            idEtablissementEmetteurAutreMinistere = DataBaseQueries.recupererIdEtablissementParSonAbreviation(emetteur.getMinistereAutreMinistere());
            idEtablissementDestinataire = DataBaseQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);
            idTypeDetablissement= DataBaseQueries.recupererIdTypeDEtablissementParTitre(TypeDEtablissement.entreprisePrivee);
            idFonctionEmetteur = DataBaseQueries.recupererIdFonctionParSonTitreEtSonType(emetteur.getFonction(), TypeDeFonctions.interne);
            idFonctionDestinataire = DataBaseQueries.recupererIdFonctionParSonTitreEtSonType(destinataire.getFonction(), TypeDeFonctions.interne);
           
            String insertionCourrierSQL = null;
            String ajouterCorrespondanceEtapeCourrierSQL = null;


            if(courrier.getConfidentiel().equalsIgnoreCase("Oui")){
                courrier.setObjetCourrier("Confidentiel");
                courrier.setCommentairesCourrier("Confidentiel");
                courrier.setMotsclesCourrier("Confidentiel");
                courrier.setNomCourrier("Fichier_Confidentiel");

                if(!emetteur.getTypeDEmetteur().equals("Agent du Ministere")){
                    insertionCourrierSQL = "insert into `courrier` (`date_reception`, `heure_reception`, `objet`, `reference`, `mots_cles`,  `commentaires`, `priorite`, `confidentiel`, `nom_fichier`, `extension_fichier`, `etat`, `genre`, `fk_type_courrier`)  VALUES" +
                            " ('" + courrier.getDateDeReception() + "'," + "'" + DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception()) + "'," + "'" + courrier.getObjetCourrier().replaceAll("'"," ") + "'," + "'" + courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +courrier.getMotsclesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +  courrier.getCommentairesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" + courrier.getPrioriteCourrier()+ "'," +  "'" + EtatCourrier.confidentiel+ "'," +
                            "'" + fichierConfidentiel + "'," + "'" + fichierConfidentiel + "'," + "'" +EtatCourrier.courrierEnvoye  + "'," + "'" + courrier.getGenreCourrier()+"',"+"'"+ idTypeDeCourrier +"')";

                    ajouterCorrespondanceEtapeCourrierSQL = "insert into `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                            "((select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"') ,'"+EtatCourrier.courrierRecu+"');";

                }else{
                    insertionCourrierSQL = "insert into `courrier` (`date_reception`, `heure_reception`, `objet`, `reference`, `mots_cles`,  `commentaires`, `priorite`, `confidentiel`, `nom_fichier`, `extension_fichier`, `etat`, `genre`, `fk_type_courrier`)  VALUES" +
                            " ('" + courrier.getDateDeReception() + "'," + "'" + DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception()) + "'," + "'" + courrier.getObjetCourrier().replaceAll("'"," ") + "'," + "'" + courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +courrier.getMotsclesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +  courrier.getCommentairesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" + courrier.getPrioriteCourrier()+ "'," +  "'" + EtatCourrier.confidentiel+ "'," +
                            "'" + fichierConfidentiel + "'," + "'" + fichierConfidentiel + "'," + "'" +EtatCourrier.courrierEnregistre  + "'," + "'" + courrier.getGenreCourrier()+"',"+"'"+ idTypeDeCourrier +"')";
                    ajouterCorrespondanceEtapeCourrierSQL = "insert into `correspondance_etape_courrier` (`id_courrier`) VALUES" +
                            "((select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'));";


                }

            }else if(courrier.getConfidentiel().equalsIgnoreCase("Non")){

                if(!emetteur.getTypeDEmetteur().equals("Agent du Ministere")){
                    insertionCourrierSQL = "insert into `courrier` (`date_reception`, `heure_reception`, `objet`, `reference`, `mots_cles`,  `commentaires`, `priorite`, `confidentiel`,`nom_fichier`, `extension_fichier`, `etat`, `genre`, `fk_type_courrier`)  VALUES" +
                            " ('" + courrier.getDateDeReception() + "'," + "'" + DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception()) + "'," + "'" + courrier.getObjetCourrier().replaceAll("'"," ") + "'," + "'" + courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +courrier.getMotsclesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +  courrier.getCommentairesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" + courrier.getPrioriteCourrier()+ "'," +  "'" + EtatCourrier.pasConfidentiel+ "'," +
                            "'"  + courrier.getNomCourrier().replaceAll("'","_") + "'," + "'" + FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier()) + "'," + "'" +EtatCourrier.courrierEnvoye + "'," + "'" + courrier.getGenreCourrier()+"',"+"'"+idTypeDeCourrier +"')";

                    ajouterCorrespondanceEtapeCourrierSQL = "insert into `correspondance_etape_courrier` (`id_courrier`,`etat_correspondance`) VALUES" +
                            "((select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"') ,'"+EtatCourrier.courrierRecu+"');";

                }else{
                    insertionCourrierSQL = "insert into `courrier` (`date_reception`, `heure_reception`, `objet`, `reference`, `mots_cles`,  `commentaires`, `priorite`, `confidentiel`,`nom_fichier`, `extension_fichier`, `etat`, `genre`, `fk_type_courrier`)  VALUES" +
                            " ('" + courrier.getDateDeReception() + "'," + "'" + DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception()) + "'," + "'" + courrier.getObjetCourrier().replaceAll("'"," ") + "'," + "'" + courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +courrier.getMotsclesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" +  courrier.getCommentairesCourrier().replace("\\","/").replaceAll("'"," ") + "'," + "'" + courrier.getPrioriteCourrier()+ "'," +  "'" + EtatCourrier.pasConfidentiel+ "'," +
                            "'"  + courrier.getNomCourrier().replaceAll("'","_") + "'," + "'" + FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier()) + "'," + "'" +EtatCourrier.courrierEnregistre + "'," + "'" + courrier.getGenreCourrier()+"',"+"'"+idTypeDeCourrier +"')";

                    ajouterCorrespondanceEtapeCourrierSQL = "insert into `correspondance_etape_courrier` (`id_courrier`) VALUES" +
                            "((select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'));";

                }
            }


            String ajouterCourrierSQL = "insert into `ajouter_courrier` (`id_personne`,`id_courrier`) VALUES ('" + idUser +"',"+" (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'))";

            String ajouterEtapeCourrierSQL = "insert into `etape` (`titre`, `etat`) VALUES ('" + EtatCourrier.courrierEnregistre +"',"+"'"+ EtatEtape.termine +"')";

            String ajouterCorrespondanceEtapePersonneSQL = "insert into `correspondance_personne_etape` (`id_personne`) VALUES" +
                    " ('" + idUser +"')";

            Connection connection = DatabasConnection.getConnexion();
            Statement statement = null;

            switch (emetteur.getTypeDEmetteur()){

                case "Agent du Ministere":

                    if (destinataire.getTypeDestinataire().equals("Agent du Ministere")){

                        ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDEmetteur+"',"+"'" +idTypeDEmetteur+"',"+"'"+ idFonctionEmetteur + "',"+"'" +idDirectionEmetteur + "',"+ "'" +idEtablissementEmetteur + "')";

                        ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                        envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                        recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`, `accuse_reception`)  VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"');";

                    }else if(destinataire.getTypeDestinataire().equals("Agent Autre Ministere")){

                        ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDEmetteur+"',"+"'" +idTypeDEmetteur+"',"+"'"+ idFonctionEmetteur + "',"+"'" +idDirectionEmetteur + "',"+ "'" +idEtablissementEmetteur + "')";

                        ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + destinataire.getFonctionAutreMinistere().replaceAll("'"," ")  +"')";

                        ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + destinataire.getDirection() + "'," +"'"+  idEtablissementDestinataireAutreMinistere + "')";

                        ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + destinataire.getFonctionAutreMinistere().replaceAll("'"," ") + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + destinataire.getDirectionAutreMinistere().replaceAll("'"," ") + "' order by id_direction desc limit 1)" + ","+ "'" +idEtablissementDestinataireAutreMinistere + "')";

                        envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                        recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`)  VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"');";

                    }else if(destinataire.getTypeDestinataire().equals("Entreprise") || destinataire.getTypeDestinataire().equals("Association")) {

                        ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDEmetteur+"',"+"'" +idTypeDEmetteur+"',"+"'"+ idFonctionEmetteur + "',"+"'" +idDirectionEmetteur + "',"+ "'" +idEtablissementEmetteur + "')";

                        ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + destinataire.getFonctionEntreprise().replaceAll("'"," ")  +"')";

                        ajouterEtablissementSQL = "INSERT INTO `etablissement` (`nom_etablissement`, `tel_etablissement`, `mail_etablissement`, `adresse_etablissement`, `fk_type_etablissement`) VALUES" +
                                " ('" + destinataire.getRaisonSocial().replaceAll("'"," ") +"',"+"'" +destinataire.getTelephoneEntreprise()+"',"+"'"+ destinataire.getEmailEntreprise() + "',"+"'" +destinataire.getAdresseEntreprise().replaceAll("'"," ") + "',"+ "'" +idTypeDetablissement + "')";
                        System.out.println("ajouterEtablissementSQL = " + ajouterEtablissementSQL);
                        ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + destinataire.getDirectionEntreprise().replaceAll("'"," ")  + "'," +  "(select id_etablissement from `etablissement` where nom_etablissement = '" + destinataire.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+destinataire.getTelephoneEntreprise() +"' and mail_etablissement = '"+destinataire.getEmailEntreprise()+"'  and adresse_etablissement = '"+destinataire.getAdresseEntreprise().replaceAll("'"," ")  +"' order by  id_etablissement desc limit 1)"+ ")";
                        System.out.println("ajouterDirectionSQL = " + ajouterDirectionSQL);
                        ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + destinataire.getFonctionEntreprise().replaceAll("'"," ")  + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + destinataire.getDirectionEntreprise().replaceAll("'"," ")  + "' order by id_direction desc limit 1)" + ","+ "(select id_etablissement from `etablissement` where nom_etablissement = '" + destinataire.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+destinataire.getTelephoneEntreprise() +"' and mail_etablissement = '"+destinataire.getEmailEntreprise()+"'  and adresse_etablissement = '"+destinataire.getAdresseEntreprise().replaceAll("'"," ")  +"' order by id_etablissement desc limit 1)"+ ")";

                        envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                        recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`)  VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"');";

                    }else if(destinataire.getTypeDestinataire().equals("Particulier")) {

                        ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                                " ('" + finalUniqueIDEmetteur+"',"+"'" +idTypeDEmetteur+"',"+"'"+ idFonctionEmetteur + "',"+"'" +idDirectionEmetteur + "',"+ "'" +idEtablissementEmetteur + "')";

                        ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `nom`, `prenom`, `tel`, `mail`, `fk_type_personne`) VALUES  ('" +finalUniqueIDDestinataire+ "',"+"'"+ destinataire.getNomParticulier().replaceAll("'"," ")  + "'," + "'" + destinataire.getPrenomParticulier().replaceAll("'"," ") + "'," + "'" + destinataire.getTelephoneParticulier() + "'," + "'" + destinataire.getEmailParticulier()+ "'," + "'" +idTypeDeDestinataire+ "')";

                        envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                        recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`)  VALUES" +
                                "('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"');";

                    }

                    if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
                        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrierMinistre));
                        updateCourrierAlfrescoSQL = "update `courrier` SET `dossier_alfresco_emetteur` = '"+courrierMinistre+"', `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' WHERE `courrier`.`id_courrier` = (select id_courrier from (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"') as temp)";
                    }

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(insertionCourrierSQL);
                        statement.addBatch(ajouterCourrierSQL);
                        statement.addBatch(ajouterEmetteurSQL);

                        if (ajouterFonctionSQL != null){
                            statement.addBatch(ajouterFonctionSQL);
                        }
                        if (ajouterEtablissementSQL != null){
                            statement.addBatch(ajouterEtablissementSQL);
                        }
                        if (ajouterDirectionSQL != null){
                            statement.addBatch(ajouterDirectionSQL);
                        }
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(envoyerCourrierSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);

                        if(updateCourrierAlfrescoSQL != null){
                            statement.addBatch(updateCourrierAlfrescoSQL);
                        }

                        if(isAnnexe){
                            listeIdAlfrescoAnnexe.clear();
                            for (int a = 0; a < listeAnnexe.size() ; a++ ){
                                listeIdAlfrescoAnnexe.add(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(listeAnnexe.get(a).getCheminAnnexeSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(listeAnnexe.get(a).getNomAnnexe())),courrierMinistre));
                            }
                            for (int a =0; a < listeAnnexe.size(); a++){
                                ajouterAnnexeAlfrescoSQL = "insert into `annexe` (`titre`, `type_fichier`, `identifiant_alfresco_annexe`, `document_alfresco`, `id_courrier` ) VALUES" +
                                        " ('" + listeAnnexe.get(a).getNomAnnexe()+"',"+"'" +listeAnnexe.get(a).getTypeDeFichierAnnexe()+"',"+"'"+ listeIdAlfrescoAnnexe.get(a) + "',"+"'" +courrierMinistre + "',"+ "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'))";
                                statement.addBatch( ajouterAnnexeAlfrescoSQL);
                            }
                        }

                        statement.executeBatch();
                        connection.commit();
                        reinitialiserLeFormulaire();
                        PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoyer').show()");
                        listeAnnexe.clear();
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite lors de l'enregistrement du courrier"));
                        e.printStackTrace();
                    }

                    break;

                case "Agent Autre Ministere":

                    ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + emetteur.getFonctionAutreMinistere().replaceAll("'"," ")  +"')";

                    ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + emetteur.getDirectionAutreMinistere().replaceAll("'"," ")  + "'," +"'"+  idEtablissementEmetteurAutreMinistere + "')";

                    ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDEmetteur+"',"+"'"+idTypeDEmetteur+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + emetteur.getFonctionAutreMinistere().replaceAll("'"," ")  + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + emetteur.getDirectionAutreMinistere().replaceAll("'"," ")  + "' order by id_direction desc limit 1)"+ ",'"+idEtablissementEmetteurAutreMinistere+ "')";

                    ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                    envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";


                    recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`,`reference_interne`) VALUES  ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') , '"+EtatCourrier.accuseDeReceptionNon+"',"+"'"+courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+"')";


                    if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
                        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrierMinistre));
                        updateCourrierAlfrescoSQL = "update `courrier` SET `dossier_alfresco_emetteur` = '"+courrierMinistre+"', `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' WHERE `courrier`.`id_courrier` = (select id_courrier from (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier() +"') as temp)";
                    }


                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(insertionCourrierSQL);
                        statement.addBatch(ajouterCourrierSQL);
                        statement.addBatch(ajouterFonctionSQL);
                        statement.addBatch(ajouterDirectionSQL);
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(ajouterEmetteurSQL);

                        statement.addBatch(envoyerCourrierSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);
                        if(updateCourrierAlfrescoSQL != null){
                            statement.addBatch(updateCourrierAlfrescoSQL);
                        }

                        if(isAnnexe){
                             listeIdAlfrescoAnnexe.clear();
                            for (int a = 0; a < listeAnnexe.size() ; a++ ){
                                listeIdAlfrescoAnnexe.add(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(listeAnnexe.get(a).getCheminAnnexeSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(listeAnnexe.get(a).getNomAnnexe())),courrierMinistre));
                            }

                            for (int a =0; a < listeAnnexe.size(); a++){
                                ajouterAnnexeAlfrescoSQL = "insert into `annexe` (`titre`, `type_fichier`, `identifiant_alfresco_annexe`, `document_alfresco`, `id_courrier` ) VALUES" +
                                        " ('" + listeAnnexe.get(a).getNomAnnexe()+"',"+"'" +listeAnnexe.get(a).getTypeDeFichierAnnexe()+"',"+"'"+ listeIdAlfrescoAnnexe.get(a) + "',"+"'" +courrierMinistre + "',"+ "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'))";
                                statement.addBatch( ajouterAnnexeAlfrescoSQL);
                            }
                        }

                        statement.executeBatch();
                        connection.commit();
                        reinitialiserLeFormulaire();
                        PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoyer').show()");
                        listeAnnexe.clear();
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite lors de l'enregistrement du courrier"));
                        e.printStackTrace();
                    }


                    break;
                case "Entreprise":

                    ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + emetteur.getFonctionEntreprise().replaceAll("'"," ")  +"')";

                    ajouterEtablissementSQL = "INSERT INTO `etablissement` (`nom_etablissement`, `tel_etablissement`, `mail_etablissement`, `adresse_etablissement`, `fk_type_etablissement`) VALUES" +
                            " ('" + emetteur.getRaisonSocial().replaceAll("'"," ") +"',"+"'" +emetteur.getTelephoneEntreprise()+"',"+"'"+ emetteur.getEmailEntreprise() + "',"+"'" +emetteur.getAdresseEntreprise().replaceAll("'"," ") + "',"+ "'" +idTypeDetablissement + "')";

                    ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + emetteur.getDirectionEntreprise().replaceAll("'"," ")  + "'," +  "(select id_etablissement from `etablissement` where nom_etablissement = '" + emetteur.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+emetteur.getTelephoneEntreprise() +"' and mail_etablissement = '"+emetteur.getEmailEntreprise()+"'  and adresse_etablissement = '"+emetteur.getAdresseEntreprise().replaceAll("'"," ")  +"' order by  id_etablissement desc limit 1)"+ ")";

                    ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDEmetteur+"',"+"'"+idTypeDEmetteur+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + emetteur.getFonctionEntreprise().replaceAll("'"," ")  + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + emetteur.getDirectionEntreprise().replaceAll("'"," ")  + "' order by id_direction desc limit 1)" + ","+ "(select id_etablissement from `etablissement` where nom_etablissement = '" + emetteur.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+emetteur.getTelephoneEntreprise() +"' and mail_etablissement = '"+emetteur.getEmailEntreprise()+"'  and adresse_etablissement = '"+emetteur.getAdresseEntreprise().replaceAll("'"," ")  +"' order by id_etablissement desc limit 1)"+ ")";
                    System.out.println("ajouterEmetteurSQL = " + ajouterEmetteurSQL);
                    ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                    envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                    recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`,`reference_interne`) VALUES  ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"',"+"'"+courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+"')";


                    if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
                        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrierMinistre));
                        updateCourrierAlfrescoSQL = "update `courrier` SET `dossier_alfresco_emetteur` = '"+courrierMinistre+"', `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' WHERE `courrier`.`id_courrier` = (select id_courrier from (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"') as temp)";
                    }

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(insertionCourrierSQL);
                        statement.addBatch(ajouterCourrierSQL);
                        statement.addBatch(ajouterEtablissementSQL);
                        statement.addBatch(ajouterFonctionSQL);
                        statement.addBatch(ajouterDirectionSQL);
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(ajouterEmetteurSQL);

                        statement.addBatch(envoyerCourrierSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);

                        if(updateCourrierAlfrescoSQL != null){
                            statement.addBatch(updateCourrierAlfrescoSQL);
                        }

                        if(isAnnexe){
                             listeIdAlfrescoAnnexe.clear();
                            for (int a = 0; a < listeAnnexe.size() ; a++ ){
                                listeIdAlfrescoAnnexe.add(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(listeAnnexe.get(a).getCheminAnnexeSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(listeAnnexe.get(a).getNomAnnexe())),courrierMinistre));
                            }

                            for (int a =0; a < listeAnnexe.size(); a++){
                                ajouterAnnexeAlfrescoSQL = "insert into `annexe` (`titre`, `type_fichier`, `identifiant_alfresco_annexe`, `document_alfresco`, `id_courrier` ) VALUES" +
                                        " ('" + listeAnnexe.get(a).getNomAnnexe()+"',"+"'" +listeAnnexe.get(a).getTypeDeFichierAnnexe()+"',"+"'"+ listeIdAlfrescoAnnexe.get(a) + "',"+"'" +courrierMinistre + "',"+ "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'))";
                                statement.addBatch( ajouterAnnexeAlfrescoSQL);
                            }
                        }

                        statement.executeBatch();
                        connection.commit();
                        reinitialiserLeFormulaire();
                        PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoyer').show()");
                         listeAnnexe.clear();
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite lors de l'enregistrement du courrier"));
                        e.printStackTrace();
                    }


                    break;
                case "Association":

                    ajouterFonctionSQL = "insert into `fonction` (`titre_fonction`) VALUES ('" + emetteur.getFonctionEntreprise().replaceAll("'"," ")  +"')";

                    ajouterEtablissementSQL = "INSERT INTO `etablissement` (`nom_etablissement`, `tel_etablissement`, `mail_etablissement`, `adresse_etablissement`, `fk_type_etablissement`) VALUES" +
                            " ('" + emetteur.getRaisonSocial().replaceAll("'"," ") +"',"+"'" +emetteur.getTelephoneEntreprise()+"',"+"'"+ emetteur.getEmailEntreprise() + "',"+"'" +emetteur.getAdresseEntreprise().replaceAll("'"," ") + "',"+ "'" +idTypeDetablissement + "')";

                    ajouterDirectionSQL = "insert into `direction` (`nom_direction`, `fk_etablissement`) VALUES ('" + emetteur.getDirectionEntreprise().replaceAll("'"," ")  + "'," +  "(select id_etablissement from `etablissement` where nom_etablissement = '" + emetteur.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+emetteur.getTelephoneEntreprise() +"' and mail_etablissement = '"+emetteur.getEmailEntreprise()+"'  and adresse_etablissement = '"+emetteur.getAdresseEntreprise().replaceAll("'"," ")  +"' order by  id_etablissement desc limit 1)"+ ")";

                    ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`,`id_fonction`, `id_direction`, `id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDEmetteur+"',"+"'"+idTypeDEmetteur+"',"+  "(select id_fonction from `fonction` where titre_fonction = '" + emetteur.getFonctionEntreprise().replaceAll("'"," ")  + "' order by id_fonction desc limit 1)" +"," +"(select id_direction from `direction` where nom_direction = '" + emetteur.getDirectionEntreprise().replaceAll("'"," ")  + "' order by id_direction desc limit 1)" + ","+ "(select id_etablissement from `etablissement` where nom_etablissement = '" + emetteur.getRaisonSocial().replaceAll("'"," ")  + "' and tel_etablissement  = '"+emetteur.getTelephoneEntreprise() +"' and mail_etablissement = '"+emetteur.getEmailEntreprise()+"'  and adresse_etablissement = '"+emetteur.getAdresseEntreprise().replaceAll("'"," ")  +"' order by id_etablissement desc limit 1)"+ ")";

                    ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                    envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                    recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`,`reference_interne`) VALUES  ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"',"+"'"+courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+"')";


                    if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
                        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrierMinistre));
                        updateCourrierAlfrescoSQL = "update `courrier` SET `dossier_alfresco_emetteur` = '"+courrierMinistre+"', `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' WHERE `courrier`.`id_courrier` = (select id_courrier from (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"') as temp)";

                    }

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(insertionCourrierSQL);
                        statement.addBatch(ajouterCourrierSQL);
                        statement.addBatch(ajouterEtablissementSQL);
                        statement.addBatch(ajouterFonctionSQL);
                        statement.addBatch(ajouterDirectionSQL);
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(ajouterEmetteurSQL);

                        statement.addBatch(envoyerCourrierSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);

                        if(updateCourrierAlfrescoSQL != null){
                            statement.addBatch(updateCourrierAlfrescoSQL);
                        }

                        if(isAnnexe){
                             listeIdAlfrescoAnnexe.clear();
                            for (int a = 0; a < listeAnnexe.size() ; a++ ){
                                listeIdAlfrescoAnnexe.add(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(listeAnnexe.get(a).getCheminAnnexeSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(listeAnnexe.get(a).getNomAnnexe())),courrierMinistre));
                            }

                            for (int a =0; a < listeAnnexe.size(); a++){
                                ajouterAnnexeAlfrescoSQL = "insert into `annexe` (`titre`, `type_fichier`, `identifiant_alfresco_annexe`, `document_alfresco`, `id_courrier` ) VALUES" +
                                        " ('" + listeAnnexe.get(a).getNomAnnexe()+"',"+"'" +listeAnnexe.get(a).getTypeDeFichierAnnexe()+"',"+"'"+ listeIdAlfrescoAnnexe.get(a) + "',"+"'" +courrierMinistre + "',"+ "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'))";
                                statement.addBatch( ajouterAnnexeAlfrescoSQL);
                            }
                        }

                        statement.executeBatch();
                        connection.commit();
                        reinitialiserLeFormulaire();
                        PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoyer').show()");
                        listeAnnexe.clear();
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite lors de l'enregistrement du courrier"));
                        e.printStackTrace();
                    }

                    break;
                case "Particulier":

                    ajouterEmetteurSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `nom`, `prenom`, `tel`, `mail`) VALUES" +
                            " ('" + finalUniqueIDEmetteur+"',"+"'"+idTypeDEmetteur+"',"+"'"+emetteur.getNomParticulier().replaceAll("'"," ") +"',"+"'"+emetteur.getPrenomParticulier().replaceAll("'"," ") +"',"+"'"+emetteur.getTelephoneParticulier()+"',"+"'"+emetteur.getEmailParticulier()+ "')";

                    ajouterDestinataireSQL = "insert into `personne` (`unique_id`, `fk_type_personne`, `id_fonction`, `id_direction` ,`id_etablissement`) VALUES" +
                            " ('" + finalUniqueIDDestinataire+"',"+"'"+idTypeDeDestinataire+"',"+"'"+ idFonctionDestinataire + "',"+"'" +idDirectionDestinataire + "',"+ "'" +idEtablissementDestinataire+ "')";

                    envoyerCourrierSQL = "insert into envoyer_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`confirmation_reception`) VALUES ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'","_") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDEmetteur+"'), '"+EtatCourrier.confirmationDeRecepetionNon+"');";

                    recevoirCourrierSQL = "insert into recevoir_courrier (`favoris`,`archive`,`id_courrier`,`id_personne`,`accuse_reception`,`reference_interne`) VALUES  ('" + EtatCourrier.pasfavoris+"',"+"'"+EtatCourrier.archiveNonActive+"',"+  "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'),(select id_personne from `personne` where unique_id = '"+finalUniqueIDDestinataire+"') ,'"+EtatCourrier.accuseDeReceptionNon+"',"+"'"+courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+"');";

                    if(courrier.getConfidentiel().equalsIgnoreCase("Non")){
                        courrier.setIdAlfresco(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(courrier.getCheminCourrierSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(courrier.getNomCourrier())),courrierMinistre));
                        updateCourrierAlfrescoSQL = "update `courrier` SET `dossier_alfresco_emetteur` = '"+courrierMinistre+"', `identifiant_alfresco` = '"+courrier.getIdAlfresco()+"' WHERE `courrier`.`id_courrier` = (select id_courrier from (select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"') as temp)";
                    }

                    try {
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                        statement.addBatch(insertionCourrierSQL);
                        statement.addBatch(ajouterCourrierSQL);
                        statement.addBatch(ajouterDestinataireSQL);
                        statement.addBatch(ajouterEmetteurSQL);
                        statement.addBatch(envoyerCourrierSQL);
                        statement.addBatch(recevoirCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapeCourrierSQL);
                        statement.addBatch(ajouterCorrespondanceEtapePersonneSQL);
                        statement.addBatch(ajouterEtapeCourrierSQL);
                        statement.addBatch(updateCourrierAlfrescoSQL);

                        if(updateCourrierAlfrescoSQL != null){
                            statement.addBatch(updateCourrierAlfrescoSQL);
                        }

                        if(isAnnexe){
                             listeIdAlfrescoAnnexe.clear();
                            for (int a = 0; a < listeAnnexe.size() ; a++ ){
                                listeIdAlfrescoAnnexe.add(ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(listeAnnexe.get(a).getCheminAnnexeSurPC()),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(listeAnnexe.get(a).getNomAnnexe())),courrierMinistre));
                            }

                            for (int a =0; a < listeAnnexe.size(); a++){
                                ajouterAnnexeAlfrescoSQL = "insert into `annexe` (`titre`, `type_fichier`, `identifiant_alfresco_annexe`, `document_alfresco`, `id_courrier` ) VALUES" +
                                        " ('" + listeAnnexe.get(a).getNomAnnexe()+"',"+"'" +listeAnnexe.get(a).getTypeDeFichierAnnexe()+"',"+"'"+ listeIdAlfrescoAnnexe.get(a) + "',"+"'" +courrierMinistre + "',"+ "(select id_courrier from `courrier` where date_reception ='"+ courrier.getDateDeReception()+"' and heure_reception ='"+ DateUtils.convertirHeureDeReceptionAuBonFormat(courrier.getHeureDeReception())+"' and objet = '"+courrier.getObjetCourrier().replaceAll("'"," ")+ "' and reference = '"+ courrier.getReferenceCourrier().replace("\\","/").replaceAll("'"," ")+ "' and nom_fichier = '"+ courrier.getNomCourrier().replaceAll("'"," ") +"'))";
                                statement.addBatch( ajouterAnnexeAlfrescoSQL);
                            }
                        }

                        statement.executeBatch();
                        connection.commit();
                        reinitialiserLeFormulaire();
                        PrimeFaces.current().executeScript("PF('dialogueCourrierBienEnvoyer').show()");
                        listeAnnexe.clear();
                    } catch (SQLException e) {
                        FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur s'est produite lors de l'enregistrement du courrier"));
                        e.printStackTrace();
                    }

                    break;
            }
        }else{
            FacesContext.getCurrentInstance().addMessage("messagesErreur", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Vous devez renseigner la date et l'heure de reception du courrier"));
        }
        //return  null;
    }

    public void reinitialiserLeFormulaire(){
        courrier.setDateDeReception(null);
        courrier.setNomCourrier(null);
        courrier.setTypeCourrier(null);
        courrier.setObjetCourrier(null);
        courrier.setCommentairesCourrier(null);
        courrier.setMotsclesCourrier(null);
        courrier.setPrioriteCourrier(null);
        courrier.setGenreCourrier(null);
        courrier.setReferenceCourrier(null);
        courrier.setConfidentiel("Non");

        emetteur.setDirection(null);
        emetteur.setFonction(null);
        emetteur.setMinistereAutreMinistere(null);
        emetteur.setFonctionAutreMinistere(null);
        emetteur.setDirectionAutreMinistere(null);
        emetteur.setTypeDEmetteur(null);
        emetteur.setNomParticulier(null);
        emetteur.setPrenomParticulier(null);
        emetteur.setTelephoneParticulier(null);
        emetteur.setAdresseEntreprise(null);
        emetteur.setRaisonSocial(null);
        emetteur.setDirectionEntreprise(null);
        emetteur.setFonctionEntreprise(null);

        destinataire.setDirection(null);
        destinataire.setFonction(null);
        destinataire.setTypeDestinataire(null);
        destinataire.setMinistereAutreMinistere(null);
        destinataire.setFonctionAutreMinistere(null);
        destinataire.setDirectionAutreMinistere(null);
        destinataire.setNomParticulier(null);
        destinataire.setPrenomParticulier(null);
        destinataire.setTelephoneParticulier(null);
        destinataire.setAdresseEntreprise(null);
        destinataire.setRaisonSocial(null);
        destinataire.setDirectionEntreprise(null);
        destinataire.setFonctionEntreprise(null);
    }

    public Annexe getAnnexe() {
        return annexe;
    }

    public void setAnnexe(Annexe annexe) {
        this.annexe = annexe;
    }

    public Destinataire getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Destinataire destinataire) {
        this.destinataire = destinataire;
    }

    public Emetteur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Emetteur emetteur) {
        this.emetteur = emetteur;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public Emetteur getTypeDEmetteur() {
        return typeDEmetteur;
    }

    public void setTypeDEmetteur(Emetteur typeDEmetteur) {
        this.typeDEmetteur = typeDEmetteur;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Fonction getFonction() {
        return fonction;
    }

    public void setFonction(Fonction fonction) {
        this.fonction = fonction;
    }

    public boolean isThisIsConfidentiel() {
        return thisIsConfidentiel;
    }

    public void setThisIsConfidentiel(boolean thisIsConfidentiel) {
        this.thisIsConfidentiel = thisIsConfidentiel;
    }

    public Date getDateTemp() {
        return dateTemp;
    }

    public void setDateTemp(Date dateTemp) {
        this.dateTemp = dateTemp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIsResponsable() {
        return isResponsable;
    }

    public void setIsResponsable(String isResponsable) {
        this.isResponsable = isResponsable;
    }
}
