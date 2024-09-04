package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.AttachmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AttachmentTypeReadDataJPARepository extends JpaRepository<AttachmentType, UUID>, 
        JpaSpecificationExecutor<AttachmentType> {

    Page<AttachmentType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM AttachmentType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM AttachmentType b WHERE b.defaults = true AND b.id <> :id")
    Long countByDefaultAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM AttachmentType b WHERE b.antiToIncomeImport = true AND b.id <> :id")
    Long countByAntiToIncomeImportAndNotId(@Param("id") UUID id);

    Optional<AttachmentType> findByCode(String code);
}
