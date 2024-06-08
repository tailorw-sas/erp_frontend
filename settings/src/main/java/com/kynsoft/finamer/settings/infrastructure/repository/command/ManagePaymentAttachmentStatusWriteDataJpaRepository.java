package com.kynsoft.finamer.settings.infrastructure.repository.command;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentAttachmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagePaymentAttachmentStatusWriteDataJpaRepository extends JpaRepository<ManagePaymentAttachmentStatus, UUID> {
}
