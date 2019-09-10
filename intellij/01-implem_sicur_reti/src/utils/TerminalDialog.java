package utils;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import utils.except.osNotRecognizedException;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public abstract class TerminalDialog {
    LinkedList<String> fileList;
    Terminal terminale1;
    String dirPath;

    /**
     * Questo metodo implementa il design pattern TEMPLATE METHOD
     */
    public void run() throws IOException, osNotRecognizedException, ParseException {
        terminale1 = inizializzaTerminale();

        terminale1.startReadThread();

        getDirPath();

        terminale1.rimuoviFileNonCcs(dirPath);

        fileList = terminale1.getListaFile(terminale1);

        stampaStringList(fileList);

        //elaboraFileList(fileList);

        System.exit(0);
    }

    private void getDirPath() throws ParseException, IOException {

        Object objIstanza = new JSONParser().parse(new FileReader("src/json/parametri.json"));

        // typecasting obj to JSONObject
        JSONObject joIstanza = (JSONObject) objIstanza;

        Map parametri = ((Map) joIstanza.get("parametri"));

        Iterator<Map.Entry> itrParametri = parametri.entrySet().iterator();
        while (itrParametri.hasNext()) {
            Map.Entry pairParametri = itrParametri.next();

            switch (pairParametri.getKey().toString()) {
                case "percorso_ccs_da_analizzare":
                    dirPath = (String) pairParametri.getValue();
                    break;
            }
        }
    }

    private Terminal inizializzaTerminale() throws osNotRecognizedException, IOException {
        if (OsUtils.getOsType() == OsType.LINUX){
            return new TerminalLinux();
        }
        else if(OsUtils.getOsType() == OsType.WINDOWS){
            return new TerminalWindows();
        }
        return new TerminalLinux();
    }



    public static void stampaStringList(LinkedList<String> stringList) {
        while (stringList.size() > 0) {
            System.out.println(stringList.remove());
        }
    }

    public static void busyWaiting(int durata) {
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

    private void debugTerminal() throws osNotRecognizedException, IOException {
        if (OsUtils.getOsType() == OsType.LINUX) {
            terminale1.executeTerminalCommand("cd ~");
            terminale1.executeTerminalCommand("ls -l");
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            terminale1.executeTerminalCommand("cd %HOMEPATH%");
            terminale1.executeTerminalCommand("dir");
        }
    }

    private void elaboraFileList(LinkedList<String> fileList){
        copiaFileOriginali();
        elaboraFileOriginali(fileList);

        //elaboraMetodiRidotti();

    }

    protected abstract void copiaFileOriginali();

    private void elaboraFileOriginali(LinkedList<String> fileList){
        while(fileList.size() > 0){
            elaboraSingoloFileOriginale(fileList.remove());
        }
    }

    private void elaboraSingoloFileOriginale(String file){

    }


}