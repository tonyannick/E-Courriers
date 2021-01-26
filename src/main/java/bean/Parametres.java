package bean;

import alfresco.ConnexionAlfresco;
import databaseManager.*;
import dateAndTime.DateUtils;
import fileManager.FileManager;
import fileManager.PropertiesFilesReader;
import mailManager.EmailValidation;
import messages.FacesMessages;
import model.*;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import sessionManager.SessionUtils;
import stringManager.StringUtils;
import variables.EtatCompteUser;
import variables.Ministere;
import variables.TypeDEtablissement;
import variables.TypeDeFonctions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SessionScoped
@Named
public class Parametres implements Serializable {

    private static final long serialVersionUID = -1117872938068484700L;
    private User user;
    private Etablissement etablissement;
    private Direction direction;
    private Fonction fonction;
    private Integer first = 0;
    private Integer rows = 10;
    private String nomPhoto;
    private String cheminPhotoSurPC;
    private Boolean isPhotoAjoute = false;
    private String dossierPhoto = "Photos";
    private Courrier courrier;
    private String phraseListeDesUtilisateurs;
    private String phraseEtatCompteUser;
    private String idUserTemp = null;
    private String userNomTemp;
    private String userPrenomTemp;
    private String userEmailTemp;
    private String userPseudoTemp;
    private String profilUtilisateur = "4";
    private String idMinistereBudget = "1";
    private String userTelTemp;
    private String userFonctionTemp;
    private String userDirectionTemp;
    private String userDirectionPourAjoutTemp;
    private String userFonctionPourAjoutTemp;
    private String userNomPourAjoutTemp;
    private String userPrenomPourAjoutTemp;
    private String userMailPourAjoutTemp;
    private String userTelPourAjoutTemp;
    private String userServiceTemp;
    private String nomMinistereTemp;
    private String abreviationTemp;
    private String infosMinistereAModifier;
    private String elementDuMinistereAModifier;
    private String nomEntite;
    private String typeDeCourrier;
    private List<Courrier> typeCourriers = new ArrayList<>();

    @PostConstruct
    public void initialisation(){
        user = new User();
        etablissement = new Etablissement();
        direction = new Direction();
        fonction = new Fonction();
        courrier = new Courrier();
        direction.setListeObjetsDirection(DirectionQueries.recupererLaListeDesDirectionsDuMinistere());
        recupererListeDesDirections();
        recupererListeDesTypesDeCourriers();
    }

    public void recupererLesInfosDeMonCompte(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        UsersQueries.recupererInfosUsersParSonId(idPersonne);
        user.setUserName(UsersQueries.nomUser);
        user.setUserPrenom(UsersQueries.prenomUser);
        user.setUserMail(UsersQueries.emailUser);
        user.setUserPseudo(UsersQueries.pseudUser);
        user.setUserTel(UsersQueries.telUser);
        user.setUserDirection(UsersQueries.directionUser);
        user.setUserFonction(UsersQueries.fonctionUser);
        user.setUserEtablissement(UsersQueries.etablissementUser);
        user.setUserPassword(UsersQueries.passwordUser);
        user.setUserProfil(UsersQueries.profilUser);
        user.setUserId(UsersQueries.idPersonne);
    }

    public void voirLesDetailsDUnUser(){
        Map<String,String> viewParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idUserTemp = viewParams.get("userId");
        UsersQueries.recupererInfosUsersParSonId(idUserTemp);
        userNomTemp = UsersQueries.nomUser;
        userPrenomTemp = UsersQueries.prenomUser;
        userEmailTemp = UsersQueries.emailUser;
        userPseudoTemp = UsersQueries.pseudUser;
        userTelTemp = UsersQueries.telUser;
        userFonctionTemp = UsersQueries.fonctionUser;
        userDirectionTemp = UsersQueries.directionUser;
        userServiceTemp = UsersQueries.serviceUser;

    }

    public void recupererInfosMinistereDuBudget(){
        List<Etablissement> tousLesMinisteresListe = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnexion();
        String requeteMinistereBudget = "select * from etablissement inner join type_etablissement on etablissement.fk_type_etablissement = type_etablissement.id_type_etablissement where etablissement.abreviation = '"+ Ministere.MinistereDuBudget +"' ;";
        String requeteTousLesMinisteres = "select * from etablissement inner join type_etablissement on etablissement.fk_type_etablissement = type_etablissement.id_type_etablissement where titre_type_etablissement = '"+ TypeDEtablissement.ministere +"' and   etablissement.abreviation != '"+ Ministere.MinistereDuBudget +"' ;";

        ResultSet resultSetTousLesMinisteres = null;
        ResultSet resultSetUnMinistere = null;

        try {
            resultSetUnMinistere = connection.createStatement().executeQuery(requeteMinistereBudget);
            resultSetTousLesMinisteres = connection.createStatement().executeQuery(requeteTousLesMinisteres);

            while (resultSetUnMinistere.next()){
                etablissement.setIdetablissement(resultSetUnMinistere.getString("id_etablissement"));
                etablissement.setTitreEtablissement(resultSetUnMinistere.getString("nom_etablissement"));
                etablissement.setAbreviationEtablissement(resultSetUnMinistere.getString("abreviation"));
                etablissement.setTelEtablissement(resultSetUnMinistere.getString("tel_etablissement"));
                etablissement.setMailEtablissement(resultSetUnMinistere.getString("mail_etablissement"));
                etablissement.setAdresseEtablissement(resultSetUnMinistere.getString("adresse_etablissement"));
            }

            while(resultSetTousLesMinisteres.next()){
                tousLesMinisteresListe.add(new Etablissement(
                        resultSetTousLesMinisteres.getString("id_etablissement"),
                        resultSetTousLesMinisteres.getString("nom_etablissement"),
                        resultSetTousLesMinisteres.getString("abreviation"),
                        resultSetTousLesMinisteres.getString("tel_etablissement"),
                        resultSetTousLesMinisteres.getString("mail_etablissement")));
            }

            etablissement.setListeDesEtablissements(tousLesMinisteresListe);


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }

    public void mettreAJourMonCompte(){
        String updateNomUserSQL = "update `personne` set `nom` = '"+user.getUserName().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";
        String updatePrenomUserSQL = "update `personne` set `prenom` = '"+user.getUserPrenom().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";
        String updateTelUserSQL = "update `personne` set `tel` = '"+user.getUserTel().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";
        String updateMailUserSQL = "update `personne` set `mail` = '"+user.getUserMail().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";
        String updatePseudoUserSQL = "update `personne` set `pseudo` = '"+user.getUserPseudo().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";
        String updateMotDePasseUserSQL = "update `personne` set `mot_de_passe` = '"+user.getUserPassword().replaceAll("'"," ")+"' where id_personne  = '"+user.getUserId()+"' ;";

        Connection connection = DatabaseConnection.getConnexion();
        Statement statement = null;

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(updateNomUserSQL);
            statement.addBatch(updatePrenomUserSQL);
            statement.addBatch(updateTelUserSQL);
            statement.addBatch(updateMailUserSQL);
            statement.addBatch(updatePseudoUserSQL);
            statement.addBatch(updateMotDePasseUserSQL);
            statement.executeBatch();
            connection.commit();
            PrimeFaces.current().executeScript("swal('Validation','Modification(s) effectuée(s)', 'success');");
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("swal('Erreur','Une erreur s'est produite avec la base de données', 'error');");
            e.printStackTrace();
        }

    }

    public void recupererEtatDuCompte(){
        Map<String,String> viewParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        user.setUserEtat(viewParams.get("etatCompte"));
        idUserTemp = viewParams.get("userId");
        if(user.getUserEtat().equalsIgnoreCase("Actif")){
            phraseEtatCompteUser = "Bloquer cet utilisateur ?";
        }else{
            phraseEtatCompteUser = "Débloquer cet utilisateur ?";
        }
    }

    public void bloquerUnCompte(){
        PrimeFaces.current().executeScript("PF('panelboutonsetatducompte').close()");
        PrimeFaces.current().executeScript("PF('panelloadingetatducompte').toggle()");
        String updateCompteUserSQL = null;
        if(user.getUserEtat().equalsIgnoreCase("Actif")){
            updateCompteUserSQL = "update `personne` set `etat_du_compte` = '"+ EtatCompteUser.bloque+"' where id_personne  = '"+idUserTemp+"';";
        }else{
            updateCompteUserSQL = "update `personne` set `etat_du_compte` = '"+ EtatCompteUser.actif+"' where id_personne  = '"+idUserTemp+"';";
        }
        Connection connection = DatabaseConnection.getConnexion();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(updateCompteUserSQL);
            statement.executeBatch();
            connection.commit();
            PrimeFaces.current().executeScript("PF('panelloadingetatducompte').close()");
            PrimeFaces.current().executeScript("PF('panelrechargerlapageetatducompte').toggle()");
            FacesContext.getCurrentInstance().addMessage("messageetatducompte", new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","Modification réussie"));
        } catch (SQLException e) {
            PrimeFaces.current().executeScript("PF('panelloadingetatducompte').close()");
            PrimeFaces.current().executeScript("PF('panelboutonsetatducompte').toggle()");
            FacesContext.getCurrentInstance().addMessage("messageetatducompte", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite"));
            e.printStackTrace();
        }

    }

    public void afficherLaListeDesUser(){
        HttpSession session = SessionUtils.getSession();
        String directionUser = session.getAttribute("directionUser").toString();
        String fonctionUser = session.getAttribute("fonctionUser").toString();
        /***TODO Ameliorer***/
        if(fonctionUser.equals("Secrétaire Général") || fonctionUser.equals("Directeur de Cabinet") || fonctionUser.equals("Secrétaire Général Adjoint")
                || fonctionUser.equals("Ministre") || fonctionUser.equals("Ministre Délégué")){
            user.setUserList(UsersQueries.recupererLaListeDesUsers());
            phraseListeDesUtilisateurs = "Liste de tous les agents du Ministère enregistrés dans l'application";
        }else{
            user.setUserList(UsersQueries.recupererLesAgentsDUneDirection(directionUser));
            phraseListeDesUtilisateurs = "Liste des agents de votre direction enregistrés dans l'application";
        }

    }

    public void recupererListeDesDirections(){
        HttpSession session = SessionUtils.getSession();
        String nomDirection = session.getAttribute("directionUser").toString();
        direction.setListeDirection(DirectionQueries.recupererLaListeDesDirections());
        direction.getListeDirection().removeIf(e -> !e.equals(nomDirection));
    }

    public List<String> recupererListeFonctionsParDirectionDuDestinataire(){
        fonction.setListeFonction(DataBaseQueries.recupererLaListeDesFonctionsDesAgentsParDirection());
        return fonction.getListeFonction();
    }

    public void checkDuProfilDuUser(){
        HttpSession session = SessionUtils.getSession();
        boolean isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            PrimeFaces.current().executeScript("afficherTabAutoriseParProfil()");
        }
    }

    public void voirLahoto(){
        user.setUserPhoto(ConnexionAlfresco.telechargerDocumentDansAlfresco(UsersQueries.photoUser));
    }

    public void ajouterUnUser(){
        if(userNomPourAjoutTemp.isEmpty() || userPrenomPourAjoutTemp.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_WARN,"Erreur","Vous devez renseigner le nom et le prénom de l'utilisateur"));
        }else{
            if(userMailPourAjoutTemp.isEmpty() || userTelPourAjoutTemp.isEmpty()){
                FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_WARN,"Erreur","Vous devez renseigner le mail et le numéro de l'utilisateur"));
            }else{
                /**TODO check si email valide **/
                if(EmailValidation.voirSiEmailEstValide(userMailPourAjoutTemp.trim())){
                    FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_WARN,"Erreur","L'email ne semble pas correcte"));
                    setUserMailPourAjoutTemp(null);
                }else{
                    if(userFonctionPourAjoutTemp == null || userDirectionPourAjoutTemp.equals("rien")){
                        FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_WARN,"Erreur","Vous devez renseigner la direction et la fonction de l'utilisateur"));

                    }else{
                        PrimeFaces.current().executeScript("PF('panelajoutuser').close()");
                        PrimeFaces.current().executeScript("PF('paneluserloading').toggle()");
                        String idType = EtablissementQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);
                        String idFonction = DataBaseQueries.recupererIdFonctionParSonTitreEtSonType(userFonctionPourAjoutTemp, TypeDeFonctions.interne);
                        String idDirection = DataBaseQueries.recupererIdDirectionParSonNom(userDirectionPourAjoutTemp);
                        String idEtablissement = EtablissementQueries.recupererIdEtablissementParSonAbreviation(Ministere.MinistereDuBudget);
                        String  ajouterUserSQL = "insert into `personne` (`fk_type_personne`, `nom`, `prenom`,`tel`, `mail`, `id_fonction`,`id_profil`,`etat_du_compte`,`id_direction` ,`id_etablissement`) VALUES" +
                                " ('" +idType+"',"+"'"+userNomPourAjoutTemp.trim().replaceAll("'"," ")+"',"+"'"+userPrenomPourAjoutTemp.trim().replaceAll("'"," ")+"',"+"'"+userTelPourAjoutTemp.trim()+"',"+"'"+userMailPourAjoutTemp.trim()+"',"+"'"+ idFonction+ "',"+"'"+profilUtilisateur+"',"+"'"+EtatCompteUser.enAttente+"',"+"'" +idDirection+ "',"+ "'" +idEtablissement+ "')";
                        Connection connection = DatabaseConnection.getConnexion();
                        Statement statement = null;
                        try {
                            connection.setAutoCommit(false);
                            statement = connection.createStatement();
                            statement.addBatch(ajouterUserSQL);
                            statement.executeBatch();
                            connection.commit();
                            FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","L'utilisateur à bien été ajouté"));
                            afficherLaListeDesUser();
                            //MailUtils.envoyerUnMailSimple(userMailPourAjoutTemp.trim(),"E-Courrier, création de votre compte","Votre compte sur E-Courrier vient d'etre créer");
                            PrimeFaces.current().executeScript("PF('paneluserloading').close()");
                            PrimeFaces.current().executeScript("PF('paneluserrechargerlapage').toggle()");
                            setUserNomPourAjoutTemp(null);
                            setUserPrenomPourAjoutTemp(null);
                            setUserMailPourAjoutTemp(null);
                            setUserTelPourAjoutTemp(null);
                            setUserFonctionPourAjoutTemp(null);
                            setUserDirectionPourAjoutTemp(null);
                        } catch (SQLException e) {
                            FacesContext.getCurrentInstance().addMessage("ajouterDUnUser", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur sur le réseau s'est produite"));
                            PrimeFaces.current().executeScript("PF('panelajoutuser').toggle()");
                            PrimeFaces.current().executeScript("PF('paneluserloading').close()");
                            e.printStackTrace();
                        }finally {
                            if ( statement!= null) {
                                try {
                                    statement.close();
                                } catch (SQLException e) { /* ignored */}
                            }
                            if ( connection != null) {
                                try {
                                    connection.close();
                                } catch (SQLException e) { /* ignored */}
                            }
                        }
                    }
                }

            }
        }

    }

    public void mettreNomEnMajuscule(){
        setUserNomPourAjoutTemp(StringUtils.mettreMotEnMajuscule(userNomPourAjoutTemp));
    }

    public void mettrePremiereLettreDuPrenomEnMajuscule(){
        setUserPrenomPourAjoutTemp(StringUtils.mettrePremiereLettreDuMotEnMajuscule(userPrenomPourAjoutTemp));
    }

    public void mettrePremiereLettreEnMajuscule(){
        setNomEntite(StringUtils.mettrePremiereLettreDuMotEnMajuscule(nomEntite));
    }

    public void afficherDetailsDUnMinistereParSonId(){
        Map<String,String> stringMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idEtab = stringMap.get("idEtablissement");
        System.out.println("idEtab = " + idEtab);
        EtablissementQueries.recupererInfosMinistereParSonId(idEtab);
        nomMinistereTemp = EtablissementQueries.nomCompletMinistere;
        abreviationTemp = EtablissementQueries.abreviationMinistere;
    }

    public void recupererListeDesTypesDeCourriers(){
        typeCourriers.addAll(CourriersQueries.recupererTousLesTypesDeCourrier());
    }

    public void ajouterUnePhoto(FileUploadEvent fileUploadEvent){
        byte[] bytes = null;
        nomPhoto = DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName();
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        cheminPhotoSurPC = FileManager.tempDirectoryOSPath +"photo_"+nomPhoto;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(cheminPhotoSurPC)));
            stream.write(bytes);
            stream.flush();
            stream.close();
            isPhotoAjoute = true;
            FacesContext.getCurrentInstance().addMessage("messagephoto", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Photo bien ajouté !!"));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("messagephoto", new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "Une erreur s'est produite lors de l'ajout du fichier !!"));
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

    public void ajouterUnTypeDeCourrier(){
        if(typeDeCourrier.isEmpty()){
            FacesMessages.errorMessage("messagesNomTypeCourrier","Erreur", "Vous devez renseigner une valeur");
        }else{
            PrimeFaces.current().executeScript("PF('paneltypecourrier').close()");
            PrimeFaces.current().executeScript("PF('paneltypecourrierloading').toggle()");
            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;
            String ajouterTypeCOurrierSQL = "INSERT INTO `type_courrier` (`titre_type_courrier`) VALUES ('"+typeDeCourrier+"');";
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(ajouterTypeCOurrierSQL);
                statement.executeBatch();
                connection.commit();
                setTypeDeCourrier(null);
                PrimeFaces.current().executeScript("PF('paneltypecourrierloading').close()");
                PrimeFaces.current().executeScript("PF('panelajouttypecourriervalide').toggle()");
                typeCourriers.clear();
                recupererListeDesTypesDeCourriers();
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("PF('paneltypecourrierloading').close()");
                PrimeFaces.current().executeScript("PF('paneltypecourrier').toggle()");
                e.printStackTrace();
            }

        }
    }

    public void ajouterUneEntiteAuMinistere(){
        if(nomEntite.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagesNomEntite", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez renseigner une valeur"));
        }else{
            PrimeFaces.current().executeScript("PF('panelajoutentite').close()");
            PrimeFaces.current().executeScript("PF('panelajoutentiteloading').toggle()");
            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;
            String ajouterEntiteSQL = "INSERT INTO `direction` (`nom_direction`, `fk_etablissement`) VALUES ('"+nomEntite+"', '"+idMinistereBudget+"');";
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(ajouterEntiteSQL);
                statement.executeBatch();
                connection.commit();
                setNomEntite(null);
                PrimeFaces.current().executeScript("PF('panelajoutentiteloading').close()");
                PrimeFaces.current().executeScript("PF('panelajoutentitevalide').toggle()");
                direction.setListeObjetsDirection(DirectionQueries.recupererLaListeDesDirectionsDuMinistere());
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("PF('panelajoutentiteloading').close()");
                PrimeFaces.current().executeScript("PF('panelajoutentite').toggle()");
                e.printStackTrace();
            }

        }
    }

    public void confirmerAjoutDUnePhoto(){
        if(isPhotoAjoute){
            PrimeFaces.current().executeScript("PF('panelfichier').close()");
            PrimeFaces.current().executeScript("PF('panelloading').toggle()");
            HttpSession session = SessionUtils.getSession();
            String idUser = (String) session.getAttribute( "idUser");
            String directionUser = session.getAttribute("directionUser").toString();
            PropertiesFilesReader.trouverLesDossiersDeLaDirectionDansAlfresco("dossiersAlfrescoMinistere.properties",directionUser);

            String identifiantAlfresco = null;
            identifiantAlfresco = ConnexionAlfresco.enregistrerFichierCourrierDansAlfresco(new File(cheminPhotoSurPC),FileManager.determinerTypeDeFichierParSonExtension(FileManager.recupererExtensionDUnFichierParSonNom(nomPhoto)),PropertiesFilesReader.dossierPhotos);
            if(identifiantAlfresco != null){
                String updatePhotoUserSQL = "update `personne` set `id_alfresco_photo` = '"+identifiantAlfresco+"' where id_personne  = '"+idUser+"' ;";
                Connection connection = DatabaseConnection.getConnexion();
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    statement.addBatch(updatePhotoUserSQL);
                    statement.executeBatch();
                    connection.commit();
                    PrimeFaces.current().executeScript("PF('panelloading').close()");
                    FacesContext.getCurrentInstance().addMessage("messagegeneral",new FacesMessage(FacesMessage.SEVERITY_INFO,"Validation","Votre photo à bien été ajoutée"));
                    PrimeFaces.current().executeScript("PF('panelrechargerlapage').toggle()");
                } catch (SQLException e) {
                    PrimeFaces.current().executeScript("PF('panelfichier').toggle()");
                    PrimeFaces.current().executeScript("PF('panelloading').close()");
                    FacesContext.getCurrentInstance().addMessage("messagegeneral",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite avec la base de données"));
                    e.printStackTrace();
                }
            }else{
                FacesContext.getCurrentInstance().addMessage("messagegeneral",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur s'est produite avec le serveur de fichier Alfresco"));
                PrimeFaces.current().executeScript("PF('panelloading').close()");
                PrimeFaces.current().executeScript("PF('panelrechargerlapage').toggle()");
            }
        }else{
            FacesContext.getCurrentInstance().addMessage("messagephoto",new FacesMessage(FacesMessage.SEVERITY_WARN,"Attention","Vous devez ajouté une photo"));
        }


    }

    public void recupererElementAModifierAuClick(ActionEvent actionEvent){
        HttpSession session = SessionUtils.getSession();
        String fonctionUser = session.getAttribute("fonctionUser").toString();

        if(fonctionUser.equals("Secrétaire Général") || fonctionUser.equals("Secrétaire Général Adjoint")){
            Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            elementDuMinistereAModifier = String.valueOf(params.get("valeurMinistereAModifier"));
            PrimeFaces.current().executeScript("PF('dialogueMettreAJourInfosDuMinistere').show()");
        }else{
            PrimeFaces.current().executeScript("swal('Oups','Votre profil ne vous permets de réaliser cette action', 'warning');");
        }

    }

    public void miseAJourDesInfosDuMinistere(){
        String colonne = null;
        String updateSQL = null;
        String table = "etablissement";
        boolean isEmail = false;
        switch (elementDuMinistereAModifier){
            case "nom":
                colonne = "nom_etablissement";
                updateSQL = "update `"+table+"` set `"+colonne+"` = '"+infosMinistereAModifier.trim().replaceAll("'"," ")+"' where id_etablissement = '"+Ministere.idMinistereDuBudget+"'" ;
                break;
            case "email":
                isEmail = true;
                colonne = "mail_etablissement";
                updateSQL = "update `"+table+"` set `"+colonne+"` = '"+infosMinistereAModifier.trim().replaceAll("'"," ")+"' where id_etablissement = '"+Ministere.idMinistereDuBudget+"'" ;
                break;
            case "tel":
                colonne = "tel_etablissement";
                updateSQL = "update `"+table+"` set `"+colonne+"` = '"+infosMinistereAModifier.trim().replaceAll("'"," ")+"' where id_etablissement = '"+Ministere.idMinistereDuBudget+"'" ;
                break;
            case "adresse":
                colonne = "adresse_etablissement";
                updateSQL = "update `"+table+"` set `"+colonne+"` = '"+infosMinistereAModifier.trim().replaceAll("'"," ")+"' where id_etablissement = ' "+Ministere.idMinistereDuBudget+"'" ;
                break;
        }

        if(infosMinistereAModifier.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagesInfosMinistere", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Vous devez renseigner une valeur"));
        }else{
            if(isEmail){
                if(!EmailValidation.voirSiEmailEstValide(infosMinistereAModifier.trim())){
                    FacesContext.getCurrentInstance().addMessage("messagesInfosMinistere", new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Cette email ne semble pas correct"));
                    setInfosMinistereAModifier(null);
                }
            }

            PrimeFaces.current().executeScript("PF('panelmodifinfosministere').close()");
            PrimeFaces.current().executeScript("PF('panelinfosministereloading').toggle()");
            Connection connection = DatabaseConnection.getConnexion();
            Statement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                statement.addBatch(updateSQL);
                statement.executeBatch();
                connection.commit();
                setInfosMinistereAModifier(null);
                PrimeFaces.current().executeScript("PF('panelinfosministereloading').close()");
                PrimeFaces.current().executeScript("PF('panelinfosministererechargerlapage').toggle()");
            } catch (SQLException e) {
                PrimeFaces.current().executeScript("PF('panelmodifinfosministere').toggle()");
                PrimeFaces.current().executeScript("PF('panelinfosministereloading').close()");
                e.printStackTrace();
            }
        }
    }

    public String getNomEntite() {
        return nomEntite;
    }

    public void setNomEntite(String nomEntite) {
        this.nomEntite = nomEntite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getPhraseListeDesUtisateurs() {
        return phraseListeDesUtilisateurs;
    }

    public void setPhraseListeDesUtisateurs(String phraseListeDesUtisateurs) {
        this.phraseListeDesUtilisateurs = phraseListeDesUtisateurs;
    }

    public String getPhraseEtatCompteUser() {
        return phraseEtatCompteUser;
    }

    public String getInfosMinistereAModifier() {
        return infosMinistereAModifier;
    }

    public void setInfosMinistereAModifier(String infosMinistereAModifier) {
        this.infosMinistereAModifier = infosMinistereAModifier;
    }

    public void setPhraseEtatCompteUser(String phraseEtatCompteUser) {
        this.phraseEtatCompteUser = phraseEtatCompteUser;
    }

    public String getUserNomTemp() {
        return userNomTemp;
    }

    public void setUserNomTemp(String userNomTemp) {
        this.userNomTemp = userNomTemp;
    }

    public String getUserPrenomTemp() {
        return userPrenomTemp;
    }

    public void setUserPrenomTemp(String userPrenomTemp) {
        this.userPrenomTemp = userPrenomTemp;
    }

    public String getUserEmailTemp() {
        return userEmailTemp;
    }

    public void setUserEmailTemp(String userEmailTemp) {
        this.userEmailTemp = userEmailTemp;
    }

    public String getUserTelTemp() {
        return userTelTemp;
    }

    public void setUserTelTemp(String userTelTemp) {
        this.userTelTemp = userTelTemp;
    }

    public String getUserFonctionTemp() {
        return userFonctionTemp;
    }

    public void setUserFonctionTemp(String userFonctionTemp) {
        this.userFonctionTemp = userFonctionTemp;
    }

    public String getUserDirectionTemp() {
        return userDirectionTemp;
    }

    public void setUserDirectionTemp(String userDirectionTemp) {
        this.userDirectionTemp = userDirectionTemp;
    }

    public String getUserServiceTemp() {
        return userServiceTemp;
    }

    public void setUserServiceTemp(String userServiceTemp) {
        this.userServiceTemp = userServiceTemp;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
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

    public String getUserDirectionPourAjoutTemp() {
        return userDirectionPourAjoutTemp;
    }

    public void setUserDirectionPourAjoutTemp(String userDirectionPourAjoutTemp) {
        this.userDirectionPourAjoutTemp = userDirectionPourAjoutTemp;
    }

    public String getUserFonctionPourAjoutTemp() {
        return userFonctionPourAjoutTemp;
    }

    public void setUserFonctionPourAjoutTemp(String userFonctionPourAjoutTemp) {
        this.userFonctionPourAjoutTemp = userFonctionPourAjoutTemp;
    }

    public String getUserNomPourAjoutTemp() {
        return userNomPourAjoutTemp;
    }

    public void setUserNomPourAjoutTemp(String userNomPourAjoutTemp) {
        this.userNomPourAjoutTemp = userNomPourAjoutTemp;
    }

    public String getUserPrenomPourAjoutTemp() {
        return userPrenomPourAjoutTemp;
    }

    public void setUserPrenomPourAjoutTemp(String userPrenomPourAjoutTemp) {
        this.userPrenomPourAjoutTemp = userPrenomPourAjoutTemp;
    }

    public String getUserMailPourAjoutTemp() {
        return userMailPourAjoutTemp;
    }

    public void setUserMailPourAjoutTemp(String userMailPourAjoutTemp) {
        this.userMailPourAjoutTemp = userMailPourAjoutTemp;
    }

    public String getUserTelPourAjoutTemp() {
        return userTelPourAjoutTemp;
    }

    public void setUserTelPourAjoutTemp(String userTelPourAjoutTemp) {
        this.userTelPourAjoutTemp = userTelPourAjoutTemp;
    }

    public String getNomMinistereTemp() {
        return nomMinistereTemp;
    }

    public void setNomMinistereTemp(String nomMinistereTemp) {
        this.nomMinistereTemp = nomMinistereTemp;
    }

    public String getAbreviationTemp() {
        return abreviationTemp;
    }

    public void setAbreviationTemp(String abreviationTemp) {
        this.abreviationTemp = abreviationTemp;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public List<Courrier> getTypeCourriers() {
        return typeCourriers;
    }

    public void setTypeCourriers(List<Courrier> typeCourriers) {
        this.typeCourriers = typeCourriers;
    }

    public String getTypeDeCourrier() {
        return typeDeCourrier;
    }

    public void setTypeDeCourrier(String typeDeCourrier) {
        this.typeDeCourrier = typeDeCourrier;
    }
}
