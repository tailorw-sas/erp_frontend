package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageAgency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageAgencyReadDataJPARepository extends JpaRepository<ManageAgency, UUID>,
        JpaSpecificationExecutor<ManageAgency> {

    Page<ManageAgency> findAll(Specification specification, Pageable pageable);

    boolean existsByCode(String code);

   Optional<ManageAgency> findByCode(String code);
}
