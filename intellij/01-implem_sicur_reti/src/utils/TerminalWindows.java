package utils;

import java.io.IOException;

public class TerminalWindows extends Terminal{

    public TerminalWindows() throws IOException {
    }

    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("cmd");
    }
}
