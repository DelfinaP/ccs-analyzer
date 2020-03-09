package test;

import tool.*;
import tool.exceptions.osNotRecognizedException;
import utils.FileManager;

public class CwbTest {
    public static void main(String[] args) {
        cwbTest();
    }

    private static void cwbTest() {
        try {
            if (OsUtils.getOsType() == OsType.LINUX) {
                String projectDirectory = FileManager.getProjectDirectory();

                Terminal terminal = new TerminalLinux();

                terminal.addCommand("./lib/cwb/linux/cwb-nc-ccs-x86-linux.bin");
                terminal.addCommand("load " + projectDirectory + "/" + "src/test/files/cwbTest/a.a.ccs");
                terminal.addCommand("size COREEFILETESTCLASSCOMROCKSTARGAMESAApublicstaticinta");
                terminal.addCommand("quit");
                terminal.addCommand("echo 'terminal execution terminated'");
                terminal.addCommand("echo 'terminal execution terminated'");

                terminal.executeCommands();

                Tool.stampaStringList(terminal.getTerminalOutput());
            }
            else if (OsUtils.getOsType() == OsType.WINDOWS){
//                Command to be executed in Windows:
//                cd C:\CWB-NC\bin
//                cwb-nc.bat
//                load C:\Users\sicurezza\Documents\Alessandro\01-Progetto_Sicurezza\01-ccs\test-cwb\a.a.ccs
//                size COREEFILETESTCLASSCOMROCKSTARGAMESAApublicstaticinta
//                quit
            }
        } catch (osNotRecognizedException e) {
            e.printStackTrace();
        }
    }
}
