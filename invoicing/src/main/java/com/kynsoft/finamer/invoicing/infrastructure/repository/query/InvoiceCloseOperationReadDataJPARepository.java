package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.InvoiceCloseOperation;
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
public interface InvoiceCloseOperationReadDataJPARepository extends JpaRepository<InvoiceCloseOperation, UUID>,
        JpaSpecificationExecutor<InvoiceCloseOperation> {

    Page<InvoiceCloseOperation> findAll(Specification specification, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM InvoiceCloseOperation b WHERE b.hotel.id = :hotelId")
    Long findByHotelId(@Param("hotelId") UUID hotelId);

}
