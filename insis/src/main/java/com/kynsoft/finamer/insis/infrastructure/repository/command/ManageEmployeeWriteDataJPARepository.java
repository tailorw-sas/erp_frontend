package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageEmployeeWriteDataJPARepository extends JpaRepository<ManageEmployee, UUID> {
}
