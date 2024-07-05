package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.time.LocalDate;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantCommissionCommand implements ICommand {

    private final UUID id;
    private final UUID managerMerchant;
    private final UUID manageCreditCartType;
    private final Double commission;
    private final String calculationType;
    private final String description;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final Status status;

    public CreateManageMerchantCommissionCommand(UUID managerMerchant, UUID manageCreditCartType, Double commission,
                                                 String calculationType, String description, LocalDate fromDate,
                                                 LocalDate toDate, Status status) {
        this.status = status;
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
                request.getToDate(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantCommissionMessage(id);
    }
}
