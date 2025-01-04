package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageNightType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ManageNightTypeReadDataJPARepository extends JpaRepository<ManageNightType, UUID>,
        JpaSpecificationExecutor<ManageNightType> {

    Page<ManageNightType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageNightType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    boolean existsManageNightTypeByCode(String code);

    Optional<ManageNightType> findManageNightTypeByCode(String code);

}
