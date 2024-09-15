package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyCommand implements ICommand {

    private UUID id;
    private String name;
    private UUID client;
    @JsonProperty("cif")
    private String cif;
    @JsonProperty("address")
    private String address;
    @JsonProperty("sentB2BPartner")
    private UUID sentB2BPartner;
    @JsonProperty("cityState")
    private UUID cityState;
    @JsonProperty("country")
    private UUID country;
    private String mailingAddress;
    private String zipCode;
    private String city;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyMessage(id);
    }
}
