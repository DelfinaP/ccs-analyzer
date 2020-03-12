package tool;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tool.exceptions.osNotRecognizedException;
import utils.FileManager;
import utils.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class Terminal {
    protected Process process;
    protected LinkedList<String> commandsList;
    protected String batchExtension;
    protected String nomeDirFileBatch;
    protected boolean isExecuted;

    protected Thread readBufferThread;

    public Terminal() {
        commandsList = new LinkedList<String>();
        isExecuted = false;

        nomeDirFileBatch = JsonUtils.readValue("src/json/parametri.json", "parametri", "batch_files_path");
    }

    public static Terminal createTerminal() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                return new TerminalLinux();
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                return new TerminalWindows();
            }
        } catch (osNotRecognizedException e) {
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
        commandsList.add(command);
    }

    /**
     * Execute commands contained in the queue.
     */
    public void executeCommands() {
        if (!isExecuted) {
            String nameBatchFile = createFile();

            makeBatchFileExecutable(nomeDirFileBatch, nameBatchFile);

            executeBatchFile(nameBatchFile);
        }
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
        Path file = Paths.get(costruisciPath(nomeDirFileBatch, nameBatchFile));
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
            // Se è un file
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getName());
            }
            // Altrimenti, se è una directory
            else if (listOfFiles[i].isDirectory()) {
                // Non fare nulla
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

    public LinkedList<String> getTerminalOutput() {
        LinkedList<String> commandList = new LinkedList<String>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line = "";
        try {
            while (true) {
                line = reader.readLine();
                if (line == null || line == "terminal execution terminated") {
                    break;
                }
                else {
                    System.out.println(line);
                    commandList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            while ((line = reader.readLine()) != null) {
//                commandList.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int exitVal = 0;

        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exitVal == 0) {
            return commandList;
        } else {
            return null;
        }
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

    protected abstract String costruisciPath(String pathParte1, String parthParte2);

    protected abstract void executeCwb();

    private void loadCwbMethod(PrintStream writer, String fileName){
        String projectDirectory = FileManager.getProjectDirectory();

        writer.println("load " + costruisciPath(projectDirectory, fileName));
    }

    public int getMethodSize(String fileName, String methodName) {
        Process process = null;
        executeCwb();

        PrintStream writer = new PrintStream(process.getOutputStream());

        loadCwbMethod(writer, fileName);

        writer.println("size " + methodName);
        writer.println("quit");
        writer.close();

        LinkedList<String> stringList = new LinkedList<String>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                stringList.add(line);
            }

            int exitVal = 0;

            exitVal = process.waitFor();

            if (exitVal == 0) {
                // Success
                System.out.println("Success");
                Tool.printStringList(stringList);
            } else {
                // Failure
                System.out.println("Failure");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
