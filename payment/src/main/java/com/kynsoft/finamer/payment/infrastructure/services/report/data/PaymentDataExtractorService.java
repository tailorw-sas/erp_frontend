package com.kynsoft.finamer.payment.infrastructure.services.report.data;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentDataExtractorService {

    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;

    // Cache para evitar consultas repetitivas dentro del mismo request
    private final Map<UUID, PaymentDto> paymentCache = new ConcurrentHashMap<>();
    private final Map<UUID, List<PaymentDetailDto>> paymentDetailsCache = new ConcurrentHashMap<>();
    private final Map<UUID, List<UUID>> invoiceIdsCache = new ConcurrentHashMap<>();

    public PaymentDataExtractorService(IPaymentService paymentService,
                                       IPaymentDetailService paymentDetailService) {
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
    }

    public PaymentDto getPayment(UUID paymentId) {
        return paymentCache.computeIfAbsent(paymentId, id -> paymentService.findById(id));
    }

    public List<PaymentDetailDto> getPaymentDetails(UUID paymentId) {
        return paymentDetailsCache.computeIfAbsent(paymentId,
                id -> paymentDetailService.findByPaymentId(id));
    }

    public List<UUID> getRelatedInvoiceIds(UUID paymentId) {
        return invoiceIdsCache.computeIfAbsent(paymentId, this::extractInvoiceIds);
    }

    public String getCommaSeparatedInvoiceIds(UUID paymentId) {
        List<UUID> invoiceIds = getRelatedInvoiceIds(paymentId);
        return invoiceIds.stream()
                .map(UUID::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    public void clearCache() {
        paymentCache.clear();
        paymentDetailsCache.clear();
        invoiceIdsCache.clear();
    }

    private List<UUID> extractInvoiceIds(UUID paymentId) {
        List<PaymentDetailDto> paymentDetails = getPaymentDetails(paymentId);

        return paymentDetails.stream()
                .map(PaymentDetailDto::getManageBooking)
                .filter(Objects::nonNull)
                .map(ManageBookingDto::getInvoice)
                .filter(Objects::nonNull)
                .map(ManageInvoiceDto::getId)
                .distinct()
                .toList();
    }
}
