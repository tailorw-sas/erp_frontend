package com.kynsoft.finamer.creditcard.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageBankAccountCommandHandler implements ICommandHandler<UpdateManageBankAccountCommand> {

    private final IManageBankAccountService service;

    private final IManagerBankService bankService;

    private final IManageHotelService hotelService;

    private final IManagerAccountTypeService accountTypeService;

    public UpdateManageBankAccountCommandHandler(IManageBankAccountService service, 
                                                 IManagerBankService bankService, 
                                                 IManageHotelService hotelService, 
                                                 IManagerAccountTypeService accountTypeService) {
        this.service = service;
        this.bankService = bankService;
        this.hotelService = hotelService;
        this.accountTypeService = accountTypeService;
    }

    @Override
    public void handle(UpdateManageBankAccountCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Bank Account ID cannot be null."));

        ManageBankAccountDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        updateBank(dto::setManageBank, command.getManageBank(), dto.getManageBank().getId(), update::setUpdate);
        updateHotel(dto::setManageHotel, command.getManageHotel(), dto.getManageHotel().getId(), update::setUpdate);
        updateAccountType(dto::setManageAccountType, command.getManageAccountType(), dto.getManageAccountType().getId(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateBank(Consumer<ManagerBankDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerBankDto dto = bankService.findById(newValue);
            setter.accept(dto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateAccountType(Consumer<ManagerAccountTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerAccountTypeDto dto = accountTypeService.findById(newValue);
            setter.accept(dto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto hotelDto = hotelService.findById(newValue);
            setter.accept(hotelDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
