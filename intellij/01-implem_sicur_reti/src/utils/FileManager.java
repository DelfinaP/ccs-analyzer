package utils;

import tool.OsType;
import tool.OsUtils;
import tool.ToolLinux;
import tool.ToolWindows;
import tool.exceptions.osNotRecognizedException;

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
        } catch (osNotRecognizedException e) {
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
        } catch (osNotRecognizedException e) {
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
}