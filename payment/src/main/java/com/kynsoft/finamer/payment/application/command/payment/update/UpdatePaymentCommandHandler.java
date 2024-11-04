package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.payment.UpdatePaymentValidateChangeStatusRule;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdatePaymentCommandHandler implements ICommandHandler<UpdatePaymentCommand> {

    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentService paymentService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    public UpdatePaymentCommandHandler(IManagePaymentSourceService sourceService, 
                                       IManagePaymentStatusService statusService, 
                                       IManageClientService clientService, 
                                       IManageAgencyService agencyService, 
                                       IManageHotelService hotelService, 
                                       IManageBankAccountService bankAccountService, 
                                       IManagePaymentAttachmentStatusService attachmentStatusService, 
                                       IPaymentService paymentService,
                                       IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                       IManageEmployeeService manageEmployeeService) {
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.bankAccountService = bankAccountService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentService = paymentService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
    }

    @Override
    public void handle(UpdatePaymentCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Payment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentSource(), "Payment Source", "Payment Source ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentStatus(), "Payment Status", "Payment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getClient(), "Client", "Client ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAgency(), "Agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "Hotel", "Hotel ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBankAccount(), "Bank Account", "Bank Account ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAttachmentStatus(), "Attachment Status", "Attachment Status ID cannot be null."));

        PaymentDto paymentDto = this.paymentService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(paymentDto::setReference, command.getReference(), paymentDto.getReference(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(paymentDto::setRemark, command.getRemark(), paymentDto.getRemark(), update::setUpdate);

        this.updateDate(paymentDto::setTransactionDate, command.getTransactionDate(), paymentDto.getTransactionDate(), update::setUpdate);
        this.updateManagePaymentSource(paymentDto::setPaymentSource, command.getPaymentSource(), paymentDto.getPaymentSource().getId(), update::setUpdate);
        this.updateManagePaymentStatus(paymentDto::setPaymentStatus, command.getPaymentStatus(), paymentDto.getPaymentStatus().getId(), update::setUpdate, paymentDto, command.getEmployee());
        this.updateManageClient(paymentDto::setClient, command.getClient(), paymentDto.getClient().getId(), update::setUpdate);
        this.updateManageAgency(paymentDto::setAgency, command.getAgency(), paymentDto.getAgency().getId(), update::setUpdate);
        this.updateManageHotel(paymentDto::setHotel, command.getHotel(), paymentDto.getHotel().getId(), update::setUpdate);
//        this.updateManageBankAccount(paymentDto::setBankAccount, command.getBankAccount(), paymentDto.getBankAccount().getId(), update::setUpdate);
        this.updateManageAttachmentStatus(paymentDto::setAttachmentStatus, command.getAttachmentStatus(), paymentDto.getAttachmentStatus().getId(), update::setUpdate);

        //if (update.getUpdate() > 0) {
        paymentDto.setBankAccount(this.bankAccountService.findById(command.getBankAccount()));
        this.paymentService.update(paymentDto);
        //}

    }

    private boolean updateManagePaymentSource(Consumer<ManagePaymentSourceDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagePaymentSourceDto paymentSourceDto = this.sourceService.findById(newValue);
            setter.accept(paymentSourceDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManagePaymentStatus(Consumer<ManagePaymentStatusDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update, PaymentDto paymentDto, UUID employee) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(newValue);
            RulesChecker.checkRule(new UpdatePaymentValidateChangeStatusRule(paymentDto.getPaymentStatus(), paymentStatusDto, paymentDto.isApplyPayment()));
            ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;
            createPaymentAttachmentStatusHistory(employeeDto, paymentDto, paymentStatusDto);
            setter.accept(paymentStatusDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, ManagePaymentStatusDto paymentStatusDto) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(paymentStatusDto.getCode() + "-" + paymentStatusDto.getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private boolean updateManageClient(Consumer<ManageClientDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageClientDto clientDto = this.clientService.findById(newValue);
            setter.accept(clientDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageAgency(Consumer<ManageAgencyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageAgencyDto agencyDto = this.agencyService.findById(newValue);
            setter.accept(agencyDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto hotelDto = this.hotelService.findById(newValue);
            setter.accept(hotelDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageBankAccount(Consumer<ManageBankAccountDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(newValue);
            setter.accept(bankAccountDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageAttachmentStatus(Consumer<ManagePaymentAttachmentStatusDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findById(newValue);
            setter.accept(attachmentStatusDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private void updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
