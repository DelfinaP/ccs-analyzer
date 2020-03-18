package tool;

import utils.FileManager;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

public class ShellWindows extends Shell {

    public ShellWindows() {
        batchExtension = "bat";
    }

    @Override
    protected void makeBatchFileExecutable(String dirPath, String fileName) {
        // In Windows it is not necessary doing anything.
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
                popString(terminalOutputList);
                count++;
            }
            else {
                try{
                    directoryString = parseDirectoryString(popString(terminalOutputList));
                    returnList.add(directoryString);
                } catch (StringIndexOutOfBoundsException e){
                    ;
                }
            }
        }
        return returnList;
    }

    @Override
    public void executeCwb() {
        try {
            process = Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "lib\\cwb\\windows\\bin\\cwb-nc.bat ccs"});

            writer = new PrintStream(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void executeBatchFile(String nameBatchFile) {
        try {
            process = Runtime.getRuntime().exec("cmd /c \"\" " + FileManager.buildPath(nomeDirFileBatch, nameBatchFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isExecuted = true;
    }
}
