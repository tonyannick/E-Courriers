package dcsibudget.model;

import java.util.List;

public class Fonction {

    private String titreFonction;
    private String idFonction;
    private List<String> listeFonction;

    public Fonction() {
    }

    public Fonction(String titreFonction, String idFonction) {
        this.titreFonction = titreFonction;
        this.idFonction = idFonction;
    }

    public List<String> getListeFonction() {
        return listeFonction;
    }

    public void setListeFonction(List<String> listeFonction) {
        this.listeFonction = listeFonction;
    }

    public String getTitreFonction() {
        return titreFonction;
    }

    public void setTitreFonction(String titreFonction) {
        this.titreFonction = titreFonction;
    }

    public String getIdFonction() {
        return idFonction;
    }

    public void setIdFonction(String idFonction) {
        this.idFonction = idFonction;
    }
}
