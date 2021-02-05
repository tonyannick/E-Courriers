package bean;

import databaseManager.DataBaseQueries;
import databaseManager.StatistiquesQueries;
import dateAndTime.DateUtils;
import functions.ColorsRandomGenerator;
import functions.GraphicsManager;
import org.primefaces.model.charts.ChartData;

import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.pie.PieChartModel;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SessionScoped
@Named
public class Statistiques implements Serializable {

    private static final long serialVersionUID = -5729086590993362739L;
    private HorizontalBarChartModel hbarModel;
    private DonutChartModel donutChartCourriersParPrioriteDuJour;
    private DonutChartModel donutChartCourriersConfidentielDuJour;
    private BarChartModel barModelCourrierParJourDeLaSemaine;
    private DonutChartModel donutChartModelParUrgenceDeLaSemaine;
    private DonutChartModel donutChartModelParUrgenceDuMois;
    private DonutChartModel donutChartModelCourriersEntrantEtSortantDeLaSemaine;
    private DonutChartModel donutChartModelCourriersEntrantEtSortantDuMois;
    private BarChartModel barModelNombreDeCourrierParTypeDuMois;
    private BarChartModel barModelNombreDeCourrierParMoisPourLAnnee;
    private BarChartModel barModelNombreDeCourrierRecusParDirectionDuMois;
    private BarChartModel barModelNombreDeCourrierEnvoyesParDirectionDuMois;
    private model.Statistiques statistiques;
    private String totalCourrierTraitesParJour;
    private String totalCourrierTraitesParSemaine;
    private String totalCourrierTraitesParMois;

    @PostConstruct
    public void initialisation(){
        statistiques = new model.Statistiques();
        hbarModel = new HorizontalBarChartModel();
    }

    public void recupererStatistiquesDuJour(){
        StatistiquesQueries.recupererLesStatistiquesDuJour();
        statistiques.setNombreDeCourrierRecusDuJour(String.valueOf( StatistiquesQueries.nombreCourrierRecusDuJour));
        statistiques.setNombreDeCourrierEnvoyesDuJour(String.valueOf( StatistiquesQueries.nombreCourrierEnvoyesDuJour));
        statistiques.setNombreDeCourrierUrgentDuJour(String.valueOf( StatistiquesQueries.nombreCourrierUrgentDuJour));
        statistiques.setNombreDeCourrierConfidentielDuJour(String.valueOf( StatistiquesQueries.nombreCourrierConfidentielDuJour));
        totalCourrierTraitesParJour = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDuJour()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDuJour()));
        creerGraphiqueCourriersTraitesParJour();
        creerGraphiqueCourriersParPrioriteDuJour();
        creerGraphiqueCourriersConfidentielParJour();
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
        HttpSession session = SessionUtils.getSession();
        String nomDirection = session.getAttribute("directionUser").toString();
        String idDirection = session.getAttribute("idDirectionUser").toString();
        StatistiquesQueries.calculerLeNombreDeCourrierRecusParDirectionLeMoisCourant(nomDirection);
        StatistiquesQueries.calculerLeNombreDeCourrierEnvoyesParDirectionLeMoisCourant(nomDirection);
        StatistiquesQueries.calculerLesStatistiquesDuMoisEnCoursPourUneDirection(idDirection);
        StatistiquesQueries.calculerLesStatistiquesDesCourriersTraitesParTypesDeCourrierDuMoisEnCours(idDirection);
        creerGraphiqueCourriersParPrioriteDuMois();
        creerGraphiqueCourriersEntrantEtSortantDuMois();
        creerGraphiqueNombreDeCourriersParTypeLeMoisEnCours();
        creerGraphiqueNombreDeCourriersRecusParDirectionLeMoisEnCours();
        creerGraphiqueNombreDeCourriersEnvoyesParDirectionLeMoisEnCours();
        creerGraphiqueNombreDeCourriersParMoisPourLAnneeEnCours();
        statistiques.setNombreDeCourrierConfidentielDuMois(String.valueOf(StatistiquesQueries.nombreCourrierConfidentielDuMois));
        statistiques.setNombreDeCourrierUrgentDuMois(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDuMois));
        statistiques.setNombreDeCourrierEnvoyesDuMois(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDuMois));
        statistiques.setNombreDeCourrierRecusDuMois(String.valueOf(StatistiquesQueries.nombreCourrierRecusDuMois));
        totalCourrierTraitesParMois = String.valueOf(Integer.parseInt(statistiques.getNombreDeCourrierRecusDuMois()) + Integer.parseInt(statistiques.getNombreDeCourrierEnvoyesDuMois()));
    }

    public void recupererStatistiqueDeLAnneeEnCours(){
        HttpSession session = SessionUtils.getSession();
        String nomDirection = session.getAttribute("directionUser").toString();
        StatistiquesQueries.recupererLeNombreDeCourrierTraitesParMoisPourLAnneeEnCours(nomDirection);
    }

    private void creerGraphiqueCourriersTraitesParJour(){
        ChartData dataHorizontalBar = new ChartData();
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
       // hbarModel.setExtender("skinBar");
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierRecusDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDuJour)),values);
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
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierPasUrgentDuJour)),values);

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
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierConfidentielDuJour)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreCourrierPasConfidentielDuJour)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgba(197, 0, 10, 0.7)",bgColors);
        remplirListeBackgroundColorGraphique("rgba(75, 192, 192, 0.7)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers confidentiels",labels);
        remplirListeLabelsGraphique("Courriers normaux",labels);

        GraphicsManager.creerGraphiqueDonut(donutChartCourriersConfidentielDuJour,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueCourriersParJourDeLaSemaine(){

        barModelCourrierParJourDeLaSemaine = new BarChartModel();
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

    private void creerGraphiqueCourriersParPrioriteDuMois() {
        donutChartModelParUrgenceDuMois = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierUrgentDuMois)),values);
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierPasUrgentDuMois)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgb(255, 205, 86)",bgColors);
        remplirListeBackgroundColorGraphique("rgb(54, 162, 235)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers urgents",labels);
        remplirListeLabelsGraphique("Courriers normaux",labels);

        GraphicsManager.creerGraphiqueDonut( donutChartModelParUrgenceDuMois,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueCourriersEntrantEtSortantDuMois() {
        donutChartModelCourriersEntrantEtSortantDuMois = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierRecusDuMois)),values);
        remplirValeursGraphique(Integer.parseInt(String.valueOf(StatistiquesQueries.nombreCourrierEnvoyesDuMois)),values);

        List<String> bgColors = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgb(83, 169,241)",bgColors);
        remplirListeBackgroundColorGraphique("rgb(113, 199, 170)",bgColors);

        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Courriers reçus",labels);
        remplirListeLabelsGraphique("Courriers envoyés",labels);

        GraphicsManager.creerGraphiqueDonut( donutChartModelCourriersEntrantEtSortantDuMois,dataSet,values,bgColors,labels,data);
    }

    private void creerGraphiqueNombreDeCourriersParTypeLeMoisEnCours(){

        barModelNombreDeCourrierParTypeDuMois = new BarChartModel();
        ChartData data = new ChartData();
        data.setLabels(null);
        BarChartDataSet barDataSet = new BarChartDataSet();
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        ColorsRandomGenerator colorsRandomGenerator = new ColorsRandomGenerator();
        for (Map.Entry<String, Integer> entry : StatistiquesQueries.mapNombreCourrierParTypeDuMoisEnCours.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColor.add(colorsRandomGenerator.creerUneCouleurAleatoireAuFormatRGB());
            borderColor.add("rgb(255,255,255)");
        }

        BarChartOptions options = new BarChartOptions();
        barModelNombreDeCourrierParTypeDuMois.setOptions(options);
        GraphicsManager.creerGraphiqueBarVertical( barModelNombreDeCourrierParTypeDuMois, barDataSet,values,bgColor,2,borderColor,labels,data)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphiqueVertical(options,true,true,"Nombre de courriers traités par type",
                        "top","bold","#2980B9",false));

    }

    private void creerGraphiqueNombreDeCourriersRecusParDirectionLeMoisEnCours(){

        barModelNombreDeCourrierRecusParDirectionDuMois = new BarChartModel();
        ChartData data = new ChartData();
        data.setLabels(null);
        BarChartDataSet barDataSet = new BarChartDataSet();
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        ColorsRandomGenerator colorsRandomGenerator = new ColorsRandomGenerator();
        for (Map.Entry<String, Integer> entry : StatistiquesQueries.mapNombreCourrierRecusParDirectionDuMoisEnCours.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColor.add(colorsRandomGenerator.creerUneCouleurAleatoireAuFormatRGB());
            borderColor.add("rgb(255,255,255)");

        }

        BarChartOptions options = new BarChartOptions();
        barModelNombreDeCourrierRecusParDirectionDuMois.setOptions(options);
        GraphicsManager.creerGraphiqueBarVertical(  barModelNombreDeCourrierRecusParDirectionDuMois, barDataSet,values,bgColor,2,borderColor,labels,data)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphiqueVertical(options,true,true,"Nombre de courriers reçus par direction",
                        "top","bold","#2980B9",false));

    }

    private void creerGraphiqueNombreDeCourriersEnvoyesParDirectionLeMoisEnCours(){

        barModelNombreDeCourrierEnvoyesParDirectionDuMois = new BarChartModel();
        ChartData data = new ChartData();
        data.setLabels(null);
        BarChartDataSet barDataSet = new BarChartDataSet();
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();
        ColorsRandomGenerator colorsRandomGenerator = new ColorsRandomGenerator();

        for (Map.Entry<String, Integer> entry : StatistiquesQueries.mapNombreCourrierEnvoyesParDirectionDuMoisEnCours.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColor.add(colorsRandomGenerator.creerUneCouleurAleatoireAuFormatRGB());
            borderColor.add("rgb(255,255,255)");
        }

        BarChartOptions options = new BarChartOptions();
        barModelNombreDeCourrierEnvoyesParDirectionDuMois.setOptions(options);
        GraphicsManager.creerGraphiqueBarVertical(  barModelNombreDeCourrierEnvoyesParDirectionDuMois, barDataSet,values,bgColor,2,borderColor,labels,data)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphiqueVertical(options,true,true,"Nombre de courriers envoyés par direction",
                        "top","bold","#2980B9",false));

    }

    private void creerGraphiqueNombreDeCourriersParMoisPourLAnneeEnCours(){

        barModelNombreDeCourrierParMoisPourLAnnee = new BarChartModel();
        ChartData data = new ChartData();
        data.setLabels(null);
        BarChartDataSet barDataSet = new BarChartDataSet();

        List<String> bgColor = new ArrayList<>();
        remplirListeBackgroundColorGraphique("rgba(255, 99, 132, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(255, 159, 64, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(255, 205, 86, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(75, 192, 192, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(54, 162, 235, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(153, 102, 255, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(201, 203, 207, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(75, 203, 207, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(201, 20, 207, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(201, 203, 10, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(89, 203, 207, 0.7)",bgColor);
        remplirListeBackgroundColorGraphique("rgba(201, 58, 34, 0.7)",bgColor);


        List<String> borderColor = new ArrayList<>();
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);
        remplirListeBorderColorGraphique("rgb(255,255,255)",borderColor);


        List<String> labels = new ArrayList<>();
        remplirListeLabelsGraphique("Janvier",labels);
        remplirListeLabelsGraphique("Février",labels);
        remplirListeLabelsGraphique("Mars",labels);
        remplirListeLabelsGraphique("Avril",labels);
        remplirListeLabelsGraphique("Mai",labels);
        remplirListeLabelsGraphique("Juin",labels);
        remplirListeLabelsGraphique("Juillet",labels);
        remplirListeLabelsGraphique("Aout",labels);
        remplirListeLabelsGraphique("Septembre",labels);
        remplirListeLabelsGraphique("Octobre",labels);
        remplirListeLabelsGraphique("Novembre",labels);
        remplirListeLabelsGraphique("Décembre",labels);

        List<Number> values = new ArrayList<>();
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierJanvier)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierFevrier)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierMars)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierAvril)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierMai)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierJuin)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierJuillet)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierAout)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierSeptembre)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierOctobre)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierNovembre)),values);
        remplirValeursGraphique(Integer.valueOf(String.valueOf(StatistiquesQueries.nombreDeCourrierDecembre)),values);


        BarChartOptions options = new BarChartOptions();
        barModelNombreDeCourrierParMoisPourLAnnee.setOptions(options);
        GraphicsManager.creerGraphiqueBarVertical( barModelNombreDeCourrierParMoisPourLAnnee, barDataSet,values,bgColor,2,borderColor,labels,data)
                .setOptions(GraphicsManager.creerOptionsDAffichageDuGraphiqueVertical(options,true,true,"Nombre de courriers traités par mois de l'année en cours",
                        "top","bold","#2980B9",false));

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

    public HorizontalBarChartModel getHbarModel() {
        return hbarModel;
    }

    public void setHbarModel(HorizontalBarChartModel hbarModel) {
        this.hbarModel = hbarModel;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public DonutChartModel getDonutChartCourriersParPrioriteDuJour() {
        return donutChartCourriersParPrioriteDuJour;
    }

    public void setDonutChartCourriersParPrioriteDuJour(DonutChartModel donutChartCourriersParPrioriteDuJour) {
        this.donutChartCourriersParPrioriteDuJour = donutChartCourriersParPrioriteDuJour;
    }

    public BarChartModel getBarModelCourrierParJourDeLaSemaine() {
        return barModelCourrierParJourDeLaSemaine;
    }

    public void setBarModelCourrierParJourDeLaSemaine(BarChartModel barModelCourrierParJourDeLaSemaine) {
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

    public DonutChartModel getDonutChartModelParUrgenceDuMois() {
        return donutChartModelParUrgenceDuMois;
    }

    public void setDonutChartModelParUrgenceDuMois(DonutChartModel donutChartModelParUrgenceDuMois) {
        this.donutChartModelParUrgenceDuMois = donutChartModelParUrgenceDuMois;
    }

    public DonutChartModel getDonutChartModelCourriersEntrantEtSortantDuMois() {
        return donutChartModelCourriersEntrantEtSortantDuMois;
    }

    public void setDonutChartModelCourriersEntrantEtSortantDuMois(DonutChartModel donutChartModelCourriersEntrantEtSortantDuMois) {
        this.donutChartModelCourriersEntrantEtSortantDuMois = donutChartModelCourriersEntrantEtSortantDuMois;
    }

    public BarChartModel getBarModelNombreDeCourrierParTypeDuMois() {
        return barModelNombreDeCourrierParTypeDuMois;
    }

    public void setBarModelNombreDeCourrierParTypeDuMois(BarChartModel barModelNombreDeCourrierParTypeDuMois) {
        this.barModelNombreDeCourrierParTypeDuMois = barModelNombreDeCourrierParTypeDuMois;
    }

    public BarChartModel getBarModelNombreDeCourrierRecusParDirectionDuMois() {
        return barModelNombreDeCourrierRecusParDirectionDuMois;
    }

    public void setBarModelNombreDeCourrierRecusParDirectionDuMois(BarChartModel barModelNombreDeCourrierRecusParDirectionDuMois) {
        this.barModelNombreDeCourrierRecusParDirectionDuMois = barModelNombreDeCourrierRecusParDirectionDuMois;
    }

    public BarChartModel getBarModelNombreDeCourrierEnvoyesParDirectionDuMois() {
        return barModelNombreDeCourrierEnvoyesParDirectionDuMois;
    }

    public void setBarModelNombreDeCourrierEnvoyesParDirectionDuMois(BarChartModel barModelNombreDeCourrierEnvoyesParDirectionDuMois) {
        this.barModelNombreDeCourrierEnvoyesParDirectionDuMois = barModelNombreDeCourrierEnvoyesParDirectionDuMois;
    }

    public BarChartModel getBarModelNombreDeCourrierParMoisPourLAnnee() {
        return barModelNombreDeCourrierParMoisPourLAnnee;
    }

    public void setBarModelNombreDeCourrierParMoisPourLAnnee(BarChartModel barModelNombreDeCourrierParMoisPourLAnnee) {
        this.barModelNombreDeCourrierParMoisPourLAnnee = barModelNombreDeCourrierParMoisPourLAnnee;
    }
}
