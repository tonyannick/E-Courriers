package bean;

import database.DataBaseQueries;
import database.StatistiquesQueries;
import dateAndTime.DateUtils;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

@SessionScoped
@Named
public class Statistiques implements Serializable {

    private static final long serialVersionUID = -5729086590993362739L;
    private BarChartModel barModelRecus;
    private BarChartModel barModelEnvoyes;
    private BarChartModel barModelTypeDeCourrier;
    private model.Statistiques statistiques;
    private String totalCourrierTraitesParJour;
    private String totalCourrierTraitesParSemaine;
    private String totalCourrierTraitesParMois;
    private int nombreDeCourrierTraitesJanvier;
    private int nombreDeCourrierTraitesFevrier;
    private int nombreDeCourrierTraitesMars;
    private int nombreDeCourrierTraitesAvril;
    private int nombreDeCourrierTraitesMai;
    private int nombreDeCourrierTraitesJuin;
    private int nombreDeCourrierTraitesJuillet;
    private int nombreDeCourrierTraitesAout;
    private int nombreDeCourrierTraitesSeptembre;
    private int nombreDeCourrierTraitesOctobre;
    private int nombreDeCourrierTraitesNovembre;
    private int nombreDeCourrierTraitesDecembre;
    private int nombreDeCourrierInterneDuMois;
    private int nombreDeCourrierExterneDuMois;


    @PostConstruct
    public void initialisation(){
        statistiques = new model.Statistiques();
        barModelRecus = new BarChartModel();
        barModelEnvoyes = new BarChartModel();
        barModelTypeDeCourrier = new BarChartModel();
        barModelEnvoyes.setTitle("Nombre de courriers envoyés par directions");
        barModelRecus.setTitle("Nombre de courriers reçus par directions");
        barModelRecus.setSeriesColors("53A9F1");
        barModelEnvoyes.setSeriesColors("6FBC6F");
        barModelRecus.setShowDatatip(false);
        barModelEnvoyes.setShowDatatip(false);
        barModelEnvoyes.setAnimate(true);
        barModelRecus.setAnimate(true);
        barModelEnvoyes.setMouseoverHighlight(true);
    }


    public void recupererStatistiquesDuJour(){
        DataBaseQueries.recupererLesStatistiquesPourLaPageDAccueil();
        statistiques.setNombreDeCourrierRecusDuJour(String.valueOf(DataBaseQueries.nombreCourrierRecusDuJour));
        statistiques.setNombreDeCourrierEnvoyesDuJour(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDuJour));
        statistiques.setNombreDeCourrierUrgentDuJour(String.valueOf(DataBaseQueries.nombreCourrierUrgentDuJour));
        statistiques.setNombreDeCourrierConfidentielDuJour(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDuJour));
        totalCourrierTraitesParJour = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDuJour()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDuJour()));
        nombreDeCourrierInterneDuMois = DataBaseQueries.nombreCourrierInterneDuMois;
        nombreDeCourrierExterneDuMois = DataBaseQueries.nombreCourrierExterneDuMois;
    }

    public void recupererStatistiquesDeLaSemaine(){
        DateUtils.recupererLePremierEtLeDernierJourDelaSemaine();
        DateUtils.recupererTousLesMoisDeLAnnee();
        statistiques.setDebutDeSemaine(DateUtils.premierJourDeLaSemaine);
        statistiques.setFinDeSemaine(DateUtils.dernierJourDeLaSemaine);
        statistiques.setNombreDeCourrierConfidentielDeLaSemaine(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDeLaSemaine));
        statistiques.setNombreDeCourrierUrgentDeLaSemaine(String.valueOf(DataBaseQueries.nombreCourrierUrgentDeLaSemaine));
        statistiques.setNombreDeCourrierEnvoyesDeLaSemaine(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDeLaSemaine));
        statistiques.setNombreDeCourrierRecusDeLaSemaine(String.valueOf(DataBaseQueries.nombreCourrierRecusDeLaSemaine));
        totalCourrierTraitesParSemaine = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDeLaSemaine()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDeLaSemaine()));
    }

    public void recupererStatistiquesDuMoisEnCours(){
        statistiques.setNombreDeCourrierConfidentielDuMois(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDuMois));
        statistiques.setNombreDeCourrierUrgentDuMois(String.valueOf(DataBaseQueries.nombreCourrierUrgentDuMois));
        statistiques.setNombreDeCourrierEnvoyesDuMois(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDuMois));
        statistiques.setNombreDeCourrierRecusDuMois(String.valueOf(DataBaseQueries.nombreCourrierRecusDuMois));
        totalCourrierTraitesParMois = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDuMois()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDuMois()));
    }

    public void recupererLesStatistiquesParMois(){
        HttpSession session = SessionUtils.getSession();
        String nomDirection = session.getAttribute("directionUser").toString();
        String idDirection = session.getAttribute("idDirectionUser").toString();
        StatistiquesQueries.recupererLeNombreDeCourrierTraitesParDirectionParMoisPourLAnneeEnCours(nomDirection);
        StatistiquesQueries.recupererLeNombreDeCourrierRecusParDirection(nomDirection);
        StatistiquesQueries.recupererLeNombreDeCourrierEnvoyesParDirection(nomDirection);
        //DataBaseQueries.calculerLesStatistiquesDesCourriersTraitesParTypesDeCourrierDuMoisEnCours(idDirection);

        StatistiquesQueries.calculerLesStatistiquesDesCourriersTraitesParTypesDeCourrierDuMoisEnCours(idDirection);
        nombreDeCourrierTraitesJanvier = StatistiquesQueries.nombreDeCourrierJanvier;
        nombreDeCourrierTraitesFevrier = StatistiquesQueries.nombreDeCourrierFevrier;
        nombreDeCourrierTraitesMars = StatistiquesQueries.nombreDeCourrierMars;
        nombreDeCourrierTraitesAvril = StatistiquesQueries.nombreDeCourrierAvril;
        nombreDeCourrierTraitesMai = StatistiquesQueries.nombreDeCourrierMai;
        nombreDeCourrierTraitesJuin = StatistiquesQueries.nombreDeCourrierJuin;
        nombreDeCourrierTraitesJuillet = StatistiquesQueries.nombreDeCourrierJuillet;
        nombreDeCourrierTraitesAout = StatistiquesQueries.nombreDeCourrierAout;
        nombreDeCourrierTraitesSeptembre = StatistiquesQueries.nombreDeCourrierSeptembre;
        nombreDeCourrierTraitesOctobre = StatistiquesQueries.nombreDeCourrierOctobre;
        nombreDeCourrierTraitesNovembre = StatistiquesQueries.nombreDeCourrierNovembre;
        nombreDeCourrierTraitesDecembre = StatistiquesQueries.nombreDeCourrierDecembre;
        barModelRecus.clear();
        barModelEnvoyes.clear();
        barModelTypeDeCourrier.clear();

        ChartSeries directionRecusStats = new ChartSeries();
        ChartSeries directionEnvoyesStats = new ChartSeries();
        ChartSeries courrierPaType = new ChartSeries();

        StatistiquesQueries.myMapCourrierRecusParDirection.forEach((key,value) -> directionRecusStats.set(key,value));
        barModelRecus.addSeries(directionRecusStats);
        Axis xAxisRecus =  barModelRecus.getAxis(AxisType.X);
        xAxisRecus.setLabel("Directions du Ministère");

        StatistiquesQueries.myMapCourrierEnvoyesParDirection.forEach((key,value) -> directionEnvoyesStats.set(key,value));
        barModelEnvoyes.addSeries(directionEnvoyesStats);
        Axis xAxisEnvoyes = barModelEnvoyes.getAxis(AxisType.X);
        xAxisEnvoyes.setLabel("Directions du Ministère");

        StatistiquesQueries.mapNombreCourrierParTypeDuMoisEnCours.forEach((key,value) -> courrierPaType.set(key,value));
        barModelTypeDeCourrier.addSeries(courrierPaType);
        Axis xAxisTypeCourrier = barModelTypeDeCourrier.getAxis(AxisType.X);
        xAxisTypeCourrier.setLabel("Courriers par types");

    }

    public void clickSurBarChat(ItemSelectEvent itemSelectEvent){
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected", "Item Index: " + itemSelectEvent.getItemIndex() +
                ", Series Index:" + itemSelectEvent.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage("toastBarChat", msg);
    }


    public BarChartModel getBarModelTypeDeCourrier() {
        return barModelTypeDeCourrier;
    }

    public void setBarModelTypeDeCourrier(BarChartModel barModelTypeDeCourrier) {
        this.barModelTypeDeCourrier = barModelTypeDeCourrier;
    }

    public BarChartModel getBarModel() {
        return  barModelRecus;
    }

    public void setBarModel(BarChartModel barModel) {
        this. barModelRecus = barModel;
    }

    public BarChartModel getBarModelEnvoyes() {
        return barModelEnvoyes;
    }

    public void setBarModelEnvoyes(BarChartModel barModelEnvoyes) {
        this.barModelEnvoyes = barModelEnvoyes;
    }

    public model.Statistiques getStatistiques() {
        return statistiques;
    }

    public void setStatistiques(model.Statistiques statistiques) {
        this.statistiques = statistiques;
    }

    public String getTotalCourrierTraitesParJour() {
        return totalCourrierTraitesParJour;
    }

    public void setTotalCourrierTraitesParJour(String totalCourrierTraitesParJour) {
        this.totalCourrierTraitesParJour = totalCourrierTraitesParJour;
    }

    public String getTotalCourrierTraitesParSemaine() {
        return totalCourrierTraitesParSemaine;
    }

    public void setTotalCourrierTraitesParSemaine(String totalCourrierTraitesParSemaine) {
        this.totalCourrierTraitesParSemaine = totalCourrierTraitesParSemaine;
    }

    public String getTotalCourrierTraitesParMois() {
        return totalCourrierTraitesParMois;
    }

    public void setTotalCourrierTraitesParMois(String totalCourrierTraitesParMois) {
        this.totalCourrierTraitesParMois = totalCourrierTraitesParMois;
    }

    public int getNombreDeCourrierTraitesJanvier() {
        return nombreDeCourrierTraitesJanvier;
    }

    public void setNombreDeCourrierTraitesJanvier(int nombreDeCourrierTraitesJanvier) {
        this.nombreDeCourrierTraitesJanvier = nombreDeCourrierTraitesJanvier;
    }

    public int getNombreDeCourrierTraitesFevrier() {
        return nombreDeCourrierTraitesFevrier;
    }

    public void setNombreDeCourrierTraitesFevrier(int nombreDeCourrierTraitesFevrier) {
        this.nombreDeCourrierTraitesFevrier = nombreDeCourrierTraitesFevrier;
    }

    public int getNombreDeCourrierTraitesMars() {
        return nombreDeCourrierTraitesMars;
    }

    public void setNombreDeCourrierTraitesMars(int nombreDeCourrierTraitesMars) {
        this.nombreDeCourrierTraitesMars = nombreDeCourrierTraitesMars;
    }

    public int getNombreDeCourrierTraitesAvril() {
        return nombreDeCourrierTraitesAvril;
    }

    public void setNombreDeCourrierTraitesAvril(int nombreDeCourrierTraitesAvril) {
        this.nombreDeCourrierTraitesAvril = nombreDeCourrierTraitesAvril;
    }

    public int getNombreDeCourrierTraitesMai() {
        return nombreDeCourrierTraitesMai;
    }

    public void setNombreDeCourrierTraitesMai(int nombreDeCourrierTraitesMai) {
        this.nombreDeCourrierTraitesMai = nombreDeCourrierTraitesMai;
    }

    public int getNombreDeCourrierTraitesJuin() {
        return nombreDeCourrierTraitesJuin;
    }

    public void setNombreDeCourrierTraitesJuin(int nombreDeCourrierTraitesJuin) {
        this.nombreDeCourrierTraitesJuin = nombreDeCourrierTraitesJuin;
    }

    public int getNombreDeCourrierTraitesJuillet() {
        return nombreDeCourrierTraitesJuillet;
    }

    public void setNombreDeCourrierTraitesJuillet(int nombreDeCourrierTraitesJuillet) {
        this.nombreDeCourrierTraitesJuillet = nombreDeCourrierTraitesJuillet;
    }

    public int getNombreDeCourrierTraitesAout() {
        return nombreDeCourrierTraitesAout;
    }

    public void setNombreDeCourrierTraitesAout(int nombreDeCourrierTraitesAout) {
        this.nombreDeCourrierTraitesAout = nombreDeCourrierTraitesAout;
    }

    public int getNombreDeCourrierTraitesSeptembre() {
        return nombreDeCourrierTraitesSeptembre;
    }

    public void setNombreDeCourrierTraitesSeptembre(int nombreDeCourrierTraitesSeptembre) {
        this.nombreDeCourrierTraitesSeptembre = nombreDeCourrierTraitesSeptembre;
    }

    public int getNombreDeCourrierTraitesOctobre() {
        return nombreDeCourrierTraitesOctobre;
    }

    public void setNombreDeCourrierTraitesOctobre(int nombreDeCourrierTraitesOctobre) {
        this.nombreDeCourrierTraitesOctobre = nombreDeCourrierTraitesOctobre;
    }

    public int getNombreDeCourrierTraitesNovembre() {
        return nombreDeCourrierTraitesNovembre;
    }

    public void setNombreDeCourrierTraitesNovembre(int nombreDeCourrierTraitesNovembre) {
        this.nombreDeCourrierTraitesNovembre = nombreDeCourrierTraitesNovembre;
    }

    public int getNombreDeCourrierTraitesDecembre() {
        return nombreDeCourrierTraitesDecembre;
    }

    public void setNombreDeCourrierTraitesDecembre(int nombreDeCourrierTraitesDecembre) {
        this.nombreDeCourrierTraitesDecembre = nombreDeCourrierTraitesDecembre;
    }

    public int getNombreDeCourrierInterneDuMois() {
        return nombreDeCourrierInterneDuMois;
    }

    public void setNombreDeCourrierInterneDuMois(int nombreDeCourrierInterneDuMois) {
        this.nombreDeCourrierInterneDuMois = nombreDeCourrierInterneDuMois;
    }

    public int getNombreDeCourrierExterneDuMois() {
        return nombreDeCourrierExterneDuMois;
    }

    public void setNombreDeCourrierExterneDuMois(int nombreDeCourrierExterneDuMois) {
        this.nombreDeCourrierExterneDuMois = nombreDeCourrierExterneDuMois;
    }
}
