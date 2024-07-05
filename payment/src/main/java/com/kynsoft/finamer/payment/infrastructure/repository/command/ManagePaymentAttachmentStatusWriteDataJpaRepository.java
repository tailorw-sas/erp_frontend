package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentAttachmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagePaymentAttachmentStatusWriteDataJpaRepository extends JpaRepository<ManagePaymentAttachmentStatus, UUID> {
}
