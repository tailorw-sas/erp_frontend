package com.kynsoft.finamer.payment.domain.excel.bean;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Data;

@Data
public class PaymentRow extends Row {
    @Cell(position = 0)
    private String agencyCode;
    @Cell(position = 1)
    private String hotelCode;
    @Cell(position = 2,cellType = CustomCellType.NUMERIC)
    private Double paymentExp;
    @Cell(position = 3)
    private String remarks;
    @Cell(position = 4,cellType = CustomCellType.DATAFORMAT)
    private String transactionDate;

    public PaymentDto toAggregate(){
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentAmount(this.paymentExp);
        paymentDto.setRemark(this.remarks);
        paymentDto.setTransactionDate(DateUtil.parseDateToLocalDate(this.transactionDate,"dd/MM/yyyy"));
        return paymentDto;
    }
}
