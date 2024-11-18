package com.kynsoft.finamer.payment.application.query.collections.payment;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaymentCollectionsSummaryQueryHandler implements IQueryHandler<PaymentCollectionsSummaryQuery, PaymentCollectionResponse> {

    private final IPaymentService paymentService;

    public PaymentCollectionsSummaryQueryHandler(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @Override
    public PaymentCollectionResponse handle(PaymentCollectionsSummaryQuery query) {
      Page<PaymentDto> paymentDtos = paymentService.paymentCollectionSummary(query.getPageable(),query.getFilter());
      int totalEntries = paymentDtos.getContent().size();
      double totalDeposit = paymentDtos.getContent().parallelStream().mapToDouble(PaymentDto::getDepositBalance).sum();
      double totalDepositPercentage = totalDeposit * 100 / totalEntries;
      double totalAmount = paymentDtos.getContent().parallelStream().mapToDouble(PaymentDto::getPaymentAmount).sum();
      double totalApplied= paymentDtos.getContent().parallelStream().mapToDouble(PaymentDto::getApplied).sum();
      double totalAppliedPercentage = totalApplied * 100 / totalEntries;
      double totalNotApplied=paymentDtos.getContent().parallelStream().mapToDouble(PaymentDto::getNotApplied).sum();
      double totalNotAppliedPercentage = totalNotApplied * 100 / totalEntries;

      PaymentCollectionsSummaryResponse paymentCollectionsSummaryResponse = PaymentCollectionsSummaryResponse.builder().totalEntries(totalEntries)
              .totalAmount(totalAmount)
              .totalDeposit(totalDeposit)
              .totalDepositPercentage(totalDepositPercentage)
              .totalApplied(totalApplied)
              .totalAppliedPercentage(totalAppliedPercentage)
              .totalNotApplied(totalNotApplied)
              .totalNotAppliedPercentage(totalNotAppliedPercentage)
              .build();

        PaginatedResponse paginatedResponse = new PaginatedResponse(paymentDtos.getContent()
                ,paymentDtos.getTotalPages(),
                paymentDtos.getNumberOfElements(),
                paymentDtos.getTotalElements()
                ,paymentDtos.getSize(),
                paymentDtos.getNumber());
        return new PaymentCollectionResponse(paginatedResponse,paymentCollectionsSummaryResponse);
    }
}
