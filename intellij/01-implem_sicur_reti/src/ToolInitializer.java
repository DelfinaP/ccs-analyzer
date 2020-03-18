import tool.OsUtils;
import tool.*;
import tool.exceptions.OsNotRecognizedException;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public class ToolInitializer {

    public void run() {
        Tool tool;

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                tool = new ToolLinux();
                tool.run();
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                tool = new ToolWindows();
                tool.run();
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }
    }
}