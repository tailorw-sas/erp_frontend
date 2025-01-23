package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.InnsistConnectionParams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InnsistConnectionParamsWriteDataJPARepository extends JpaRepository<InnsistConnectionParams, UUID> {

}
