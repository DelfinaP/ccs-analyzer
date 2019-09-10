import utils.*;
import utils.except.osNotRecognizedException;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public class Tool {

    public void run() throws IOException, osNotRecognizedException, ParseException {
        TerminalDialog terminalDialog;

        if (OsUtils.getOsType() == OsType.LINUX) {
            terminalDialog = new TerminalDialogLinux();
            terminalDialog.run();
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            terminalDialog = new TerminalDialogWindows();
            terminalDialog.run();
        }
    }
}