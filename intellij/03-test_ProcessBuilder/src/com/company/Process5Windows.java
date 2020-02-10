package com.company;

import java.io.IOException;

public class Process5Windows extends Process5 {

    public Process5Windows() {
        percorsoCcsDaAnalizzare = "C:\\Users\\Piera\\Documents\\Piera\\03-progetto_sicurezza\\01-ccs\\analisi";
    }

    protected void runBatch(){
        try {
            Process process = Runtime.getRuntime().exec(
                    "cmd /c \"\" C:\\Users\\Piera\\Documents\\Piera\\03-progetto_sicurezza\\02-varie\\01-test_process_builder\\batch.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
