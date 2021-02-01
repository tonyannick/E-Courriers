package securityManager;

import java.util.Random;

public class Captcha {

    /***Generer un captcha aléatoire***/
    public static String captchaGenerator(int tailleCaptcha){
        String captcha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890àçéè./+-=)(&@?!,:";
        StringBuilder captchaBuilder = new StringBuilder();
        Random random = new Random();
        while(captchaBuilder.length() < tailleCaptcha) {
            int index = (int) (random.nextFloat() * captcha.length());
            captchaBuilder.append(captcha.substring(index, index+1));
        }
        return captchaBuilder.toString();
    }

}
