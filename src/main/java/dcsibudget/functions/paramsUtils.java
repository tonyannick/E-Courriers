package dcsibudget.functions;

import javax.faces.context.FacesContext;
import java.util.Map;

public class paramsUtils {

    public static String recupererUnParametreParSonIdentifiant(String key){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        return paramMap.get(key);
    }
}
