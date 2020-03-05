package tool;

import org.apache.commons.io.FileUtils;
import tool.exceptions.erroreCancellazioneFileException;
import tool.exceptions.osNotRecognizedException;
import org.json.simple.parser.ParseException;
import utils.FileManager;
import utils.JsonUtils;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public abstract class Tool {
    LinkedList<String> fileList;
    Terminal terminale1;
    static String analysisDirPath;
    static String nameDirOriginalFiles; // Nome directory contenente i file originali
    static String nameDirModifiedFiles; // Nome directory contenent i file con invokemethod sostituito
    static String cwbPath;
    static String nameDirFileBatch;

    public Tool() {
        // Initialize "nameDirFileBatch"
        nameDirFileBatch = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "batch_files_path"));
        FileManager.deleteDirectoryWithRelativePath(nameDirFileBatch);
        FileManager.createDirectoryWithRelativePath(nameDirFileBatch);

        // Initialize "cwbPath"
        cwbPath = "";
        String relativeCwbPath = "";

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                relativeCwbPath = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "cwb_linux_path"));
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                relativeCwbPath = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "cwb_windows_path"));
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        cwbPath = FileManager.trasformRelativeToAbsolutePath(relativeCwbPath);
    }

    /**
     * Questo metodo implementa il design pattern 'Template method'
     */
    public void run() throws IOException, osNotRecognizedException, ParseException {
        controllaEsistenzaJson();
        
        terminale1 = createTerminal();

        getDirPath();

        rimuoviFileNonCcs(analysisDirPath);

        processFiles(fileList);

        System.exit(0);
    }

    protected void deleteBatchFile() {
        File folder = new File(nameDirFileBatch);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            // Se è un file
            if (listOfFiles[i].isFile()) {
                File f = new File(buildPath(nameDirFileBatch, listOfFiles[i].getName()));
                f.delete();
            }
            // Altrimenti, se è una directory
            else if (listOfFiles[i].isDirectory()) {
                // Non fare nulla
            }
        }
    }

    private void controllaEsistenzaJson() {
        String jsonPath = buildPath(System.getProperty("user.dir"), "src", "json");
        LinkedList<String> fileList = getFileList(jsonPath);
        boolean fileEsiste = false;
        String nomeFile;

        while (fileList.size() > 0) {
            nomeFile = fileList.remove();

            if (nomeFile.equals("parametri.json")) {
                fileEsiste = true;
            }
        }

        if (!fileEsiste) {
            System.out.println("Il file 'parametri.json' non esiste. E' necessario crearlo, " +
                    "seguendo le istruzioni riportate nel file 'src/json/istruzioni.txt'.");
            System.exit(0);
        }
    }

    private void getDirPath() {
        String relativeAnalysisDirPath = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "ccs_analysis_path"));
        analysisDirPath = FileManager.trasformRelativeToAbsolutePath(relativeAnalysisDirPath);
    }

    protected void rimuoviFileNonCcs(String dirPath) {
        LinkedList<String> fileNonCcs = new LinkedList<String>();
        String fileString;

        LinkedList<String> fileList = Tool.getFileList(dirPath);

        while (fileList.size() > 0){
            fileString = fileList.remove();

            if (!fileString.endsWith(".ccs")){
                fileNonCcs.add(fileString);
            }
        }

        while (fileNonCcs.size() > 0) {
            fileString = fileNonCcs.remove();

            File fileDaCancellare = createFile(dirPath, fileString);

            if (fileDaCancellare.delete()) {
                // La cancellazione ha avuto successo. Non fare niente
            } else {
                try {
                    throw new erroreCancellazioneFileException(fileString);
                } catch (erroreCancellazioneFileException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private Terminal createTerminal() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX){
                return new TerminalLinux();
            }
            else if(OsUtils.getOsType() == OsType.WINDOWS){
                return new TerminalWindows();
            }
            else {
                try {
                    throw new osNotRecognizedException();
                } catch (osNotRecognizedException e) {
                    e.printStackTrace();
                }
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }
        return new TerminalLinux();
    }

    public static void stampaStringList(LinkedList<String> stringList) {
        while (stringList.size() > 0) {
            System.out.println(stringList.remove());
        }
    }

    public static void busyWaiting(int durata) {
        Timestamp time1 = new Timestamp(System.currentTimeMillis());
        Timestamp time2 = new Timestamp(System.currentTimeMillis());

        long time1millis = time1.getTime();
        long time2millis = time2.getTime();
        long diffTimeMillis = time2millis - time1millis;

        while (diffTimeMillis < durata) {
            time2 = new Timestamp(System.currentTimeMillis());
            time2millis = time2.getTime();
            diffTimeMillis = time2millis - time1millis;
        }
    }

    static public LinkedList<String> getFileList(String dirPath) {
        LinkedList<String> fileList = new LinkedList<String>();

        File folder = new File(dirPath);
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

        return fileList;
    }

    private void debugTerminal() throws osNotRecognizedException, IOException {
        if (OsUtils.getOsType() == OsType.LINUX) {
            terminale1.addCommand("cd ~");
            terminale1.addCommand("ls -l");
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            terminale1.addCommand("cd %HOMEPATH%");
            terminale1.addCommand("dir");
        }
    }

    private void processFiles(LinkedList<String> fileList) throws IOException, ParseException, osNotRecognizedException {
        copyFilesToDirectory();
        processOriginalFiles();
//        processModifiedFiles();
    }

    private void copyFilesToDirectory() {
        nameDirOriginalFiles = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "original_files_path"));
        nameDirModifiedFiles = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", "modified_files_path"));

        // Crea cartella per i file originali
        String nomeCartella = buildPath(analysisDirPath, nameDirOriginalFiles);
        File dir = new File(nomeCartella);
        dir.mkdir();

        // Crea cartella per i file invokeMethodSostituito
        nomeCartella = buildPath(analysisDirPath, nameDirModifiedFiles);
        dir = new File(nomeCartella);
        dir.mkdir();

        copiaFileInDirectory(nameDirOriginalFiles);
        copiaFileInDirectory(nameDirModifiedFiles);
        rimuoviFileInDirectory(analysisDirPath);
    }

    private void copiaFileInDirectory(String nomeDirectory) {
        LinkedList<String> fileList = Tool.getFileList(analysisDirPath);
        String nomeFile;

        while (fileList.size() > 0) {
            nomeFile = fileList.remove();

            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(buildPath(analysisDirPath, nomeFile));
                os = new FileOutputStream(buildPath(analysisDirPath, nomeDirectory, nomeFile));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getJsonParameter(String jsonObject, String chiave) {
        String valore = "";

        valore = JsonUtils.readValue("src/json/parametri.json", jsonObject, chiave);

        return valore;
    }

    /**
     * Per ciascun .ccs calcola la size dei metodi
     */
    private void processOriginalFiles() {
        processCwbFiles("original_files_path", CcsFileType.ORIGINAL);
    }

    private void processCwbFiles(String fileDir, CcsFileType ccsFileType) {
        LinkedList<String> fileList = new LinkedList<String>();
        String nameSubdirectory = JsonUtils.localizeDirectorySeparator(JsonUtils.readValue("src/json/parametri.json", "parametri", fileDir));

        fileList = getFileList(buildPath(analysisDirPath, nameSubdirectory));
        String filePath;

        while (fileList.size() > 0) {
            filePath = buildPath(analysisDirPath, nameSubdirectory, fileList.remove());
            processFile(filePath, ccsFileType);
        }
    }

    LinkedList<String> getMetodiList(String filePath) {
        LinkedList<String> metodiList = new LinkedList<String>();
        String stringaEstratta;
        String methodString;
        String procAllString = "";
        int plusIndex = 0;

        List<String> lines = null;
        try {
            lines = FileUtils.readLines(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (lines.size() > 0) {
            stringaEstratta = lines.remove(0);
            if (stringaEstratta.startsWith("proc ALL")) {
                procAllString = stringaEstratta;
            }
        }

        //tagliamo i primi 10 caratteri
        procAllString = procAllString.substring(10, procAllString.length());

        while (esistePlus(procAllString)){
            plusIndex = getIndexNextPlus(procAllString);
            methodString = procAllString.substring(0, plusIndex - 1);
            metodiList.add(methodString);
            procAllString = procAllString.substring(plusIndex + 1, procAllString.length());
        }

        return metodiList;
    }

    private boolean esistePlus(String procAllString) {
        boolean plusEsiste = false;

        for (int i = 0; i < procAllString.length(); i++) {
            if (procAllString.charAt(i) == '+') {
                plusEsiste = true;
            }
        }

        return plusEsiste;
    }

    private int getIndexNextPlus(String procAllString) {
        int i;

        for (i = 0; i < procAllString.length(); i++) {
            if (procAllString.charAt(i) == '+') {
                break;
            }
        }

        return i;
    }

    /**
     * Process .ccs file. The "invoke*" calls are substituted with "t" (tau) and for each method we take the
     * original size and the reduced size, and we add these two number to a list.
     * @param filePath The path of the file being analyzed.
     * @param ccsFileType The type of the file ("ORIGINAL" or "OBFUSCATED" if we are in the training phase,
     *                    "TEST" if we are in the testing phase.
     */
    private void processFile(String filePath, CcsFileType ccsFileType) {
        LinkedList<String> metodiList = getMetodiList(filePath);
        String nomeMetodo;
        String dir;
        Terminal terminal = createTerminal();

        while (metodiList.size() > 0) {
            try {
                if (OsUtils.getOsType() == OsType.LINUX) {
                    terminal = new TerminalLinux();
                }
                else if (OsUtils.getOsType() == OsType.WINDOWS){
                    terminal = new TerminalWindows();
                }
            } catch (osNotRecognizedException e) {
                e.printStackTrace();
            }

            nomeMetodo = metodiList.remove();

            dir = buildPath(analysisDirPath, nameDirOriginalFiles);

            terminal.addCommand("cd " + dir);

            startCwb(terminal);

            terminal.addCommand("load " + filePath);

            terminal.executeCommands();

            stampaStringList(terminal.getTerminalOutput());

            int sizeMetodo = terminal.getSizeSingoloMetodo(nomeMetodo);
        }
    }

    protected abstract void startCwb(Terminal terminal);

    protected abstract String buildPath(String pathParte1, String parthParte2);

    private String buildPath(String pathParte1, String pathParte2, String pathParte3) {
        return buildPath(buildPath(pathParte1, pathParte2), pathParte3);
    }

    private void stampaFileList(LinkedList<String> fileList) {
        System.out.println("Num file: " + fileList.size());

        while (fileList.size() > 0) {
            System.out.println(fileList.remove());
        }
    }

    static public LinkedList<String> getFileNonCcs(LinkedList<String> fileList) {
        LinkedList<String> fileNonCcsList = new LinkedList<String>();
        String fileString;

        while (fileList.size() > 0) {
            fileString = fileList.remove();

            if (!fileString.endsWith(".ccs")) {
                fileNonCcsList.add(fileString);
            }
        }

        return fileNonCcsList;
    }

    private void rimuoviFileInDirectory(String directoryPath) {
        LinkedList<String> fileList = new LinkedList<String>();
        File fileDaCancellare;
        String fileName;

        File folder = new File(analysisDirPath);
        File[] listOfFiles = folder.listFiles();

        // Seleziona i file scartando le directory
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

        // Elimina i file
        while (fileList.size() > 0) {
            fileName = fileList.remove();
            fileDaCancellare = createFile(analysisDirPath, fileName);

            if (fileDaCancellare.delete()) {
                // La cancellazione ha avuto successo. Non fare niente
            } else {
                try {
                    throw new erroreCancellazioneFileException(fileName);
                } catch (erroreCancellazioneFileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract File createFile(String dirPath, String fileString);
}