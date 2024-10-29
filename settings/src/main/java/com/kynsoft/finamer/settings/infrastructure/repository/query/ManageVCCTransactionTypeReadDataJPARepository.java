package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageVCCTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageVCCTransactionTypeReadDataJPARepository extends JpaRepository<ManageVCCTransactionType, UUID>,
        JpaSpecificationExecutor<ManageVCCTransactionType> {

    Page<ManageVCCTransactionType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.isDefault = true AND b.subcategory = false AND b.id <> :id")
    Long countByIsDefaultsAndNotSubCategoryAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.isDefault = true AND b.subcategory = true AND b.id <> :id")
    Long countByIsDefaultsAndSubCategoryAndNotId(@Param("id") UUID id);

}
