package dateAndTime;

import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class DateUtils {

    public static Date premierJourDuMois;
    public static Date dernierJourDuMois;
    public static String premierJourDeLaSemaine;
    public static String dernierJourDeLaSemaine;
    public static String premierJourDeLaSemaineFormatUS;
    public static String dernierJourDeLaSemaineFormatUS;
    public static final DateFormatSymbols french_dfs = new DateFormatSymbols(Locale.FRENCH);


    public static String recupererNomDuMoisParUneDate(String date) throws ParseException{
        if(date != null){
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            return monthName;
        }

        return null;

    }


    public static String[] recupererTousLesMoisDeLAnnee(){
        String[] frenchMonths = french_dfs.getMonths();
        return frenchMonths;
    }

    public static String recupererLAnneeEnCours(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String anneeEnCours = simpleDateFormat.format(date);
        return anneeEnCours;
    }

    public static void recupererLePremierEtLeDernierJourDelaSemaine(){
        Calendar semaineDuMois = Calendar.getInstance(Locale.FRANCE);
        semaineDuMois.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        SimpleDateFormat dateformat = new SimpleDateFormat("EEEEE dd/MM/yyyy");
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd");
        premierJourDeLaSemaine = dateformat.format(semaineDuMois.getTime());
        premierJourDeLaSemaineFormatUS = dateFormatUS.format(semaineDuMois.getTime());
        for (int i = 0; i<6; i++) {
            semaineDuMois.add(Calendar.DATE, 1);
        }
        dernierJourDeLaSemaine = dateformat.format(semaineDuMois.getTime());
        dernierJourDeLaSemaineFormatUS = dateFormatUS.format(semaineDuMois.getTime());
    }

    public static String recupererFullDateEnCours(){
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = DateFormat.getDateInstance(DateFormat.FULL, new Locale("fr"));
        String dateEnCours = dateFormat.format(date);
        return dateEnCours ;
    }

    public static String recupererFullHeureEnCours(){
        Date date = new Date();
        DateFormat timeFormat;
        timeFormat = DateFormat.getTimeInstance(DateFormat.FULL, new Locale("fr"));
        String heureEnCours = timeFormat.format(date);
        return heureEnCours ;
    }

    public static String recupererSimpleDateEnCours(){
        Date date = new Date();
        String myDate = null;
        Format formatDate = new SimpleDateFormat("yyyy-MM-dd");
        myDate = formatDate.format(date);
        return myDate;
    }

    public static String recupererSimpleHeuresEnCours(){
        Date date = new Date();
        String myDate = null;
        Format formatDate = new SimpleDateFormat("HH_mm_ss_SSS");
        myDate = formatDate.format(date);
        return myDate;
    }

    public static String convertirHeureDeReceptionAuBonFormat(String heure){
        try {
            heure = heure.replaceAll("\\s","").trim();
            Date valeurHeureAjout = new SimpleDateFormat("hh:mm:ss").parse(heure.trim());
            Format formatHeure = new SimpleDateFormat("HH:mm:ss");
            heure = formatHeure.format(valeurHeureAjout);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  heure;
    }

    public static LocalDate convertirStringEnLocalDate(String date){
        LocalDate localDate = LocalDate.parse("yyyy-MM-dd").parse(date);
        return localDate;
    }

    public static LocalDate convertirDateEnLocalDate(Date date){
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        LocalDate localDate = zdt.toLocalDate();
        return localDate;
    }

    public static String convertirDateEnString(Date date){
        Format formatDate = new SimpleDateFormat("yyyy-MM-dd");
        return  formatDate.format(date);
    }

    public static String convertirDateEnStringAuFormatfrancais(Date date){
        Format formatDate = new SimpleDateFormat("dd-MM-yyyy");
        return  formatDate.format(date);
    }

    public static Date convertirStringEnDateAuFormatfrancais(String date){
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static Date convertirStringEnDateAuFormatUS(String date){
        Date dateUS = null;
        try {
            dateUS = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateUS;
    }


    public static Date convertirStringEnDateTimeAuFormatfrancais(String date){
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }


    public static void recupererPremierEtDernierJourDuMoisEnCours(){

        Calendar firstMonthcalendar = Calendar.getInstance(Locale.FRANCE);
        Calendar lastMonthcalendar = Calendar.getInstance(Locale.FRANCE);

        /*Dernier jour du mois en cours*/
        lastMonthcalendar.set(Calendar.DAY_OF_MONTH, lastMonthcalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        /*Premier jour du mois en cours*/
        firstMonthcalendar.set(Calendar.DAY_OF_MONTH, firstMonthcalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        premierJourDuMois = firstMonthcalendar.getTime();
        dernierJourDuMois = lastMonthcalendar.getTime();


    }

}
