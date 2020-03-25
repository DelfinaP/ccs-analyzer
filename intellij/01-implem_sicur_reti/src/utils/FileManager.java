package utils;

import tool.OsType;
import tool.OsUtils;
import tool.exceptions.OsNotRecognizedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class FileManager {
    public static String substituteSpacesWithEscapeChars(String originalString) {
        String modifiedString = "";
        char character = ' ';

        for (int i = 0; i < originalString.length(); i++) {
            character = originalString.charAt(i);
            if (character != ' ') {
                modifiedString = modifiedString.concat(String.valueOf(character));
            }
            else {
                modifiedString = modifiedString.concat("\\ ");
            }
        }

        return modifiedString;
    }

    public static void createDirectoryWithRelativePath(String relativeDirPath) {
        String absoluteDirPath = trasformRelativeToAbsolutePath(relativeDirPath);

        new File(absoluteDirPath).mkdirs();
    }

    public static String trasformRelativeToAbsolutePath(String relativeDirPath) {
        String projectDir = System.getProperty("user.dir");
        String dirSeparator = "";

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                dirSeparator = "/";
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                dirSeparator = "\\";
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        String absoluteDirPath = projectDir + dirSeparator + relativeDirPath;

        return absoluteDirPath;
    }

    public static void deleteDirectoryWithRelativePath(String relativeDirPath) {
        String absoluteDirPath = trasformRelativeToAbsolutePath(relativeDirPath);

        File dir = new File(absoluteDirPath);

        deleteDir(dir);
    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }

        file.delete();
    }

    public static String getProjectDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Transform a Linux path (a path having "/" as directory separator) in a path suited for the architecture
     * (the path is unchanged if executing on Linux, while is transformed changing "/" to "\" if executing on Windows)
     * @param linuxPath The path to be localized
     * @return The localized path
     */
    public static String localizeLinuxPath(String linuxPath) {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                return linuxPath;
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                String localizedPath = "";

                for (int i = 0; i < linuxPath.length(); i++) {
                    if (linuxPath.charAt(i) != '/') {
                        localizedPath = localizedPath.concat(String.valueOf(linuxPath.charAt(i)));
                    }
                    else {
                        localizedPath = localizedPath.concat("\\");
                    }
                }

                return localizedPath;
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return lines of text in a file.
     * @param filePath Path of the file
     * @return The list of the lines.
     */
    public static LinkedList<String> getTextLinesInFile(String filePath) {
        LinkedList<String> textLinesList = new LinkedList<String>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                textLinesList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return textLinesList;
    }

    public static String buildPath(String pathPart1, String pathPart2) {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                return pathPart1 + "/" + pathPart2;
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                return pathPart1 + "\\" + pathPart2;
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String buildPath(String pathPart1, String pathPart2, String pathPart3) {
        return buildPath(buildPath(pathPart1, pathPart2), pathPart3);
    }

    public static String getOriginalFilesPath() {
        return buildPath(getProjectDirectory(), JsonUtils.getCcsAnalysisJsonValue(), JsonUtils.getOriginalFilesJsonValue());
    }

    public static String getModifiedFilesPath() {
        return buildPath(getProjectDirectory(), JsonUtils.getCcsAnalysisJsonValue(), JsonUtils.getModifiedFilesJsonValue());
    }

    public static String getCcsAnalysisDirectoryPath() {
        return buildPath(getProjectDirectory(), JsonUtils.getCcsAnalysisJsonValue());
    }

    /**
     * Get the file name and returns the absolute path of the file contained in the original files' directory.
     * @param fileName The file we want the path of.
     * @return The absolute path of the file.
     */
    public static String getOriginalFilePathFromName(String fileName) {
        return buildPath(getOriginalFilesPath(), fileName);
    }

    /**
     * Get the file name and returns the absolute path of the file contained in the modified files' directory.
     * @param fileName The file we want the path of.
     * @return The absolute path of the file.
     */
    public static String getModifiedFilePathFromName(String fileName) {
        return buildPath(getModifiedFilesPath(), fileName);
    }

    /**
     * Check whether the files were already processed by the tool.
     * @return Whether the files were already processed by the tool.
     */
    public static boolean filesAlreadyProcessed() {
        String originalFileDirName = JsonUtils.getOriginalFilesJsonValue();
        String modifiedFileDirName = JsonUtils.getModifiedFilesJsonValue();

        // Get list of subdirectory in the directory "res/ccs-analysis"
        String directory = getCcsAnalysisDirectoryPath();

        LinkedList<String> fileList = new LinkedList<String>();

        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            // If it's a file
            if (listOfFiles[i].isFile()) {
                // Do nothing
            }
            // Otherwise, if it's a directory
            else if (listOfFiles[i].isDirectory()) {
                fileList.add(listOfFiles[i].getName());
            }
        }

        boolean originalFileDirExists = false;
        boolean modifiedFileDirExists = false;

        for (String file : fileList) {
            if (file.equals(originalFileDirName)) {
                originalFileDirExists = true;
            }
            else if (file.equals(modifiedFileDirName)) {
                modifiedFileDirExists = true;
            }
        }

        if (originalFileDirExists && modifiedFileDirExists) {
            return true;
        }
        else {
            return false;
        }
    }
}