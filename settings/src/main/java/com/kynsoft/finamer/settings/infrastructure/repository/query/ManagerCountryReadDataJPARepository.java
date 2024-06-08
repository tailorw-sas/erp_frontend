package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagerCountry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerCountryReadDataJPARepository extends JpaRepository<ManagerCountry, UUID>,
        JpaSpecificationExecutor<ManagerCountry> {

    Page<ManagerCountry> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerCountry b WHERE b.code = :code AND b.id <> :id AND b.deleted = false")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagerCountry b WHERE b.name = :name AND b.id <> :id AND b.deleted = false")
    Long countByNameAndNotId(@Param("name") String name, @Param("id") UUID id);

}
