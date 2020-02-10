package com.company.utils;

import com.company.utils.exceptions.osNotRecognizedException;

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

    public static OsType getOsType(){
        if (OS == null) {
            if (!osRecognized()) {
                try {
                    throw new osNotRecognizedException();
                } catch (osNotRecognizedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (isWindows()) {
                osType = OsType.WINDOWS;
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }
        try {
            if (isLinux()) {
                osType = OsType.LINUX;
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
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