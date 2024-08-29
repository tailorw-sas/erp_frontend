package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.payment.CheckIfTransactionDateIsWithInRangeCloseOperationRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;

@Component
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
            IMasterPaymentAttachmentService masterPaymentAttachmentService) {
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
    }

    @Override
    public void handle(CreatePaymentCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentSource(), "paymentSource", "Payment Source ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentStatus(), "paymentStatus", "Payment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getClient(), "client", "Client ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAgency(), "agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "hotel", "Hotel ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBankAccount(), "bankAccount", "Bank Account ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAttachmentStatus(), "attachmentStatus", "Attachment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "employee", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());
        RulesChecker.checkRule(new CheckIfDateIsBeforeCurrentDateRule(command.getTransactionDate()));
        RulesChecker.checkRule(new CheckPaymentAmountGreaterThanZeroRule(command.getPaymentAmount()));

        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        PaymentCloseOperationDto closeOperationDto = this.closeOperationService.findByHotelIds(hotelDto.getId());
        RulesChecker.checkRule(new CheckIfTransactionDateIsWithInRangeCloseOperationRule(command.getTransactionDate(), closeOperationDto.getBeginDate(), closeOperationDto.getEndDate()));

        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findById(command.getPaymentSource());
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(command.getPaymentStatus());
        ManageClientDto clientDto = this.clientService.findById(command.getClient());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(command.getBankAccount());
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findById(command.getAttachmentStatus());

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
                command.getPaymentAmount(),//Payment Amount - Deposit Balance - (Suma de trx tipo check Cash en el Manage Payment Transaction Type)
                0.0,
                command.getRemark(),
                null,
                null,
                null
        );

        command.setPayment(this.paymentService.create(paymentDto));
        if (command.getAttachments() != null) {
            paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto));
            this.createAttachmentStatusHistory(employeeDto, paymentDto);
            //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }

        this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
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
        Integer countDefaults = 0;
        for (CreateAttachmentRequest attachment : attachments) {
            AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(attachment.getAttachmentType());
            ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(attachment.getResourceType());
            dtos.add(new MasterPaymentAttachmentDto(
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
            ));
            if (manageAttachmentTypeDto.getDefaults()) {
                countDefaults++;
            }
        }

        RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule(countDefaults));
        this.masterPaymentAttachmentService.create(dtos);
        return dtos;
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {
        List<MasterPaymentAttachmentDto> list = masterPaymentAttachmentService.findAllByPayment(payment.getId());
        for (MasterPaymentAttachmentDto attachment : list) {

            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + attachment.getFileName());
            attachmentStatusHistoryDto.setEmployee(employeeDto);
            attachmentStatusHistoryDto.setPayment(payment);
            attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

        }
    }

}
