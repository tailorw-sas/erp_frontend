package com.kynsoft.finamer.settings.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageHotelCommand implements ICommand {

    private UUID id;
    private String code;
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

    public CreateManageHotelCommand(String code, String description, Status status, String name,
                                    String babelCode, UUID manageCountry, UUID manageCityState,
                                    String city, String address, UUID manageCurrency, UUID manageRegion,
                                    UUID manageTradingCompanies, Boolean applyByTradingCompany,
                                    String prefixToInvoice, Boolean isVirtual, Boolean requiresFlatRate,
                                    Boolean isApplyByVCC, Boolean autoApplyCredit) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.babelCode = babelCode;
        this.manageCountry = manageCountry;
        this.manageCityState = manageCityState;
        this.city = city;
        this.address = address;
        this.manageCurrency = manageCurrency;
        this.manageRegion = manageRegion;
        this.manageTradingCompanies = manageTradingCompanies;
        this.applyByTradingCompany = applyByTradingCompany;
        this.prefixToInvoice = prefixToInvoice;
        this.isVirtual = isVirtual;
        this.requiresFlatRate = requiresFlatRate;
        this.isApplyByVCC = isApplyByVCC;
        this.autoApplyCredit = autoApplyCredit;
    }

    public static CreateManageHotelCommand fromRequest(CreateManageHotelRequest request){
        return new CreateManageHotelCommand(
                request.getCode(), request.getDescription(), request.getStatus(),
                request.getName(), request.getBabelCode(), request.getManageCountry(),
                request.getManageCityState(), request.getCity(), request.getAddress(),
                request.getManageCurrency(), request.getManageRegion(), request.getManageTradingCompanies(),
                request.getApplyByTradingCompany(), request.getPrefixToInvoice(),
                request.getIsVirtual(), request.getRequiresFlatRate(), request.getIsApplyByVCC(),
                request.getAutoApplyCredit()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageHotelMessage(id);
    }
}
