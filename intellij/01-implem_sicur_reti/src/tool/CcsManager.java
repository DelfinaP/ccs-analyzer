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
     * @param filePath Absolute path to the file.
     * @param methodName Name of the CWB method.
     * @return Number of states of the method.
     */
    public static int getMethodSize(String filePath, String methodName) {
        Shell shell = Shell.createShell();

        shell.startCwb();

        shell.addCommand("load " + filePath);
        shell.addCommand("size " + methodName);
        shell.addCommand("quit");

        shell.executeCommands();

        return getNumberOfStates(shell.getOutputList());
    }

    public static int getNumberOfStates(LinkedList<String> outputList) {
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

    public static boolean isMethodCallSequenceLinear(String filePath, String method) {
        // If the call has the form "return.nil" or "allreturn.nil"
        if (isMethodCallEqualToReturn(filePath, method)) {
            return true;
        }
        // If the method call is linear
        else if (isMethodCallLinear(filePath, method)) {
            // Recursively call the method
            return isMethodCallSequenceLinear(filePath, getInvokedMethod(filePath, method));
        }
        // Otherwise, if the method call is not linear
        else {
            return false;
        }
    }

    private static boolean isMethodCallEqualToReturn(String filePath, String method) {
        String instruction = getMethodInvocation(filePath, method);

        if (instruction.equals("return.nil") || instruction.equals("allreturn.nil")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Search for the text line beginning with <code>proc method</code>, and returns the method invoke at the right
     * of the '=' sign.
     * @param filePath The file containing the text line.
     * @param method The method in <code>proc method</code>.
     * @return The method invoked at the right of the '=' sign.
     */
    private static String getInvokedMethod(String filePath, String method) {
        String instruction = getMethodInvocation(filePath, method);

        int charIndex = 0;

        while (isCharAlphanumeric(instruction.charAt(charIndex))) {
            charIndex++;
        }

        if (instruction.charAt(charIndex) == '.') {
            charIndex++;
        }

        return instruction.substring(charIndex);
    }

    /**
     * Search the line in the file beginning with <code>proc method</code>, and check if the method invocation
     * at the right of the '=' sign is linear.
     * @param filePath The file containing the line.
     * @param method The method name in <code>proc method</code>.
     * @return Whether the method call is linear.
     */
    private static boolean isMethodCallLinear(String filePath, String method) {
        String instruction = getMethodInvocation(filePath, method);

        int charIndex = 0;

        while (isCharAlphanumeric(instruction.charAt(charIndex))) {
            charIndex++;
        }

        if (instruction.charAt(charIndex) == '.') {
            charIndex++;
        }

        while (charIndex < instruction.length() && isCharAlphanumeric(instruction.charAt(charIndex))) {
            charIndex++;
        }

        if (charIndex == instruction.length()) {
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean isCharAlphanumeric(char character) {
        int charCode = Integer.valueOf(character);

        if ((charCode >= 48 && charCode < 58) || (charCode >= 65 && charCode < 91) ||
                (charCode >= 97 && charCode < 123)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Search the text line beginning with <code>proc method</code>, and returns the substring of that text line
     * starting at the right of the "=" sign, excluding white spaces.
     * @param filePath The file containing the text line.
     * @param method The method name in <code>proc method</code>.
     * @return The substring at the right of the "=" sign.
     */
    private static String getMethodInvocation(String filePath, String method) {
        String line = getTextLineBeginningWith(filePath, "proc " + method);
        int equalSignPosition = 0; // The position of the "=" character

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '=') {
                equalSignPosition = i;
                break;
            }
        }

        int startIndex = equalSignPosition + 1;

        // Search the first character different from the white space character
        while (line.charAt(startIndex) == ' ') {
            startIndex++;
        }

        return line.substring(startIndex);
    }

    private static String getTextLineBeginningWith(String filePath, String string) {
        LinkedList<String> textLinesList = FileManager.getTextLinesInFile(filePath);
        String line = "";

        // Assign to "i" the value of the index where "proc ALL" is
        for (int i = 0; i < textLinesList.size(); i++) {
            line = textLinesList.get(i);

            if (line.startsWith(string)) {
                return line;
            }
        }

        return null;
    }

    public static void substituteInvocationWithTau(String filePath, String method) {
        // If the call has the form "return.nil" or "allreturn.nil"
        if (isMethodCallEqualToReturn(filePath, method)) {
            substituteReturnWithTau();
        }
        // If the method call is linear
        else if (isMethodCallLinear(filePath, method)) {
            // Recursively call the method
            return isMethodCallSequenceLinear(filePath, getInvokedMethod(filePath, method));
        }
        // Otherwise, if the method call is not linear
        else {
            return false;
        }
    }

    private static void substituteReturnWithTau() {

    }
}