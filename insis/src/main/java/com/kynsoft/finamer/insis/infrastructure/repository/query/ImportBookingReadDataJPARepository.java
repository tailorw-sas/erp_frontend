package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ImportBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImportBookingReadDataJPARepository extends JpaRepository<ImportBooking, UUID> {

    List<ImportBooking> findByBooking_Id(UUID id);

    List<ImportBooking> findByImportProcess_Id(UUID id);

    List<ImportBooking> findByImportProcess_IdAndBooking_Id(UUID importProcessId, UUID bookingId);

    List<ImportBooking> findByImportProcess_IdAndBookingIn(UUID importProcessId, List<Booking> bookings);
}
