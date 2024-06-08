package com.kynsoft.finamer.settings.application.command.manageB2BPartner.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

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

        ManagerB2BPartnerDto managerB2BPartnerDto = this.service.findById(command.getId());
        ManageB2BPartnerTypeDto manageB2BPartnerTypeDto = this.b2BPartnerTypeService.findById(managerB2BPartnerDto.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setDescription, command.getDescription(), managerB2BPartnerDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setUrl, command.getUrl(), managerB2BPartnerDto.getUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setIp, command.getIp(), managerB2BPartnerDto.getIp(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(managerB2BPartnerDto::setName, command.getName(), managerB2BPartnerDto.getName(), update::setUpdate);

        managerB2BPartnerDto.setUserName(command.getUserName());
        managerB2BPartnerDto.setPassword(command.getPassword());
        managerB2BPartnerDto.setToken(command.getToken());
        managerB2BPartnerDto.setManageB2BPartnerType(command.getManageB2BPartnerType());
        managerB2BPartnerDto.setB2BPartnerTypeDto(manageB2BPartnerTypeDto);
        this.updateStatus(managerB2BPartnerDto::setStatus, command.getStatus(), managerB2BPartnerDto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(managerB2BPartnerDto);
        }

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
