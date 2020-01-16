package tool;

import java.io.File;

public class ToolWindows extends Tool {
    @Override
    protected String costruisciPath(String pathParte1, String parthParte2) {
        return pathParte1 + "\\" + parthParte2;
    }

    @Override
    protected File createFile(String dirPath, String fileString) {
        return new File(dirPath + "\\" + fileString);
    }
}