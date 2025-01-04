package com.kynsoft.finamer.settings.application.command.manageHotel.update;

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
public class UpdateManageHotelCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private String babelCode;
    private UUID manageCountry;
    private UUID manageCityState;
    private String city;
    private String address;
    private UUID manageCurrency;
    private UUID manageRegion;
    private UUID manageTradingCompanies;
    private Boolean applyByTradingCompany;
    private String prefixToInvoice;
    private Boolean isVirtual;
    private Boolean requiresFlatRate;
    private Boolean isApplyByVCC;
    private Boolean autoApplyCredit;

    public static UpdateManageHotelCommand fromRequest(UpdateManageHotelRequest request, UUID id) {
        return new UpdateManageHotelCommand(
                id, request.getDescription(), request.getStatus(), request.getName(),
                request.getBabelCode(), request.getManageCountry(), request.getManageCityState(),
                request.getCity(), request.getAddress(), request.getManageCurrency(), request.getManageRegion(),
                request.getManageTradingCompanies(), request.getApplyByTradingCompany(),
                request.getPrefixToInvoice(), request.getIsVirtual(), request.getRequiresFlatRate(),
                request.getIsApplyByVCC(), request.getAutoApplyCredit()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageHotelMessage(id);
    }
}
