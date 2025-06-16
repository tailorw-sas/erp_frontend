package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.InvoiceStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface InvoiceStatusHistoryWriteDataJPARepository extends JpaRepository<InvoiceStatusHistory, UUID> {

    @Query(value = "select * from fn_insert_invoice_status_history(:p_id, :p_description, :p_employee, :p_invoice_status, :p_updatedat, :p_invoice_id)", nativeQuery = true)
    Map<String, Object> insertInvoiceStatusHistory(@Param("p_id") UUID id,
                                                   @Param("p_description") String description,
                                                   @Param("p_employee") String employee,
                                                   @Param("p_invoice_status") String invoiceStatus,
                                                   @Param("p_updatedat")LocalDateTime updatedAt,
                                                   @Param("p_invoice_id") UUID invoiceId);
}
