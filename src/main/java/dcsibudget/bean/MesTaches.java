package dcsibudget.bean;

import dcsibudget.databaseManager.DataBaseQueries;
import dcsibudget.databaseManager.DatabaseConnection;
import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.messages.FacesMessages;
import dcsibudget.model.Etape;
import dcsibudget.model.User;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import dcsibudget.sessionManager.SessionUtils;
import dcsibudget.variables.TypeDePersonne;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SessionScoped
@Named
public class MesTaches implements Serializable {

    private static final long serialVersionUID = -684928672440884703L;
    private Etape etape;
    private Integer first = 0;
    private Integer rowsPerPage = 15;
    private String titreDeLaPage;
    private boolean isResponsable = false;
    private String phrase;
    private String idAgentAffecteAUneTache = null;
    private List<String> mesAgentsListe = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Etape> mesTachesSauvegardeListe = new ArrayList<>();

    @PostConstruct
    public void initialisation(){
        etape = new Etape();
    }

    public void recupererLaListeDeMesTaches(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            titreDeLaPage = PropertiesFilesReader.mapTitreDesPages.get("tachesQueVousAvezCrees");
            etape.setListeDeMesTaches(DataBaseQueries.recupererLesTachesCreesParUnUser(idUser));
        }else{
            titreDeLaPage = PropertiesFilesReader.mapTitreDesPages.get("mesTaches");;
            etape.setListeDeMesTaches(DataBaseQueries.recupererToutesLesTachesDUnAgent(idUser));
        }
        mesTachesSauvegardeListe.clear();
        mesTachesSauvegardeListe.addAll(etape.getListeDeMesTaches());
    }

    public List<String> recupererLesAgentsDUneDirectionALaSaisie(String query) {
        userList.clear();
        mesAgentsListe.clear();
        HttpSession session = SessionUtils.getSession();
        String direction = (String) session.getAttribute("directionUser");

        String idTypeDePersonne = DataBaseQueries.recupererIdTypeDePersonneParTitre(TypeDePersonne.agentDuMinistere);
        String requeteAgentSQL = "select * from `personne` inner join `direction` on personne.id_direction = direction.id_direction inner join profil on personne.id_profil = profil.id_profil inner join fonction on personne.id_fonction = fonction.id_fonction where nom_direction = '"+direction+"' and fk_type_personne = '"+idTypeDePersonne+"';";
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(requeteAgentSQL);
            while ( resultSet.next()) {
                userList.add(new User(
                        resultSet.getString("id_personne"),
                        resultSet.getString("nom") + " "+resultSet.getString("prenom"),
                        resultSet.getString("titre_fonction")));
            }

            for (int i = 0; i <  userList.size(); i++){
                mesAgentsListe.add(  userList.get(i).getUserName()+" ("+ userList.get(i).getUserFonction()+")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if ( resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        return  mesAgentsListe;
    }

    public void recupererIdDeLAgentChoisi(SelectEvent event) {
        for(int i = 0; i < userList.size(); i++){
            if (userList.get(i).getUserName().contains(event.getObject().toString().substring(0,event.getObject().toString().indexOf("(") - 1))){
                idAgentAffecteAUneTache = userList.get(i).getUserId();
            }
        }
    }

    public void faireUneRechercheAvanceeParAgent(){
        if(idAgentAffecteAUneTache != null){
            etape.getListeDeMesTaches().clear();
            etape.setListeDeMesTaches( DataBaseQueries.recupererToutesLesTachesDUnAgent(idAgentAffecteAUneTache));
            etape.setActeur(null);
            gestionDeLAffichageDesBoutonsDeRecherche();
        }else{
            FacesMessages.errorMessage("messagesRechercheAgent","Erreur","Une erreur s'est produite");
        }

    }

    public String voirUneTache(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int etapeId = Integer.valueOf(params.get("etapeId"));
        String natureEtape = params.get("natureEtape");
        HttpSession session = SessionUtils.getSession();
        session.setAttribute("natureEtape",natureEtape);
        return DataBaseQueries.repondreAUneTache(String.valueOf(etapeId));
    }

    private void gestionDeLAffichageDesBoutonsDeRecherche(){
        PrimeFaces.current().executeScript("PF('dialogueRechercheParAgent').hide()");
        PrimeFaces.current().executeScript("afficherBoutonAnnulerRecherche()");
    }

    public void annulerUneRechercheAvancee(){
        etape.getListeDeMesTaches().clear();
        etape.setListeDeMesTaches(mesTachesSauvegardeListe);
        PrimeFaces.current().executeScript("afficherBoutonFaireUneRecherche()");
    }

    public void recupererLeCreateurDUneTache(){
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idTache = String.valueOf(params.get("etapeId"));
        HttpSession session = SessionUtils.getSession();
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        String requeteSQL = null;
        if(isResponsable){
            phrase = "Tache déstinée à : ";
            requeteSQL="select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.receveurTache +"' order by etape.id_etape desc";
        }else{
            phrase = "Tache crée par : ";
            requeteSQL = "select nom,prenom from `etape` inner join `correspondance_personne_etape` on etape.id_etape = correspondance_personne_etape.id_etape inner join `personne` on correspondance_personne_etape.id_personne = personne.id_personne where etape.id_etape = '"+idTache+"' and correspondance_personne_etape.role_agent = '"+ TypeDePersonne.affecteurTache +"' order by etape.id_etape desc";
        }
        Connection connection = DatabaseConnection.getConnexion();
        ResultSet resultSet = null;
        try{
            resultSet = connection.createStatement().executeQuery(requeteSQL);
            if (resultSet.next()){
                etape.setCreateurDUneTache( resultSet.getString("nom")+" "+ resultSet.getString("prenom"));
            }
            PrimeFaces.current().executeScript("PF('dlgDetailsTache').show()");
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("messagerreurtache",new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur","Une erreur avec la base de données s'est produitre"));
        }
    }

    public Etape getEtape() {
        return etape;
    }

    public void setEtape(Etape etape) {
        this.etape = etape;
    }

    public String getTitreDeLaPage() {
        return titreDeLaPage;
    }

    public void setTitreDeLaPage(String titreDeLaPage) {
        this.titreDeLaPage = titreDeLaPage;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
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
}
