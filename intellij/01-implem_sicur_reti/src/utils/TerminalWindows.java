package utils;

import java.io.IOException;

public class TerminalWindows extends Terminal{

    public TerminalWindows() throws IOException {
    }

    @Override
    protected void avviaTerminale() throws IOException {
        process = Runtime.getRuntime().exec("cmd");
    }

    @Override
    protected void changeDirectory(String dirPath) throws IOException {
        executeTerminalCommand("cd " + dirPath);
    }

    @Override
    protected void rimuoviFileNonCcs(String dirPath) throws IOException {
        System.out.println("Inizio rimozione file");

        changeDirectory(dirPath);

        executeTerminalCommand("mkdir cartella");
        executeTerminalCommand("move *.ccs cartella");
        executeTerminalCommand("del * /Q");
        executeTerminalCommand("cd cartella");
        executeTerminalCommand("move * ../");
        executeTerminalCommand("cd ../");
        executeTerminalCommand("rmdir cartella");

        System.out.println("File rimossi");
    }


}
