package test;

import tool.*;
import tool.exceptions.osNotRecognizedException;

public class TerminalTest {

    public static void main(String[] args) {

        testTerminalExecution();
    }

    private static void testTerminalExecution() {
        Terminal terminal = null;
        Tool tool = null;

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                terminal = new TerminalLinux();
                tool = new ToolLinux();
            }
            else {
                if (OsUtils.getOsType() == OsType.WINDOWS){
                    terminal = new TerminalWindows();
                    tool = new ToolWindows();
                }
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                terminal.addCommand("cd ../");
                terminal.addCommand("ls -l");
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                //terminal.addCommand("@ECHO OFF");
                terminal.addCommand("cd ../");
                terminal.addCommand("dir");
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        terminal.executeCommands();

        tool.printStringList(terminal.getTerminalOutput());
    }

    private static void testCreateFile() {
        Terminal terminal = null;
        Tool tool = null;

        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                terminal = new TerminalLinux();
                tool = new ToolLinux();
            }
            else {
                if (OsUtils.getOsType() == OsType.WINDOWS){
                    terminal = new TerminalWindows();
                    tool = new ToolWindows();
                }
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }

        terminal.createFile();
        terminal.createFile();
        terminal.createFile();
    }
}
