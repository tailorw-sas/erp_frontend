package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.http.entity.income.*;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CreateAntiToIncomeCommand implements ICommand {

    private List<CreateIncomeCommand> createIncomeCommands;

    public CreateAntiToIncomeCommand(List<CreateIncomeCommand> createIncomeCommands) {
        this.createIncomeCommands = createIncomeCommands;
    }

    public static CreateAntiToIncomeCommand fromRequest(CreateAntiToIncomeRequest request) {
        List<CreateIncomeCommand> commands = request.getCreateIncomeRequests().stream()
                .map(createIncomeRequest -> {
                    return new CreateIncomeCommand(
                            Status.valueOf(createIncomeRequest.getStatus()),
                            createIncomeRequest.getInvoiceDate(),
                            createIncomeRequest.getManual(),
                            createIncomeRequest.getAgency(),
                            createIncomeRequest.getHotel(),
                            createIncomeRequest.getInvoiceType(),
                            createIncomeRequest.getIncomeAmount(),
                            createIncomeRequest.getInvoiceNumber(),
                            createIncomeRequest.getDueDate(),
                            createIncomeRequest.getReSend(),
                            createIncomeRequest.getReSendDate(),
                            createIncomeRequest.getInvoiceStatus(),
                            createIncomeRequest.getEmployee(),
                            createIncomeRequest.getAttachments(),
                            createIncomeRequest.getAdjustments()
                    );
                })
                .collect(Collectors.toList());
        return new CreateAntiToIncomeCommand(commands);
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAntiToIncomeMessage(createIncomeCommands.stream()
                .map(command -> {
                    return new CreateIncomeMessage(command.getId(),
                        command.getInvoiceId(),
                        command.getInvoiceNo());
                })
                .toList());
    }
}
