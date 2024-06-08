package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CustomerReadDataJPARepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    Page<Customer> findAll(Specification specification, Pageable pageable);
}
