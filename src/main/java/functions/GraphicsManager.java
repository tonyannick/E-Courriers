package functions;

import com.sun.istack.internal.NotNull;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
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

import java.util.List;

public class GraphicsManager {

    public static HorizontalBarChartModel creerGraphiqueBarHorizontal(@NotNull HorizontalBarChartModel horizontalBarChartModel, HorizontalBarChartDataSet horizontalBarChartDataSet,
                                                                        List<Number> valeur, List<String> backgroungColor, int borderWidht,
                                                                        List<String> borderColor, List<String> labels, ChartData data){
        horizontalBarChartDataSet.setData(valeur);
        horizontalBarChartDataSet.setBackgroundColor(backgroungColor);
        horizontalBarChartDataSet.setBorderColor(borderColor);
        horizontalBarChartDataSet.setBorderWidth(borderWidht);
        data.addChartDataSet(horizontalBarChartDataSet);
        data.setLabels(labels);
        horizontalBarChartModel.setData(data);
        return horizontalBarChartModel;
    }

    public static BarChartModel creerGraphiqueBarVertical(@NotNull BarChartModel verticalBarChartModel, BarChartDataSet barChartDataSet,
                                                                    List<Number> valeur, List<String> backgroungColor, int borderWidht,
                                                                    List<String> borderColor, List<String> labels, ChartData data){
        barChartDataSet.setData(valeur);
        barChartDataSet.setBackgroundColor(backgroungColor);
        barChartDataSet.setBorderColor(borderColor);
        barChartDataSet.setBorderWidth(borderWidht);
        data.addChartDataSet(barChartDataSet);
        data.setLabels(labels);
        verticalBarChartModel.setData(data);
        return verticalBarChartModel;
    }

    public static BarChartOptions creerOptionsDAffichageDuGraphique(@NotNull BarChartOptions barChartOptions, boolean legendDisplay, boolean valeurLinearAxes,
                                                                    String titre, boolean afficherTitre){
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(valeurLinearAxes);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(valeurLinearAxes);
        linearAxes.setStacked(valeurLinearAxes);
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        barChartOptions.setScales(cScales);

        Legend legend = new Legend();
        legend.setDisplay(legendDisplay);
        barChartOptions.setLegend(legend);

        Title title = new Title();
        title.setDisplay(afficherTitre);
        title.setText(titre);
        barChartOptions.setTitle(title);

        return barChartOptions;
    }

    public static BarChartOptions creerOptionsDAffichageDuGraphiqueVertical(@NotNull BarChartOptions barChartOptions, boolean legendDisplay, boolean valeurLinearAxes,
                                                                    String titre, String positionLegend, String fontStyle, String color, boolean afficherTitre){

        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        barChartOptions.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText(titre);
        barChartOptions.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(false);
        legend.setPosition(positionLegend);
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle(fontStyle);
        legendLabels.setFontColor(color);
        legendLabels.setFontSize(20);
        legend.setLabels(legendLabels);
        barChartOptions.setLegend(legend);


        // disable animation
        /*Animation animation = new Animation();
        animation.setDuration(0);
        options.setAnimation(animation);*/

        return barChartOptions;
    }

    public static PieChartModel creerGraphiquePieChart(@NotNull PieChartModel pieChartModel, PieChartDataSet pieChartDataSet, List<Number> values,
                                                       List<String> backgroungColor,List<String> labels,ChartData data){
        pieChartDataSet.setData(values);
        pieChartDataSet.setBackgroundColor(backgroungColor);
        data.addChartDataSet(pieChartDataSet);
        data.setLabels(labels);
        pieChartModel.setData(data);
        return pieChartModel;
    }

    public static DonutChartModel creerGraphiqueDonut(@NotNull DonutChartModel donutChartModel, DonutChartDataSet dataSet, List<Number> values, List<String> bgColors,
                                                      List<String> labels, ChartData data){

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        donutChartModel.setData(data);
        return donutChartModel;

    }
}
