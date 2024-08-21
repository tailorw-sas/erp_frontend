package com.kynsoft.finamer.payment.domain.excel.bean.detail;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AntiToIncomeRow extends Row implements Serializable {
    @Cell(position = -1,headerName = "")
    protected int rowNumber;
    @Cell(position = 0,cellType = CustomCellType.NUMERIC,headerName = "Id ANTI")
    private Double transactionId;
    @Cell(position = 1,cellType = CustomCellType.NUMERIC,headerName = "AMOUNT")
    private Double amount;
    @Cell(position = 2,headerName = "Remark")
    private String remarks;

    private String paymentId;
    private String transactionCategoryName;
    private String transactionCheckDepositAmount;
    private String transactionCheckDepositBalance;


    public PaymentDetailDto toAggregate(){
        PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        paymentDetailDto.setAmount(this.amount);
        paymentDetailDto.setRemark(this.remarks);
        paymentDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        return paymentDetailDto;
    }
}
