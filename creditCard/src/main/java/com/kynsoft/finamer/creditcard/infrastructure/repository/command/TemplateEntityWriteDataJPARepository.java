package com.kynsoft.finamer.creditcard.infrastructure.repository.command;

import com.kynsoft.finamer.creditcard.infrastructure.identity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateEntityWriteDataJPARepository extends JpaRepository<TemplateEntity, UUID> {
}
