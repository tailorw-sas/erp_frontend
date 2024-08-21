package com.kynsoft.finamer.payment.domain.excel.bean.payment;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.*;

import java.io.Serializable;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBankRow extends Row implements Serializable {
    @Cell(position = -1,headerName = "")
    protected int rowNumber;
    @Cell(position = 0,headerName = "Agency")
    private String manageAgencyCode;
    @Cell(position = 1,headerName = "Hotel")
    private String manageHotelCode;
    @Cell(position = 2,cellType = CustomCellType.ALFANUMERIC,headerName = "Bank Account")
    private String bankAccount;
    @Cell(position = 3,cellType = CustomCellType.NUMERIC,headerName = "Payment Amount")
    private Double amount;
    @Cell(position = 4,headerName = "Remarks")
    private String remarks;
    @Cell(position = 5,cellType = CustomCellType.DATAFORMAT,headerName = "Transaction Date")
    private String transactionDate;

    private String manageClientCode;


    public PaymentDto toAggregate(){
       PaymentDto paymentDto = new PaymentDto();
       paymentDto.setPaymentAmount(this.amount);
       paymentDto.setRemark(this.remarks);
       try {
       paymentDto.setTransactionDate(DateUtil.parseDateToLocalDate(this.transactionDate,"dd/MM/yyyy"));

       }catch (Exception ex ){
           paymentDto.setTransactionDate(DateUtil.parseDateToLocalDate("20/04/2029"));
       }
       return paymentDto;
    }
}
