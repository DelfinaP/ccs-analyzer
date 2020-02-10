package com.company;

import java.io.IOException;

public class Process5Linux extends Process5 {

    protected void runBatch(){
        try {
            Process process = Runtime.getRuntime().exec("/home/alessandro/Documenti/Alessandro/03-Progetto_Sicurezza/02-test_ProcessBuilder/script.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
