package school.sorokin.reservationsystem.web;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ){

        log.error("handle  excetion",e);

        var errorDto = new ErrorResponseDto(
                "internal server error",
                LocalDateTime.now(),
                e.getMessage()
        );

        return ResponseEntity.status (HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(
            EntityNotFoundException e
    ){

        log.error("handle entity not found",e);

        var errorDto = new ErrorResponseDto(
                "Entity not found",
                LocalDateTime.now(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            Exception e
    ){

        log.error("handle bad request",e);

        var errorDto = new ErrorResponseDto(
                "bad request",
                LocalDateTime.now(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST ).body(errorDto);
    }
}
