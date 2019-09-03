package utils;

import java.io.IOException;

public class TerminalDialogWindows extends TerminalDialog {

    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("cmd");
    }

    @Override
    protected void getDirPath(boolean isEsecuzioneDebug) {

    }
}
