package andreasaderi.capstone.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation error.");
        this.errors = errors;
    }

    public ValidationException(String message) {
        super(message);
    }
}
