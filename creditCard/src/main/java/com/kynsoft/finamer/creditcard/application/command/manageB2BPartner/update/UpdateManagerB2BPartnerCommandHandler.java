package com.kynsoft.finamer.creditcard.application.command.manageB2BPartner.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerB2BPartnerDto;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManagerB2BPartnerCommandHandler implements ICommandHandler<UpdateManagerB2BPartnerCommand> {

    private final IManagerB2BPartnerService service;
    private final IManageB2BPartnerTypeService b2BPartnerTypeService;

    public UpdateManagerB2BPartnerCommandHandler(IManagerB2BPartnerService service, IManageB2BPartnerTypeService b2BPartnerTypeService) {
        this.service = service;
        this.b2BPartnerTypeService = b2BPartnerTypeService;
    }

    @Override
    public void handle(UpdateManagerB2BPartnerCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager B2BPartner ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getB2BPartnerType(), "b2BPartnerType", "Manager B2BPartner ID cannot be null."));

        ManagerB2BPartnerDto managerB2BPartnerDto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setName, command.getName(), managerB2BPartnerDto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setDescription, command.getDescription(), managerB2BPartnerDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setUrl, command.getUrl(), managerB2BPartnerDto.getUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setIp, command.getIp(), managerB2BPartnerDto.getIp(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setUserName, command.getUserName(), managerB2BPartnerDto.getUserName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setPassword, command.getPassword(), managerB2BPartnerDto.getPassword(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setToken, command.getToken(), managerB2BPartnerDto.getToken(), update::setUpdate);

        this.updateStatus(managerB2BPartnerDto::setStatus, command.getStatus(), managerB2BPartnerDto.getStatus(), update::setUpdate);
        this.updateManageB2BPartnerType(managerB2BPartnerDto::setB2BPartnerTypeDto, command.getB2BPartnerType(), managerB2BPartnerDto.getB2BPartnerTypeDto().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(managerB2BPartnerDto);
        }

    }

    private boolean updateManageB2BPartnerType(Consumer<ManageB2BPartnerTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageB2BPartnerTypeDto merchantDto = this.b2BPartnerTypeService.findById(newValue);
            setter.accept(merchantDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
