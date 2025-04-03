package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ManageEmployeeCustomRepository {

    Page<ManageEmployee> findAllCustom(Specification<ManageEmployee> specification, Pageable pageable);
}
