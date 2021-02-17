package bean;

import databaseManager.DatabaseConnection;
import databaseManager.DossiersQueries;
import fileManager.PropertiesFilesReader;
import messages.FacesMessages;
import model.Courrier;
import model.Dossier;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;


@SessionScoped
@Named
public class DetailDUnDossier implements Serializable {

    private static final long serialVersionUID = -4156275961824185238L;
    private Courrier courrier;
    private Dossier dossier;
    private String nomDossierEnCours;
    private int nombreDeCourriers = 0;
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private String messageSuppressionDossier;

    @PostConstruct
    public void initialisation(){
        courrier = new Courrier();
        dossier = new Dossier();
        PropertiesFilesReader.lireLeFichierDesMessages("messageApplication.properties","indicationSuppressionDossier");
        messageSuppressionDossier = PropertiesFilesReader.mapMessageApplication.get("indicationSuppressionDossier");
    }

    public void recupererLesDossiers(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        dossier.setDossierList(DossiersQueries.recupererLesDossiersDUnUtilisateur(idUser));
    }

    public void recupererLalisteDesCourriersDansUnDossier(){
        HttpSession session = SessionUtils.getSession();
        String idPersonne = (String) session.getAttribute("idUser");
        String idDossier = (String) session.getAttribute("idDossier");
        courrier.setListeDesCourriersDansUnDossier(DossiersQueries.recupererLesCourriersDansUnDossiersDUnUtilisateur(idPersonne,idDossier));
        nomDossierEnCours = DossiersQueries.recupererLeNomDUnDossierParSonId(idDossier);
        nombreDeCourriers = DossiersQueries.nombreDeCourrierDansUnDossier;
    }

    public void renommerUnDossier(){
        if(dossier.getNomDossier().isEmpty()){
            FacesContext.getCurrentInstance().addMessage("messagerenommerdossier", new FacesMessage(FacesMessage.SEVERITY_INFO, "Attention", "Vous devez renseigner le nom du dossier !!!"));
        }else{
            boolean trouve = false;
            for(int a =0; a < dossier.getDossierList().size(); a++){
                if(dossier.getNomDossier().equalsIgnoreCase(dossier.getDossierList().get(a).getNomDossier())){
                    trouve = true;
                    break;
                }
            }
            if(trouve){
                FacesContext.getCurrentInstance().addMessage("messagerenommerdossier", new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", "Vous avez déja un dossier de ce nom!!!"));
            }else{

                HttpSession session = SessionUtils.getSession();
                String idDossier = (String) session.getAttribute("idDossier");
                DossiersQueries.renommerUnDossier(idDossier,dossier.getNomDossier().trim().replaceAll("'"," "),"messagerenommerdossier");
                dossier.setNomDossier(null);
            }
        }

    }

    public void supprimerUnDossier(){

        HttpSession session = SessionUtils.getSession();
        String idDossier = (String) session.getAttribute("idDossier");
        Connection connection = DatabaseConnection.getConnexion();
        String supprimerDossierSQL = "DELETE FROM `dossier` WHERE `dossier`.`id_dossier` = '"+idDossier+"' ; ";
        String supprimerTousLesCourriersDansUnDossier = null;
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.addBatch(supprimerDossierSQL);
            if(nombreDeCourriers > 0 ){
                for(int a = 0; a < nombreDeCourriers; a++){
                    supprimerTousLesCourriersDansUnDossier = "DELETE FROM correspondance_dossier_courrier where correspondance_dossier_courrier.id_courrier = '" +courrier.getListeDesCourriersDansUnDossier().get(a).getIdCourrier()+"' and correspondance_dossier_courrier.id_dossier = '"+idDossier+"'";
                    statement.addBatch(supprimerTousLesCourriersDansUnDossier);
                }
            }
            statement.executeBatch();
            connection.commit();
            PrimeFaces.current().executeScript("PF('dialogueConfirmationSuppression').hide()");
            PrimeFaces.current().executeScript("PF('dialogueRetourAuxDossiers').show()");
        } catch (SQLException e) {
            FacesMessages.errorMessage("messagesupprimerdossier","Erreur", "Une erreur s'est produite!!!");
            e.printStackTrace();
        }finally {
            if ( statement != null) {
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


    public String retourALaListeDesDossiers(){
        return "mesdossiers?faces-redirect=true";
    }

    public String voirLesDetailsDuCourrier(){

        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idCourrier = (params.get("courrierId"));
        String alfrescoId = (params.get("alfrescoId"));
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("courrierId",idCourrier);
        session.setAttribute("alfrescoId",alfrescoId);
        return "detailduncourrierdansundossier.xhtml?faces-redirect=true";
    }

    /***Getter and setter**/


    public String getNomDossierEnCours() {
        return nomDossierEnCours;
    }

    public void setNomDossierEnCours(String nomDossierEnCours) {
        this.nomDossierEnCours = nomDossierEnCours;
    }
    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public String getMessageSuppressionDossier() {
        return messageSuppressionDossier;
    }

    public void setMessageSuppressionDossier(String messageSuppressionDossier) {
        this.messageSuppressionDossier = messageSuppressionDossier;
    }
}
