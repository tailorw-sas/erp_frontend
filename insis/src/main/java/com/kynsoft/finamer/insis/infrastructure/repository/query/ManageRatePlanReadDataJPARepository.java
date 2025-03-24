package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRatePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageRatePlanReadDataJPARepository extends JpaRepository<ManageRatePlan, UUID>, JpaSpecificationExecutor<ManageRatePlan> {

    Optional<ManageRatePlan> findByCode(String code);

    Page<ManageRatePlan> findAll(Specification specification, Pageable pageable);

    @Query("SELECT m.code, m.id FROM ManageRatePlan m WHERE m.code IN :codes AND m.hotel.id = :hotel")
    List<Object[]> findRatePlanIdsByCodesAndHotel(@Param("codes") List<String> codes, @Param("hotel") UUID hotel);

    Optional<ManageRatePlan> findByCodeAndHotel_Id(String code, UUID hotelId);

    List<ManageRatePlan> findByCodeInAndHotel_Id(List<String> codes, UUID hotelId);
}
