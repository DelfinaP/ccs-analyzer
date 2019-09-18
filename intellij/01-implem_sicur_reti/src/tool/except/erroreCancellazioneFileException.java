package tool.except;

public class erroreCancellazioneFileException extends Throwable {
    public erroreCancellazioneFileException(String fileString){
        super("Errore cancellazione file \"" + fileString + "\"");
    }
}
