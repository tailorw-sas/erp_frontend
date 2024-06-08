package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageTradingCompanies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageTradingCompaniesReadDataJPARepository extends JpaRepository<ManageTradingCompanies, UUID>,
        JpaSpecificationExecutor<ManageTradingCompanies> {

    Page<ManageTradingCompanies> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageTradingCompanies b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);
}
