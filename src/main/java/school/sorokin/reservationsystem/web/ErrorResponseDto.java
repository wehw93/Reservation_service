package school.sorokin.reservationsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        String message,
        LocalDateTime errorTime,
        String detailedMessage
){}
