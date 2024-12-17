package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantCommission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageMerchantCommissionReadDataJPARepository extends JpaRepository<ManageMerchantCommission, UUID>,
        JpaSpecificationExecutor<ManageMerchantCommission> {

    Page<ManageMerchantCommission> findAll(Specification specification, Pageable pageable);

    @Query("SELECT m FROM ManageMerchantCommission m WHERE m.managerMerchant.id = :managerMerchant AND m.manageCreditCartType.id = :manageCreditCartType")
    List<ManageMerchantCommission> findAllByManagerMerchantAndManageCreditCartType(@Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType);

    @Query("SELECT m FROM ManageMerchantCommission m WHERE m.managerMerchant.id = :managerMerchant AND m.manageCreditCartType.id = :manageCreditCartType AND m.fromDate <= :date AND (m.toDate IS NULL OR m.toDate >= :date)")
    Optional<ManageMerchantCommission> findByManagerMerchantAndManageCreditCartTypeAndDateWithinRangeOrNoEndDate(@Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType, @Param("date") LocalDate date);

    @Query("SELECT m FROM ManageMerchantCommission m WHERE m.managerMerchant.id = :managerMerchant AND m.manageCreditCartType.id = :manageCreditCartType AND m.id <> :id")
    List<ManageMerchantCommission> findAllByManagerMerchantAndManageCreditCartTypeById(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType);

    @Query("SELECT COUNT(m) FROM ManageMerchantCommission m "
            + "WHERE m.managerMerchant.id = :managerMerchant "
            + "AND m.manageCreditCartType.id = :manageCreditCartType "
            + "AND m.commission = :commission "
            + "AND m.calculationType = :calculationType "
            + "AND m.fromDate <= :toDate "
            + "AND m.toDate >= :fromDate "
            + "AND m.id <> :id")
    Long countOverlappingRecords(
            @Param("id")UUID id,
            @Param("managerMerchant") UUID managerMerchant,
            @Param("manageCreditCartType") UUID manageCreditCartType,
            @Param("commission") Double commission,
            @Param("calculationType") String calculationType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
