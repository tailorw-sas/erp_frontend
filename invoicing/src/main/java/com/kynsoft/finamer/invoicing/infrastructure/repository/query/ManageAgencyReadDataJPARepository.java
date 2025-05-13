package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
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
public interface ManageAgencyReadDataJPARepository extends JpaRepository<ManageAgency, UUID>,
        JpaSpecificationExecutor<ManageAgency> {

    Page<ManageAgency> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageAgency b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    boolean existsManageAgenciesByCode(String code);

    Optional<ManageAgency> findManageAgenciesByCode(String code);

    List<ManageAgency> findByCodeIn(List<String> codes);
}
