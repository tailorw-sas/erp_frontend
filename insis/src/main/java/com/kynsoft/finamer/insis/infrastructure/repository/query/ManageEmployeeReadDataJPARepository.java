package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageEmployeeReadDataJPARepository extends JpaRepository<ManageEmployee, UUID>, JpaSpecificationExecutor<ManageEmployee> {
}
