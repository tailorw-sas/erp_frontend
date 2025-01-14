package com.tailorw.tcaInnsist.infrastructure.repository.command;

import com.tailorw.tcaInnsist.infrastructure.model.ManageHotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageHotelWriteDataJPARepository extends JpaRepository<ManageHotel, UUID> {
}
