package bean;


import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class SessionExpiree implements Serializable {

    private static final long serialVersionUID = -8065765061079742763L;

    public String allerVersPageDeConnexion(){
        return "login.xhtml?faces-redirect=true";
    }
}
