package com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateAdjustmentTransactionCommand implements ICommand {

    private Long id;
    private UUID transactionUuid;
    private UUID agency;
    private UUID transactionCategory;
    private UUID transactionSubCategory;
    private Double amount;
    private String reservationNumber;
    private String referenceNumber;
    private LocalDate transactionDate;

    public CreateAdjustmentTransactionCommand(UUID agency, UUID transactionCategory,
                                              UUID transactionSubCategory, Double amount,
                                              String reservationNumber, String referenceNumber) {
        this.transactionUuid = UUID.randomUUID();
        this.agency = agency;
        this.transactionCategory = transactionCategory;
        this.transactionSubCategory = transactionSubCategory;
        this.amount = amount;
        this.reservationNumber = reservationNumber;
        this.referenceNumber = referenceNumber;
        this.transactionDate = LocalDate.now();
    }

    public static CreateAdjustmentTransactionCommand fromRequest(CreateAdjustmentTransactionRequest request){
        return new CreateAdjustmentTransactionCommand(
                request.getAgency(), request.getTransactionCategory(),
                request.getTransactionSubCategory(), request.getAmount(),
                request.getReservationNumber(), request.getReferenceNumber()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAdjustmentTransactionMessage(id);
    }
}
