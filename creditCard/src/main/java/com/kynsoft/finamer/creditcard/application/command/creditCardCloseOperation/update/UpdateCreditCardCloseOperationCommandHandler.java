package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import java.time.LocalDate;
import java.util.UUID;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateCreditCardCloseOperationCommandHandler implements ICommandHandler<UpdateCreditCardCloseOperationCommand> {

    private final ICreditCardCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public UpdateCreditCardCloseOperationCommandHandler(ICreditCardCloseOperationService closeOperationService, IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateCreditCardCloseOperationCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Payment Close Operation ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "id", "Manage Hotel ID cannot be null."));

        CreditCardCloseOperationDto closeOperationDto = this.closeOperationService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateStatus(closeOperationDto::setStatus, command.getStatus(), closeOperationDto.getStatus(), update::setUpdate);
        this.updateLocalDate(closeOperationDto::setBeginDate, command.getBeginDate(), closeOperationDto.getBeginDate(), update::setUpdate);
        this.updateLocalDate(closeOperationDto::setEndDate, command.getEndDate(), closeOperationDto.getEndDate(), update::setUpdate);
        this.updateManageClient(closeOperationDto::setHotel, command.getHotel(), closeOperationDto.getHotel().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.closeOperationService.update(closeOperationDto);
        }

    }

    private boolean updateManageClient(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto clientDto = this.hotelService.findById(newValue);
            setter.accept(clientDto);
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

    private void updateLocalDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
