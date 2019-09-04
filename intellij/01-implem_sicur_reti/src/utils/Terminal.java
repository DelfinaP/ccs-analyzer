package utils;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;

public abstract class Terminal{
    protected Process process;
    protected OutputStream stdin;
    protected InputStream stderr;
    protected InputStream stdout;

    protected BufferedReader reader;
    protected BufferedWriter writer;

    Thread readBufferThread;

    LinkedList<String> stringList;

    public Terminal() throws IOException {

        avviaTerminale();

        stdin = process.getOutputStream();
        stderr = process.getErrorStream();
        stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));

        stringList = new LinkedList<String>();
    }

    protected abstract void avviaTerminale() throws IOException;

    public void startReadThread() {
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

    public void executeTerminalCommand(String command) throws IOException {
        command += "\n";
        writer.write(command);
        writer.flush();
    }

    public LinkedList<String> getStringList() {
        return stringList;
    }

    protected abstract void changeDirectory (String dirPath) throws IOException;

    protected abstract void rimuoviFileNonCcs(String dirPath)throws IOException;

    public LinkedList<String> consumaLista(int durata) {
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
                    if (getStringList().size() > 0) {
                        stringheEstratte.add(getStringList().remove());
                        isVuota = false;
                    }

                    if (getStringList().size() == 0 && !isVuota) {
                        time1 = new Timestamp(System.currentTimeMillis());
                        time1millis = time1.getTime();
                        isVuota = true;
                    }

                    if (getStringList().size() == 0 && isVuota) {
                        time2 = new Timestamp(System.currentTimeMillis());
                        time2millis = time2.getTime();
                        diffTimeMillis = time2millis - time1millis;
                    }

                    if (diffTimeMillis > durata) {
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
}
