package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageBookingReadDataJPARepository extends JpaRepository<ManageBooking, UUID>,
        JpaSpecificationExecutor<ManageBooking> {

    @Override
    Page<ManageBooking> findAll(Specification specification, Pageable pageable);
}
