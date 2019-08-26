package utils.except;

import utils.OsUtils;

public class osNotRecognizedException extends Throwable {
    public osNotRecognizedException() {
        super("OS not recognized. OS supported: 'Windows' and 'Linux'. OS found: '" + OsUtils.getOsName() + "'");
    }
}
