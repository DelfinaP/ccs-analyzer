package utils;

import java.io.IOException;

public class TerminalDialogLinux extends TerminalDialog {
    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("/bin/bash");
    }

}
