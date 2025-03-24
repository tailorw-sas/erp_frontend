package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageLanguageWriteDataJPARepository extends JpaRepository<ManageLanguage, UUID> {
}
