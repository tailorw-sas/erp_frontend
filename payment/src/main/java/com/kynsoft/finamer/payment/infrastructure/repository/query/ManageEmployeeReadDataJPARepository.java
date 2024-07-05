package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageEmployeeReadDataJPARepository extends JpaRepository<ManageEmployee, UUID>,
        JpaSpecificationExecutor<ManageEmployee> {

    Page<ManageEmployee> findAll(Specification specification, Pageable pageable);
}
