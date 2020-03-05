import tool.OsUtils;
import tool.*;
import tool.exceptions.osNotRecognizedException;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public class ToolInitializer {

    public void run() throws IOException, osNotRecognizedException, ParseException {
        Tool tool;

        if (OsUtils.getOsType() == OsType.LINUX) {
            tool = new ToolLinux();
            tool.run();
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS) {
            tool = new ToolWindows();
            tool.run();
        }
    }
}