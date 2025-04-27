package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageAgencyCustomRepository {

    Optional<ManageAgency> findByIdCustom(UUID id);

    Page<ManageAgency> findAllCustom(Specification<ManageAgency> specification, Pageable pageable);
}
