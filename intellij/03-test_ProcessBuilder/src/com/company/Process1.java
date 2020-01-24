package com.company;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

public class Process1 {
    void run() {
        File logFile = Paths.get("/home/alessandro/Documenti", "test_log_file.txt").toFile();

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File("/usr/bin"));
        pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
//            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = null;

        pb.command("ffmpeg");

        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = process.getOutputStream();
        PrintStream printOutputStream = new PrintStream(outputStream);

//        String[] commandsToRun = {"cd ../", "ls -l"};
//
//        for (String command : commandsToRun) {
//            printOutputStream.println(command);
//            printOutputStream.flush();
//        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printOutputStream.close();
    }
}
