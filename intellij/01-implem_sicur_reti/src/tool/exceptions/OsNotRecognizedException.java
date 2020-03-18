package tool.exceptions;

import tool.OsUtils;

public class OsNotRecognizedException extends Throwable {
    public OsNotRecognizedException() {
        super("OS not recognized. OS supported: 'Windows' and 'Linux'. OS found: '" + OsUtils.getOsName() + "'");
    }
}
