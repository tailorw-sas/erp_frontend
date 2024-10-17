package com.kynsoft.finamer.creditcard.application.command.transaction.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageAgencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateTransactionCommandHandler implements ICommandHandler<UpdateTransactionCommand> {

    private final ITransactionService transactionService;

    private final IManageAgencyService agencyService;

    private final IManageLanguageService languageService;

    public UpdateTransactionCommandHandler(ITransactionService transactionService, IManageAgencyService agencyService, IManageLanguageService languageService) {
        this.transactionService = transactionService;
        this.agencyService = agencyService;
        this.languageService = languageService;
    }

    @Override
    public void handle(UpdateTransactionCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Transaction ID cannot be null."));
        TransactionDto dto = this.transactionService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        updateEntity(dto::setAgency, command.getAgency(), dto.getAgency() != null ? dto.getAgency().getId() : null, this.agencyService::findById, update::setUpdate);
        updateEntity(dto::setLanguage, command.getLanguage(), dto.getLanguage() != null ? dto.getLanguage().getId() : null, this.languageService::findById, update::setUpdate);
        updateLocalDate(dto::setCheckIn, command.getCheckIn(), dto.getCheckIn(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setReservationNumber, command.getReservationNumber(), dto.getReservationNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setReferenceNumber, command.getReferenceNumber(), dto.getReferenceNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHotelContactEmail, command.getHotelContactEmail(), dto.getHotelContactEmail(), update::setUpdate);

        if (update.getUpdate() > 0){
            this.transactionService.update(dto);
        }
    }

    public static void updateLocalDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update){
        if(newValue != null && !newValue.equals(oldValue)){
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private <T> void updateEntity(Consumer<T> setter, UUID newValue, UUID oldValue, EntityFinder<T> finder, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            T entity = finder.findById(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

    @FunctionalInterface
    private interface EntityFinder<T> {
        T findById(UUID id);
    }
}
