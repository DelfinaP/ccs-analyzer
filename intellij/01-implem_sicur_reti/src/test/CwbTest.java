package test;

import tool.*;
import tool.exceptions.OsNotRecognizedException;
import utils.FileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;

public class CwbTest {
    public static void main(String[] args) {
        cwbTest();
    }

    private static void cwbTest() {
        String localizedFilePath = FileManager.localizeLinuxPath("src/test/files/cwbTest/a.a.ccs");
        CcsManager.getMethodSize(localizedFilePath, "COREEFILETESTCLASSCOMROCKSTARGAMESAApublicstaticinta");
    }

    public static void cwbTestV2() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(new String[] {"./lib/cwb/linux/cwb-nc-ccs-x86-linux.bin"});
                    System.out.println(process);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String projectDirectory = FileManager.getProjectDirectory();

                PrintStream writer = new PrintStream(process.getOutputStream());
                writer.println("load " + projectDirectory + "/src/test/files/cwbTest/a.a.ccs");
                writer.println("size COREEFILETESTCLASSCOMROCKSTARGAMESAApublicstaticinta");
                writer.println("quit");
                writer.close();

                LinkedList<String> stringList = new LinkedList<String>();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line = "";

                try {
                    while ((line = reader.readLine()) != null) {
                        stringList.add(line);
                    }

                    int exitVal = 0;

                    exitVal = process.waitFor();

                    if (exitVal == 0) {
                        // Success
                        System.out.println("Success");
                        Tool.printStringList(stringList);
                    } else {
                        // Failure
                        System.out.println("Failure");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
                Process process = null;

                try {
                    process = Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "lib\\cwb\\windows\\bin\\cwb-nc.bat ccs"});
                    System.out.println(process);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String projectDirectory = FileManager.getProjectDirectory();

                PrintStream writer = new PrintStream(process.getOutputStream());
                
                writer.println("load " + projectDirectory + "\\src\\test\\files\\cwbTest\\a.a.ccs");
                writer.println("size COREEFILETESTCLASSCOMROCKSTARGAMESAApublicstaticinta");
                writer.println("quit");
                writer.close();

                LinkedList<String> stringList = new LinkedList<String>();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line = "";

                try {
                    while ((line = reader.readLine()) != null) {
                        stringList.add(line);
                    }

                    int exitVal = 0;

                    exitVal = process.waitFor();

                    if (exitVal == 0) {
                        // Success
                        System.out.println("Success");
                        Tool.printStringList(stringList);
                    } else {
                        // Failure
                        System.out.println("Failure");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (OsNotRecognizedException e) {
            e.printStackTrace();
        }
    }
}
