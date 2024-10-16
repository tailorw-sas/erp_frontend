package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageAgencyType;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentShareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentShareFileWriteDataJPARepository extends JpaRepository<PaymentShareFile, UUID> {
}
