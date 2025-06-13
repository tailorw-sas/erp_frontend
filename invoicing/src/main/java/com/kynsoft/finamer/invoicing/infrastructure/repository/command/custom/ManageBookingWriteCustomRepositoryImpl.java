package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsof.share.core.infrastructure.exceptions.InvoiceInsertException;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Repository
public class ManageBookingWriteCustomRepositoryImpl implements ManageBookingWriteCustomRepository{

    private static final Logger log = LoggerFactory.getLogger(ManageInvoiceWriteCustomRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void insert(Booking booking) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("pr_insert_booking")
                    .registerStoredProcedureParameter("p_id", UUID.class, ParameterMode.INOUT)
                    .registerStoredProcedureParameter("p_adults", Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_bookingdate", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_checkin", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_checkout", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_children", Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_contract", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_couponnumber", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_deleteinvoice", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_deleted", Boolean.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_deletedat", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_description", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_dueamount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_firstname", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_folionumber", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_fullname", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hotelamount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hotelbookingnumber", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hotelcreationdate", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hotelinvoicenumber", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_invoiceamount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lastname", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_nights", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_rateadult", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ratechild", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_roomnumber", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_updatedat", LocalDateTime.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_invoice", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_night_type", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_parent_id", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_rate_plan", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_room_category", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_manage_room_type", UUID.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_reservation_number", Integer.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_createdat", LocalDateTime.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_bookingid", Integer.class, ParameterMode.OUT);

            if (Objects.isNull(booking.getId())) {
                booking.setId(UUID.randomUUID());
            }

            query.setParameter("p_id", booking.getId());
            query.setParameter("p_adults", booking.getAdults());
            query.setParameter("p_bookingdate", booking.getBookingDate());
            query.setParameter("p_checkin", booking.getCheckIn());
            query.setParameter("p_checkout", booking.getCheckOut());
            query.setParameter("p_children", booking.getChildren());
            query.setParameter("p_contract", booking.getContract());
            query.setParameter("p_couponnumber", booking.getCouponNumber());
            query.setParameter("p_deleteinvoice", booking.isDeleteInvoice());
            query.setParameter("p_deleted", booking.getDeleted());
            query.setParameter("p_deletedat", booking.getDeletedAt());
            query.setParameter("p_description", booking.getDescription());
            query.setParameter("p_dueamount", booking.getDueAmount());
            query.setParameter("p_firstname", booking.getFirstName());
            query.setParameter("p_folionumber", booking.getFolioNumber());
            query.setParameter("p_fullname", booking.getFullName());
            query.setParameter("p_hotelamount", booking.getHotelAmount());
            query.setParameter("p_hotelbookingnumber", booking.getHotelBookingNumber());
            query.setParameter("p_hotelcreationdate", booking.getHotelCreationDate());
            query.setParameter("p_hotelinvoicenumber", booking.getHotelInvoiceNumber());
            query.setParameter("p_invoiceamount", booking.getInvoiceAmount());
            query.setParameter("p_lastname", booking.getLastName());
            query.setParameter("p_nights", booking.getNights());
            query.setParameter("p_rateadult", booking.getRateAdult());
            query.setParameter("p_ratechild", booking.getRateChild());
            query.setParameter("p_roomnumber", booking.getRoomNumber());
            query.setParameter("p_updatedat", booking.getUpdatedAt());
            query.setParameter("p_manage_invoice", booking.getInvoice() != null ? booking.getInvoice().getId() : null);
            query.setParameter("p_manage_night_type", booking.getNightType() != null ? booking.getNightType().getId() : null);
            query.setParameter("p_parent_id", booking.getParent() != null ? booking.getParent().getId() : null);
            query.setParameter("p_manage_rate_plan", booking.getRatePlan() != null ? booking.getRatePlan().getId() : null);
            query.setParameter("p_manage_room_category", booking.getRoomCategory() != null ? booking.getRoomCategory().getId() : null);
            query.setParameter("p_manage_room_type", booking.getRoomType() != null ? booking.getRoomType().getId() : null);

            query.execute();

            Integer reservationNumber = (Integer) query.getOutputParameterValue("p_reservation_number");
            LocalDateTime createdAt = (LocalDateTime) query.getOutputParameterValue("p_createdat");
            Integer bookingid = (Integer) query.getOutputParameterValue("p_bookingid");

            booking.setReservationNumber(reservationNumber.longValue());
            booking.setCreatedAt(createdAt);
            booking.setBookingId(bookingid.longValue());
        } catch (PersistenceException e) {
            log.error("Error inserting invoice into database", e);
            throw new InvoiceInsertException("Error inserting invoice into database", e);
        } catch (Exception exception){
            log.error("Error inserting invoice via JDBC", exception);
            throw new InvoiceInsertException("Error inserting invoice into database via JDBC", exception);
        }
    }
}
