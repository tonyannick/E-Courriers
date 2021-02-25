package logsManager;

import fileManager.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class LoggerCreator {

    public static Logger creerUnLog(String nomDeLaClasse){

       /* LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File(FileManager.logFilePathOnSystem);
        context.setConfigLocation(file.toURI());*/
        Logger logger = LogManager.getLogger(nomDeLaClasse.getClass().getName());
        return logger;
    }

    public static void definirMessageAlerte(Logger logger,String message){
        logger.warn(message);                                                                                                                                                                                                                                       ;
    }

    public static void definirMessageInfo(Logger logger,String message){
        logger.info(message);                                                                                                                                                                                                                                       ;
    }

    public static void definirMessageErreur(Logger logger,String message){
        logger.error(message);                                                                                                                                                                                                                                       ;
    }

    public static void definirMessageFatal(Logger logger,String message){
        logger.fatal(message);                                                                                                                                                                                                                                       ;
    }
}
