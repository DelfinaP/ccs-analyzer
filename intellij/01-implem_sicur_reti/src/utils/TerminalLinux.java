package utils;

import java.io.IOException;

public class TerminalLinux extends Terminal {
    public TerminalLinux() throws IOException {
    }

    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("/bin/bash");
    }
}
