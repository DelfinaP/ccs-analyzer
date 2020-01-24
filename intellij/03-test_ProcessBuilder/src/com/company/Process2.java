package com.company;

import java.io.*;

public class Process2 {
    void run() {
        ProcessBuilder pb = null;
        Process p;
        String cmd2 = "";
        String workingDir = System.getProperty("user.home");
        System.out.println("" + workingDir);
        String scriptloc = "'" + workingDir + "/Documenti/Alessandro/03-Progetto_Sicurezza/02-test_ProcessBuilder/process-executor.sh'";
        String cmd[] = {"/bin/bash", scriptloc};

        for (int i = 0; i < cmd.length; i++) {
            cmd2 += " " + cmd[i];
        }
        System.out.println("" + cmd2);
        pb = new ProcessBuilder(cmd);
        pb.directory(new File(workingDir));

        p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        OutputStream outputStream = p.getOutputStream();
//        PrintStream printOutputStream = new PrintStream(outputStream);

        InputStream inputStream = p.getInputStream();

        int returnedByte = 0;
        do {
            try {
                returnedByte = inputStream.read();
                if (returnedByte != -1) {
                    System.out.print((char) returnedByte);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (returnedByte != -1);

//        printOutputStream.flush();
//
//        try {
//            p.waitFor();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        printOutputStream.close();

//        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//        // read the output from the command
//        System.out.println("Here is the standard output of the command:\n");
//
//        String s = null;
//        String output = "";
//        while ((s = stdInput.readLine()) != null) {
//            System.out.println(s);
//
//
//        }
//        output = "";
//
//        // read any errors from the attempted command
//        System.out.println("Here is the standard error of the command (if any):\n");
//        while ((s = stdError.readLine()) != null) {
//            System.out.println(s);
//        }
    }
}
