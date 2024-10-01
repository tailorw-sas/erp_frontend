package com.kynsoft.finamer.payment.domain.excel.bean.payment;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentExpenseBookingRow extends Row implements Serializable {
    @Cell(position = -1)
    private int rowNumber;
    @Cell(position = 0,cellType = CustomCellType.DATAFORMAT)
    private String bookingId;
    @Cell(position = 1)
    private String transactionType;
    @Cell(position = 2,cellType = CustomCellType.NUMERIC)
    private Double balance;
    @Cell(position = 3)
    private String remarks;

    private String hotelId;
    private String clientName;

}
