package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface ManageHotelCustomRepository {
    Page<ManageHotel> findAllCustom(Specification<ManageHotel> specification, Pageable pageable);

    Optional<ManageHotel> findByIdCustom(UUID id);
}
