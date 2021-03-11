package dcsibudget.securityManager;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String crypterUnMot(String motACrypter){
        String motCrypter = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            //Add password bytes to digest
            md.update(motACrypter.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            motCrypter = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("motCrypter = " + motCrypter);
        return motCrypter;
    }
}
