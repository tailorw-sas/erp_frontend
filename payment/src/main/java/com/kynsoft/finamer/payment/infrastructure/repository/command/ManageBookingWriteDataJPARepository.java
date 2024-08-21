package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageBookingWriteDataJPARepository extends JpaRepository<ManageBooking, UUID> {
}
