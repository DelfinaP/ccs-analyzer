package tool;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import tool.except.erroreCancellazioneFileException;
import tool.except.osNotRecognizedException;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public abstract class TerminalDialog {
    LinkedList<String> fileList;
    Terminal terminale1;
    static String analysisDirPath;
    static String nomeDirFileOriginali;
    static String nomeDirFileInvokemethodSostituito;

    /**
     * Questo metodo implementa il design pattern TEMPLATE METHOD
     */
    public void run() throws IOException, osNotRecognizedException, ParseException {
        terminale1 = inizializzaTerminale();

        terminale1.startReadThread();

        getDirPath();

        terminale1.rimuoviFileNonCcs(analysisDirPath);

        elaboraFile(fileList);

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
                    analysisDirPath = (String) pairParametri.getValue();
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

    static public LinkedList<String> getListaFile(String dirPath) {
        LinkedList<String> fileList = new LinkedList<String>();

        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            // Se è un file
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getName());
            }
            // Altrimenti, se è una directory
            else if (listOfFiles[i].isDirectory()) {
                // Non fare nulla
            }
        }

        return fileList;
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

    private void elaboraFile(LinkedList<String> fileList) throws IOException, ParseException {
        copiaFileInCartelle();

//        elaboraFileOriginali();
//        elaboraFileConInvokemethodSostituito();
    }

    private void copiaFileInCartelle() throws IOException, ParseException {
        Object objIstanza = new JSONParser().parse(new FileReader("src/json/parametri.json"));

        // typecasting obj to JSONObject
        JSONObject joIstanza = (JSONObject) objIstanza;

        Map parametri = ((Map) joIstanza.get("parametri"));

        Iterator<Map.Entry> itrParametri = parametri.entrySet().iterator();
        while (itrParametri.hasNext()) {
            Map.Entry pairParametri = itrParametri.next();

            switch (pairParametri.getKey().toString()) {
                case "nome_dir_file_originali":
                    nomeDirFileOriginali = (String) pairParametri.getValue();
                    break;
                case "nome_dir_file_invokemethod_sostituito":
                    nomeDirFileInvokemethodSostituito = (String) pairParametri.getValue();
                    break;
            }
        }

        // Crea cartella per i file originali
        String nomeCartella = costruisciPath(analysisDirPath, nomeDirFileOriginali);
        File dir = new File(nomeCartella);
        dir.mkdir();

        // Crea cartella per i file originali
        nomeCartella = costruisciPath(analysisDirPath, nomeDirFileInvokemethodSostituito);
        dir = new File(nomeCartella);
        dir.mkdir();

        copiaFileInDirectory(nomeDirFileOriginali);
        copiaFileInDirectory(nomeDirFileInvokemethodSostituito);
        rimuoviFileInDirectory(analysisDirPath);
    }

    private void copiaFileInDirectory(String nomeDirectory) {
        LinkedList<String> fileList = TerminalDialog.getListaFile(analysisDirPath);
        String nomeFile;

        while (fileList.size() > 0) {
            nomeFile = fileList.remove();

            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(costruisciPath(analysisDirPath, nomeFile));
                os = new FileOutputStream(costruisciPath(analysisDirPath, nomeDirectory, nomeFile));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract String costruisciPath(String pathParte1, String parthParte2);

    private String costruisciPath(String pathParte1, String pathParte2, String pathParte3) {
        return costruisciPath(costruisciPath(pathParte1, pathParte2), pathParte3);
    }

    private void elaboraSingoloFileOriginale(String file){

    }

    private void stampaFileList(LinkedList<String> fileList) {
        System.out.println("Num file: " + fileList.size());

        while (fileList.size() > 0) {
            System.out.println(fileList.remove());
        }
    }

    static public LinkedList<String> getFileNonCcs(LinkedList<String> fileList) {
        LinkedList<String> fileNonCcsList = new LinkedList<String>();
        String fileString;

        while (fileList.size() > 0) {
            fileString = fileList.remove();

            if (!fileString.endsWith(".ccs")) {
                fileNonCcsList.add(fileString);
            }
        }

        return fileNonCcsList;
    }

    private void rimuoviFileInDirectory(String directoryPath) {
        LinkedList<String> fileList = new LinkedList<String>();
        File fileDaCancellare;
        String fileName;

        File folder = new File(analysisDirPath);
        File[] listOfFiles = folder.listFiles();

        // Seleziona i file scartando le directory
        for (int i = 0; i < listOfFiles.length; i++) {
            // Se è un file
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getName());
            }
            // Altrimenti, se è una directory
            else if (listOfFiles[i].isDirectory()) {
                // Non fare nulla
            }
        }

        // Elimina i file
        while (fileList.size() > 0) {
            fileName = fileList.remove();
            fileDaCancellare = createFile(analysisDirPath, fileName);

            if (fileDaCancellare.delete()) {
                // La cancellazione ha avuto successo. Non fare niente
            } else {
                try {
                    throw new erroreCancellazioneFileException(fileName);
                } catch (erroreCancellazioneFileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract File createFile(String dirPath, String fileString);
}