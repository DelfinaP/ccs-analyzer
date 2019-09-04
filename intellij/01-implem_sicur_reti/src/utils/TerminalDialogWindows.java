package utils;

import java.io.IOException;

public class TerminalDialogWindows extends TerminalDialog {

    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {
        if (isEsecuzioneDebug){
            dirPath = "C:\\Users\\Piera\\Documents\\Prog_sicurezza_elaborazione_CCS";
        }
        else {
            System.out.println("Inserisci directory ccs");
            ReadInput readInput = new ReadInput();
            dirPath = readInput.read();
            System.out.println("Directory: " + dirPath);
        }
    }
}
