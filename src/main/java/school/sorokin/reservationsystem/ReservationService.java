package school.sorokin.reservationsystem;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService   {

    public Reservation getReservationsById(
            Long id
    ){
        if (!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id " + id);
        }
        return reservationMap.get(id);
    }

    private final Map<Long,Reservation> reservationMap;

    private final AtomicLong idCounter;


    public ReservationService(){
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public List<Reservation> findAllReservations(){
        return reservationMap.values().stream().toList();
    }

    public Reservation createReservation(Reservation reservationToCreate){
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("id should be empty");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("status should be empty");
        }
        var newReservation = new Reservation(
                idCounter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public Reservation UpdateReservation(Long id,Reservation reservationToUpdate){
        if (!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id " + id);
        }
        var reservation = reservationMap.get(id);
        if (reservation.status()!= ReservationStatus.PENDING){
            throw new IllegalArgumentException("status is " + reservation.status());
        }
        var updatedReservation = new Reservation(
                reservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate .roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(id, updatedReservation);
        return updatedReservation;
    }

    public void DeleteReservation(Long id){
        if (!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id " + id);
        }
        reservationMap.remove(id);
    }

    public Reservation ApproveReservation(Long id){
        if (!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id " + id);
        }
        var reservation = reservationMap.get(id);
        if (reservation.status()!= ReservationStatus.PENDING){
            throw new IllegalArgumentException("status is " + reservation.status());
        }
        var isConflict  = isReservationConflict(reservation);
        if (isConflict){
            throw new IllegalStateException(String.format("Reservation with id %s is conflict", reservation.id()));
        }

        var approvedReservation = new Reservation(
                reservation.id(),
                  reservation.userId(),
                reservation .roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED
        );
        reservationMap.put(id, approvedReservation);
        return  approvedReservation;
    }
    private boolean isReservationConflict(Reservation reservation){
        return reservationMap.values().stream()
                .anyMatch(existingReservation ->
                        !existingReservation.id().equals(reservation.id()) &&
                                reservation.roomId().equals(existingReservation.roomId()) &&
                                existingReservation.status().equals(ReservationStatus.APPROVED) &&
                                reservation.startDate().isBefore(existingReservation.endDate()) &&
                                reservation.endDate().isAfter(existingReservation.startDate())
                );
    }
}
