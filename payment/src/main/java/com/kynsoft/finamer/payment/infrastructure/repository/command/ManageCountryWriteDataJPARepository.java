package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ManageCountryWriteDataJPARepository extends JpaRepository<ManageCountry, UUID> {
}
