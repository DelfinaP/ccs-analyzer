package tool;

import org.apache.commons.io.FileUtils;
import tool.exceptions.FileDeletionErrorException;
import tool.exceptions.OsNotRecognizedException;
import org.json.simple.parser.ParseException;
import utils.FileManager;
import utils.JsonUtils;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public abstract class Tool {
    LinkedList<String> fileNamesList;
    static String analysisDirPath;
    static String nameDirOriginalFiles; // Nome directory contenente i file originali
    static String nameDirModifiedFiles; // Nome directory contenent i file con invokemethod sostituito
    static String cwbPath;
    static String nameDirBatchFile;

    public Tool() {
        // Initialize "nameDirFileBatch"
        nameDirBatchFile = JsonUtils.readValue("src/json/parametri.json", "parametri", "batch_files_path");
        FileManager.deleteDirectoryWithRelativePath(nameDirBatchFile);
        FileManager.createDirectoryWithRelativePath(nameDirBatchFile);

        // Initialize "cwbPath"
        cwbPath = "";
        String relativeCwbPath = "";

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                relativeCwbPath = JsonUtils.readValue("src/json/parametri.json", "parametri", "cwb_linux_path");
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                relativeCwbPath = JsonUtils.readValue("src/json/parametri.json", "parametri", "cwb_windows_path");
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        cwbPath = FileManager.trasformRelativeToAbsolutePath(relativeCwbPath);
    }

    private Shell createTerminal() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX){
                return new ShellLinux();
            }
            else if(OsUtils.getOsType() == OsType.WINDOWS){
                return new ShellWindows();
            }
            else {
                try {
                    throw new OsNotRecognizedException();
                } catch (OsNotRecognizedException e) {
                    e.printStackTrace();
                }
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }
        return new ShellLinux();
    }

    /**
     * Questo metodo implementa il design pattern 'Template method'
     */
    public void run() {
        checkJsonExistence();

        getDirPath();

        removeNonCcsFiles(analysisDirPath);

        distributeFilesToDirectories();

        System.out.println(FileManager.getOriginalFilesPath());
        fileNamesList = getFileNamesList(FileManager.getOriginalFilesPath());

        processFiles(fileNamesList);

        System.exit(0);
    }

    protected void deleteBatchFile() {
        File folder = new File(nameDirBatchFile);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            // Se è un file
            if (listOfFiles[i].isFile()) {
                File f = new File(FileManager.buildPath(nameDirBatchFile, listOfFiles[i].getName()));
                f.delete();
            }
            // Altrimenti, se è una directory
            else if (listOfFiles[i].isDirectory()) {
                // Non fare nulla
            }
        }
    }

    private void checkJsonExistence() {
        String jsonPath = FileManager.buildPath(System.getProperty("user.dir"), "src", "json");
        LinkedList<String> fileList = getFileNamesList(jsonPath);
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
        String relativeAnalysisDirPath = JsonUtils.readValue("src/json/parametri.json", "parametri", "ccs_analysis_path");
        analysisDirPath = FileManager.trasformRelativeToAbsolutePath(relativeAnalysisDirPath);
    }

    protected void removeNonCcsFiles(String dirPath) {
        LinkedList<String> nonCcsFiles = new LinkedList<String>();
        String fileString;

        LinkedList<String> fileList = Tool.getFileNamesList(dirPath);

        while (fileList.size() > 0){
            fileString = fileList.remove();

            if (!fileString.endsWith(".ccs")){
                nonCcsFiles.add(fileString);
            }
        }

        while (nonCcsFiles.size() > 0) {
            fileString = nonCcsFiles.remove();

            File filesToBeDeleted = createFile(dirPath, fileString);

            if (filesToBeDeleted.delete()) {
                // Deletion successful. Do nothing
            } else {
                try {
                    throw new FileDeletionErrorException(fileString);
                } catch (FileDeletionErrorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printStringList(LinkedList<String> stringList) {
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

    static public LinkedList<String> getFileNamesList(String dirPath) {
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

    private void processFiles(LinkedList<String> fileNameList) {
        for (String fileName : fileNameList) {
            processFile(fileName);
        }
    }

    /**
     * Copy .ccs files into two folders, the first for the original files, the second for the modified files.
     */
    private void distributeFilesToDirectories() {
        nameDirOriginalFiles = JsonUtils.readValue("src/json/parametri.json", "parametri", "original_files_path");
        nameDirModifiedFiles = JsonUtils.readValue("src/json/parametri.json", "parametri", "modified_files_path");

        // Create directory for original files
        String directoryName = FileManager.buildPath(analysisDirPath, nameDirOriginalFiles);
        File dir = new File(directoryName);
        dir.mkdir();

        // Create directory for modified files
        directoryName = FileManager.buildPath(analysisDirPath, nameDirModifiedFiles);
        dir = new File(directoryName);
        dir.mkdir();

        copyFilesToDirectory(nameDirOriginalFiles);
        copyFilesToDirectory(nameDirModifiedFiles);
        removeFilesInDirectory(analysisDirPath);
    }

    /**
     * Copy .ccs files from the "analysisDirPath" into a subdirectory.
     * @param directoryName The subdirectory into which to copy files.
     */
    private void copyFilesToDirectory(String directoryName) {
        LinkedList<String> fileList = Tool.getFileNamesList(analysisDirPath);
        String fileName;

        while (fileList.size() > 0) {
            fileName = fileList.remove();

            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(FileManager.buildPath(analysisDirPath, fileName));
                os = new FileOutputStream(FileManager.buildPath(analysisDirPath, directoryName, fileName));
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

    private void processFile(String fileName) {
        // Take the list of the methods in "proc ALL"
        LinkedList<String> methodsList = CcsManager.getMethodsListInProcAll(fileName);

        for (String method : methodsList) {
            processMethod(fileName, method);
        }
    }

    private void processMethod(String fileName, String method) {
        //int methodSize = CcsManager.getMethodSize();
        // Filter methods with size <= 5
        //if (methodSize <= 5) {
            // Chiamiamo un metodo che ci naviga la sequenza delle chiamate, e ci restituisce un boolean che ci dice se è lineare oppure no

            // Se la sequenza è lineare, effettuiamo le sostituzioni

            // Calcoliamo size ALL
            //originalFilePath = buildAbsolutePathOriginalFile(fileName);
            //int sizeAll = computeSizeAll(originalFilePath, method);

            // Calcoliamo size all_min
            //modifiedFilePath = buildAbsolutePathModifiedFile(fileName);
            //int sizeAllMin = computeSizeAllMin(modifiedFilePath, method);

            // Calcoliamo percentuale riduzione
            //int reductionPercentage = (size ALL - size all_min) / size ALL * 100;

            // Aggiungi percentuale di riduzione alla lista
            //percentagesList.add(reductionPercentage);
        //}
        //else {
            // Do nothing
        //}
    }

    private String getJsonParameter(String jsonObject, String chiave) throws IOException, ParseException {
        String valore = "";

        valore = JsonUtils.readValue("src/json/parametri.json", jsonObject, chiave);

        return valore;
    }

    LinkedList<String> getMethodList(String filePath) {
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

    protected abstract void startCwb(Shell shell);

    private void printFileList(LinkedList<String> fileList) {
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

    private void removeFilesInDirectory(String directoryPath) {
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
                    throw new FileDeletionErrorException(fileName);
                } catch (FileDeletionErrorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract File createFile(String dirPath, String fileString);
}