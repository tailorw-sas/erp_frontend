package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.closeOperation.CheckBeginDateAndEndDateRule;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class UpdateAllPaymentCloseOperationCommandHandler implements ICommandHandler<UpdateAllPaymentCloseOperationCommand> {

    private final IPaymentCloseOperationService closeOperationService;

    public UpdateAllPaymentCloseOperationCommandHandler(IPaymentCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(UpdateAllPaymentCloseOperationCommand command) {

        RulesChecker.checkRule(new CheckBeginDateAndEndDateRule(command.getBeginDate(), command.getEndDate()));
        List<PaymentCloseOperationDto> closeOperationDtos = this.closeOperationService.findByHotelIds(command.getHotels());
        List<PaymentCloseOperationDto> update = new ArrayList<>();

        for (PaymentCloseOperationDto closeOperationDto : closeOperationDtos) {
            updatePaymentCloseOperation(closeOperationDto, command);
            update.add(closeOperationDto);
        }

        this.closeOperationService.updateAll(update);

    }

    private void updatePaymentCloseOperation(PaymentCloseOperationDto updateC, UpdateAllPaymentCloseOperationCommand command) {

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateStatus(updateC::setStatus, command.getStatus(), updateC.getStatus(), update::setUpdate);
        this.updateLocalDate(updateC::setBeginDate, command.getBeginDate(), updateC.getBeginDate(), update::setUpdate);
        this.updateLocalDate(updateC::setEndDate, command.getEndDate(), updateC.getEndDate(), update::setUpdate);

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
