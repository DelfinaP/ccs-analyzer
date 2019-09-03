import utils.*;
import utils.except.osNotRecognizedException;

import java.io.IOException;

public class Tool {

    public void run(boolean isEsecuzioneDebug) throws IOException, osNotRecognizedException {
        TerminalDialog terminalDialog;

        if (OsUtils.getOsType() == OsType.LINUX) {
            terminalDialog = new TerminalDialogLinux();
            terminalDialog.run(isEsecuzioneDebug);
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            terminalDialog = new TerminalDialogWindows();
            terminalDialog.run(isEsecuzioneDebug);
        }
    }
}