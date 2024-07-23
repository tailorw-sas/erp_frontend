package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.PaymentCloseOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PaymentCloseOperationReadDataJPARepository extends JpaRepository<PaymentCloseOperation, UUID>,
        JpaSpecificationExecutor<PaymentCloseOperation> {

    Page<PaymentCloseOperation> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM PaymentCloseOperation b WHERE b.hotel.id = :hotelId")
    Long findByHotelId(@Param("hotelId") UUID hotelId);

    @Query("SELECT b FROM PaymentCloseOperation b WHERE b.hotel.id IN :hotelIds")
    List<PaymentCloseOperation> findByHotelIds(@Param("hotelIds") List<UUID> hotelIds);

    @Query("SELECT b FROM PaymentCloseOperation b WHERE b.hotel.id = :hotel")
    Optional<PaymentCloseOperation> findByHotelIds(@Param("hotel") UUID hotel);

}
