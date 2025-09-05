package school.sorokin.reservationsystem;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(
            Exception e
    ){
        log.error("handle  excetion",e);
        return ResponseEntity.status (HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(
            EntityNotFoundException e
    ){
        log.error("handle entity not found",e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<String> handleIllegalArgument(
            Exception e
    ){
        log.error("handle bad request",e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST ).body(e.getMessage());
    }
}
