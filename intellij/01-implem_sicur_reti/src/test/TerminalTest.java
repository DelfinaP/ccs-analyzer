package test;

import tool.*;
import tool.exceptions.osNotRecognizedException;

public class TerminalTest {

    public static void main(String[] args) {

        testTerminalExecution();
    }

    private static void testTerminalExecution() {
        Shell shell = null;
        Tool tool = null;

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                shell = new ShellLinux();
                tool = new ToolLinux();
            }
            else {
                if (OsUtils.getOsType() == OsType.WINDOWS){
                    shell = new ShellWindows();
                    tool = new ToolWindows();
                }
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                shell.addCommand("cd ../");
                shell.addCommand("ls -l");
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                //shell.addCommand("@ECHO OFF");
                shell.addCommand("cd ../");
                shell.addCommand("dir");
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

//        shell.executeCommands();

//        tool.printStringList(shell.getTerminalOutput());
    }

    private static void testCreateFile() {
        Shell shell = null;
        Tool tool = null;

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                shell = new ShellLinux();
                tool = new ToolLinux();
            }
            else {
                if (OsUtils.getOsType() == OsType.WINDOWS){
                    shell = new ShellWindows();
                    tool = new ToolWindows();
                }
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        shell.createFile();
        shell.createFile();
        shell.createFile();
    }
}
