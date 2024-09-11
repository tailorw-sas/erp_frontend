package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTradingCompaniesCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String company;
    private String cif;
    private String address;
    private UUID country;
    private UUID cityState;
    private String city;
    private String zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;

    public CreateManageTradingCompaniesCommand(String code, String description, Status status, String company, String cif, String address, UUID country, UUID cityState, String city, String zipCode, String innsistCode, Boolean isApplyInvoice) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.company = company;
        this.cif = cif;
        this.address = address;
        this.country = country;
        this.cityState = cityState;
        this.city = city;
        this.zipCode = zipCode;
        this.innsistCode = innsistCode;
        this.isApplyInvoice = isApplyInvoice;
    }

    public static CreateManageTradingCompaniesCommand fromRequest(CreateManageTradingCompaniesRequest request){
        return new CreateManageTradingCompaniesCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getCompany(),
                request.getCif(),
                request.getAddress(),
                request.getCountry(),
                request.getCityState(),
                request.getCity(),
                request.getZipCode(),
                request.getInnsistCode(),
                request.getIsApplyInvoice()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTradingCompaniesMessage(id);
    }
}
