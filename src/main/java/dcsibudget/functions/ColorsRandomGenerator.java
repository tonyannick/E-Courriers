package dcsibudget.functions;

import java.util.Random;

public class ColorsRandomGenerator {

    public String creerUneCouleurAleatoireAuFormatRGB(){
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        String myColor = "rgb("+red+","+green+","+blue+")";
        return myColor;
    }

}
