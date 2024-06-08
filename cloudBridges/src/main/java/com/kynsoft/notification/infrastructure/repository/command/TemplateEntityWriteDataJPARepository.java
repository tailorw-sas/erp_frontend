package com.kynsoft.notification.infrastructure.repository.command;

import com.kynsoft.notification.infrastructure.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateEntityWriteDataJPARepository extends JpaRepository<TemplateEntity, UUID> {
}
