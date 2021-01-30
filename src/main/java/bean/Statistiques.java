package bean;

import databaseManager.DataBaseQueries;
import databaseManager.StatistiquesQueries;
import dateAndTime.DateUtils;
import functions.GraphicsManager;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SessionScoped
@Named
public class Statistiques implements Serializable {

    private static final long serialVersionUID = -5729086590993362739L;
    private HorizontalBarChartModel hbarModel;
    private PieChartModel chartCourriersParConfidentialite;
    private DonutChartModel donutChartCourriersParPrioriteDuJour;
    private DonutChartModel donutChartCourriersConfidentielDuJour;
    private org.primefaces.model.charts.bar.BarChartModel barModelCourrierParJourDeLaSemaine;
    private DonutChartModel donutChartModelParUrgenceDeLaSemaine;
    private DonutChartModel donutChartModelCourriersEntrantEtSortantDeLaSemaine;
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
        hbarModel = new HorizontalBarChartModel();
        barModelEnvoyes.setTitle("Nombre de courriers envoyés par directions");
        barModelRecus.setTitle("Nombre de courriers reçus par directions");
        barModelRecus.setSeriesColors("53A9F1");
        barModelEnvoyes.setSeriesColors("6FBC6F");
        barModelTypeDeCourrier.setSeriesColors("E6B332");
        barModelRecus.setShowDatatip(false);
        barModelTypeDeCourrier.setShowDatatip(false);
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
        creerGraphiqueCourriersTraitesParJour();
        creerGraphiqueCourriersParPrioriteDuJour();
        creerGraphiqueCourriersConfidentielParJour();
    }

    private void creerGraphiqueCourriersTraitesParJour(){
        ChartData dataHorizontalBar = new ChartData();
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
       // hbarModel.setExtender("skinBar");
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierRecusDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierEnvoyesDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDuJour()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDuJour()))),values);

        List<String> bgColor = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgba(83, 169,241, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(113, 199, 170, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(205, 179, 138, 0.7)",bgColor);

        List<String> borderColor = new ArrayList<>();
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Nbre de courriers reçus",labels);
        remplirListeLabelsGraphique("Nbre de courriers envoyés",labels);
        remplirListeLabelsGraphique("Total courriers traités",labels);

        BarChartOptions options = new BarChartOptions();
        GraphicsManager.creerGraphiqueBarHorizontal(hbarModel, hbarDataSet,values,bgColor,2,borderColor,labels,dataHorizontalBar)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphique(options,false,true,"",false));
    }

    private void creerGraphiqueCourriersParPrioriteDuJour() {
        donutChartCourriersParPrioriteDuJour = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierUrgentDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierPasUrgentDuJour)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgb(255, 205, 86)",bgColors);
        remplirListeBackgroundColorGraphique("rgb(54, 162, 235)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers urgents",labels);
        remplirListeLabelsGraphique("Courriers normaux",labels);

        GraphicsManager.creerGraphiqueDonut( donutChartCourriersParPrioriteDuJour,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueCourriersConfidentielParJour() {

        donutChartCourriersConfidentielDuJour = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();

        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierConfidentielDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(DataBaseQueries.nombreCourrierPasConfidentielDuJour)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgba(197, 0, 10, 0.7)",bgColors);
        remplirListeBackgroundColorGraphique("rgba(75, 192, 192, 0.7)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers confidentiels",labels);
        remplirListeLabelsGraphique("Courriers normaux",labels);

        GraphicsManager.creerGraphiqueDonut(donutChartCourriersConfidentielDuJour,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueCourriersParJourDeLaSemaine(){

        barModelCourrierParJourDeLaSemaine = new org.primefaces.model.charts.bar.BarChartModel();
        ChartData data = new ChartData();
        data.setLabels(null);
        BarChartDataSet barDataSet = new BarChartDataSet();
       // barModelCourrierParJourDeLaSemaine.setExtender("skinBar");
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierLundiDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierMardiDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierMercrediDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierJeudiDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierVendrediDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierSamediDeLaSemaine)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierDimancheDeLaSemaine)),values);

        List<String> bgColor = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgba(255, 99, 132, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(255, 159, 64, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(255, 205, 86, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(75, 192, 192, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(54, 162, 235, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(153, 102, 255, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(201, 203, 207, 0.7)",bgColor);

        List<String> borderColor = new ArrayList<>();
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Lundi",labels);
        remplirListeLabelsGraphique("Mardi",labels);
        remplirListeLabelsGraphique("Mercredi",labels);
        remplirListeLabelsGraphique("Jeudi",labels);
        remplirListeLabelsGraphique("Vendredi",labels);
        remplirListeLabelsGraphique("Samedi",labels);
        remplirListeLabelsGraphique("Dimanche",labels);

        BarChartOptions options = new BarChartOptions();
        barModelCourrierParJourDeLaSemaine.setOptions(options);
        GraphicsManager.creerGraphiqueBarVertical(barModelCourrierParJourDeLaSemaine, barDataSet,values,bgColor,2,borderColor,labels,data)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphiqueVertical(options,true,true,"Nombre de courriers traités par jours",
                        "top","bold","#2980B9",false));

    }

    private void creerGraphiqueCourriersParPrioriteParSemaine() {
        donutChartModelParUrgenceDeLaSemaine = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDeLaSemaine)),values);
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierPasUrgentDeLaSemaine)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgb(255, 205, 86)",bgColors);
        remplirListeBackgroundColorGraphique("rgb(54, 162, 235)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers urgents",labels);
        remplirListeLabelsGraphique("Courriers normaux",labels);

        GraphicsManager.creerGraphiqueDonut( donutChartModelParUrgenceDeLaSemaine,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueCourriersEntrantEtSortantParSemaine() {
        donutChartModelCourriersEntrantEtSortantDeLaSemaine = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierRecusDeLaSemaine)),values);
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDeLaSemaine)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgb(83, 169,241)",bgColors);
        remplirListeBackgroundColorGraphique("rgb(113, 199, 170)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers reçus",labels);
        remplirListeLabelsGraphique("Courriers envoyés",labels);

        GraphicsManager.creerGraphiqueDonut( donutChartModelCourriersEntrantEtSortantDeLaSemaine,dataSet,values,bgColors,labels,data);
    }

    public void recupererStatistiquesDeLaSemaine(){
        DateUtils.recupererLePremierEtLeDernierJourDelaSemaine();
        DateUtils.recupererTousLesMoisDeLAnnee();
        DateUtils.recupererTousLesJoursDeLaSemaineEnCours();
        HttpSession session = SessionUtils.getSession();
        String idDirection = (String) session.getAttribute("idDirectionUser");
        StatistiquesQueries.calculerLesStatistiquesDeLaSemainesPourUneDirection(idDirection);

        statistiques.setDebutDeSemaine(DateUtils.premierJourDeLaSemaine);
        statistiques.setFinDeSemaine(DateUtils.dernierJourDeLaSemaine);
        statistiques.setNombreDeCourrierConfidentielDeLaSemaine(String.valueOf(StatistiquesQueries.nombreCourrierConfidentielDeLaSemaine));
        statistiques.setNombreDeCourrierUrgentDeLaSemaine(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDeLaSemaine));
        statistiques.setNombreDeCourrierEnvoyesDeLaSemaine(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDeLaSemaine));
        statistiques.setNombreDeCourrierRecusDeLaSemaine(String.valueOf(StatistiquesQueries.nombreCourrierRecusDeLaSemaine));
        totalCourrierTraitesParSemaine = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDeLaSemaine()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDeLaSemaine()));

        creerGraphiqueCourriersParJourDeLaSemaine();
        creerGraphiqueCourriersParPrioriteParSemaine();
        creerGraphiqueCourriersEntrantEtSortantParSemaine();
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
        StatistiquesQueries.recupererLeNombreDeCourrierTraitesParMoisPourLAnneeEnCours(nomDirection);
        StatistiquesQueries.recupererLeNombreDeCourrierRecusParDirection(nomDirection);
        StatistiquesQueries.recupererLeNombreDeCourrierEnvoyesParDirection(nomDirection);
        StatistiquesQueries.calculDuNombreDeCourrierTraitesParPrioriteEtParConfidentielaiteLeMoisCourant(nomDirection);

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

    private void remplirListeBackgroundColorGraphique( String couleur, List<String> bgColor){
        bgColor.add(couleur);
    }

    private void remplirListeBorderColorGraphique( String couleur, List<String> borderColor){
        borderColor.add(couleur);
    }

    private void remplirListeLabelsGraphique( String label, List<String> labelsListe){
        labelsListe.add(label);
    }

    private void remplirValeursGraphique( int valeurs, List<Number> valeursListe){
        valeursListe.add(valeurs);
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

    public HorizontalBarChartModel getHbarModel() {
        return hbarModel;
    }

    public void setHbarModel(HorizontalBarChartModel hbarModel) {
        this.hbarModel = hbarModel;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public PieChartModel getChartCourriersParConfidentialite() {
        return chartCourriersParConfidentialite;
    }

    public void setChartCourriersParConfidentialite(PieChartModel chartCourriersParConfidentialite) {
        this.chartCourriersParConfidentialite = chartCourriersParConfidentialite;
    }

    public DonutChartModel getDonutChartCourriersParPrioriteDuJour() {
        return donutChartCourriersParPrioriteDuJour;
    }

    public void setDonutChartCourriersParPrioriteDuJour(DonutChartModel donutChartCourriersParPrioriteDuJour) {
        this.donutChartCourriersParPrioriteDuJour = donutChartCourriersParPrioriteDuJour;
    }

    public org.primefaces.model.charts.bar.BarChartModel getBarModelCourrierParJourDeLaSemaine() {
        return barModelCourrierParJourDeLaSemaine;
    }

    public void setBarModelCourrierParJourDeLaSemaine(org.primefaces.model.charts.bar.BarChartModel barModelCourrierParJourDeLaSemaine) {
        this.barModelCourrierParJourDeLaSemaine = barModelCourrierParJourDeLaSemaine;
    }

    public DonutChartModel getDonutChartModelParUrgenceDeLaSemaine() {
        return donutChartModelParUrgenceDeLaSemaine;
    }

    public void setDonutChartModelParUrgenceDeLaSemaine(DonutChartModel donutChartModelParUrgenceDeLaSemaine) {
        this.donutChartModelParUrgenceDeLaSemaine = donutChartModelParUrgenceDeLaSemaine;
    }

    public DonutChartModel getDonutChartModelCourriersEntrantEtSortantDeLaSemaine() {
        return donutChartModelCourriersEntrantEtSortantDeLaSemaine;
    }

    public void setDonutChartModelCourriersEntrantEtSortantDeLaSemaine(DonutChartModel donutChartModelCourriersEntrantEtSortantDeLaSemaine) {
        this.donutChartModelCourriersEntrantEtSortantDeLaSemaine = donutChartModelCourriersEntrantEtSortantDeLaSemaine;
    }

    public DonutChartModel getDonutChartCourriersConfidentielDuJour() {
        return donutChartCourriersConfidentielDuJour;
    }

    public void setDonutChartCourriersConfidentielDuJour(DonutChartModel donutChartCourriersConfidentielDuJour) {
        this.donutChartCourriersConfidentielDuJour = donutChartCourriersConfidentielDuJour;
    }
}
