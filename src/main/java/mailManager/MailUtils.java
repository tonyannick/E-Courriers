package mailManager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtils {

    private static Session parametrageDesProprietes(){
        Properties properties = new Properties();
        //Enable authentication
        properties.put("mail.smtp.auth", "true");
        //Set TLS encryption enabled
        properties.put("mail.smtp.starttls.enable", "true");
        //Set SMTP host
        properties.put("mail.smtp.host", "smtp.gmail.com");
        //Set smtp port
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(IdentifiantsDeConnexion.login, IdentifiantsDeConnexion.motDePasse);
            }
        });

        return session;
    }


    public static boolean envoyerUnMailSimple(String destinataireMail, String sujetMail, String messageMail){
        boolean mailEnvoye = false;
        Message message = new MimeMessage(parametrageDesProprietes());
        try {
            message.setFrom(new InternetAddress(IdentifiantsDeConnexion.login));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinataireMail));
            message.setSubject(sujetMail);
            message.setText(messageMail);
            Transport.send(message);
            mailEnvoye = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mailEnvoye;
    }
}
