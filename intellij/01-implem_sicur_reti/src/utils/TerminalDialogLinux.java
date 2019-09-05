package utils;

import java.io.IOException;
import java.util.LinkedList;

public class TerminalDialogLinux extends TerminalDialog {


    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {

        if (isEsecuzioneDebug){
            dirPath = "/home/alessandro/Documenti/Alessandro/07-Progetto_Sicur_Reti/01-Cartella_default_elaborazione_CCS/leila-ale/apk1/evaluated";
        }
        else {
            System.out.println("Inserisci directory ccs");
            ReadInput readInput = new ReadInput();
            dirPath = readInput.read();
            System.out.println("Directory: " + dirPath);
        }
    }
}
