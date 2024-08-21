package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.DBConection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DBConectionReadDataJPARepository extends JpaRepository<DBConection, UUID>,
        JpaSpecificationExecutor<DBConection> {

    Page<DBConection> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM DBConection b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);
}
