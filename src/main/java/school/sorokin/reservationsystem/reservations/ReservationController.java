package school.sorokin.reservationsystem.reservations;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity <Reservation> getReservationsById(
            @PathVariable("id") Long id
    ){
        log.info("Getting reservations by id {}", id);

        return ResponseEntity.status(200)
                .body(reservationService.getReservationsById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> geAllReservationsById(){
        log.info("Getting all reservations by id");

        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid Reservation reservationToCreate
    ){
        log.info("Creating reservation {}", reservationToCreate);

        return ResponseEntity.status(201)
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody @Valid Reservation reservationToUpdate
    ){
        log.info("Updating reservation id = {} , reservation to update = {}",id, reservationToUpdate);

        var updated = reservationService.UpdateReservation(id,reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Reservation> DeleteReservation(
            @PathVariable("id") Long id
    ){
        log.info("Deleted reservation id = {}" ,id);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ){
        log.info("Approving reservation id = {}" ,id);

       var reservation = reservationService.ApproveReservation(id);
       return ResponseEntity.ok(reservation);
    }
}
