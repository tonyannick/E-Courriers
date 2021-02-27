package dcsibudget.securityManager;

import javax.crypto.Cipher;
import java.util.Base64;

public class Decryptage {

    /**Fonction qui decrypte un mot**/
    public static String decrypterUnMot(String strToDecrypt,  String cleDeCryptage) {
        try {
            GenerateurCleSecrete.setKey(cleDeCryptage);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, GenerateurCleSecrete.secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            //System.out.println("Error while decrypting: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
