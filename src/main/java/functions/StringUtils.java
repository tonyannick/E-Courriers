package functions;

public class StringUtils {

    public static String mettreMotEnMajuscule(String mot){
        if(voirSiMotEstVide(mot)){
            mot = mot.toUpperCase();
        }
        return  mot;
    }

    public static String mettrePremiereLettreDuMotEnMajuscule(String mot){
        if(voirSiMotEstVide(mot)){
            mot = mot.substring(0,1).toUpperCase() + mot.substring(1);
        }
        return mot;
    }

    public static String mettreMotEnMinuscule(String mot){

        if(voirSiMotEstVide(mot)){
            mot = mot.toLowerCase();
        }
        return  mot;
    }

    private static boolean voirSiMotEstVide(String mot){
        if(mot == null || mot.isEmpty()) {
            return false;
        }else{
            return true;
        }
    }
}
