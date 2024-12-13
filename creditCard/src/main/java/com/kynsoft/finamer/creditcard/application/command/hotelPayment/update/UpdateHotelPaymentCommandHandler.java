package com.kynsoft.finamer.creditcard.application.command.hotelPayment.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UpdateHotelPaymentCommandHandler implements ICommandHandler<UpdateHotelPaymentCommand> {

    private final IHotelPaymentService hotelPaymentService;

    private final IManageBankAccountService bankAccountService;

    private final IManagePaymentTransactionStatusService transactionStatusService;

    private final ITransactionService transactionService;

    private final IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService;

    private final ICreditCardCloseOperationService creditCardCloseOperationService;

    public UpdateHotelPaymentCommandHandler(IHotelPaymentService hotelPaymentService, IManageBankAccountService bankAccountService, IManagePaymentTransactionStatusService transactionStatusService, ITransactionService transactionService, IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService, ICreditCardCloseOperationService creditCardCloseOperationService) {
        this.hotelPaymentService = hotelPaymentService;
        this.bankAccountService = bankAccountService;
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.hotelPaymentStatusHistoryService = hotelPaymentStatusHistoryService;
        this.creditCardCloseOperationService = creditCardCloseOperationService;
    }

    @Override
    public void handle(UpdateHotelPaymentCommand command) {
        HotelPaymentDto hotelPaymentDto = this.hotelPaymentService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(hotelPaymentDto::setRemark, command.getRemark(), hotelPaymentDto.getRemark(), update::setUpdate);

        if (command.getManageBankAccount() != null && !command.getManageBankAccount().equals(hotelPaymentDto.getManageBankAccount().getId())) {
            ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(command.getManageBankAccount());
            hotelPaymentDto.setManageBankAccount(bankAccountDto);
            update.setUpdate(1);
        }

        if (command.getStatus() != null && !command.getStatus().equals(hotelPaymentDto.getStatus().getId()) && hotelPaymentDto.getStatus().isInProgress()) {
            ManagePaymentTransactionStatusDto transactionStatusDto = this.transactionStatusService.findById(command.getStatus());
            updateStatus(hotelPaymentDto, transactionStatusDto, command.getEmployee(), update);
        }

        if (update.getUpdate() > 0){
            this.hotelPaymentService.update(hotelPaymentDto);
        }
        command.setHotelPaymentId(hotelPaymentDto.getHotelPaymentId());
    }

    private void updateStatus(HotelPaymentDto hotelPaymentDto, ManagePaymentTransactionStatusDto transactionStatusDto, String employee, ConsumerUpdate update) {
        if (transactionStatusDto.isCompleted()) {
            Set<TransactionDto> updatedTransactions = this.transactionService.changeAllTransactionStatus(hotelPaymentDto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet()), ETransactionStatus.PAID, employee);
            hotelPaymentDto.setStatus(transactionStatusDto);
            hotelPaymentDto.setTransactions(updatedTransactions);
            hotelPaymentDto.setTransactionDate(this.creditCardCloseOperationService.hotelCloseOperationDateTime(hotelPaymentDto.getManageHotel().getId()));
            update.setUpdate(1);
            this.hotelPaymentStatusHistoryService.create(hotelPaymentDto, employee);

        } else if (transactionStatusDto.isCancelled()) {
            if (hotelPaymentDto.getTransactions() == null || hotelPaymentDto.getTransactions().isEmpty()){
                hotelPaymentDto.setStatus(transactionStatusDto);
                update.setUpdate(1);
                this.hotelPaymentStatusHistoryService.create(hotelPaymentDto, employee);
            } else {
                throw new BusinessException(
                        DomainErrorMessage.HOTEL_PAYMENT_CANCELLED_STATUS,
                        DomainErrorMessage.HOTEL_PAYMENT_CANCELLED_STATUS.getReasonPhrase()
                );
            }
        }
    }
}
