package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ManageBookingWriteDataJpaRepository extends JpaRepository<Booking, UUID> {

    @Query(value = "select * from fr_insert_booking(:p_id, " +
            ":p_adults, " +
            ":p_bookingdate, " +
            ":p_checkin, " +
            ":p_checkout, " +
            ":p_children, " +
            ":p_contract, " +
            ":p_couponnumber, " +
            ":p_deleteinvoice, " +
            ":p_deleted, " +
            ":p_deletedat, " +
            ":p_description, " +
            ":p_dueamount, " +
            ":p_firstname, " +
            ":p_folionumber, " +
            ":p_fullname, " +
            ":p_hotelamount, " +
            ":p_hotelbookingnumber, " +
            ":p_hotelcreationdate, " +
            ":p_hotelinvoicenumber, " +
            ":p_invoiceamount, " +
            ":p_lastname, " +
            ":p_nights, " +
            ":p_rateadult, " +
            ":p_ratechild, " +
            ":p_roomnumber, " +
            ":p_updatedat, " +
            ":p_manage_invoice, " +
            ":p_manage_night_type, " +
            ":p_parent_id, " +
            ":p_manage_rate_plan, " +
            ":p_manage_room_category, " +
            ":p_manage_room_type)", nativeQuery = true)
    Map<String, Object> insertBooking(@Param("p_id") UUID id,
                                      @Param("p_adults") Integer adults,
                                      @Param("p_bookingdate")LocalDateTime bookingDate,
                                      @Param("p_checkin") LocalDateTime checkIn,
                                      @Param("p_checkout") LocalDateTime checkOut,
                                      @Param("p_children") Integer children,
                                      @Param("p_contract") String contract,
                                      @Param("p_couponnumber") String couponNumber,
                                      @Param("p_deleteinvoice") boolean isDeleteInvoice,
                                      @Param("p_deleted") Boolean deleted,
                                      @Param("p_deletedat") LocalDateTime deletedAt,
                                      @Param("p_description") String description,
                                      @Param("p_dueamount") Double dueAmount,
                                      @Param("p_firstname") String fistName,
                                      @Param("p_folionumber") String folioNumber,
                                      @Param("p_fullname") String fullName,
                                      @Param("p_hotelamount") Double hotelAmount,
                                      @Param("p_hotelbookingnumber") String hotelBookinNumber,
                                      @Param("p_hotelcreationdate") LocalDateTime hotelCreationDate,
                                      @Param("p_hotelinvoicenumber") String hotelInvoiceNumber,
                                      @Param("p_invoiceamount") Double invoiceAmount,
                                      @Param("p_lastname") String lastName,
                                      @Param("p_nights") Long nights,
                                      @Param("p_rateadult") Double rateAdult,
                                      @Param("p_ratechild") Double rateChild,
                                      @Param("p_roomnumber") String roomNumber,
                                      @Param("p_updatedat") LocalDateTime updatedAt,
                                      @Param("p_manage_invoice") UUID invoiceId,
                                      @Param("p_manage_night_type") UUID nightTypeId,
                                      @Param("p_parent_id") UUID parentId,
                                      @Param("p_manage_rate_plan") UUID ratePlanId,
                                      @Param("p_manage_room_category") UUID roomCategoryId,
                                      @Param("p_manage_room_type") UUID roomTypeId);
}
