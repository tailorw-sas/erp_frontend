package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BookingWriteDataJPARepository extends JpaRepository<Booking, UUID> {

    @Modifying
    @Query("UPDATE Booking b SET b.agency = :agency WHERE b.agencyCode = :agencyCode AND b.status in ('PENDING', 'FAILED')")
    int updateAgencyByAgencyCodeAndStatus(@Param("agency") ManageAgency agency, @Param("agencyCode") String agencyCode);
}
