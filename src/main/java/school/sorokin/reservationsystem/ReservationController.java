package school.sorokin.reservationsystem;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
            @RequestBody Reservation reservationToCreate
    ){
        log.info("Creating reservation {}", reservationToCreate);
        return ResponseEntity.status(201)
                .header("test header", "123")
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate
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
        try {
            reservationService.CancelReservation(id);
            return ResponseEntity.ok().build();
        }catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
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
