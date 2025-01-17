package com.tailorw.tcaInnsist.infrastructure.repository.query;

import com.tailorw.tcaInnsist.infrastructure.model.ManageHotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ManageHotelReadDataJPARepository extends JpaRepository<ManageHotel, UUID> {
    Optional<ManageHotel> findByCode(String code);
}
