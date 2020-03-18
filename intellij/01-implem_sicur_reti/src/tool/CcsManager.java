package tool;

import utils.FileManager;

import java.util.LinkedList;

public class CcsManager {
    public static LinkedList<String> getMethodsListInProcAll(String filePath) {
        LinkedList<String> textLinesList = FileManager.getTextLinesInFile(filePath);
        LinkedList<String> methodsList = new LinkedList<String>();
        String procAllLine = "";
        String methodString = "";
        int i = textLinesList.size() - 1;
        int plusPosition = 0;

        // Assign to "i" the value of the index where "proc ALL" is
        for (i = textLinesList.size() - 1; i >= 0; i--) {
            procAllLine = textLinesList.get(i);

            if (procAllLine.startsWith("proc ALL")) {
                break;
            }
        }

        // Remove "proc ALL= " at the beginning of the line
        procAllLine = procAllLine.substring(10);

        System.out.println(procAllLine);

        while (existsPlus(procAllLine)) {
            plusPosition = getPlusPosition(procAllLine);

            methodString = procAllLine.substring(0, plusPosition);
            methodsList.add(methodString);

            procAllLine = procAllLine.substring(plusPosition + 1);
        }

        methodsList.add(procAllLine);

        return methodsList;
    }

    private static int getPlusPosition(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '+') {
                return i;
            }
        }

        // If plus is not found
        return -1;
    }

    private static boolean existsPlus(String line) {
        boolean plusExists = false;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '+') {
                plusExists = true;
            }
        }

        return plusExists;
    }

    /**
     *
     * @param fileName Absolute path to the file.
     * @param methodName Name of the CWB method.
     * @return Number of states of the method.
     */
    public static int getMethodSize(String fileName, String methodName) {
        Shell shell = Shell.createShell();

        shell.executeCwb();

        shell.addCommand("load " + fileName);
        shell.addCommand("size " + methodName);
        shell.addCommand("quit");

        return getNumberOfStates(shell.getOutputList());
    }

    private static int getNumberOfStates(LinkedList<String> outputList) {
        String numberOfStatesAsString = "";
        int numberOfStates = 0;
        String line = "";

        for (int i = 0; i < outputList.size(); i++) {
            line = outputList.get(i);

            if (line.startsWith("States: ")) {
                numberOfStatesAsString = line.substring(8);
                numberOfStates = Integer.valueOf(numberOfStatesAsString);
            }
        }

        return numberOfStates;
    }
}