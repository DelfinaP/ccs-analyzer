package tool;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.JsonUtils;

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
        percorsoCwb = JsonUtils.readValue("src/json/parametri.json", "parametri", "percorso_cwb");

        terminal.addCommand(percorsoCwb + " ccs");
    }
}
