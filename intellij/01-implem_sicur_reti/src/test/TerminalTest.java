package test;

import tool.*;
import tool.exceptions.OsNotRecognizedException;

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
        } catch (OsNotRecognizedException e) {
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
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }
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
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }

        shell.createFile();
        shell.createFile();
        shell.createFile();
    }
}
