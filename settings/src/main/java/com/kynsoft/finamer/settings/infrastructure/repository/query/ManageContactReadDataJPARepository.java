package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageContactReadDataJPARepository extends JpaRepository<ManageContact, UUID>,
        JpaSpecificationExecutor<ManageContact> {

    Page<ManageContact> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ManageContact r WHERE r.code = :code AND r.manageHotel.id = :manageHotelId AND r.id <> :id")
    Long countByCodeAndManageHotelIdAndNotId(@Param("code") String code, @Param("manageHotelId") UUID manageHotelId, @Param("id") UUID id);

    @Query("SELECT COUNT(r) FROM ManageContact r WHERE r.email = :email AND r.manageHotel.id = :manageHotelId AND r.id <> :id")
    Long countByEmailAndManageHotelIdAndNotId(@Param("email") String email, @Param("manageHotelId") UUID manageHotelId, @Param("id") UUID id);
}
