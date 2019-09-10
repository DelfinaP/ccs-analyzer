package utils;

import java.io.IOException;
import java.util.LinkedList;

public class TerminalDialogWindows extends TerminalDialog {

    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {
        if (isEsecuzioneDebug){
            dirPath = "C:\\Users\\Piera\\Documents\\Prog_sicurezza_elaborazione_CCS\\leila-ale\\apk1\\evaluated";
        }
        else {
            System.out.println("Inserisci directory ccs");
            ReadInput readInput = new ReadInput();
            dirPath = readInput.read();
            System.out.println("Directory: " + dirPath);
        }
    }

    @Override
    protected void copiaFileOriginali() {

    }
}
