package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageLanguageReadDataJPARepository extends JpaRepository<ManageLanguage, UUID> {
}
