package utils;

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
}
