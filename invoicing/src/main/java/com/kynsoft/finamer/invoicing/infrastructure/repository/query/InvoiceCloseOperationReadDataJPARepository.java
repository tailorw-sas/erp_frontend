package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.InvoiceCloseOperation;
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
public interface InvoiceCloseOperationReadDataJPARepository extends JpaRepository<InvoiceCloseOperation, UUID>,
        JpaSpecificationExecutor<InvoiceCloseOperation> {

    Page<InvoiceCloseOperation> findAll(Specification specification, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM InvoiceCloseOperation b WHERE b.hotel.id = :hotelId")
    Long findByHotelId(@Param("hotelId") UUID hotelId);

    @Query("SELECT b FROM InvoiceCloseOperation b WHERE b.hotel.id IN :hotelIds")
    List<InvoiceCloseOperation> findByHotelIds(@Param("hotelIds") List<UUID> hotelIds);

    @Query("SELECT b FROM InvoiceCloseOperation b WHERE b.hotel.id = :hotelId AND b.status = 'ACTIVE'")
    Optional<InvoiceCloseOperation> findActiveByHotelId(@Param("hotelId") UUID hotelId);
}
