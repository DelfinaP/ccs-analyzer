import utils.*;
import utils.except.osNotRecognizedException;

import java.io.IOException;

public class Tool {

    public void run(boolean isEsecuzioneDebug) throws IOException, osNotRecognizedException {
        ReadInput readInput = new ReadInput();

        if (isEsecuzioneDebug){
            ;
        }
        else {
            System.out.println("Risultato: " + readInput.read());
            System.out.println("Risultato: " + readInput.read());
        }

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