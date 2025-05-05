package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RoomRateWriteDataJPARepository extends CrudRepository<RoomRate, UUID> {

    @Modifying
    @Query("UPDATE RoomRate r SET r.agency = :agency WHERE r.agencyCode = :agencyCode")
    int updateAgencyByAgencyCodeAndStatus(@Param("agency") ManageAgency agency, @Param("agencyCode") String agencyCode);
}
