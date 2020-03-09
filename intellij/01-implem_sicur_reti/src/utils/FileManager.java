package utils;

import tool.OsType;
import tool.OsUtils;
import tool.ToolLinux;
import tool.ToolWindows;
import tool.exceptions.osNotRecognizedException;

import java.io.File;
import java.nio.file.Files;

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
     * Converts a relative path of a directory to the corresponding absolute path. The relative path is
     * assumed to be based on the Java project main directory.
     * @param   relativePath   The relative path of the directory.
     * @return   The absolute path of the directory.
     */
//    private String getAbsoluteDirPath(String relativePath) {
//
//    }
}
