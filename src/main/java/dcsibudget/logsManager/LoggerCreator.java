package dcsibudget.logsManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerCreator {

    public static Logger creerUnLog(String nomDeLaClasse){
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
