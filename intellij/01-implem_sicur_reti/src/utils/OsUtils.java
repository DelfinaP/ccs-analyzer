package utils;

import utils.except.OsType;
import utils.except.osNotRecognizedException;

public class OsUtils
{
    private static String OS = null;
    private static OsType osType = null;

    public static String getOsName()
    {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static OsType getOsType() throws osNotRecognizedException {
        if (OS == null) {
            if (!osRecognized()) {
                throw new osNotRecognizedException();
            }
        }
        if (isWindows()) {
            osType = OsType.WINDOWS;
        }
        if (isLinux()) {
            osType = OsType.LINUX;
        }

        return osType;
    }

    public static boolean isWindows() throws osNotRecognizedException {
        if (!osRecognized()) {
            throw new osNotRecognizedException();
        }
        return getOsName().startsWith("Windows");
    }

    public static boolean isLinux() throws osNotRecognizedException {
        if (!osRecognized()) {
            throw new osNotRecognizedException();
        }
        return getOsName().startsWith("Linux");
    }

    public static boolean osRecognized() {
        return (getOsName().startsWith("Windows") || getOsName().startsWith("Linux"));
    }
}