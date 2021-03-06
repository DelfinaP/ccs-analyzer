package tool;

import tool.exceptions.OsNotRecognizedException;
import utils.FileManager;
import utils.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Create shell, allowing to execute CWB commands. There are three method to be executed, in this
 * order: 1. startCwb(); 2. addCommand(); 3. executeCommands().
 */
public abstract class Shell {
    protected Process process;
    protected PrintStream writer;
    protected LinkedList<String> commandsList;
    protected LinkedList<String> outputList;
    protected String batchExtension;
    protected String nomeDirFileBatch;
    protected boolean isExecuted;

    protected Thread readBufferThread;

    public Shell() {
        commandsList = new LinkedList<String>();
        outputList = new LinkedList<String>();
        isExecuted = false;

        nomeDirFileBatch = JsonUtils.readValue("src/json/parametri.json", "parametri", "batch_files_path");
    }

    public static Shell createShell() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                return new ShellLinux();
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                return new ShellWindows();
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add command to be executed. The command is not executed immediately,
     * instead it is inserted in a queue, and at a certain point the commands
     * are executed one after the other.
     */
    public void addCommand(String command) {
        writer.println(command);
    }

    protected abstract void makeBatchFileExecutable(String dirPath, String fileName);

    protected abstract void executeBatchFile(String nameBatchFile);

    public String createFile() {
        String nameBatchFile = defineBatchFileName();

        String batchFileName = tryToCreateBatchFile(nameBatchFile);

        return nameBatchFile;
    }

    protected String tryToCreateBatchFile(String nameBatchFile) {
        List<String> commands = commandsList;
        Path file = Paths.get(FileManager.buildPath(nomeDirFileBatch, nameBatchFile));
        try {
            Files.write(file, commands, StandardCharsets.UTF_8);
        } catch (IOException e) {
            createFile();
        }
        return nameBatchFile;
    }

    protected String defineBatchFileName() {
        LinkedList<String> fileList = new LinkedList<String>();

        File folder = new File(nomeDirFileBatch);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            // If it's a file
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getName());
            }
            // Otherwise, it's a directory
            else if (listOfFiles[i].isDirectory()) {
                // Do nothing
            }
        }

        int candidateName = 1;
        String completeName = "";

        while (true) {
            completeName = Integer.toString(candidateName) + "." + batchExtension;
            boolean nameOccupied = false;

            for (int i = 0; i < fileList.size(); i++) {
                if (fileList.get(i).equals(completeName)) {
                    nameOccupied = true;
                }
            }
            if (!nameOccupied) {
                break;
            }
            candidateName++;
        }

        completeName = Integer.toString(candidateName) + "." + batchExtension;

        return completeName;
    }

    private void printCharsConvertedToInt(String string) {
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            int charCode = (int) string.charAt(i);
            System.out.println("" + character + charCode + " ");
        }
    }

    protected abstract String parseDirectoryString(String stringToBeParsed);

    protected String popString(LinkedList<String> linkedList) {
        return linkedList.remove();
    }

    protected abstract LinkedList<String> buildReturnList(LinkedList<String> terminalOutputList);

    protected void debugParseString() {
        String string = "123456    abcdef   123456   abcdef";

        int index = vaiASpazioSuccessivo(string, 0);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiASpazioSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiASpazioSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));
    }

    protected int vaiASpazioSuccessivo(String stringa, int indiceDiPartenza) throws StringIndexOutOfBoundsException{
        int i = indiceDiPartenza + 1;

        while (stringa.charAt(i) != ' ') {
            i++;
        }

        return i;
    }

    protected int vaiACarattereSuccessivo(String stringa, int indiceDiPartenza) {
        int i = indiceDiPartenza + 1;

        while (stringa.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    public abstract void startCwb();

    public void executeCommands() {
        writer.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                outputList.add(line);
            }

            int exitVal = 0;

            exitVal = process.waitFor();

            if (exitVal == 0) {
                // Success
            } else {
                // Failure
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> getOutputList() {
        return outputList;
    }
}