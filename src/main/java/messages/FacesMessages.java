package messages;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesMessages {

    public static void infoMessage(String destinataire,String titre,String detailMessage){
        FacesContext.getCurrentInstance().addMessage(destinataire,new FacesMessage(FacesMessage.SEVERITY_INFO,titre,detailMessage));
    }

    public static void warningMessage(String destinataire,String titre,String detailMessage){
        FacesContext.getCurrentInstance().addMessage(destinataire,new FacesMessage(FacesMessage.SEVERITY_WARN,titre,detailMessage));
    }

    public static void errorMessage(String destinataire,String titre,String detailMessage){
        FacesContext.getCurrentInstance().addMessage(destinataire,new FacesMessage(FacesMessage.SEVERITY_ERROR,titre,detailMessage));
    }

    public static void fatalMessage(String destinataire,String titre,String detailMessage){
        FacesContext.getCurrentInstance().addMessage(destinataire,new FacesMessage(FacesMessage.SEVERITY_FATAL,titre,detailMessage));
    }

}
