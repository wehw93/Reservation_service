package school.sorokin.reservationsystem.reservations.availability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.sorokin.reservationsystem.reservations.ReservationRepository;
import school.sorokin.reservationsystem.reservations.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {

    private final ReservationRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ReservationAvailabilityController.class);

    public ReservationAvailabilityService( ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean isReservationVailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){

        if (!endDate.isAfter(startDate)){
            throw new IllegalArgumentException("end date should be after start date");
        }

        List<Long> ConflictingIds = repository.findConflictReservationIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        if (ConflictingIds.isEmpty()) {return true;}

        log.info("ConflictingIds {}", ConflictingIds);
        return false;
    }

}
