package utils;

import java.io.IOException;

public class TerminalLinux extends Terminal {
    public TerminalLinux() throws IOException {
    }

    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("/bin/bash");
    }

    @Override
    protected void changeDirectory(String dirPath) throws IOException {
        executeTerminalCommand("cd " + "\"" + dirPath + "\"");
    }

    @Override
    protected void rimuoviFileNonCcs(String dirPath) throws IOException {
        System.out.println("Inizio rimozione file");

        changeDirectory(dirPath);

        executeTerminalCommand("find . ! -name '*.ccs' -type f -exec rm -f {} +");

        System.out.println("File rimossi");
    }
}
