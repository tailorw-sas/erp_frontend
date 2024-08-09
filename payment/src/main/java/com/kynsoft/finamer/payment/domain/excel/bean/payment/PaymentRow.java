package com.kynsoft.finamer.payment.domain.excel.bean.payment;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRow extends Row implements Serializable {
    @Cell(position = -1)
    protected int rowNumber;
    @Cell(position = 0)
    private String manageAgencyCode;
    @Cell(position = 1)
    private String manageHotelCode;
    @Cell(position = 2,cellType = CustomCellType.NUMERIC)
    private Double amount;
    @Cell(position = 3)
    private String remarks;
    @Cell(position = 4,cellType = CustomCellType.DATAFORMAT)
    private String transactionDate;

    private String manageClientCode;

    public PaymentDto toAggregate(){
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentAmount(this.amount);
        paymentDto.setRemark(this.remarks);
        paymentDto.setTransactionDate(DateUtil.parseDateToLocalDate(this.transactionDate,"dd/MM/yyyy"));
        return paymentDto;
    }
}
