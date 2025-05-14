package com.kynsoft.finamer.payment.infrastructure.repository.query.custom;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageEmployee;

import java.util.Optional;
import java.util.UUID;

public interface ManageEmployeeCustomRepository {

    Optional<ManageEmployee> findByIdCustom(UUID id);
}
