package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.time.LocalDate;
import java.util.UUID;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;

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
        UpdateIfNotNull.updateDouble(paymentDto::setPaymentAmount, command.getPaymentAmount(), paymentDto.getPaymentAmount(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, command.getPaymentBalance(), paymentDto.getPaymentBalance(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setDepositAmount, command.getDepositAmount(), paymentDto.getDepositAmount(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setDepositBalance, command.getDepositBalance(), paymentDto.getDepositBalance(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setOtherDeductions, command.getOtherDeductions(), paymentDto.getOtherDeductions(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setIdentified, command.getIdentified(), paymentDto.getIdentified(), update::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, command.getNotIdentified(), paymentDto.getNotIdentified(), update::setUpdate);

        this.updateStatus(paymentDto::setStatus, command.getStatus(), paymentDto.getStatus(), update::setUpdate);
        this.updateDate(paymentDto::setTransactionDate, command.getTransactionDate(), paymentDto.getTransactionDate(), update::setUpdate);
        this.updateManagePaymentSource(paymentDto::setPaymentSource, command.getPaymentSource(), paymentDto.getPaymentSource().getId(), update::setUpdate);
        this.updateManagePaymentStatus(paymentDto::setPaymentStatus, command.getPaymentStatus(), paymentDto.getPaymentStatus().getId(), update::setUpdate);
        this.updateManageClient(paymentDto::setClient, command.getClient(), paymentDto.getClient().getId(), update::setUpdate);
        this.updateManageAgency(paymentDto::setAgency, command.getAgency(), paymentDto.getAgency().getId(), update::setUpdate);
        this.updateManageHotel(paymentDto::setHotel, command.getHotel(), paymentDto.getHotel().getId(), update::setUpdate);
        this.updateManageBankAccount(paymentDto::setBankAccount, command.getBankAccount(), paymentDto.getBankAccount().getId(), update::setUpdate);
        this.updateManageAttachmentStatus(paymentDto::setAttachmentStatus, command.getAttachmentStatus(), paymentDto.getAttachmentStatus().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.paymentService.update(paymentDto);
            this.createPaymentAttachmentStatusHistory(command.getEmployee(), paymentDto);
        }

    }

    private void createPaymentAttachmentStatusHistory(UUID employee, PaymentDto payment) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(employee, "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(employee);

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Updating the Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
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

    private boolean updateManagePaymentStatus(Consumer<ManagePaymentStatusDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(newValue);
            setter.accept(paymentStatusDto);
            update.accept(1);

            return true;
        }
        return false;
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

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
