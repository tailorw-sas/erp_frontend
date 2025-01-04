package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgencyContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageAgencyContactReadDataJPARepository extends JpaRepository<ManageAgencyContact, UUID>,
        JpaSpecificationExecutor<ManageAgencyContact> {

    Page<ManageAgencyContact> findAll(Specification specification, Pageable pageable);
}
