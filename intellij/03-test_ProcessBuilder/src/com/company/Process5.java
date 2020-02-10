package com.company;

import com.company.utils.OsType;
import com.company.utils.OsUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public abstract class Process5 {

    Process process;
    String percorsoCcsDaAnalizzare;

    void run (){

        // Cancella file non ccs

        File folder = new File(percorsoCcsDaAnalizzare);

        // eseguiamo il processo

        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line = "";
        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.append(line + "\n");
        }

        int exitVal = 0;
        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exitVal == 0) {
            System.out.println("Success!");
            System.out.println(output);
            System.exit(0);
        } else {
            //abnormal...
        }
    }

    protected abstract void runBatch();
}
