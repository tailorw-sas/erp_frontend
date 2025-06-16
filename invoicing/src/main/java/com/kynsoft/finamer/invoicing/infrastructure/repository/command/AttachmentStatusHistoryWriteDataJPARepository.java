package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.AttachmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface AttachmentStatusHistoryWriteDataJPARepository extends JpaRepository<AttachmentStatusHistory, UUID> {

    @Query(value = "select * from fn_insert_attachment_status_history(:p_id, :p_attachment_id, :p_description, :p_employee, :p_employee_id, :p_updatedat, :p_invoice_id)", nativeQuery = true)
    Map<String, Object> insertAttachmentStatusHistory(@Param("p_id") UUID id,
                                                      @Param("p_attachment_id") Long attachmentId,
                                                      @Param("p_description") String description,
                                                      @Param("p_employee") String employeeName,
                                                      @Param("p_employee_id") UUID employeeId,
                                                      @Param("p_updatedat")LocalDateTime updatedAt,
                                                      @Param("p_invoice_id") UUID invoiceId);
}
