package dcsibudget.securityManager;

import javax.crypto.Cipher;
import java.util.Base64;


public class Cryptage {

    /***Fonction qui permet de crypter un mot***/
    public static String crypterUnMot(String motACrypter, String cleDeCryptage) {
        try {
            GenerateurCleSecrete.setKey(cleDeCryptage);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, GenerateurCleSecrete.secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(motACrypter.getBytes("UTF-8")));
        } catch (Exception e) {
            //System.out.println("Error while encrypting: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
