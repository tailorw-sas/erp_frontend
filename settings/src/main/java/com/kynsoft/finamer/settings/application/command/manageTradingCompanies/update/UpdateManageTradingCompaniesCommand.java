package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageTradingCompaniesCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String company;
    private String cif;
    private String address;
    private UUID country;
    private UUID cityState;
    private String city;
    private Long zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;

    public static UpdateManageTradingCompaniesCommand fromRequest(UpdateManageTradingCompaniesRequest request, UUID id){
        return new UpdateManageTradingCompaniesCommand(
                id, request.getDescription(), request.getStatus(), request.getCompany(),
                request.getCif(), request.getAddress(), request.getCountry(), request.getCityState(),
                request.getCity(), request.getZipCode(), request.getInnsistCode(), request.getIsApplyInvoice()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageTradingCompaniesMessage(id);
    }
}
