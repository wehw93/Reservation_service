package school.sorokin.reservationsystem.reservations;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.reservationsystem.reservations.availability.ReservationAvailabilityService;

import java.util.*;

@Service
public class ReservationService   {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationAvailabilityService reservationAvailabilityService;

    private final ReservationRepository repository;

    private final ReservationMapper mapper;

    public ReservationService(ReservationAvailabilityService reservationAvailabilityService, ReservationRepository repository, ReservationMapper mapper){
        this.reservationAvailabilityService = reservationAvailabilityService;
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<Reservation> findAllReservations(){
        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream()
                .map(mapper::toDomain
                ).toList();
    }

    public Reservation getReservationsById(
            Long id
    ){
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));

        return mapper.toDomain(reservationEntity);

    }

    public Reservation createReservation(Reservation reservationToCreate){
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("id should be empty");
        }

        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())){
            throw new IllegalArgumentException("end date should be after start date");
        }

        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("status should be empty");
        }



        var entityToSave = mapper.toEntity(reservationToCreate);
        entityToSave.setStatus(ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);

        return mapper.toDomain(savedEntity);
    }

    public Reservation UpdateReservation(Long id,Reservation reservationToUpdate){

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));

        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())){
            throw new IllegalArgumentException("end date should be after start date");
        }

        if (reservationEntity.getStatus()!= ReservationStatus.PENDING){
            throw new IllegalArgumentException("status is " + reservationEntity.getStatus());
        }


        var reservationToSave = mapper.toEntity(reservationToUpdate);
        reservationToSave.setId(reservationEntity.getId());
        reservationToSave.setStatus(ReservationStatus.PENDING);

        var updatedReservation = repository.save(reservationToSave);

        return mapper.toDomain(repository.save(reservationToSave));
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

        var isConflict  = reservationAvailabilityService.isReservationVailable(
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate()
        );
        if (isConflict){
            throw new IllegalStateException(String.format("Reservation with id %s is conflict", reservationEntity.getId()));
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);

        repository.save(reservationEntity);

        return  mapper.toDomain(reservationEntity);
    }


}
