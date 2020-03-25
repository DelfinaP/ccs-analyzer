package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
    /**
     * @param   jsonPath   The path of the json to be read. Example of variable's value:
     *                     <code>src/json/parameters.json</code>
     * @param   jsonObjectName   The name of an object positioned at the first level of
     *                           the json. Example value: <code>parameters</code>
     * @param   keyName   The name of the key positioned inside the object <code>jsonObjectName</code>
     */
    public static String readValue(String jsonPath, String jsonObjectName, String keyName) {
        String value = "";
        String keyExtracted = "";

        Object objIstanza = null;
        try {
            objIstanza = new JSONParser().parse(new FileReader(jsonPath));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // typecasting obj to JSONObject
        JSONObject joIstanza = (JSONObject) objIstanza;

        Map parametri = ((Map) joIstanza.get(jsonObjectName));

        Iterator<Map.Entry> itrParametri = parametri.entrySet().iterator();
        while (itrParametri.hasNext()) {
            Map.Entry pairParametri = itrParametri.next();

            keyExtracted = pairParametri.getKey().toString();
            if (keyExtracted.equals(keyName)) {
                value = (String) pairParametri.getValue();
            }
        }

        return value;
    }

    public static String getOriginalFilesJsonValue() {
        return readValue("src/json/parametri.json", "parametri", "original_files_path");
    }

    public static String getModifiedFilesJsonValue() {
        return readValue("src/json/parametri.json", "parametri", "modified_files_path");
    }

    public static String getCcsAnalysisJsonValue() {
        return readValue("src/json/parametri.json", "parametri", "ccs_analysis_path");
    }
}