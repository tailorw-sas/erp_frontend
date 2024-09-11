package com.kynsoft.finamer.invoicing.infrastructure.repository.command;


import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageCityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageCityStateWriteDataJPARepository extends JpaRepository<ManageCityState, UUID> {

}
