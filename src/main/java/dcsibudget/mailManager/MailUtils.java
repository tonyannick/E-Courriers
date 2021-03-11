package dcsibudget.mailManager;

import dcsibudget.fileManager.PropertiesFilesReader;
import dcsibudget.logsManager.LoggerCreator;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class MailUtils {

    private static Logger mailLogger = LoggerCreator.creerUnLog("MailUtils");

    public static void envoyerUnMailSimple(String destinataire, String sujet, String message){

        PropertiesFilesReader.lireLeFichierMailConfiguration("mailConfiguration.properties");
        Properties properties = new Properties();
        properties.put("mail.smtp.host", PropertiesFilesReader.mapMailConfiguration.get("mail.smtp.host"));
        properties.put("mail.smtp.port", PropertiesFilesReader.mapMailConfiguration.get("mail.smtp.port"));
        properties.put("mail.smtp.auth", PropertiesFilesReader.mapMailConfiguration.get("mail.smtp.auth"));
        properties.put("mail.smtp.starttls.enable", PropertiesFilesReader.mapMailConfiguration.get("mail.smtp.starttls.enable"));

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(PropertiesFilesReader.mapMailConfiguration.get("account"),PropertiesFilesReader.mapMailConfiguration.get("password"));
            }
        };

        Session session = Session.getInstance(properties, auth);
        session.setDebug(true);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(PropertiesFilesReader.mapMailConfiguration.get("account")));
            InternetAddress[] toAddresses = { new InternetAddress(destinataire) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(sujet);
            msg.setSentDate(new Date());
            msg.setText(message);
            Transport.send(msg);
        } catch (MessagingException e) {
            LoggerCreator.definirMessageErreur(mailLogger,"Une erreur s'est produite lors de l'envoi du mail "+e);
            e.printStackTrace();
        }
    }

}
