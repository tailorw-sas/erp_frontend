package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageEmployeeCustomRepository {

    Page<ManageEmployee> findAllCustom(Specification<ManageEmployee> specification, Pageable pageable);

    Optional<ManageEmployee> findByIdCustom(UUID id);

    List<ManageEmployee> findAllCustom();

    List<ManageEmployee> findAllByIdCustom(List<UUID> ids);
}
