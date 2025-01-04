package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageBookingReadDataJPARepository extends JpaRepository<Booking, UUID>, 
        JpaSpecificationExecutor<Booking> {

    @Override
    Page<Booking> findAll(Specification specification, Pageable pageable);

    boolean existsManageBookingByBookingId(long bookingId);

    Optional<Booking> findManageBookingByBookingId(long bookingId);
}
