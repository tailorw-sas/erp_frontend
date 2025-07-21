package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageRoomCategoryReadDataJPARepository extends JpaRepository<ManageRoomCategory, UUID>, JpaSpecificationExecutor<ManageRoomCategory> {

    Optional<ManageRoomCategory> findByCode(String code);

    @Query("SELECT m.code, m.id FROM ManageRoomCategory m WHERE m.code IN :codes")
    List<Object[]> findIdsByCodes(@Param("codes") List<String> codes);

    List<ManageRoomCategory> findByCodeIn(List<String> codes);
}
