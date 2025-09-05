package school.sorokin.reservationsystem;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService   {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;


    public ReservationService(ReservationRepository repository){
        this.repository = repository;
    }

    public List<Reservation> findAllReservations(){
        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream()
                .map(this::toDomainReservation
                ).toList();
    }

    public Reservation getReservationsById(
            Long id
    ){
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));
        return toDomainReservation(reservationEntity);

    }

    public Reservation createReservation(Reservation reservationToCreate){
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("id should be empty");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("status should be empty");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        var savedEntity = repository.save(entityToSave);

        return toDomainReservation(savedEntity);
    }

    public Reservation UpdateReservation(Long id,Reservation reservationToUpdate){



        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));

        if (reservationEntity.getStatus()!= ReservationStatus.PENDING){
            throw new IllegalArgumentException("status is " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate .roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        return toDomainReservation(repository.save(reservationToSave));
    }

    @Transactional
    public void CancelReservation(Long id){
        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Reservation not found by id: " + id);
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Reservation has been cancelled by user: " + id);
    }

    public Reservation ApproveReservation(Long id){

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));

        if (reservationEntity.getStatus()!= ReservationStatus.PENDING){
            throw new IllegalArgumentException("status is " + reservationEntity.getStatus());
        }

        var isConflict  = isReservationConflict(reservationEntity);
        if (isConflict){
            throw new IllegalStateException(String.format("Reservation with id %s is conflict", reservationEntity.getId()));
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);

        repository.save(reservationEntity);
        return  toDomainReservation(reservationEntity);
    }
    private boolean isReservationConflict(ReservationEntity reservation){
        return repository.findAll().stream()
                .anyMatch(existingReservationEntity ->
                        !existingReservationEntity.getId().equals(reservation.getId()) &&
                                reservation.getRoomId().equals(existingReservationEntity.getRoomId()) &&
                                existingReservationEntity.getStatus().equals(ReservationStatus.APPROVED) &&
                                reservation.getStartDate().isBefore(existingReservationEntity.getEndDate()) &&
                                reservation.getEndDate().isAfter(existingReservationEntity.getStartDate())
                );
    }

    private  Reservation toDomainReservation(ReservationEntity reservation){
        return  new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }
}
