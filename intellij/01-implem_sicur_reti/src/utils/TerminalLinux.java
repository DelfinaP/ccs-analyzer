package utils;

import java.io.IOException;
import java.util.LinkedList;

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

    @Override
    protected String parseDirectoryString(String stringToBeParsed) {
        String parsedString;
        int indexStartName = 0;
        int indexEndName = 0;
        int numeroBlocchi = 8;

        for (int i = 0; i < numeroBlocchi; i++) {
            indexStartName = vaiASpazioSuccessivo(stringToBeParsed, indexStartName);
            indexStartName = vaiACarattereSuccessivo(stringToBeParsed, indexStartName);
        }

        // A questo punto la variabile indexStartName avra' l'indice del carattere in cui inizia il nome

        parsedString = stringToBeParsed.substring(indexStartName, stringToBeParsed.length());

        return parsedString;
    }

    @Override
    protected void eseguiStampaContenutoDirectory() throws IOException {
        executeTerminalCommand("ls -l");
    }
}
