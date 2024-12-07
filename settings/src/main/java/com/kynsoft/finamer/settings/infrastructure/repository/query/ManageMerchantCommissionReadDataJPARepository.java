package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantCommission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ManageMerchantCommissionReadDataJPARepository extends JpaRepository<ManageMerchantCommission, UUID>,
        JpaSpecificationExecutor<ManageMerchantCommission> {

    Page<ManageMerchantCommission> findAll(Specification<ManageMerchantCommission> specification, Pageable pageable);

    @Query("SELECT m FROM ManageMerchantCommission m WHERE m.managerMerchant.id = :managerMerchant AND m.manageCreditCartType.id = :manageCreditCartType")
    List<ManageMerchantCommission> findAllByManagerMerchantAndManageCreditCartType(@Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType);

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
//
//    @Query("SELECT COUNT(m) FROM ManageMerchantCommission m "
//            + "WHERE m.managerMerchant.id = :managerMerchant "
//            + "AND m.manageCreditCartType.id = :manageCreditCartType "
//            + "AND m.fromDate <= :toDate "
//            + "AND m.toDate >= :fromDate "
//            + "AND m.id <> :id")
//    Long countOverlappingRecords(
//            @Param("id")UUID id, 
//            @Param("managerMerchant") UUID managerMerchant, 
//            @Param("manageCreditCartType") UUID manageCreditCartType, 
//            @Param("fromDate") LocalDate fromDate, 
//            @Param("toDate") LocalDate toDate
//    );
}