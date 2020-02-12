package tool;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ToolWindows extends Tool {
    @Override
    protected String costruisciPath(String pathParte1, String parthParte2) {
        return pathParte1 + "\\" + parthParte2;
    }

    @Override
    protected File createFile(String dirPath, String fileString) {
        return new File(dirPath + "\\" + fileString);
    }

    @Override
    protected void startCwb(Terminal terminal) {
        Object objIstanza = null;
        try {
            objIstanza = new JSONParser().parse(new FileReader("src/json/parametri.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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

        terminal.addCommand(percorsoCwb + " ccs");
    }
}
