package bean;

import fileManager.PropertiesFilesReader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
@Named
public class FooterBean implements Serializable {

    private static final long serialVersionUID = -6013361321175191359L;
    private String copyright;

    @PostConstruct
    public void initialisation(){
        PropertiesFilesReader.lireLeFichierDuCopyright("copyrith.properties");
        copyright = PropertiesFilesReader.copyright;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
