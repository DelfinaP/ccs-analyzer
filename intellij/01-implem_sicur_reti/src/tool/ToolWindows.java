package tool;

import utils.JsonUtils;

import java.io.File;

public class ToolWindows extends Tool {

    @Override
    protected File createFile(String dirPath, String fileString) {
        return new File(dirPath + "\\" + fileString);
    }

    @Override
    protected void startCwb(Shell shell) {
        cwbPath = JsonUtils.readValue("src/json/parametri.json", "parametri", "percorso_cwb");

        shell.addCommand(cwbPath + " ccs");
    }
}
