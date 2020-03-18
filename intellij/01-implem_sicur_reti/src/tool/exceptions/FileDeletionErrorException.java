package tool.exceptions;

public class FileDeletionErrorException extends Throwable {
    public FileDeletionErrorException(String fileString){
        super("Errore cancellazione file \"" + fileString + "\"");
    }
}
