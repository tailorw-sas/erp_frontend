package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository.ManageHotelCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageHotelReadDataJPARepository extends JpaRepository<ManageHotel, UUID>,
        JpaSpecificationExecutor<ManageHotel>, ManageHotelCustomRepository {

    Page<ManageHotel> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageHotel b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT m FROM ManageHotel m " +
            "LEFT JOIN FETCH m.manageCountry " +
            "LEFT JOIN FETCH m.manageCityState " +
            "LEFT JOIN FETCH m.manageCurrency " +
            "LEFT JOIN FETCH m.manageRegion " +
            "LEFT JOIN FETCH m.manageTradingCompanies " +
            "WHERE m.id = :id")
    Optional<ManageHotel> findManageHotelWithAllRelationsById(@Param("id") UUID id);
}
