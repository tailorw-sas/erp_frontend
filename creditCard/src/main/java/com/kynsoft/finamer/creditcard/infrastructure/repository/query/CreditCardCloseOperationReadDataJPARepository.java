package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.CreditCardCloseOperation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CreditCardCloseOperationReadDataJPARepository extends JpaRepository<CreditCardCloseOperation, UUID>,
        JpaSpecificationExecutor<CreditCardCloseOperation> {

    Page<CreditCardCloseOperation> findAll(Specification specification, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM CreditCardCloseOperation b WHERE b.hotel.id = :hotelId")
    Long findByHotelId(@Param("hotelId") UUID hotelId);

    @Query("SELECT b FROM CreditCardCloseOperation b WHERE b.hotel.id IN :hotelIds")
    List<CreditCardCloseOperation> findByHotelIds(@Param("hotelIds") List<UUID> hotelIds);

    @Query("FROM CreditCardCloseOperation b WHERE b.hotel.id = :hotelId AND b.status = 'ACTIVE'")
    Optional<CreditCardCloseOperation> findActiveByHotelId(@Param("hotelId") UUID hotelId);
}
