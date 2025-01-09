package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.infrastructure.identity.HotelInvoiceNumberSequence;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ManageHotelInvoiceNumberSequenceReadDataJPARepository extends JpaRepository<HotelInvoiceNumberSequence, UUID>,
        JpaSpecificationExecutor<HotelInvoiceNumberSequence> {

    @Override
    Page<HotelInvoiceNumberSequence> findAll(Specification specification, Pageable pageable);

    @Query("SELECT b FROM HotelInvoiceNumberSequence b WHERE b.hotel.code = :code AND b.invoiceType = :invoiceType")
    Optional<HotelInvoiceNumberSequence> getByHotelCodeAndInvoiceType(@Param("code") String code, @Param("invoiceType") EInvoiceType invoiceType);

    @Query("SELECT b FROM HotelInvoiceNumberSequence b WHERE b.manageTradingCompanies.code = :code AND b.invoiceType = :invoiceType")
    Optional<HotelInvoiceNumberSequence> getByTradingCompanyCodeAndInvoiceType(@Param("code") String code, @Param("invoiceType") EInvoiceType invoiceType);

}
