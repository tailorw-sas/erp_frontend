package com.kynsoft.finamer.payment.domain.excel.bean.detail;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.AllArgsConstructor;
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
public class PaymentDetailRow extends Row implements Serializable {
    @Cell(position = -1)
    protected int rowNumber;
    @Cell(position = 0,cellType = CustomCellType.DATAFORMAT)
    private String paymentId;
    @Cell(position = 1)
    private String coupon;
    @Cell(position = 2,cellType = CustomCellType.DATAFORMAT)
    private String invoiceNo;
    @Cell(position = 3,cellType = CustomCellType.NUMERIC)
    private Double balance;
    @Cell(position = 4)
    private String transactionType;
    @Cell(position = 5)
    private String anti;
    @Cell(position = 5)
    private String remarks;

    public PaymentDetailDto toAggregate(){
        PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
        paymentDetailDto.setAmount(this.balance);
        paymentDetailDto.setRemark(this.remarks);
        paymentDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setCouponNo(coupon);
        return paymentDetailDto;
    }
}
