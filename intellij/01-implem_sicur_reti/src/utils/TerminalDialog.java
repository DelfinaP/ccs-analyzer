package utils;

import utils.except.osNotRecognizedException;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public abstract class TerminalDialog {
    Process process;
    OutputStream stdin;
    InputStream stderr;
    InputStream stdout;

    BufferedReader reader;
    BufferedWriter writer;

    LinkedList<String> stringList;
    LinkedList<String> stringheEstratte;
    Thread readBufferThread;
    String dirPath;

    /**
     * Questo metodo implementa il design pattern TEMPLATE METHOD
     */
    public void run(boolean isEsecuzioneDebug) throws IOException, osNotRecognizedException {
        stringList = new LinkedList<String>();

        avviaTerminale();

        stdin = process.getOutputStream();
        stderr = process.getErrorStream();
        stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));

        startReadThread();

        getDirPath(isEsecuzioneDebug);

        if (OsUtils.getOsType() == OsType.LINUX) {
            executeTerminalCommand("cd ~");
            executeTerminalCommand("ls -l");
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            executeTerminalCommand("cd %HOMEPATH%");
            executeTerminalCommand("dir");
        }

        busyWaiting(200);

        stringheEstratte = consumaLista();

        stampaStringList(stringheEstratte);
    }

    protected abstract void getDirPath(boolean isEsecuzioneDebug);

    private void startReadThread() {
        readBufferThread = new Thread() {
            public void run() {
                try {
                    String line;
                    // Leggiamo con readLine() e aggiungiamo alla lista
                    while ((line = reader.readLine()) != null) {
                        stringList.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        readBufferThread.start();
    }

    private void executeTerminalCommand(String command) throws IOException {
        command += "\n";
        writer.write(command);
        writer.flush();
    }

    protected abstract void avviaTerminale() throws IOException;

    private LinkedList<String> consumaLista() {
        LinkedList<String> stringheEstratte = new LinkedList<String>();

        Thread readBufferThread = new Thread() {
            public void run() {
                Timestamp time1 = new Timestamp(System.currentTimeMillis());
                Timestamp time2 = new Timestamp(System.currentTimeMillis());

                long time1millis = time1.getTime();
                long time2millis = time2.getTime();
                long diffTimeMillis = time2millis - time1millis;

                boolean continuaConsumo = true;
                boolean isVuota = false;

                while (continuaConsumo) {
                    if (stringList.size() > 0) {
                        stringheEstratte.add(stringList.remove());
                        isVuota = false;
                    }

                    if (stringList.size() == 0 && !isVuota) {
                        time1 = new Timestamp(System.currentTimeMillis());
                        time1millis = time1.getTime();
                        isVuota = true;
                    }

                    if (stringList.size() == 0 && isVuota) {
                        time2 = new Timestamp(System.currentTimeMillis());
                        time2millis = time2.getTime();
                        diffTimeMillis = time2millis - time1millis;
                    }

                    if (diffTimeMillis > 200) {
                        continuaConsumo = false;
                    }
                }

                this.interrupt();
            }
        };

        readBufferThread.start();
        while (!readBufferThread.isInterrupted()) {
            // Busy waiting
            ;
        }

        return stringheEstratte;
    }

    private void stampaStringList(LinkedList<String> stringList) {
        while (stringList.size() > 0) {
            System.out.println(stringList.remove());
        }
    }

    private void busyWaiting(int durata) {
        Timestamp time1 = new Timestamp(System.currentTimeMillis());
        Timestamp time2 = new Timestamp(System.currentTimeMillis());

        long time1millis = time1.getTime();
        long time2millis = time2.getTime();
        long diffTimeMillis = time2millis - time1millis;

        while (diffTimeMillis < durata) {
            time2 = new Timestamp(System.currentTimeMillis());
            time2millis = time2.getTime();
            diffTimeMillis = time2millis - time1millis;
        }
    }
}