package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagePaymentStatusWriteDataJpaRepository extends JpaRepository<ManagePaymentStatus, UUID> {
}
