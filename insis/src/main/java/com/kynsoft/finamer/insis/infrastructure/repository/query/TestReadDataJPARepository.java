package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.identity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestReadDataJPARepository extends JpaRepository<Test, UUID>,
        JpaSpecificationExecutor<Test> {

    Page<Test> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Test b WHERE b.userName = :userName AND b.id <> :id")
    Long countByUserNameAndNotId(@Param("userName") String userName, @Param("id") UUID id);

}
