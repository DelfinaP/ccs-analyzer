package utils;

import java.io.IOException;

public class TerminalDialogLinux extends TerminalDialog {


    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {

        if (isEsecuzioneDebug){
            dirPath = "/home/alessandro/Documenti/Alessandro/07-Progetto_Sicur_Reti/01-Cartella_default_elaborazione_CCS";
        }
        else {
            ReadInput readInput = new ReadInput();
            dirPath = readInput.read();
        }
    }
}
