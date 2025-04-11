package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageAgencyCustomRepository {

    Optional<ManageAgency> getAgencyByIdWithAllRelations(UUID id);
}
