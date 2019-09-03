package utils;

import java.io.IOException;

public class TerminalDialogLinux extends TerminalDialog {
    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("/bin/bash");
    }

    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {
        ReadInput readInput = new ReadInput();

        if (isEsecuzioneDebug){
            
        }
        else {
            System.out.println("Risultato: " + readInput.read());
            System.out.println("Risultato: " + readInput.read());
        }
    }
}
