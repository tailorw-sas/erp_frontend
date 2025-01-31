package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    PaymentDto create(PaymentDto dto);

    void update(PaymentDto dto);

    void delete(PaymentDto dto);

    PaymentDto findById(UUID id);

    /**
     * Permite obtener los Payment con Detalles.
     * Cuando existe pago aplicado, te proporciona los Booking y el invoice al cual
     * esta asociado el booking.
     * @param id
     * @return 
     */
    PaymentDto findPaymentByIdAndDetails(UUID id);

    boolean existPayment(long genId);

    PaymentDto findByPaymentId(long paymentId);

    PaymentProjection findByPaymentIdProjection(long paymentId);

    PaymentProjectionSimple findPaymentIdCacheable(long paymentId);

    void clearCache();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Page<PaymentDto> paymentCollectionSummary(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse searchExcelExporter(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<PaymentDto> createBulk(List<PaymentDto> dtoList);

    void getAll();

    Long countByAgency(UUID agencyId);

    Long countByAgencyOther(UUID agencyId);

    Long findMaxId();
}
