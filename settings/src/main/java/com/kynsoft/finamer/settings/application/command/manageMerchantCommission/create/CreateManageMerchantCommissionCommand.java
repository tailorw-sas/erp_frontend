package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantCommissionCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID manageCreditCartType;
    private Double commission;
    private String calculationType;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;

    public CreateManageMerchantCommissionCommand(UUID managerMerchant, UUID manageCreditCartType, Double commission, String calculationType, String description, LocalDate fromDate, LocalDate toDate) {
        this.id = UUID.randomUUID();
        this.managerMerchant = managerMerchant;
        this.manageCreditCartType = manageCreditCartType;
        this.commission = commission;
        this.calculationType = calculationType;
        this.description = description;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static CreateManageMerchantCommissionCommand fromRequest(CreateManageMerchantCommissionRequest request) {
        return new CreateManageMerchantCommissionCommand(
                request.getManagerMerchant(),
                request.getManageCreditCartType(),
                request.getCommission(),
                request.getCalculationType(),
                request.getDescription(),
                request.getFromDate(),
                request.getToDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantCommissionMessage(id);
    }
}
