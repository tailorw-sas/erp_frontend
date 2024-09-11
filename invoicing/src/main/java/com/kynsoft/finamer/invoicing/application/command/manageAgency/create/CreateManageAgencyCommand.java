package com.kynsoft.finamer.invoicing.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private UUID client;
    private EGenerationType generationType;
    private String status;
    private String cif;
    private String address;
    private UUID sentB2BPartner;
    private UUID cityState;
    private UUID country;
    private String mailingAddress;

    public CreateManageAgencyCommand(UUID id, String code, String name, UUID client, EGenerationType generationType,
                                     String status, String cif, String address, UUID sentB2BPartner, UUID cityState, UUID country,
                                     String mailingAddress) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.client = client;
        this.generationType = generationType;
        this.status = status;
        this.cif = cif;
        this.address = address;
        this.sentB2BPartner = sentB2BPartner;
        this.cityState = cityState;
        this.country = country;
        this.mailingAddress = mailingAddress;
    }



    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
