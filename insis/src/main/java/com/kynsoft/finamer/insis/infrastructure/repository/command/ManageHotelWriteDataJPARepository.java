package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageHotelWriteDataJPARepository extends JpaRepository<ManageHotel, UUID> {
}
