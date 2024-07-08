package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import java.time.LocalDate;
import java.util.UUID;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdatePaymentCloseOperationCommandHandler implements ICommandHandler<UpdatePaymentCloseOperationCommand> {

    private final IPaymentCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public UpdatePaymentCloseOperationCommandHandler(IPaymentCloseOperationService closeOperationService, IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdatePaymentCloseOperationCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Payment Close Operation ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "id", "Manage Hotel ID cannot be null."));

        PaymentCloseOperationDto closeOperationDto = this.closeOperationService.findById(command.getId());

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
