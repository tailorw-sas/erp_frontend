package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ManageInvoiceWriteDataJPARepository extends JpaRepository<Invoice, UUID> {

    @Query(value = "SELECT * FROM fn_insert_invoice(:p_id, :p_aging, :p_autorec, :p_cloneparent, :p_credits, :p_deleteinvoice, :p_due_amount, " +
                                                   ":p_duedate, :p_hasattachments, :p_importtype, :p_invoiceamount, :p_invoicedate, :p_invoicestatus, " +
                                                   ":p_invoicetype, :p_iscloned, :p_ismanual, :p_originalamount, :p_resend, :p_resenddate, :p_sendstatuserror, " +
                                                   ":p_updatedat, :p_manage_agency, :p_manage_hotel, :p_invoice_status_id, :p_invoice_type_id, :p_parent_id)",
            nativeQuery = true)
    Map<String, Object> insertInvoice(@Param("p_id") UUID id, @Param("p_aging") Integer aging,
                                      @Param("p_autorec") Boolean autorec, @Param("p_cloneparent") Boolean cloneparent,
                                      @Param("p_credits") Double credits, @Param("p_deleteinvoice") Boolean deleteInvoice,
                                      @Param("p_due_amount") Double dueAmount, @Param("p_duedate") LocalDate dueDate,
                                      @Param("p_hasattachments") Boolean hasAttachments, @Param("p_importtype") String importType,
                                      @Param("p_invoiceamount") Double invoiceAmount, @Param("p_invoicedate") LocalDateTime invoiceDate,
                                      @Param("p_invoicestatus") String invoiceStatus, @Param("p_invoicetype") String invoiceType,
                                      @Param("p_iscloned") Boolean isCloned, @Param("p_ismanual") Boolean isManual,
                                      @Param("p_originalamount") Double originalAmount, @Param("p_resend") Boolean resend,
                                      @Param("p_resenddate") LocalDate resendDate, @Param("p_sendstatuserror") String sendStatusError,
                                      @Param("p_updatedat") LocalDateTime updatedAt, @Param("p_manage_agency") UUID manageAgency,
                                      @Param("p_manage_hotel") UUID manageHotel, @Param("p_invoice_status_id") UUID invoiceStatusId,
                                      @Param("p_invoice_type_id") UUID invoiceTypeId, @Param("p_parent_id") UUID parentId);

}
