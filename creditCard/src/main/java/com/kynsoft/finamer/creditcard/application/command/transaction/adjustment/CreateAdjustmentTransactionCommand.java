package com.kynsoft.finamer.creditcard.application.command.transaction.adjustment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

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
    private String employee;

    public CreateAdjustmentTransactionCommand(UUID agency, UUID transactionCategory,
                                              UUID transactionSubCategory, Double amount,
                                              String reservationNumber, String referenceNumber,
                                              String employee) {
        this.transactionUuid = UUID.randomUUID();
        this.agency = agency;
        this.transactionCategory = transactionCategory;
        this.transactionSubCategory = transactionSubCategory;
        this.amount = amount;
        this.reservationNumber = reservationNumber;
        this.referenceNumber = referenceNumber;
        this.employee = employee;
    }

    public static CreateAdjustmentTransactionCommand fromRequest(CreateAdjustmentTransactionRequest request){
        return new CreateAdjustmentTransactionCommand(
                request.getAgency(), request.getTransactionCategory(),
                request.getTransactionSubCategory(), request.getAmount(),
                request.getReservationNumber(), request.getReferenceNumber(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAdjustmentTransactionMessage(id);
    }
}
