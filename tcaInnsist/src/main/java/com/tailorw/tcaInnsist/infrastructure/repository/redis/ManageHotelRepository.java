package com.tailorw.tcaInnsist.infrastructure.repository.redis;

import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageHotel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageHotelRepository extends CrudRepository<ManageHotel, UUID> {
    Optional<ManageHotel> findByCode(String code);
}
