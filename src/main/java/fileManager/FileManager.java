package fileManager;

import dateAndTime.DateUtils;
import org.primefaces.event.FileUploadEvent;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static final String tempProperty = "java.io.tmpdir";
    public static final String userHomeDirectory = System.getProperty("user.home");
    public static final String tempDirectoryOSPath = System.getProperty(tempProperty);
    public static final String eCourrierDirectory = "E-courrier";
    public static final String eCourrierLogFile = "e-courrier.log";
    public static final String logFilePathOnSystem = userHomeDirectory+"\\"+eCourrierDirectory+"\\"+eCourrierLogFile;

    public static String recupererExtensionDUnFichierParSonNom(String nomFichier) {
        if(nomFichier.lastIndexOf(".") != -1 && nomFichier.lastIndexOf(".") != 0)
            return nomFichier.substring(nomFichier.lastIndexOf(".")+1);
        else return "";
    }

    public static String determinerTypeDeFichierParSonExtension(String extensionFichier){
        String typeDeFichier = null;
        switch (extensionFichier){
            case "pdf":
                typeDeFichier = "application/pdf";
                break;
            case "doc":
                typeDeFichier = "application/msword";
                break;
            case "docx":
                typeDeFichier = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "xls":
                typeDeFichier = "application/vnd.ms-excel";
                break;
            case "xlsx":
                typeDeFichier = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "ppt":
                typeDeFichier = "application/vnd.ms-powerpoint";
                break;
            case "pptx":
                typeDeFichier = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                break;
            case "jpg":
                typeDeFichier = "image/jpeg";
                break;
            case "jpeg":
                typeDeFichier = "image/jpeg";
                break;
            case "png":
                typeDeFichier = "image/png";
                break;
        }
        return typeDeFichier;
    }

    public static boolean ajouterUnFichierDansLeDossierTemporaireDuSysteme(FileUploadEvent fileUploadEvent){
        boolean ajout = false;
        byte[] bytes = null;
        String nomFichier = DateUtils.recupererSimpleHeuresEnCours()+"_"+fileUploadEvent.getFile().getFileName();
        bytes = fileUploadEvent.getFile().getContent();
        BufferedOutputStream stream = null;
        String cheminFichierDansTemp = FileManager.tempDirectoryOSPath +"photo_"+ nomFichier;

        try {
            stream = new BufferedOutputStream(new FileOutputStream(new File(cheminFichierDansTemp)));
            stream.write(bytes);
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().addMessage("messagefichiertemp", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Fichier bien ajouté !!"));
            ajout = true;
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("messagefichiertemp", new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "Une erreurManager s'est produite lors de l'ajout du fichier !!"));
            e.printStackTrace();
        }finally {
            try {
                if(stream != null){
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ajout;
    }

    public static void creerDossierECourrier(){
        Path path = Paths.get(userHomeDirectory+"\\"+eCourrierDirectory);
        if(!Files.exists(path)){
             try {
                Files.createDirectories(path);
                creerFichierLog();
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }else{
            creerFichierLog();
        }
    }

    private static void creerFichierLog(){
        File file = new File(logFilePathOnSystem);
        boolean result;
        try {
            result = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calculerLaTailleDuFichierLogEnCoursDUtilisation(){

    }

}
