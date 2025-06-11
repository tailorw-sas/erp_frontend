package com.kynsoft.finamer.invoicing.infrastructure.repository.query.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageEmployee;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ManageEmployeeCustomRepository {
    Optional<ManageEmployee> findByIdWithoutRelations(@Param("employeeId") UUID employeeId);
}
