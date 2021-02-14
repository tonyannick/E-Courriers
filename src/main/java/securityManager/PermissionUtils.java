package securityManager;

import java.util.List;

public class PermissionUtils {

    public static boolean verifierPermission(String permission, List<String> listeDesPermissions){
        boolean find = false;
        if(listeDesPermissions.contains(permission)){
            find = true;
        }
        return find;
        /*for(int a = 0; a < listeDesPermissions.size(); a++){

        }*/
    }
}
