package bean;

import databaseManager.DataBaseQueries;
import databaseManager.DatabaseConnection;
import model.Etape;
import org.primefaces.PrimeFaces;
import sessionManager.SessionUtils;
import variables.TypeDePersonne;

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

    @PostConstruct
    public void initialisation(){
        etape = new Etape();
    }

    public void recupererLaListeDeMesTaches(){
        HttpSession session = SessionUtils.getSession();
        String idUser = (String) session.getAttribute( "idUser");
        isResponsable = (Boolean) session.getAttribute("isResponsable");
        if(isResponsable){
            titreDeLaPage = "Liste des taches que vous avez crées";
            etape.setListeDeMesTaches(DataBaseQueries.recupererLesTachesCreesParUnUser(idUser));
        }else{
            titreDeLaPage = "Liste de mes taches";
            etape.setListeDeMesTaches(DataBaseQueries.recupererToutesLesTachesDUnAgent(idUser));
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
