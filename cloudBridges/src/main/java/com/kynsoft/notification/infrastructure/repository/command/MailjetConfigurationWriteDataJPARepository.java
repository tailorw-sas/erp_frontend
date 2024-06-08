package com.kynsoft.notification.infrastructure.repository.command;

import com.kynsoft.notification.infrastructure.entity.MailjetConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MailjetConfigurationWriteDataJPARepository extends JpaRepository<MailjetConfiguration, UUID> {
}
