package com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.IPaymentImportExtrasFieldProcessor;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AntiPaymentImportExtrasFieldProcessorImpl implements IPaymentImportExtrasFieldProcessor<PaymentAntiRowError> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService transactionTypeService;

    private final Logger log = LoggerFactory.getLogger(AntiPaymentImportExtrasFieldProcessorImpl.class);

    public AntiPaymentImportExtrasFieldProcessorImpl(IPaymentDetailService paymentDetailService,
                                                     IManagePaymentTransactionTypeService transactionTypeService) {
        this.paymentDetailService = paymentDetailService;
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public PaymentAntiRowError addExtrasField(PaymentAntiRowError rowError) {
            this.addPaymentExtraField(rowError);
            this.addTransactionCategoryName(rowError);
        return rowError;
    }

    private void addPaymentExtraField(PaymentAntiRowError rowError) {
        if (Objects.nonNull(rowError.getRow().getTransactionId()) &&
                paymentDetailService.existByGenId(rowError.getRow().getTransactionId().intValue())) {
            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(rowError.getRow().getTransactionId().intValue());
            PaymentDto paymentDto = paymentDetailDto.getPayment();
            rowError.getRow().setPaymentId(String.valueOf(paymentDto.getPaymentId()));
            if (Objects.nonNull(paymentDetailDto.getAmount())) {
                rowError.getRow().setTransactionCheckDepositAmount(String.valueOf(paymentDetailDto.getAmount()));
            }
            if (Objects.nonNull(paymentDetailDto.getApplyDepositValue())){
                rowError.getRow().setTransactionCheckDepositBalance(String.valueOf(paymentDetailDto.getApplyDepositValue()));
            }

        }
    }


    private void addTransactionCategoryName(PaymentAntiRowError rowError) {
        FilterCriteria markAsDefault = new FilterCriteria();
        markAsDefault.setKey("defaults");
        markAsDefault.setValue(true);
        markAsDefault.setOperator(SearchOperation.EQUALS);
        markAsDefault.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria applyDeposit = new FilterCriteria();
        applyDeposit.setKey("applyDeposit");
        applyDeposit.setValue(true);
        applyDeposit.setOperator(SearchOperation.EQUALS);
        applyDeposit.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);
        PaginatedResponse response = transactionTypeService.search(Pageable.unpaged(), List.of(markAsDefault, applyDeposit, statusActive));
        //Assert.notEmpty(response.getData(), "There is not  default apply deposit transaction type");
        if (Objects.isNull(response.getData()) || response.getData().isEmpty()) {
            log.error("There is not  default apply deposit transaction type");
        } else {
            ManagePaymentTransactionTypeResponse response1 = (ManagePaymentTransactionTypeResponse) response.getData().get(0);
            rowError.getRow().setTransactionCategoryName(response1.getCode()+"-"+response1.getName());
        }
    }


}
