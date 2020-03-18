package tool;

import java.io.File;

public class ToolLinux extends Tool {

    @Override
    protected File createFile(String dirPath, String fileString) {
        return new File(dirPath + "/" + fileString);
    }

    @Override
    protected void startCwb(Shell shell) {


    }
}
