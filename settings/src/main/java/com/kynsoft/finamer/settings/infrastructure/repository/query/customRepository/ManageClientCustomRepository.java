package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageClient;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface ManageClientCustomRepository {

    Page<ManageClient> findAllCustom(Specification<ManageClient> specification, Pageable pageable);

    Optional<ManageClient> findByIdCustom(UUID id);
}
