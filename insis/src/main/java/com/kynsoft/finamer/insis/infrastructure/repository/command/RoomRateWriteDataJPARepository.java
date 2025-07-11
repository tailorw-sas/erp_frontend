package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import com.kynsoft.finamer.insis.infrastructure.repository.projection.RoomRateResult;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface RoomRateWriteDataJPARepository extends CrudRepository<RoomRate, UUID> {

    @Modifying
    @Query("UPDATE RoomRate r SET r.agency = :agency WHERE r.agencyCode = :agencyCode")
    int updateAgencyByAgencyCodeAndStatus(@Param("agency") ManageAgency agency, @Param("agencyCode") String agencyCode);

    @Query(value = "select o_id as id from public.fn_insert_room_rate(:p_id, :p_adults, :p_agency_code, :p_amount, :p_amount_payment_applied, " +
                                                    " :p_check_in_date, :p_check_out_date, :p_children, :p_coupon_number," +
                                                    " :p_first_name, :p_guest_name, :p_hash, :p_hotel_creation_date, :p_hotel_invoice_amount," +
                                                    " :p_hotel_invoice_number, :p_invoice_folio_number, :p_invoice_date, :p_last_name," +
                                                    " :p_original_amount, :p_quote, :p_rate_by_adult, :p_rate_by_child, :p_rate_plan_code," +
                                                    " :p_remarks, :p_renewal_number, :p_reservation_code, :p_room_number, :p_room_type_code," +
                                                    " :p_status, :p_stay_days, :p_total_number_of_guest, :p_updated_at, :p_booking_id," +
                                                    " :p_hotel_id, :p_invoice_id, :p_room_category_code, :p_agency_id, :p_rate_plan_id," +
                                                    " :p_room_category_id, :p_room_type_id)", nativeQuery = true)
    RoomRateResult insertRoomRate(@Param("p_id") UUID id, @Param("p_adults") int adults,
                          @Param("p_agency_code") String agencyCode, @Param("p_amount") Double amount,
                          @Param("p_amount_payment_applied") Double amountPaymentApplied, @Param("p_check_in_date") LocalDate checkInDate,
                          @Param("p_check_out_date") LocalDate checkOutDate, @Param("p_children") int children,
                          @Param("p_coupon_number") String couponNumber, @Param("p_first_name") String firstName,
                          @Param("p_guest_name") String guestName, @Param("p_hash") String hash,
                          @Param("p_hotel_creation_date") LocalDate hotelCreationDate, @Param("p_hotel_invoice_amount") Double hotelInvoiceAmount,
                          @Param("p_hotel_invoice_number") String hotelInvoiceNumber, @Param("p_invoice_folio_number") String invoiceFolioNumber,
                          @Param("p_invoice_date") LocalDate invoiceDate, @Param("p_last_name") String lastName,
                          @Param("p_original_amount") Double originalAmount, @Param("p_quote") Double quote,
                          @Param("p_rate_by_adult") Double rateByAdult, @Param("p_rate_by_child") Double rateByChild,
                          @Param("p_rate_plan_code") String ratePlanCode, @Param("p_remarks") String remarks,
                          @Param("p_renewal_number") String renewalNumber, @Param("p_reservation_code") String reservationCode,
                          @Param("p_room_number") String roomNumber, @Param("p_room_type_code") String roomTypeCode,
                          @Param("p_status") String status, @Param("p_stay_days") int stayDays,
                          @Param("p_total_number_of_guest") int totalNumberOfGuest, @Param("p_updated_at") LocalDateTime updatedAt,
                          @Param("p_booking_id") UUID bookingId, @Param("p_hotel_id") UUID hotelId,
                          @Param("p_invoice_id") UUID invoiceId, @Param("p_room_category_code") String roomCategoryCode,
                          @Param("p_agency_id") UUID agencyId, @Param("p_rate_plan_id") UUID ratePlanId,
                          @Param("p_room_category_id") UUID roomCategoryId, @Param("p_room_type_id") UUID roomTypeId);

    //@Query(value = "select  from public.fn_insert_room_rate(:p_id, :p_adults, :p_agency_code)", nativeQuery = true)
    //RoomRateResult insertRoomRate(@Param("p_id") UUID id, @Param("p_adults") int adults, @Param("p_agency_code") String agencyCode);
}
