package school.sorokin.reservationsystem.reservations.availability;

public record CheckAvailabilityResponse (
        String message,
        AvailabilityStatus status
){
}
