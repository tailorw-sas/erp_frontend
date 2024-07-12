package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomType;
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
public interface ManageRoomTypeReadDataJPARepository extends JpaRepository<ManageRoomType, UUID>,
        JpaSpecificationExecutor<ManageRoomType> {

    Page<ManageRoomType> findAll(Specification specification, Pageable pageable);

    Optional<ManageRoomType> findManageRoomTypeByCode(String code);

    @Query("SELECT COUNT(b) FROM ManageRoomType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(r) FROM ManageRoomType r WHERE r.code = :code  AND r.id <> :id")
    Long countByCodeAndManageHotelIdAndNotId(@Param("code") String code, @Param("id") UUID id);

    boolean existsManageRoomTypeByCode(String code);
}
