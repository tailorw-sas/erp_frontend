package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerWriteDataJPARepository extends JpaRepository<Customer, UUID> {
}
