package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeResponse;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.payment.CheckIfTransactionDateIsWithInRangeCloseOperationRule;
import com.kynsoft.finamer.payment.domain.rules.payment.PaymentValidateBankAccountAndHotelRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.ManageEmployeeHttpService;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CreatePaymentCommandHandler implements ICommandHandler<CreatePaymentCommand> {

    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentService paymentService;
    private final IPaymentCloseOperationService closeOperationService;

    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    private final ManageEmployeeHttpService employeeHttpService;

    public CreatePaymentCommandHandler(IManagePaymentSourceService sourceService,
            IManagePaymentStatusService statusService,
            IManageClientService clientService,
            IManageAgencyService agencyService,
            IManageBankAccountService bankAccountService,
            IManagePaymentAttachmentStatusService attachmentStatusService,
            IPaymentService paymentService,
            IManageHotelService hotelService,
            IPaymentCloseOperationService closeOperationService,
            IManageAttachmentTypeService manageAttachmentTypeService,
            IManageResourceTypeService manageResourceTypeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IManageEmployeeService manageEmployeeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IMasterPaymentAttachmentService masterPaymentAttachmentService,
            ManageEmployeeHttpService employeeHttpService) {
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.bankAccountService = bankAccountService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentService = paymentService;
        this.hotelService = hotelService;
        this.closeOperationService = closeOperationService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.employeeHttpService = employeeHttpService;
    }

    @Override
    @Transactional
    public void handle(CreatePaymentCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentSource(), "paymentSource", "Payment Source ID cannot be null."));
        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findById(command.getPaymentSource());
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentStatus(), "paymentStatus", "Payment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getClient(), "client", "Client ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAgency(), "agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "hotel", "Hotel ID cannot be null."));
        if (!command.isIgnoreBankAccount() || !paymentSourceDto.getExpense())//Se agrega esto con el objetivo de ignorar este check cuando se importa
        {
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBankAccount(), "bankAccount", "Bank Account ID cannot be null."));
        }

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAttachmentStatus(), "attachmentStatus", "Attachment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "employee", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = null;
        try {
            employeeDto = this.manageEmployeeService.findById(command.getEmployee());
        } catch (Exception e) {
            ManageEmployeeResponse response = this.employeeHttpService.sendAccountStatement(new ManageEmployeeRequest(command.getEmployee()));
            command.getMediator().send(CreateManageEmployeeCommand.fromRequest(response));
            employeeDto = response.createObject();
        }

        RulesChecker.checkRule(new CheckIfDateIsBeforeCurrentDateRule(command.getTransactionDate()));
        RulesChecker.checkRule(new CheckPaymentAmountGreaterThanZeroRule(command.getPaymentAmount()));

        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        PaymentCloseOperationDto closeOperationDto = this.closeOperationService.findByHotelIds(hotelDto.getId());
        RulesChecker.checkRule(new CheckIfTransactionDateIsWithInRangeCloseOperationRule(command.getTransactionDate(), closeOperationDto.getBeginDate(), closeOperationDto.getEndDate()));

        ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(command.getPaymentStatus());
        ManageClientDto clientDto = this.clientService.findById(command.getClient());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

        ManageBankAccountDto bankAccountDto = null;
        if (!command.isIgnoreBankAccount() || !paymentSourceDto.getExpense()) {
            bankAccountDto = this.bankAccountService.findById(command.getBankAccount());
            RulesChecker.checkRule(new PaymentValidateBankAccountAndHotelRule(hotelDto, bankAccountDto));
        }
        //ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findById(command.getAttachmentStatus());
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findByNonNone();

        PaymentDto paymentDto = new PaymentDto(
                command.getId(),
                Long.MIN_VALUE,
                command.getStatus(),
                paymentSourceDto,
                command.getReference(),
                command.getTransactionDate(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                command.getPaymentAmount(),
                command.getPaymentAmount(),
                0.0,
                0.0,
                0.0,
                0.0,
                command.getPaymentAmount(),
                command.getPaymentAmount(),//Aplicar la formula del Payment Balance
                0.0,//Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
                command.getRemark(),
                null,
                null,
                null,
                EAttachment.NONE,
                LocalTime.now()
        );

        if (command.getAttachments() != null)
            paymentDto.setHasAttachment(true);
        PaymentDto save = this.paymentService.create(paymentDto);

        if (command.getAttachments() != null) {
            List<MasterPaymentAttachmentDto> list = this.createAttachment(command.getAttachments(), save);
            paymentDto.setAttachments(list);
            this.createAttachmentStatusHistory(employeeDto, save, list);
            //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }
        if (command.getAttachments() == null || command.getAttachments().isEmpty()) {
            this.createAttachmentStatusHistoryWithoutAttachmet(employeeDto, save);
        }

        command.setPayment(save);
        this.createPaymentAttachmentStatusHistory(employeeDto, save);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private List<MasterPaymentAttachmentDto> createAttachment(List<CreateAttachmentRequest> attachments, PaymentDto paymentDto) {
        List<MasterPaymentAttachmentDto> dtos = new ArrayList<>();
        ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.attachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentStatusNonNone = this.attachmentStatusService.findByNonNone();
        ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.attachmentStatusService.findByOtherSupported();
        Integer countDefaults = 0;//El objetivo de este contador es controlar cuantos Payment Support han sido agregados.
        for (CreateAttachmentRequest attachment : attachments) {
            AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(attachment.getAttachmentType());
            ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(attachment.getResourceType());
            MasterPaymentAttachmentDto newAttachmentDto = new MasterPaymentAttachmentDto(
                    UUID.randomUUID(),
                    Status.ACTIVE,
                    paymentDto,
                    manageResourceTypeDto,
                    manageAttachmentTypeDto,
                    attachment.getFileName(),
                    attachment.getFileWeight(),
                    attachment.getPath(),
                    attachment.getRemark(),
                    0L
            );
            if (manageAttachmentTypeDto.getDefaults()) {
                countDefaults++;
                newAttachmentDto.setStatusHistory(attachmentStatusSupport.getCode() + "-" + attachmentStatusSupport.getName());
            } else {
                //newAttachmentDto.setStatusHistory(attachmentStatusNonNone.getCode() + "-" + attachmentStatusNonNone.getName());
                newAttachmentDto.setStatusHistory(attachmentOtherSupport.getCode() + "-" + attachmentOtherSupport.getName());
            }
            dtos.add(newAttachmentDto);
        }

        RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule(countDefaults));
        this.masterPaymentAttachmentService.create(dtos);

        if (countDefaults > 0) {
            paymentDto.setPaymentSupport(true);
            paymentDto.setAttachmentStatus(attachmentStatusSupport);
            //paymentDto.setAttachmentStatus(this.attachmentStatusService.findBySupported());
        } else {
            paymentDto.setAttachmentStatus(attachmentOtherSupport);
            //paymentDto.setAttachmentStatus(attachmentStatusNonNone);
            paymentDto.setPaymentSupport(false);
        }

        this.paymentService.update(paymentDto);
        return dtos;
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, List<MasterPaymentAttachmentDto> list) {
        //List<MasterPaymentAttachmentDto> list = masterPaymentAttachmentService.findAllByPayment(payment.getId());
        for (MasterPaymentAttachmentDto attachment : list) {

            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + attachment.getFileName());
            attachmentStatusHistoryDto.setEmployee(employeeDto);
            attachmentStatusHistoryDto.setPayment(payment);
            attachmentStatusHistoryDto.setStatus(attachment.getStatusHistory());
            //attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

        }
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistoryWithoutAttachmet(ManageEmployeeDto employeeDto, PaymentDto payment) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating payment without attachment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus("NON-NONE");
        //attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        //attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

    }

}
