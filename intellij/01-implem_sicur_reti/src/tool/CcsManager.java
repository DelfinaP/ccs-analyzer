package tool;

import utils.FileManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
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

    public static boolean isMethodCallSequenceValid(String filePath, String method) {
        // If the call has the form "return.nil" or "allreturn.nil"
        if (isMethodPassedAsArgumentEqualToNil(filePath, method)) {
            return true;
        }
        // If the instruction is 'invokeinit'
        else if (isInstructionEqualsToInvokeinit(filePath, method)) {
            return false;
        }
        // If the method call is linear
        else if (isMethodCallLinear(filePath, method)) {
            // Recursively call the method
            return isMethodCallSequenceValid(filePath, getMethodPassedAsArgument(filePath, method));
        }
        // Otherwise, if the method call is not linear
        else {
            return false;
        }
    }

    private static boolean isMethodPassedAsArgumentEqualToNil(String filePath, String method) {
        String methodPassedAsArgument = getMethodPassedAsArgument(filePath, method);

        if (methodPassedAsArgument.equals("nil")) {
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
    private static String getMethodPassedAsArgument(String filePath, String method) {
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
                (charCode >= 97 && charCode < 123) || charCode == 95) {
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

    private static String getInstruction(String filePath, String method) {
        String methodInvocation = getMethodInvocation(filePath, method);

        int i = 0;

        // Search the first point character
        while (methodInvocation.charAt(i) != '.') {
            i++;
        }

        return methodInvocation.substring(0, i);
    }

    private static boolean isInstructionEqualsToInvokeinit(String filePath, String method) {
        String instruction = getInstruction(filePath, method);

        if (instruction.equals("invokeinit")) {
            Tool.invokeinitMethodsCount++;

            return true;
        }
        else {
            return false;
        }
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

    public static void replaceInstructionsWithTau(String filePath, String method) {
        // If the method passed as argument is equal to "nil"
        if (isMethodPassedAsArgumentEqualToNil(filePath, method)) {
            replaceInstructionWithTauInFile(filePath, method);
        }
        // If the method passed as argument is different from "nil"
        else {
            replaceInstructionWithTauInFile(filePath, method);

            // Recursively call the method
            replaceInstructionsWithTau(filePath, getMethodPassedAsArgument(filePath, method));
        }
    }

    private static String replaceInstruction(String line, String newInstruction) {
        int equalSignPosition = 0; // The position of the "=" character

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '=') {
                equalSignPosition = i;
                break;
            }
        }

        int charIndex = equalSignPosition + 1;

        // Search the first character different from the white space character
        while (line.charAt(charIndex) == ' ') {
            charIndex++;
        }

        int instructionStart = charIndex;

        // Search the position of the character '.'
        while (line.charAt(charIndex) != '.') {
            charIndex++;
        }

        int instructionEnd = charIndex;

        String newLine = "";
        newLine = newLine.concat(line.substring(0, instructionStart));
        newLine = newLine.concat(newInstruction);
        newLine = newLine.concat(line.substring(instructionEnd));

        return newLine;
    }

    private static void replaceLineInFile(String filePath, String originalString, String modifiedString) {
        try {
            // Input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            LinkedList<String> linesList = new LinkedList<String>();

            while ((line = file.readLine()) != null) {
                linesList.add(line);
            }
            file.close();

            // Search 'originalString'
            int i = 0;
            while (i < linesList.size() && !linesList.get(i).equals(originalString)) {
                i++;
            }

            if (i < linesList.size()) {
                // Substitute 'originalString' with 'modifiedString'
                linesList.set(i, modifiedString);

                // Add text lines to inputBuffer
                for (int j = 0; j < linesList.size(); j++) {
                    inputBuffer.append(linesList.get(j));
                    inputBuffer.append('\n');
                }

                // Overwrite the file substituting 'originalString' with 'modifiedString'
                FileOutputStream fileOut = new FileOutputStream(filePath);
                fileOut.write(inputBuffer.toString().getBytes());
                fileOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void replaceInstructionInFile(String filePath, String method, String newInstruction) {
        // Substitute invocation with "tau"
        String originalLine = getTextLineBeginningWith(filePath, "proc " + method);
        String modifiedLine = replaceInstruction(originalLine, newInstruction);
        replaceLineInFile(filePath, originalLine, modifiedLine);
    }

    private static void replaceInstructionWithTauInFile(String filePath, String method) {
        replaceInstructionInFile(filePath, method, "t");
    }

    /**
     * Compute the size of "ALL" method in an original file.
     * @param originalFilePath The path file in the folder of the original files.
     * @return The size of "ALL"
     */
    public static int computeSizeAll(String originalFilePath) {
        return getMethodSize(originalFilePath, "ALL");
    }

    public static int computeSizeAllMin(String modifiedFilePath) {
        Shell shell = Shell.createShell();

        shell.startCwb();

        shell.addCommand("load " + modifiedFilePath);
        shell.addCommand("min -S obseq ALL all_min");
        shell.addCommand("size all_min");
        shell.addCommand("quit");

        shell.executeCommands();

        return getNumberOfStates(shell.getOutputList());
    }
}