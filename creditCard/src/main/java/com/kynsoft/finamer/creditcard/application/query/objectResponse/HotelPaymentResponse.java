package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HotelPaymentResponse implements IResponse {

    private UUID id;
    private Long hotelPaymentId;
    private LocalDateTime transactionDate;
    private ManageHotelResponse manageHotel;
    private ManageBankAccountResponse manageBankAccount;
    private double amount;
    private double commission;
    private double netAmount;
    private ManagePaymentTransactionStatusResponse status;
    private String remark;

    public HotelPaymentResponse(HotelPaymentDto dto) {
        this.id = dto.getId();
        this.hotelPaymentId = dto.getHotelPaymentId();
        this.transactionDate = dto.getTransactionDate();
        this.manageHotel = dto.getManageHotel() != null ? new ManageHotelResponse(dto.getManageHotel()) : null;
        this.manageBankAccount = dto.getManageBankAccount() != null ? new ManageBankAccountResponse(dto.getManageBankAccount()) : null;
        this.amount = dto.getAmount();
        this.commission = dto.getCommission();
        this.netAmount = dto.getNetAmount();
        this.status = dto.getStatus() != null ? new ManagePaymentTransactionStatusResponse(dto.getStatus()) : null;
        this.remark = dto.getRemark();
    }
}
