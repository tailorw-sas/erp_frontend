package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RoomRateWriteDataJPARepository extends CrudRepository<RoomRate, UUID> {
}
