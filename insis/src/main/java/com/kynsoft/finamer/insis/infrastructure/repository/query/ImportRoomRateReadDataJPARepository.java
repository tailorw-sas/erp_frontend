package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ImportRoomRate;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImportRoomRateReadDataJPARepository extends JpaRepository<ImportRoomRate, UUID> {

    List<ImportRoomRate> findByRoomRate_Id(UUID id);

    List<ImportRoomRate> findByImportProcess_Id(UUID id);

    List<ImportRoomRate> findByImportProcess_IdAndRoomRate_Id(UUID importProcessId, UUID roomRateId);

    List<ImportRoomRate> findByImportProcess_IdAndRoomRateIn(UUID importProcessId, List<RoomRate> roomRates);
}
