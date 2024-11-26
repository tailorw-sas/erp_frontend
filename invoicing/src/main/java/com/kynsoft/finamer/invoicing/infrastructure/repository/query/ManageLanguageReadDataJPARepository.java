package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ManageLanguageReadDataJPARepository extends JpaRepository<ManageLanguage, UUID>,
        JpaSpecificationExecutor<ManageLanguage> {

    Page<ManageLanguage> findAll(Specification specification, Pageable pageable);
}
