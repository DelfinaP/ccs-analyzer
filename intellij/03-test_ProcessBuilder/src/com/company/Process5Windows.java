package com.company;

public class Process5Windows extends Process5 {

    public Process5Windows() {
        percorsoCcsDaAnalizzare = "C:\\Users\\Piera\\Documents\\Piera\\03-progetto_sicurezza\\01-ccs\\analisi";
    }

    protected void runCommand(String commandString){
        // Run a command
        processBuilder.command("cmd.exe", "/c", commandString);

        // Run a bat file
        //processBuilder.command("C:\\Users\\mkyong\\hello.bat");
    }
}
