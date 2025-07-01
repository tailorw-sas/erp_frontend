package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ManageAdjustmentWriteDataJpaRepository extends JpaRepository<ManageAdjustment, UUID> {

    @Query(value = "SELECT * FROM fn_insert_adjustment(:p_id, :p_amount, :p_date, :p_deleteinvoice, :p_deleted, :p_deletedat, :p_description, :p_employee, :p_updatedat, :p_manage_payment_type, :p_manage_room_rate, :p_manage_transaction_type)", nativeQuery = true)
    Map<String, Object> insertAdjustment(
            @Param("p_id") UUID id,
            @Param("p_amount") Double amount,
            @Param("p_date") LocalDateTime date,
            @Param("p_deleteinvoice") Boolean deleteInvoice,
            @Param("p_deleted") Boolean deleted,
            @Param("p_deletedat") LocalDateTime deletedAt,
            @Param("p_description") String description,
            @Param("p_employee") String employee,
            @Param("p_updatedat") LocalDateTime updatedAt,
            @Param("p_manage_payment_type") UUID paymentTransactionType,
            @Param("p_manage_room_rate") UUID roomRate,
            @Param("p_manage_transaction_type") UUID transaction
    );
}
