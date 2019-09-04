package utils;

import java.io.IOException;

public class TerminalDialogWindows extends TerminalDialog {

    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {
        if (isEsecuzioneDebug){
            dirPath = "C:\\Users\\Piera\\Documents\\Prog_sicurezza_elaborazione_CCS";
        }
        else {
            ReadInput readInput = new ReadInput();
            dirPath = readInput.read();
        }
    }
}
