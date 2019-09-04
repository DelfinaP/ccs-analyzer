package utils;

import utils.except.osNotRecognizedException;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public abstract class TerminalDialog {
    LinkedList<String> stringheEstratte;
    Terminal terminale1;
    String dirPath;

    /**
     * Questo metodo implementa il design pattern TEMPLATE METHOD
     */
    public void run(boolean isEsecuzioneDebug) throws IOException, osNotRecognizedException {
        terminale1 = inizializzaTerminale();

        terminale1.startReadThread();

        getDirPath(isEsecuzioneDebug);

        if (OsUtils.getOsType() == OsType.LINUX) {
            terminale1.executeTerminalCommand("cd ~");
            terminale1.executeTerminalCommand("ls -l");
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            terminale1.executeTerminalCommand("cd %HOMEPATH%");
            terminale1.executeTerminalCommand("dir");
        }

        busyWaiting(200);

        stringheEstratte = consumaLista();

        stampaStringList(stringheEstratte);
    }

    protected abstract void getDirPath(boolean isEsecuzioneDebug);

    private Terminal inizializzaTerminale() throws osNotRecognizedException, IOException {
        if (OsUtils.getOsType() == OsType.LINUX){
            return new TerminalLinux();
        }
        else if(OsUtils.getOsType() == OsType.WINDOWS){
            return new TerminalWindows();
        }
        return new TerminalLinux();
    }

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
                    if (terminale1.getStringList().size() > 0) {
                        stringheEstratte.add(terminale1.getStringList().remove());
                        isVuota = false;
                    }

                    if (terminale1.getStringList().size() == 0 && !isVuota) {
                        time1 = new Timestamp(System.currentTimeMillis());
                        time1millis = time1.getTime();
                        isVuota = true;
                    }

                    if (terminale1.getStringList().size() == 0 && isVuota) {
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