package tool;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public Terminal() throws IOException {
        commandsList = new LinkedList<String>();
        isExecuted = false;
    }

    /**
     * Add command to be executed. The command is not executed immediately,
     * instead it is inserted in a queue, and at a certain point the commands
     * are executed one after the other.
     */
    protected void addCommand(String command) {
        commandsList.add(command);
    }

    /**
     * Execute commands contained in the queue.
     */
    protected void executeCommands() {
        if (!isExecuted) {
            Object objIstanza = null;
            try {
                objIstanza = new JSONParser().parse(new FileReader("src/json/parametri.json"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // typecasting obj to JSONObject
            JSONObject joIstanza = (JSONObject) objIstanza;

            Map parametri = ((Map) joIstanza.get("parametri"));

            Iterator<Map.Entry> itrParametri = parametri.entrySet().iterator();
            while (itrParametri.hasNext()) {
                Map.Entry pairParametri = itrParametri.next();

                switch (pairParametri.getKey().toString()) {
                    case "nome_dir_file_batch":
                        nomeDirFileBatch = (String) pairParametri.getValue();
                        break;
                }
            }

            String nameBatchFile = createFile();

            executeBatchFile(nameBatchFile);

            deleteBatchFile(nameBatchFile);
        }
    }

    protected void deleteBatchFile(String nameBatchFile) {
        File f = new File(costruisciPath(nomeDirFileBatch, nameBatchFile));

        f.delete();
    }

    protected abstract void executeBatchFile(String nameBatchFile);

    protected String createFile() {
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

        boolean nameFound = false;
        int candidateName = 1;

        while (!nameFound) {
            for (int i = 0; i < fileList.size(); i++) {
                String completeName = Integer.toString(candidateName) + "." + batchExtension;
                if (fileList.get(i) == completeName) {
                    // nameFound resta false
                }
                else {
                    nameFound = true;
                    break;
                }
            }
            nameFound = true;
        }

        String batchFileName = Integer.toString(candidateName) + "." + batchExtension;

        return batchFileName;
    }

    protected LinkedList<String> getTerminalOutput() {
        LinkedList<String> commandList = new LinkedList<String>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                commandList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    protected abstract File createFile(String dirPath, String fileString);

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
}