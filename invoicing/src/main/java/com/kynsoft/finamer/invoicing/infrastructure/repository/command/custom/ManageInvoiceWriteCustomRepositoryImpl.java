package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsof.share.core.infrastructure.exceptions.InvoiceInsertException;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

@Repository
public class ManageInvoiceWriteCustomRepositoryImpl implements ManageInvoiceWriteCustomRepository {

    private static final Logger log = LoggerFactory.getLogger(ManageInvoiceWriteCustomRepositoryImpl.class);

    @Autowired
    private DataSource dataSource;

    //@PersistenceContext
    //private EntityManager entityManager;

    @Override
    @Transactional
    public void insert(Invoice invoice) {
        String sql = "{ CALL public.pr_insert_invoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try(Connection connection = dataSource.getConnection();
            CallableStatement stmt = connection.prepareCall(sql)){

            connection.setAutoCommit(false);

            stmt.setObject(1, invoice.getId());
            stmt.setObject(2, invoice.getAging());
            stmt.setObject(3, invoice.getAutoRec());
            stmt.setObject(4, invoice.isCloneParent());
            stmt.setObject(5, invoice.getCredits());
            stmt.setObject(6, invoice.isDeleteInvoice());
            stmt.setObject(7, invoice.getDueAmount());
            stmt.setObject(8, invoice.getDueDate());
            stmt.setObject(9, invoice.getHasAttachments());
            stmt.setString(10, invoice.getImportType().name());
            stmt.setDouble(11, invoice.getInvoiceAmount());
            stmt.setObject(12, invoice.getInvoiceDate());
            stmt.setString(13, invoice.getInvoiceStatus().name());
            stmt.setString(14, invoice.getInvoiceType().name());
            stmt.setObject(15, invoice.getIsCloned());
            stmt.setObject(16, invoice.getIsManual());
            stmt.setDouble(17, invoice.getOriginalAmount());
            stmt.setObject(18, invoice.getReSend());
            stmt.setObject(19, invoice.getReSendDate());
            stmt.setString(20, invoice.getSendStatusError());
            stmt.setObject(21, invoice.getUpdatedAt());
            stmt.setObject(22, invoice.getAgency() != null ? invoice.getAgency().getId() : null);
            stmt.setObject(23, invoice.getHotel() != null ? invoice.getHotel().getId() : null);
            stmt.setObject(24, invoice.getManageInvoiceStatus() != null ? invoice.getManageInvoiceStatus().getId() : null);
            stmt.setObject(25, invoice.getManageInvoiceType() != null ? invoice.getManageInvoiceType().getId() : null);
            stmt.setObject(26, invoice.getParent() != null ? invoice.getParent().getId() : null);

            // OUT parameters
            stmt.registerOutParameter(27, Types.BIGINT); // p_invoice_no
            stmt.registerOutParameter(28, Types.VARCHAR); // p_invoicenumber
            stmt.registerOutParameter(29, Types.VARCHAR); // p_invoicenumberprefix
            stmt.registerOutParameter(30, Types.INTEGER); // p_invoiceid

            log.debug("Inserting invoice with ID: {}", invoice.getId());
            log.info("Hilo actual: {}", Thread.currentThread().getName());

            stmt.execute();

            invoice.setInvoiceNo(stmt.getLong(27));
            invoice.setInvoiceNumber(stmt.getString(28));
            invoice.setInvoiceNumberPrefix(stmt.getString(29));
            invoice.setInvoiceId((long) stmt.getInt(30));

            connection.commit();
        } catch (PersistenceException e) {
            log.error("Error inserting invoice into database", e);
            throw new InvoiceInsertException("Error inserting invoice into database", e);
        } catch (SQLException sqlException){
            log.error("Error inserting invoice via JDBC", sqlException);
            throw new InvoiceInsertException("Error inserting invoice into database via JDBC", sqlException);
        }

        /*try{
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("pr_insert_invoice")
                    .registerStoredProcedureParameter("p_id", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_aging", Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_autorec", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cloneparent", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_credits", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_deleteinvoice", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_due_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_duedate", LocalDate.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hasattachments", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_importtype", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoiceamount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoicedate", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoicestatus", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoicetype", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_iscloned", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ismanual", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_originalamount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_resend", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_resenddate", LocalDate.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_sendstatuserror", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_updatedat", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_agency", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_hotel", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoice_status_id", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoice_type_id", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_parent_id", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoice_no", Long.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_invoicenumber", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_invoicenumberprefix", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_invoiceid", Integer.class, ParameterMode.OUT);

            query.setParameter("p_id", invoice.getId());
            query.setParameter("p_aging", invoice.getAging());
            query.setParameter("p_autorec", invoice.getAutoRec());
            query.setParameter("p_cloneparent", invoice.isCloneParent());
            query.setParameter("p_credits", invoice.getCredits());
            query.setParameter("p_deleteinvoice", invoice.isDeleteInvoice());
            query.setParameter("p_due_amount", invoice.getDueAmount());
            query.setParameter("p_duedate", invoice.getDueDate());
            query.setParameter("p_hasattachments", invoice.getHasAttachments());
            query.setParameter("p_importtype", invoice.getImportType().name());
            query.setParameter("p_invoiceamount", invoice.getInvoiceAmount());
            query.setParameter("p_invoicedate", invoice.getInvoiceDate());
            query.setParameter("p_invoicestatus", invoice.getInvoiceStatus().name());
            query.setParameter("p_invoicetype", invoice.getInvoiceType().name());
            query.setParameter("p_iscloned", invoice.getIsCloned());
            query.setParameter("p_ismanual", invoice.getIsManual());
            query.setParameter("p_originalamount", invoice.getOriginalAmount());
            query.setParameter("p_resend", invoice.getReSend());
            query.setParameter("p_resenddate", invoice.getReSendDate());
            query.setParameter("p_sendstatuserror", invoice.getSendStatusError());
            query.setParameter("p_updatedat", invoice.getUpdatedAt());
            query.setParameter("p_manage_agency", invoice.getAgency() != null ? invoice.getAgency().getId() : null);
            query.setParameter("p_manage_hotel", invoice.getHotel() != null ? invoice.getHotel().getId() : null);
            query.setParameter("p_invoice_status_id", invoice.getManageInvoiceStatus() != null ? invoice.getManageInvoiceStatus().getId() : null);
            query.setParameter("p_invoice_type_id", invoice.getManageInvoiceType() != null ? invoice.getManageInvoiceType().getId() : null);
            query.setParameter("p_parent_id", invoice.getParent() != null ? invoice.getParent().getId() : null);

            log.debug("Inserting invoice with ID: {}", invoice.getId());
            log.info("Hilo actual: {}", Thread.currentThread().getName());
            query.execute();

            Long invoiceNo = (Long) query.getOutputParameterValue("p_invoice_no");
            String invoiceNumber = (String) query.getOutputParameterValue("p_invoicenumber");
            String invoiceNumberPrefix = (String) query.getOutputParameterValue("p_invoicenumberprefix");
            Integer invoiceId = (Integer) query.getOutputParameterValue("p_invoiceid");

            invoice.setInvoiceNo(invoiceNo);
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceNumberPrefix(invoiceNumberPrefix);
            invoice.setInvoiceId(invoiceId.longValue());

        } catch (PersistenceException e) {
            log.error("Error inserting invoice into database", e);
            throw new InvoiceInsertException("Error inserting invoice into database", e);
        } catch (Exception e) {
            log.error("Unexpected error inserting invoice", e);
            throw new InvoiceInsertException("Unexpected error inserting invoice", e);
        }*/
    }
}
