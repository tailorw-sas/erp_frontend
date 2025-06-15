package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ManageRoomRateWriteDataJPARepository extends JpaRepository<ManageRoomRate, UUID> {

    @Query(value = "SELECT * FROM fn_insert_room_rate(:p_id, :p_adults, :p_checkin, :p_checkout, :p_children, " +
                                                     ":p_deleteinvoice , :p_deleted, :p_deletedat, :p_hotelamount, :p_invoiceamount, " +
                                                     ":p_nights, :p_rateadult, :p_ratechild, :p_remark, :p_roomnumber, " +
                                                     ":p_updatedat, :p_booking)", nativeQuery = true)
    Map<String, Object> insertRoomRate(@Param("p_id") UUID id, @Param("p_adults") Integer adults,
                                       @Param("p_checkin") LocalDateTime checkIn, @Param("p_checkout") LocalDateTime checkOut,
                                       @Param("p_children") Integer children, @Param("p_deleteinvoice") boolean isDeleteInvoice,
                                       @Param("p_deleted") Boolean deleted, @Param("p_deletedat") LocalDateTime deletedAt,
                                       @Param("p_hotelamount") Double hotelAmount, @Param("p_invoiceamount") Double invoiceAmount,
                                       @Param("p_nights") Long nights, @Param("p_rateadult") Double rateByAdult,
                                       @Param("p_ratechild") Double rateByChild, @Param("p_remark") String remark,
                                       @Param("p_roomnumber") String roomNumber, @Param("p_updatedat") LocalDateTime updatedAt,
                                       @Param("p_booking") UUID bookingId);

}
