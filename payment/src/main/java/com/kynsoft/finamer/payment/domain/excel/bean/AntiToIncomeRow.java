package com.kynsoft.finamer.payment.domain.excel.bean;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AntiToIncomeRow extends Row {
    @Cell(position = 0,cellType = CustomCellType.DATAFORMAT)
    private String transactionId;
    @Cell(position = 1,cellType = CustomCellType.NUMERIC)
    private Double amount;
    @Cell(position = 2)
    private String remarks;


    public PaymentDetailDto toAggregate(){
        PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        paymentDetailDto.setAmount(this.amount);
        paymentDetailDto.setRemark(this.remarks);
        paymentDetailDto.setTransactionDate(LocalDate.now());
        return paymentDetailDto;
    }
}
