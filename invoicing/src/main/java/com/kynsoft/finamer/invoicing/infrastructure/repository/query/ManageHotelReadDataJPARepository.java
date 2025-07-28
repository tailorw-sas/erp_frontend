package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageHotel;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageHotelReadDataJPARepository extends JpaRepository<ManageHotel, UUID>,
        JpaSpecificationExecutor<ManageHotel> {

    Page<ManageHotel> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageHotel b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    boolean existsManageHotelByCode(String code);

    Optional<ManageHotel> findManageHotelByCode(String code);

    List<ManageHotel> findByCodeIn(List<String> codes);
}
