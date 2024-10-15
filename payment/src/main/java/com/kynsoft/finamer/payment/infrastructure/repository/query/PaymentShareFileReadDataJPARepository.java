package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.AttachmentType;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentShareFile;
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
public interface PaymentShareFileReadDataJPARepository extends JpaRepository<PaymentShareFile, UUID>,
        JpaSpecificationExecutor<PaymentShareFile> {

    Page<PaymentShareFile> findAll(Specification specification, Pageable pageable);

}
