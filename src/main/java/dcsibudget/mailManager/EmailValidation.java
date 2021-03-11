package dcsibudget.mailManager;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public class EmailValidation {

    private static final String validEmailRegex= "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    public static boolean voirSiEmailEstValide(String email){
        EmailValidator emailValidator = EmailValidator.getInstance();
        return emailValidator.isValid(email);
    }

    public static boolean verifierValiditeDUnEmail(String email){
        Pattern pattern = Pattern.compile(validEmailRegex);
        return pattern.matcher(email).matches();
    }
}
