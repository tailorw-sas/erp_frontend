package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPaymentDto {

    private UUID id;
    private Long hotelPaymentId;
    private LocalDateTime transactionDate;
    private ManageHotelDto manageHotel;
    private ManageBankAccountDto manageBankAccount;
    private double amount;
    private double commission;
    private double netAmount;
    private ManagePaymentTransactionStatusDto status;
    private String remark;
    private Set<TransactionDto> transactions;
    private List<AttachmentDto> attachments;
}
