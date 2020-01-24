package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Process3 {
    void run() {
        // build my command as a list of strings
        List<String> command = new ArrayList<String>();
        command.add("ls");
        command.add("-l");
        command.add("/var/tmp");
//        command.add("/usr/bin/ffmpeg");

        // execute my command
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            int result = commandExecutor.executeCommand();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get the output from the command
        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        // print the output from the command
        System.out.println("STDOUT");
        System.out.println(stdout);
        System.out.println("STDERR");
        System.out.println(stderr);
    }
}
