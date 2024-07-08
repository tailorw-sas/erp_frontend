package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageBookingReadDataJPARepository extends JpaRepository<ManageBooking, UUID>,
        JpaSpecificationExecutor<ManageBooking> {

    Page<ManageBooking> findAll(Specification specification, Pageable pageable);

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM manage_booking mb JOIN manage_invoice  ON mb.manage_invoice=manage_invoice.id  WHERE SUBSTR(mb.hotelbookingnumber,LENGTH(mb.hotelbookingnumber) -1) = :lastTwoChars AND manage_invoice.manage_hotel = :hotelId) THEN true ELSE false END", nativeQuery = true)
    boolean existsByExactLastTwoChars(@Param("lastTwoChars") String lastTwoChars, @Param("hotelId") UUID hotelId);

}
