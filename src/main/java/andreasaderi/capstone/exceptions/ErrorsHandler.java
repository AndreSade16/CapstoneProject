package andreasaderi.capstone.exceptions;

import andreasaderi.capstone.responseDTOs.ErrorsDTO;
import andreasaderi.capstone.responseDTOs.ErrorsWithListDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorsHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorsDTO handleNotFoundException(NotFoundException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleRecordAlreadyExsistException(RecordAlreadyExistsException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorsDTO handleUnauthorizedException(UnauthorizedException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handlePropertyReferenceException(PropertyReferenceException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ErrorsDTO(message, LocalDateTime.now());

    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsWithListDTO handleValidationException(ValidationException e) {
        return new ErrorsWithListDTO(e.getMessage(), LocalDateTime.now(), e.getErrors());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorsDTO handleGenericException(Exception e) {
        return new ErrorsDTO("Internal server error", LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return new ErrorsDTO(
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorsDTO handleRuntimeException(RuntimeException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleBadRequestException(BadRequestException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorsDTO handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FileNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleFileNotSupportedException(FileNotAllowedException e) {
        return new ErrorsDTO(e.getMessage(), LocalDateTime.now());
    }

}
