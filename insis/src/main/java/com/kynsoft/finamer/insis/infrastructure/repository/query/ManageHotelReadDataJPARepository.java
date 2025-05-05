package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface ManageHotelReadDataJPARepository extends JpaRepository<ManageHotel, UUID>, JpaSpecificationExecutor<ManageHotel> {

    Optional<ManageHotel> findByCode(String code);

    Optional<ManageHotel> findByManageTradingCompany_id(UUID id);

    List<ManageHotel> findByIdIn(List<UUID> ids);

    Page<ManageHotel> findAll(Specification specification, Pageable pageable);
}
