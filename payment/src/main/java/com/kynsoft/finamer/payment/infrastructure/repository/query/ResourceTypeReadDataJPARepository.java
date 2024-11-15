package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.MaganeResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceTypeReadDataJPARepository extends JpaRepository<MaganeResourceType, UUID>, 
        JpaSpecificationExecutor<MaganeResourceType> {

    Page<MaganeResourceType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ResourceType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);
        
    @Query("SELECT COUNT(b) FROM ResourceType b WHERE b.defaults = true AND b.id <> :id")
    Long countByDefaultAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ResourceType b WHERE b.invoice = true AND b.id <> :id")
    Long countByInvoiceAndNotId(@Param("id") UUID id);


    Optional<MaganeResourceType> findResourceTypeByCodeAndStatus(String code, Status status);

}
