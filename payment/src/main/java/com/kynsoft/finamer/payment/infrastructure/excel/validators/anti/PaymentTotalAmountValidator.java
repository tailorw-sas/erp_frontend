package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaymentTotalAmountValidator extends ExcelListRuleValidator<AntiToIncomeRow> {

    private final Cache cache;

    public PaymentTotalAmountValidator(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void validate(List<AntiToIncomeRow> obj, List<RowErrorField> errorRowList) {
        Map<Long, List<AntiToIncomeRow>> paymentDetailGroupedMap = this.getAntiToIncomeGroupedById(obj);

        for(Map.Entry<Long, List<AntiToIncomeRow>> entry : paymentDetailGroupedMap.entrySet()){
            PaymentDetailDto depositPaymentDetail = this.cache.getPaymentDetailByPaymentDetailId(entry.getKey());
            if(Objects.nonNull(depositPaymentDetail) && depositPaymentDetail.getTransactionType().getDeposit()){
                List<Integer> rowNumberList = entry.getValue().stream().map(AntiToIncomeRow::getRowNumber).toList();
                double amountToImport = entry.getValue().stream()
                        .filter(antiToIncomeRow -> Objects.nonNull(antiToIncomeRow.getAmount()))
                        .mapToDouble(AntiToIncomeRow::getAmount).sum();

                if(amountToImport > depositPaymentDetail.getApplyDepositValue()){
                    addErrorsToRowList(errorRowList, rowNumberList, new ErrorField("Payment Amount", "Deposit Amount must be greather than zero and less or equal than the selected transaction amount."));
                }
            }
        }
    }

    private Map<Long, List<AntiToIncomeRow>> getAntiToIncomeGroupedById(List<AntiToIncomeRow> antiToIncomeRows){
        return antiToIncomeRows.stream()
                .filter(row -> row.getTransactionId() != null)
                .collect(Collectors.groupingBy(antiToIncomeRow -> antiToIncomeRow.getTransactionId().longValue()));
    }
}
