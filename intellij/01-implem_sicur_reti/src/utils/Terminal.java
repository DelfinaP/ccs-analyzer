package utils;

import java.io.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public abstract class Terminal{
    protected Process process;
    protected OutputStream stdin;
    protected InputStream stderr;
    protected InputStream stdout;

    protected BufferedReader reader;
    protected BufferedWriter writer;

    protected Thread readBufferThread;

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
                // diventa 'true' quando la lista ha ricevuto in passato almeno un elemento
                boolean almeno1ElementoElaborato = false;

                TerminalDialog.busyWaiting(2000);

                while (continuaConsumo) {
                    if (getStringList().size() > 0) {
                        almeno1ElementoElaborato = true;
                        try {
                            stringheEstratte.add(getStringList().remove());
                        }
                        catch (NoSuchElementException e) {
                            continuaConsumo = false;
                        }
                        isVuota = false;
                    }
                    else if (getStringList().size() == 0 && !isVuota && almeno1ElementoElaborato) {
                        time1 = new Timestamp(System.currentTimeMillis());
                        time1millis = time1.getTime();
                        isVuota = true;
                    }
                    else if (getStringList().size() == 0 && isVuota && almeno1ElementoElaborato) {
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

    protected abstract String parseDirectoryString(String stringToBeParsed);

    protected String popStringFromList(LinkedList<String> linkedList) {
        return linkedList.remove();
    }

    protected LinkedList<String> getListaFile(Terminal terminale1) throws IOException {
        LinkedList<String> terminalOutputList;
        LinkedList<String> returnList;

        terminale1.startReadThread();

        eseguiStampaContenutoDirectory();

        terminalOutputList = consumaLista(1000);

        System.out.println("Num file: " + terminalOutputList.size());

        returnList = buildReturnList(terminalOutputList);

        return returnList;
    }

    protected abstract LinkedList<String> buildReturnList(LinkedList<String> terminalOutputList);

    protected void debugParseString() {
        String string = "123456    abcdef   123456   abcdef";

        int index = vaiASpazioSuccessivo(string, 0);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiASpazioSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiASpazioSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));

        index = vaiACarattereSuccessivo(string, index);
        System.out.println(string.substring(index, string.length()));
    }

    protected abstract void eseguiStampaContenutoDirectory() throws IOException;

    protected int vaiASpazioSuccessivo(String stringa, int indiceDiPartenza) {
        int i = indiceDiPartenza + 1;

        while (stringa.charAt(i) != ' ') {
            i++;
        }

        return i;
    }

    protected int vaiACarattereSuccessivo(String stringa, int indiceDiPartenza) {
        int i = indiceDiPartenza + 1;

        while (stringa.charAt(i) == ' ') {
            i++;
        }

        return i;
    }
}