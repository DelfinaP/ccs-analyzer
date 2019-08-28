package utils;

import java.io.*;
import java.sql.Timestamp;

public abstract class TerminalDialog {
    Process process;

    Timestamp time1;
    Timestamp time2;

    long time1millis;
    long time2millis;
    long diffTimeMillis;

    /**
     * Questo metodo implementa il design pattern TEMPLATE METHOD
     */
    public void run() throws IOException {
        String line;

        avviaTerminale();

        OutputStream stdin = process.getOutputStream();
        InputStream stderr = process.getErrorStream();
        InputStream stdout = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

//        String input = "cd ~";
//        input += "\n";
//        writer.write(input);
//        writer.flush();
//
//        input = "ls -l";
//        input += "\n";
//        writer.write(input);
//        writer.flush();

//        String input = "cd ~";
//        input += "\n";
//        writer.write(input);
//        writer.flush();

        time1 = new Timestamp(System.currentTimeMillis());
        time2 = new Timestamp(System.currentTimeMillis());

        time1millis = time1.getTime();
        time2millis = time2.getTime();

        diffTimeMillis = time2millis - time1millis;

        while (diffTimeMillis < 10 && (line = reader.readLine()) != null) {
            System.out.println("Stdout: " + line);
        }
    }

    protected abstract void avviaTerminale() throws IOException;

}
