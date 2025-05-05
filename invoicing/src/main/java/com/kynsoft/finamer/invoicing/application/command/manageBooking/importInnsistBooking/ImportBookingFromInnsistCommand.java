package com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportBookingFromInnsistCommand implements ICommand {
    private UUID importInnsitProcessId;
    private String employee;
    private List<ImportInnsistBookingRequest> importList;

    public ImportBookingFromInnsistCommand(UUID importInnsitProcessId, String employee, List<ImportInnsistBookingRequest> importList){
        this.importInnsitProcessId = importInnsitProcessId;
        this.employee = employee;
        this.importList = importList;
    }

    public static ImportBookingFromInnsistCommand fromRequest(ImportInnsistRequest request){
        return new ImportBookingFromInnsistCommand(request.getImportInnsitProcessId(),
                request.getEmployee(),
                request.getImportList());
    }

    @Override
    public ICommandMessage getMessage() {
        return new ImportBookingFromInnsistMessage(importInnsitProcessId);
    }
}
