package com.kynsoft.finamer.settings.infrastructure.repository.command;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagerPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagerPaymentStatusWriteDataJpaRepository extends JpaRepository<ManagerPaymentStatus, UUID> {
}
