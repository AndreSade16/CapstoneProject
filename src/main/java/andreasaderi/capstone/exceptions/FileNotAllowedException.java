package andreasaderi.capstone.exceptions;

public class FileNotAllowedException extends RuntimeException {
    public FileNotAllowedException(String message) {
        super(message);
    }
}
