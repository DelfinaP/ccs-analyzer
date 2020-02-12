import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Main {

    static String percorsoCwb;

    public static void main(String[] args) {
        Object objIstanza = null;
        try {
            try {
                objIstanza = new JSONParser().parse(new FileReader("src/json/parametri.json"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // typecasting obj to JSONObject
        JSONObject joIstanza = (JSONObject) objIstanza;

        Map parametri = ((Map) joIstanza.get("parametri"));

        Iterator<Map.Entry> itrParametri = parametri.entrySet().iterator();
        while (itrParametri.hasNext()) {
            Map.Entry pairParametri = itrParametri.next();

            switch (pairParametri.getKey().toString()) {
                case "percorso_cwb":
                    percorsoCwb = (String) pairParametri.getValue();
                    break;
            }
        }

        System.out.println(percorsoCwb);
    }
}