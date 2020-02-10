package com.company;

import com.company.utils.OsType;
import com.company.utils.OsUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public abstract class Process5 {

    ProcessBuilder processBuilder;
    String percorsoCcsDaAnalizzare;

    void run (){

//        //inizio test
//        File logFile = Paths.get("workingDir", "log_u_" + "currentUser" + "_r_" + "currentRule" + ".txt").toFile();
//
//        ProcessBuilder pb = new ProcessBuilder("testString", "ccs");
//        pb.directory(new File("workingDir"));
//        pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
////            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//        try {
//            Process process = pb.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //fine test

        processBuilder = new ProcessBuilder("cmd.exe", "/c", "dir C:\\Users");

//        String commandString = null;

//        if (OsUtils.getOsType() == OsType.LINUX) {
//            commandString = "ls /home/";
//        }
//        else if (OsUtils.getOsType() == OsType.WINDOWS){
//            commandString = "dir C:\\Users";
//        }
//
//        runCommand(commandString);
//
        // Cancella file non ccs

        File folder = new File(percorsoCcsDaAnalizzare);

        try {

            Process process = null;
            try {
                process = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void runCommand(String commandString);
}
