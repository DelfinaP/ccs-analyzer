package utils;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public abstract class TerminalDialog {
    Process process;
    List<String> terminalOutputStrings;
    OutputStream stdin;
    InputStream stderr;
    InputStream stdout;

    BufferedReader reader;
    BufferedWriter writer;

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

        stdin = process.getOutputStream();
        stderr = process.getErrorStream();
        stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));


        terminalOutputStrings = getTerminalOutput("cd ~");

//        Thread readBufferThread = new Thread() {
//            public void run() {
//                try {
//                    System.out.println("Does it work?");
//
//                    Thread.sleep(1000);
//
//                    System.out.println("Nope, it doesnt...again.");
//                } catch(InterruptedException v) {
//                    System.out.println(v);
//                }
//            }
//        };

        // Stampiamo la lista

        for (int i = 0; i < terminalOutputStrings.size(); i++) {
            System.out.println(terminalOutputStrings.get(i));
        }

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

//        time1 = new Timestamp(System.currentTimeMillis());
//        time2 = new Timestamp(System.currentTimeMillis());
//
//        time1millis = time1.getTime();
//        time2millis = time2.getTime();
//
//        diffTimeMillis = time2millis - time1millis;
//
//        while ((line = reader.readLine()) != null) {
//            if (line.length() == 0) {
//                break;
//            }
//            System.out.println("Stdout: " + line);
//            time2 = new Timestamp(System.currentTimeMillis());
//            time2millis = time2.getTime();
//            diffTimeMillis = time2millis - time1millis;
    }

    private ArrayList<String> getTerminalOutput(String command) throws IOException {
        ArrayList<String> stringList = new ArrayList<String>();

        command += "\n";
        writer.write(command);
        writer.flush();

        Thread readBufferThread = new Thread() {
            public void run() {
                try {
                    String line;
                    // Leggiamo con readLine() e aggiungiamo alla lista
                    System.out.println("Primo thread");
                    while ((line = reader.readLine()) != null) {
                        stringList.add(line);
                        System.out.println("Stdout: " + line);
                    }
                    // Thread.sleep(10000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread interruptBufferThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("Secondo thread");
                    reader.close();
                    System.out.println("Chiusura stream 'reader' completata");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        readBufferThread.start();
        interruptBufferThread.start();

        // Busy waiting di 1 secondo
        time1 = new Timestamp(System.currentTimeMillis());
        time2 = new Timestamp(System.currentTimeMillis());

        time1millis = time1.getTime();
        time2millis = time2.getTime();

        while (diffTimeMillis < 1000) {
            time2 = new Timestamp(System.currentTimeMillis());
            time2millis = time2.getTime();
            diffTimeMillis = time2millis - time1millis;
        }

        readBufferThread.interrupt();
        interruptBufferThread.interrupt();

        // Busy waiting di 200 ms
        time1 = new Timestamp(System.currentTimeMillis());
        time2 = new Timestamp(System.currentTimeMillis());

        time1millis = time1.getTime();
        time2millis = time2.getTime();

        while (diffTimeMillis < 200) {
            time2 = new Timestamp(System.currentTimeMillis());
            time2millis = time2.getTime();
            diffTimeMillis = time2millis - time1millis;
        }

        // Verifichiamo che i thread siano interrotti
        if (readBufferThread.isInterrupted()) {
            System.out.println("Il thread 'readBufferThread' è interrotto");
        }
        else {
            System.out.println("Il thread 'readBufferThread' non è interrotto");
        }
        if (interruptBufferThread.isInterrupted()) {
            System.out.println("Il thread 'interruptBufferThread' è interrotto");
        }
        else {
            System.out.println("Il thread 'interruptBufferThread' non è interrotto");
        }

        return stringList;
    }

    protected abstract void avviaTerminale() throws IOException;
}