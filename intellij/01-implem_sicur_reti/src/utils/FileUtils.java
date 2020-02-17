package utils;

import java.io.File;

public class FileUtils {
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

    public static void createDirectory(String dirPath) {
        String absoluteDirPath = getAbsoluteDirPath();

        new File(dirPath).mkdirs();
    }



    public static void deleteDirectory(String dirPath) {
        String absoluteDirPath = getAbsoluteDirPath();

        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    /**
     * Converts a relative path of a directory to the corresponding absolute path. The relative path is
     * assumed to be based on the Java project main directory.
     * @param   relativePath   The relative path of the directory.
     * @return   The absolute path of the directory.
     */
    private String getAbsoluteDirPath(String relativePath) {
        
    }
}
