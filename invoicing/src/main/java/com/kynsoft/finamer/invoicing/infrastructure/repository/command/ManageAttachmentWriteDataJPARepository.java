package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ManageAttachmentWriteDataJPARepository extends JpaRepository<ManageAttachment, UUID> {

    @Query(value = "select * from fn_insert_invoice_attachment(:p_id, :p_deleteinvoice, :p_deleted, :p_deletedat, :p_employee, :p_employeeid, :p_file, :p_filename, :p_remark, :p_updatedat, :p_manage_invoice, :p_resource_type, :p_manage_attachment_type)", nativeQuery = true)
    Map<String, Object> insertInvoiceAttachment(@Param("p_id") UUID id,
                                                @Param("p_deleteinvoice") boolean isDeleteInvoice,
                                                @Param("p_deleted") Boolean deleted,
                                                @Param("p_deletedat") LocalDateTime deletedAt,
                                                @Param("p_employee") String employee,
                                                @Param("p_employeeid") UUID employeeId,
                                                @Param("p_file") String file,
                                                @Param("p_filename") String filename,
                                                @Param("p_remark") String remark,
                                                @Param("p_updatedat") LocalDateTime updatedAt,
                                                @Param("p_manage_invoice") UUID invoiceId,
                                                @Param("p_resource_type") UUID resourceTypeId,
                                                @Param("p_manage_attachment_type") UUID attachmentTypeId);

}
