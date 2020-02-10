package com.company;

public class Process5Linux extends Process5 {

    protected void runCommand(String commandString){
        // Run a shell command
        processBuilder.command("bash", "-c", commandString);

        // Run a shell script
        //processBuilder.command("path/to/hello.sh");
    }
}
