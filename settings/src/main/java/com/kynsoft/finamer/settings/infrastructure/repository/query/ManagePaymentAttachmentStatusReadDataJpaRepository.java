package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentAttachmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManagePaymentAttachmentStatusReadDataJpaRepository extends JpaRepository<ManagePaymentAttachmentStatus, UUID> {
    Page<ManagePaymentAttachmentStatus> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCode(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.name = :name AND b.id <> :id")
    Long countByNameAndNotId(@Param("name") String name, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.defaults = true AND b.id <> :id")
    Long countByDefaultAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.nonNone = true AND b.id <> :id")
    Long countByNonNoneAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.patWithAttachment = true AND b.id <> :id")
    Long countByPatWithAttachmentAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentAttachmentStatus b WHERE b.pwaWithOutAttachment = true AND b.id <> :id")
    Long countByPwaWithOutAttachmentAndNotId(@Param("id") UUID id);

}
