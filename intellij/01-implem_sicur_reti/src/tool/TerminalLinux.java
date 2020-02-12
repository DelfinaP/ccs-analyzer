package tool;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class TerminalLinux extends Terminal {
    public TerminalLinux() throws IOException {
        batchExtension = "sh";
    }

    @Override
    protected String parseDirectoryString(String stringToBeParsed) {
        String parsedString;
        int indexStartName = 0;
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
    protected LinkedList<String> buildReturnList(LinkedList<String> terminalOutputList) {
        boolean isPrimaStringa = true;
        String directoryString;
        LinkedList<String> returnList = new LinkedList<String>();

        while (terminalOutputList.size() > 0) {
            if (isPrimaStringa) {
                popString(terminalOutputList);
                isPrimaStringa = false;
            }
            else {
                directoryString = parseDirectoryString(popString(terminalOutputList));
                returnList.add(directoryString);
            }
        }
        return returnList;
    }

    @Override
    protected File createFile(String dirPath, String fileString) {
        return new File(dirPath + "/" + fileString);
    }

    @Override
    protected String costruisciPath(String pathParte1, String parthParte2) {
        return pathParte1 + "/" + parthParte2;
    }

    protected void executeBatchFile(String nameBatchFile) {
        try {
            process = Runtime.getRuntime().exec(costruisciPath(nomeDirFileBatch, nameBatchFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isExecuted = true;
    }
}
