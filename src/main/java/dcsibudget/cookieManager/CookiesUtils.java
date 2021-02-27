package dcsibudget.cookieManager;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesUtils {

    public static Cookie moncookie;
    private static final int dureeDeVie = 2628000; // valeur en seconde qui est égale à  1 mois

    public static void creerUnCookie(String name, String valeur){

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        Cookie cookie = new Cookie(name,valeur);
        cookie.setMaxAge(dureeDeVie);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static Cookie recupererUnCookie(String name){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)facesContext.getExternalContext().getRequest();

        Cookie[] cookiesArray = request.getCookies();
        if(cookiesArray != null && cookiesArray.length > 0 ){
            for (int i = 0; i < cookiesArray.length; i++){
                if(cookiesArray[i].getName().equals(name)){
                    moncookie = cookiesArray[i];
                    return moncookie;
                }
            }
        }
        return null;
    }
}
