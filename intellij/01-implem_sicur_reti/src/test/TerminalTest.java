package test;

import org.json.simple.parser.ParseException;
import tool.*;
import tool.except.osNotRecognizedException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

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
        } catch (IOException e) {
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

        tool.stampaStringList(terminal.getTerminalOutput());
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        terminal.createFile();
        terminal.createFile();
        terminal.createFile();
    }
}
