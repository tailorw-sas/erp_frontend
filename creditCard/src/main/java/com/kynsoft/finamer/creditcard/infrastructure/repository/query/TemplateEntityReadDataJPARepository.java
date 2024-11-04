package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateEntityReadDataJPARepository extends JpaRepository<TemplateEntity, UUID>, JpaSpecificationExecutor<TemplateEntity> {
    Page<TemplateEntity> findAll(Specification specification, Pageable pageable);
    Optional<TemplateEntity> findByTemplateCode(String templateCode);
    List<TemplateEntity> findByLanguageCodeAndType(String languageCode, String type);
}
