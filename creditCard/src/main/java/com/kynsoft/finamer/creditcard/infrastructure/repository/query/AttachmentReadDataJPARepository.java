package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentReadDataJPARepository extends JpaRepository<Attachment, UUID>,
        JpaSpecificationExecutor<Attachment> {

    Page<Attachment> findAll(Specification specification, Pageable pageable);

    @Query("SELECT b FROM Attachment b WHERE b.transaction.id = :transaction")
    List<Attachment> findAllByTransactionId(@Param("transaction") Long transaction);

}
