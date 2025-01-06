package com.tailorw.tcaInnsist.infrastructure.repository.redis;

import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageConnection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageConnectionRepository extends CrudRepository<ManageConnection, UUID> {

    Optional<ManageConnection> findByHotel(String hotel);

    List<ManageConnection> findByTradingCompany(String tradingCompany);
}
