package tool.except;

public class erroreCancellazioneFile extends Throwable {
    public erroreCancellazioneFile(String fileString){
        super("Errore cancellazione file \"" + fileString + "\"");
    }
}
