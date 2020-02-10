package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Process6 {

    void run() {
        try {

            // -- Linux --

            // Run a shell command
            // Process process = Runtime.getRuntime().exec("ls /home/mkyong/");

            // Run a shell script
            // Process process = Runtime.getRuntime().exec("/home/alessandro/Documenti/Alessandro/03-Progetto_Sicurezza/02-test_ProcessBuilder/script.sh");

            // -- Windows --

            // Run a command
            //Process process = Runtime.getRuntime().exec("cmd /c dir C:\\Users\\mkyong");

            //Run a bat file
            Process process = Runtime.getRuntime().exec(
                    "cmd /c \"\" C:\\Users\\Piera\\Documents\\Piera\\03-progetto_sicurezza\\02-varie\\01-test_process_builder\\batch.bat");

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
}
