package utils;

import tool.OsType;
import tool.OsUtils;
import tool.exceptions.OsNotRecognizedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Classes which executes commands from terminal
 */
public class CommandRunner {
    public static LinkedList<String> runCommand(String command) {
        try {
            LinkedList<String> terminalOutput = new LinkedList<String>();
            Process process = null;

            try {
                if (OsUtils.getOsType() == OsType.LINUX) {
                    process = buildCommandLinux(command);
                }
                else if (OsUtils.getOsType() == OsType.WINDOWS){
                    process = buildCommandWindows(command);
                }
            } catch (OsNotRecognizedException e) {
                e.printStackTrace();
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                terminalOutput.add(line);
            }

            int exitVal = 0;
            exitVal = process.waitFor();
            if (exitVal == 0) {
                return terminalOutput;
            } else {
                // Abnormal case
            }

            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <code>buildCommandLinux</code> specifies which command must be executed by the terminal in Linux.
     * @param   command   Command to be executed by the terminal.
     * @return   Process   <code>Process</code> class which allows the command to be executed.
     */
    private static Process buildCommandLinux(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            return process;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <code>buildCommandWindows</code> specifies which command must be executed by the terminal in Windows.
     * @param   command   Command to be executed by the terminal.
     * @return   Process   <code>Process</code> class which allows the command to be executed.
     */
    protected static Process buildCommandWindows(String command) {
        try {
            return Runtime.getRuntime().exec("cmd /c " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
