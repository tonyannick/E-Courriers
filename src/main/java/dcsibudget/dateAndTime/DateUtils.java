package dcsibudget.dateAndTime;

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
    public static Date premierJourDuMoisAPartirDUneDate;
    public static Date dernierJourDuMoisAPartirDUneDate;
    public static String lundiDeLaSemaine;
    public static String mardiDeLaSemaine;
    public static String mercrediDeLaSemaine;
    public static String jeudiDeLaSemaine;
    public static String vendrediDeLaSemaine;
    public static String samediDeLaSemaine;
    public static String dimancheDeLaSemaine;
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

    public static Date transformerNomDuMoisEnDatePourLAnneeEnCours(String nomDuMois){
        Date date = null;
        String currentYear = recupererLAnneeEnCours();
        String selection = nomDuMois +"-"+ currentYear;
        try {
            date = new SimpleDateFormat("MMMM-yyyy").parse(selection);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        avoirLePremierEtLeDernierJoursDuMoisAPartirDUneDate(date);
        return date;

    }

    public static void avoirLePremierEtLeDernierJoursDuMoisAPartirDUneDate(Date date){
        Calendar firstDayOfDate = Calendar.getInstance(Locale.FRANCE);
        Calendar lastDayOfDate = Calendar.getInstance(Locale.FRANCE);
        firstDayOfDate.setTime(date);
        lastDayOfDate.setTime(date);
        /*Premier jour du mois en cours*/
        firstDayOfDate.set(Calendar.DAY_OF_MONTH, firstDayOfDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        /*Dernier jour du mois en cours*/
        lastDayOfDate.set(Calendar.DAY_OF_MONTH, firstDayOfDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        premierJourDuMoisAPartirDUneDate = firstDayOfDate.getTime();
        dernierJourDuMoisAPartirDUneDate = lastDayOfDate.getTime();
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

    public static Map<String,String> recupererTousLesJoursDeLaSemaineEnCours(){
        Calendar semaineEnCours = Calendar.getInstance(Locale.FRANCE);
        semaineEnCours.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/dd/MM");
        lundiDeLaSemaine = dateformat.format(semaineEnCours.getTime());
        Date[] daysOfWeek = new Date[7];
        Map<String,String> mapJoursDeLaSemaine = new HashMap<>();
        String[] tableauDesJoursDeLaSemaine = {"Lundi", "Mardi", "Mercredi","Jeudi","Vendredi","Samedi","Dimanche"};
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = semaineEnCours.getTime();
            mapJoursDeLaSemaine.put( tableauDesJoursDeLaSemaine[i],convertirDateEnString(daysOfWeek[i]));
            semaineEnCours.add(Calendar.DAY_OF_MONTH, 1);
        }
        return mapJoursDeLaSemaine;
    }



    public static int trouverLeNombreDElementEntreDeuxDate(Date dateDebut, Date dateFin, Date dateAConsiderer){
        int compteur = 0;
        if (dateAConsiderer.after(dateDebut) && dateAConsiderer.before(dateFin)) {
          compteur++;
        }
        return compteur;
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

    public static String recupererSimpleDateEnCoursAuFormatFrancais(){
        Date date = new Date();
        String myDate = null;
        Format formatDate = new SimpleDateFormat("dd-MM-yyyy");
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

    public static String recupererMiniHeuresEnCours(){
        Date date = new Date();
        String myDate = null;
        Format formatDate = new SimpleDateFormat("HH:mm:ss");
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

    public static String convertirHeureEnCoursEnString(Date date){
        Format formatDate = new SimpleDateFormat("HH:mm:ss");
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

    public static Date convertirStringEnHeure(String heureAConvertir){
        Date heureConvertie = null;
        try {
            heureConvertie = new SimpleDateFormat("HH:mm:ss").parse(heureAConvertir);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return heureConvertie;
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


    public static void recupererLePremierEtLeDernierJourDuMoisEnCours(){

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
