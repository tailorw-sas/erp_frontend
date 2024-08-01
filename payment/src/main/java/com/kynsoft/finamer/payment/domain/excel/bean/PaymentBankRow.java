package com.kynsoft.finamer.payment.domain.excel.bean;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Data;

@Data
public class PaymentBankRow extends Row{
    @Cell(position = 1)
    private String hotelCode;
    @Cell(position = 0)
    private String agencyCode;
    @Cell(position = 2,cellType = CustomCellType.DATAFORMAT)
    private String bankAccount;
    @Cell(position = 3,cellType = CustomCellType.NUMERIC)
    private Double paymentAmount;
    @Cell(position = 4)
    private String remarks;
    @Cell(position = 5,cellType = CustomCellType.DATAFORMAT)
    private String transactionDate;


    public PaymentDto toAggregate(){
       PaymentDto paymentDto = new PaymentDto();
       paymentDto.setPaymentAmount(this.paymentAmount);
       paymentDto.setRemark(this.remarks);
       paymentDto.setTransactionDate(DateUtil.parseDateToLocalDate(this.transactionDate,"dd/MM/yyyy"));
       return paymentDto;
    }
}
