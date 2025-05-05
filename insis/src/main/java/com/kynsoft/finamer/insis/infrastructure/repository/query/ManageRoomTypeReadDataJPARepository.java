package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomType;
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

public interface ManageRoomTypeReadDataJPARepository extends JpaRepository<ManageRoomType, UUID>, JpaSpecificationExecutor<ManageRoomType> {

    Optional<ManageRoomType> findByCode(String code);

    Page<ManageRoomType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT m.code, m.id FROM ManageRoomType m WHERE m.code IN :codes AND m.hotel.id = :hotel")
    List<Object[]> findRoomTypeIdsByCodesAndHotel(@Param("codes") List<String> codes, @Param("hotel") UUID hotel);

    Optional<ManageRoomType> findByCodeAndHotel_Id(String code, UUID hotelId);

    List<ManageRoomType> findByCodeInAndHotel_Id(List<String> codes, UUID hotelId);
}
