package com.kynsoft.finamer.settings.infrastructure.repository.command;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAlerts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageAlertsWriteDataJPARepository extends JpaRepository<ManageAlerts, UUID> {
}
