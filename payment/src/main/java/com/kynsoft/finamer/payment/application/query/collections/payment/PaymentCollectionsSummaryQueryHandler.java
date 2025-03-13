package com.kynsoft.finamer.payment.application.query.collections.payment;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class PaymentCollectionsSummaryQueryHandler implements IQueryHandler<PaymentCollectionsSummaryQuery, PaymentCollectionResponse> {

    private final IPaymentService paymentService;

    public PaymentCollectionsSummaryQueryHandler(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @Override
    public PaymentCollectionResponse handle(PaymentCollectionsSummaryQuery query) {
        Page<PaymentDto> paymentDtos = paymentService.paymentCollectionSummary(query.getPageable(), query.getFilter());

        List<PaymentDto> payments = paymentDtos.getContent();

        int totalEntries = payments.size();
        BigDecimal totalEntriesBD = BigDecimal.valueOf(totalEntries);

        BigDecimal totalDeposit = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalApplied = BigDecimal.ZERO;
        BigDecimal totalNotApplied = BigDecimal.ZERO;

        for (PaymentDto p : payments) {
            totalDeposit = totalDeposit.add(BigDecimal.valueOf(p.getDepositBalance()));
            totalAmount = totalAmount.add(BigDecimal.valueOf(p.getPaymentAmount()));
            totalApplied = totalApplied.add(BigDecimal.valueOf(p.getApplied()));
            totalNotApplied = totalNotApplied.add(BigDecimal.valueOf(p.getNotApplied()));
        }

        BigDecimal totalDepositPercentage = totalEntries > 0
                ? BankerRounding.round(totalDeposit.multiply(BigDecimal.valueOf(100)).divide(totalEntriesBD, 4, RoundingMode.HALF_EVEN))
                : BigDecimal.ZERO;

        BigDecimal totalAppliedPercentage = totalEntries > 0
                ? BankerRounding.round(totalApplied.multiply(BigDecimal.valueOf(100)).divide(totalEntriesBD, 4, RoundingMode.HALF_EVEN))
                : BigDecimal.ZERO;

        BigDecimal totalNotAppliedPercentage = totalEntries > 0
                ? BankerRounding.round(totalNotApplied.multiply(BigDecimal.valueOf(100)).divide(totalEntriesBD, 4, RoundingMode.HALF_EVEN))
                : BigDecimal.ZERO;

        PaymentCollectionsSummaryResponse paymentCollectionsSummaryResponse = PaymentCollectionsSummaryResponse.builder()
                .totalEntries(totalEntries)
                .totalAmount(BankerRounding.round(totalAmount).doubleValue())
                .totalDeposit(BankerRounding.round(totalDeposit).doubleValue())
                .totalDepositPercentage(totalDepositPercentage.doubleValue())
                .totalApplied(BankerRounding.round(totalApplied).doubleValue())
                .totalAppliedPercentage(totalAppliedPercentage.doubleValue())
                .totalNotApplied(BankerRounding.round(totalNotApplied).doubleValue())
                .totalNotAppliedPercentage(totalNotAppliedPercentage.doubleValue())
                .build();

        // Crear objeto de paginación
        PaginatedResponse paginatedResponse = new PaginatedResponse(
                payments,
                paymentDtos.getTotalPages(),
                paymentDtos.getNumberOfElements(),
                paymentDtos.getTotalElements(),
                paymentDtos.getSize(),
                paymentDtos.getNumber()
        );

        return new PaymentCollectionResponse(paginatedResponse, paymentCollectionsSummaryResponse);
    }
}
