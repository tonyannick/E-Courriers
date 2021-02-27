package dcsibudget.mailManager;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailValidation {

    public static boolean voirSiEmailEstValide(String email){
        EmailValidator emailValidator = EmailValidator.getInstance();
        return emailValidator.isValid(email);
    }
}
