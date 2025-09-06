package school.sorokin.reservationsystem.reservations.availability;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation/availability")
public class ReservationAvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationAvailabilityController.class);

    private final ReservationAvailabilityService service;

    public ReservationAvailabilityController(ReservationAvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(
            @Valid CheckAvailabilityRequest request
    ){
        logger.info("Check availability request request ={}",request);

        boolean isAvaiable = service.isReservationVailable(
                request.roomId(),
                request.startDate(),
                request.endDate()
        );
        var message = isAvaiable
                ? "Room available for reservation"
                : "Room not available for reservation";
        var status = isAvaiable
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.RESERVED;
        return ResponseEntity.status(200)
                .body(new CheckAvailabilityResponse(message, status));
    }

}
