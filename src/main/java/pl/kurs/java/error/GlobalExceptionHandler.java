package pl.kurs.java.error;

import lombok.Value;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        return new ResponseEntity(exc.getFieldErrors().stream()
                .map(fe -> new ValidationErrorDto(fe.getDefaultMessage(), fe.getField())).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFoundException(EntityNotFoundException exc) {
        return new ResponseEntity(
                new NotFoundDto(exc.getName(), exc.getKey()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyTakenException.class)
    public ResponseEntity handleAlreadyTakenException(AlreadyTakenException exc) {
        return new ResponseEntity(new ValidationErrorDto("DATE_RESERVED", "DOCTOR_OR_PATIENT_VISIT_DATE"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException exc) {
        String constraintName = exc.getConstraintName().substring(1, exc.getConstraintName().indexOf(" "));
        String message = null;
        String field = null;
        if ("UC_DOCTOR_NIP".equals(constraintName)) {
            message = "NIP_NOT_UNIQUE";
            field = "nip";
        } else if ("UC_PATIENT_EMAIL".equals(constraintName)){
            message = "EMAIL_NOT_UNIQUE";
            field = "email";
        }
        return new ResponseEntity(new ValidationErrorDto(message, field), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity handleEntityNotFoundException(ObjectOptimisticLockingFailureException exc) {
        return new ResponseEntity(new OptimisticLockDto("OPTIMISTIC_LOCK"), HttpStatus.BAD_REQUEST);
    }

    @Value
    class ValidationErrorDto {
        private String message;
        private String field;
    }


    @Value
    class NotFoundDto {
        private String entityName;
        private String entityKey;
    }

    @Value
    class OptimisticLockDto {
        private String message;
    }
}
