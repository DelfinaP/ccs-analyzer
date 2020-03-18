package tool;

import tool.exceptions.OsNotRecognizedException;

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

    public static OsType getOsType() throws OsNotRecognizedException {
        if (OS == null) {
            if (!osRecognized()) {
                throw new OsNotRecognizedException();
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

    public static boolean isWindows() throws OsNotRecognizedException {
        if (!osRecognized()) {
            throw new OsNotRecognizedException();
        }
        return getOsName().startsWith("Windows");
    }

    public static boolean isLinux() throws OsNotRecognizedException {
        if (!osRecognized()) {
            throw new OsNotRecognizedException();
        }
        return getOsName().startsWith("Linux");
    }

    public static boolean osRecognized() {
        return (getOsName().startsWith("Windows") || getOsName().startsWith("Linux"));
    }
}