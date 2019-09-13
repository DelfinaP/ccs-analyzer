package tool;

import java.io.IOException;
import java.util.LinkedList;

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
    protected String parseDirectoryString(String stringToBeParsed) throws StringIndexOutOfBoundsException{
        String parsedString;
        int indexStartName = 0;
        int numeroBlocchi = 3;

        for (int i = 0; i < numeroBlocchi; i++) {
            indexStartName = vaiASpazioSuccessivo(stringToBeParsed, indexStartName);
            indexStartName = vaiACarattereSuccessivo(stringToBeParsed, indexStartName);
        }

        // A questo punto la variabile indexStartName avra' l'indice del carattere in cui inizia il nome

        parsedString = stringToBeParsed.substring(indexStartName, stringToBeParsed.length());

        return parsedString;
    }

    @Override
    protected LinkedList<String> buildReturnList(LinkedList<String> terminalOutputList) {
        int count = 0;
        String directoryString;
        LinkedList<String> returnList = new LinkedList<String>();

        while (terminalOutputList.size() > 0) {
            if (count < 3) {
                popStringFromList(terminalOutputList);
                count++;
            }
            else {
                try{
                    directoryString = parseDirectoryString(popStringFromList(terminalOutputList));
                    returnList.add(directoryString);
                } catch (StringIndexOutOfBoundsException e){
                    ;
                }
            }
        }
        return returnList;
    }

    @Override
    protected void eseguiStampaContenutoDirectory() throws IOException {
        executeTerminalCommand("dir");
    }
}
