package securityManager;

import java.util.ArrayList;

public class PermissionUtils {

    public static boolean verifierPermission(String permission, ArrayList<String> listeDesPermissions){
        boolean find = false;
        if(listeDesPermissions.contains(permission)){
            find = true;
        }
        return find;
        /*for(int a = 0; a < listeDesPermissions.size(); a++){

        }*/
    }
}
