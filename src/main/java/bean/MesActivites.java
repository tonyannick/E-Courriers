package bean;

import model.Activites;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;


@SessionScoped
@Named
public class MesActivites implements Serializable {

    private static final long serialVersionUID = -832586958164004116L;
    private Activites activites;

    @PostConstruct
    public void initialisation(){
        activites = new Activites();
    }

    public Activites getActivites() {
        return activites;
    }

    public void setActivites(Activites activites) {
        this.activites = activites;
    }
}
