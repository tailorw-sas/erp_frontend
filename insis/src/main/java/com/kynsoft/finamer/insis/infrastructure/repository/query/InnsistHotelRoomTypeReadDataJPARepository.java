package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.InnsistHotelRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface InnsistHotelRoomTypeReadDataJPARepository extends JpaRepository<InnsistHotelRoomType, UUID>, JpaSpecificationExecutor<InnsistHotelRoomType> {

    Optional<InnsistHotelRoomType> findByHotel_idAndStatus(UUID id, String status);

    @Query("SELECT COUNT(i) " +
            "FROM InnsistHotelRoomType i " +
            "JOIN i.hotel h " +
            "JOIN h.manageTradingCompany t " +
            "WHERE i.roomTypePrefix = :roomTypePrefix " +
            "AND t.id = :tradingCompanyId")
    long countByRoomTypePrefixAndTradingCompanyId(@Param("roomTypePrefix") String roomTypePrefix,
                                                  @Param("tradingCompanyId") UUID tradingCompanyId);

    Page<InnsistHotelRoomType> findAll(Specification specification, Pageable pageable);

}
