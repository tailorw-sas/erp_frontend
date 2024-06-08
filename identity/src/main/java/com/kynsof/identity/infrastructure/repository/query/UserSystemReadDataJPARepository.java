package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.UserSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserSystemReadDataJPARepository extends JpaRepository<UserSystem, UUID>,
        JpaSpecificationExecutor<UserSystem> {
    Page<UserSystem> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM UserSystem b WHERE b.userName = :userName AND b.id <> :id")
    Long countByUserNameAndNotId(@Param("userName") String userName, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM UserSystem b WHERE b.email = :email AND b.id <> :id")
    Long countByEmailAndNotId(@Param("email") String email, @Param("id") UUID id);

}
