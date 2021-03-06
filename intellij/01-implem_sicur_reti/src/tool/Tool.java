package tool;

import org.apache.commons.io.FileUtils;
import tool.exceptions.FileDeletionErrorException;
import tool.exceptions.OsNotRecognizedException;
import org.json.simple.parser.ParseException;
import utils.FileManager;
import utils.JsonUtils;

import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public abstract class Tool {
    final static int sizeTreshold = 5; // The treshold of the methods' size to be considered in the analysis

    LinkedList<String> fileNamesList;
    static String analysisDirPath;
    static String nameDirOriginalFiles; // Name of directory containing original files
    static String nameDirModifiedFiles; // Name of directory containing modified files
    static String cwbPath;
    static String nameDirBatchFile;

    static int sumSizeAll;
    static int sumSizeAllMin;
    static int validMethodsCount;
    static int notValidMethodsCount;
    static int invokeinitMethodsCount;

    public Tool() {
        sumSizeAll = 0;
        sumSizeAllMin = 0;

        validMethodsCount = 0;
        notValidMethodsCount = 0;
        invokeinitMethodsCount = 0;

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

        if (!FileManager.filesAlreadyProcessed()) {
            removeNonCcsFiles(analysisDirPath);

            distributeFilesToDirectories();

            fileNamesList = getFileNamesList(FileManager.getOriginalFilesPath());

            processFiles(fileNamesList);

            outputResults();
        }
        else {
            System.out.println("Files already processed.");
        }

        System.exit(0);
    }

    private void outputResults() {
        System.out.println("#Valid: " + validMethodsCount);
        System.out.println("#Not Valid: " + notValidMethodsCount);
        System.out.println("#Invokeinit: " + invokeinitMethodsCount);
        System.out.println("#Total: " + (validMethodsCount + notValidMethodsCount));

        double reductionMean = (sumSizeAll - sumSizeAllMin) / (double) sumSizeAll * 100;

        if (reductionMean >= 10) {
            System.out.printf("Mean: %5.2f%n", reductionMean);
        }
        else {
            System.out.printf("Mean: %4.2f%n", reductionMean);
        }
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
        for (int i = 0; i < stringList.size(); i++) {
            System.out.println(stringList.get(i));
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
        String originalDirectoryName = FileManager.buildPath(analysisDirPath, nameDirOriginalFiles);
        File originalDir = new File(originalDirectoryName);
        originalDir.mkdir();

        // Create directory for modified files
        String modifiedDirectoryName = FileManager.buildPath(analysisDirPath, nameDirModifiedFiles);
        File modifiedDir = new File(modifiedDirectoryName);
        modifiedDir.mkdir();

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

        for (int i = 0; i < fileList.size(); i++) {
            fileName = fileList.get(i);

            File sourceFile = new File(FileManager.buildPath(analysisDirPath, fileName));
            File destinationFile = new File(FileManager.buildPath(analysisDirPath, directoryName, fileName));

            try {
                Files.copy(sourceFile.toPath(), destinationFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processFile(String fileName) {
        // Take the list of the methods in "proc ALL"
        String filePath = FileManager.getOriginalFilePathFromName(fileName);

        LinkedList<String> methodsList = CcsManager.getMethodsListInProcAll(filePath);

        System.out.println("Processing file: " + fileName);

        for (String method : methodsList) {
            processMethod(fileName, method);
        }

        // Compute 'sizeAll'
        String originalFilePath = FileManager.getOriginalFilePathFromName(fileName);
        int sizeAll = CcsManager.computeSizeAll(originalFilePath);

        // Compute 'sizeAllMin'
        String modifiedFilePath = FileManager.getModifiedFilePathFromName(fileName);
        int sizeAllMin = CcsManager.computeSizeAllMin(modifiedFilePath);

        // Add 'sizeAll' e 'sizeAllMin' to sum
        sumSizeAll += sizeAll;
        sumSizeAllMin += sizeAllMin;
    }

    private void processMethod(String fileName, String method) {
        String originalFilePath = FileManager.getOriginalFilePathFromName(fileName);
        String modifiedFilePath = FileManager.getModifiedFilePathFromName(fileName);

        int methodSize = CcsManager.getMethodSize(originalFilePath, method);

        // Filter methods with size <= sizeTreshold
        if (methodSize <= sizeTreshold) {
            // Call a method which navigates the sequence of method calls, and returns a boolean that indicates
            // if the sequence is linear or not
            boolean isValid = CcsManager.isMethodCallSequenceValid(originalFilePath, method);

            // If the method call sequence is linear, make the substitutions
            if (isValid) {
                System.out.println("Valid:");
                System.out.println(fileName);
                System.out.println(method);

                validMethodsCount++;

                CcsManager.replaceInstructionsWithTau(modifiedFilePath, method);
            }
            else {
                System.out.println("Not Valid:");
                System.out.println(fileName);
                System.out.println(method);

                notValidMethodsCount++;
            }
        }
        else {
            // Do nothing
        }
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