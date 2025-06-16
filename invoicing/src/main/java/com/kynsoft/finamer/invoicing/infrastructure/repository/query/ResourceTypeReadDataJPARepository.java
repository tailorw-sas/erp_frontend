package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceTypeReadDataJPARepository extends JpaRepository<ManageResourceType, UUID>, 
        JpaSpecificationExecutor<ManageResourceType> {

    Page<ManageResourceType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageResourceType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    Optional<ManageResourceType> findResourceTypeByCode(String code);

    @Query("SELECT COUNT(b) FROM ManageResourceType b WHERE b.defaults = true AND b.id <> :id")
    Long countByDefaultsAndNotId(@Param("id") UUID id);

    @Query("SELECT r FROM ManageResourceType r WHERE r.defaults = true AND r.status = 'ACTIVE'")
    Optional<ManageResourceType> findByDefaults();

    List<ManageResourceType> findByIdIn(List<UUID> ids);
}
