package com.kynsoft.finamer.settings.application.command.replicate.objects;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.*;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency.ProducerReplicateManageAgencyService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyType.ProducerReplicateManageAgencyTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAttachmentType.ProducerReplicateManageAttachmentTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBankAccount.ProducerReplicateManageBankAccount;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageClient.ProducerReplicateManageClientService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageEmployee.ProducerReplicateManageEmployeeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageHotel.ProducerReplicateManageHotelService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceStatus.ProducerReplicateManageInvoiceStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType.ProducerReplicateManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType.ProducerReplicateManageInvoiceTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentAttachmentStatus.ProducerReplicateManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentSource.ProducerReplicateManagePaymentSourceService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentStatus.ProducerReplicateManagePaymentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentTransactionType.ProducerReplicateManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateReplicateCommandHandler implements ICommandHandler<CreateReplicateCommand> {

    private final IManageInvoiceTypeService invoiceTypeService;
    private final IManageInvoiceTransactionTypeService invoiceTransactionTypeService;
    private final IManagerPaymentStatusService paymentStatusService;
    private final IManagePaymentSourceService paymentSourceService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final ProducerReplicateManageInvoiceTypeService replicateManageInvoiceTypeService;
    private final ProducerReplicateManagePaymentSourceService replicateManagePaymentSourceService;
    private final ProducerReplicateManagePaymentStatusService replicateManagePaymentStatusService;
    private final ProducerReplicateManagePaymentTransactionTypeService replicateManagePaymentTransactionTypeService;
    private final ProducerReplicateManageInvoiceStatusService replicateManageInvoiceStatusService;
    private final ProducerReplicateManageInvoiceTransactionTypeService replicateManageInvoiceTransactionTypeService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;

    private final IManageAgencyService manageAgencyService;
    private final IManageAgencyTypeService manageAgencyTypeService;
    private final IManageBankAccountService manageBankAccountService;
    private final IManageEmployeeService manageEmployeeService;
    private final IManageHotelService manageHotelService;
    private final IManagerClientService managerClientService;
    private final IManagePaymentAttachmentStatusService managePaymentAttachmentStatusService;

    private final ProducerReplicateManageAgencyService replicateManageAgencyService;
    private final ProducerReplicateManageBankAccount replicateManageBankAccount;
    private final ProducerReplicateManageEmployeeService replicateManageEmployeeService;
    private final ProducerReplicateManageHotelService replicateManageHotelService;
    private final ProducerReplicateManageClientService replicateManageClientService;
    private final ProducerReplicateManagePaymentAttachmentStatusService replicateManagePaymentAttachmentStatusService;
    private final ProducerReplicateManageAttachmentTypeService replicateManageAttachmentTypeService;
    private final ProducerReplicateManageAgencyTypeService replicateManageAgencyTypeService;

    public CreateReplicateCommandHandler(IManageInvoiceTypeService invoiceTypeService, 
                                         IManagerPaymentStatusService paymentStatusService, 
                                         IManagePaymentSourceService paymentSourceService, 
                                         IManagePaymentTransactionTypeService paymentTransactionTypeService, 
                                         ProducerReplicateManageInvoiceTypeService replicateManageInvoiceTypeService, 
                                         ProducerReplicateManagePaymentSourceService replicateManagePaymentSourceService, 
                                         ProducerReplicateManagePaymentStatusService replicateManagePaymentStatusService, 
                                         ProducerReplicateManagePaymentTransactionTypeService replicateManagePaymentTransactionTypeService, 
                                         ProducerReplicateManageInvoiceStatusService replicateManageInvoiceStatusService, 
                                         ProducerReplicateManageInvoiceTransactionTypeService replicateManageInvoiceTransactionTypeService, 
                                         IManageInvoiceStatusService invoiceStatusService, IManageAgencyService manageAgencyService, 
                                         IManageBankAccountService manageBankAccountService, IManageEmployeeService manageEmployeeService, 
                                         IManageHotelService manageHotelService, IManagerClientService managerClientService, 
                                         IManagePaymentAttachmentStatusService managePaymentAttachmentStatusService, 
                                         ProducerReplicateManageAgencyService replicateManageAgencyService, 
                                         ProducerReplicateManageBankAccount replicateManageBankAccount, 
                                         ProducerReplicateManageEmployeeService replicateManageEmployeeService, 
                                         ProducerReplicateManageHotelService replicateManageHotelService, 
                                         ProducerReplicateManageClientService replicateManageClientService, 
                                         ProducerReplicateManagePaymentAttachmentStatusService replicateManagePaymentAttachmentStatusService,
                                         IManageInvoiceTransactionTypeService invoiceTransactionTypeService,
                                         ProducerReplicateManageAttachmentTypeService replicateManageAttachmentTypeService,
                                         IManageAttachmentTypeService attachmentTypeService,
                                         IManageAgencyTypeService manageAgencyTypeService,
                                         ProducerReplicateManageAgencyTypeService replicateManageAgencyTypeService
                                         ) {
        this.invoiceTypeService = invoiceTypeService;
        this.paymentStatusService = paymentStatusService;
        this.paymentSourceService = paymentSourceService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.replicateManageInvoiceTypeService = replicateManageInvoiceTypeService;
        this.replicateManagePaymentSourceService = replicateManagePaymentSourceService;
        this.replicateManagePaymentStatusService = replicateManagePaymentStatusService;
        this.replicateManagePaymentTransactionTypeService = replicateManagePaymentTransactionTypeService;
        this.replicateManageInvoiceStatusService = replicateManageInvoiceStatusService;
        this.replicateManageInvoiceTransactionTypeService = replicateManageInvoiceTransactionTypeService;
        this.invoiceStatusService = invoiceStatusService;
        this.manageAgencyService = manageAgencyService;
        this.manageBankAccountService = manageBankAccountService;
        this.manageEmployeeService = manageEmployeeService;
        this.manageHotelService = manageHotelService;
        this.managerClientService = managerClientService;
        this.managePaymentAttachmentStatusService = managePaymentAttachmentStatusService;
        this.replicateManageAgencyService = replicateManageAgencyService;
        this.replicateManageBankAccount = replicateManageBankAccount;
        this.replicateManageEmployeeService = replicateManageEmployeeService;
        this.replicateManageHotelService = replicateManageHotelService;
        this.replicateManageClientService = replicateManageClientService;
        this.replicateManagePaymentAttachmentStatusService = replicateManagePaymentAttachmentStatusService;
        this.invoiceTransactionTypeService = invoiceTransactionTypeService;
        this.attachmentTypeService = attachmentTypeService;
        this.replicateManageAttachmentTypeService = replicateManageAttachmentTypeService;
        this.manageAgencyTypeService = manageAgencyTypeService;
        this.replicateManageAgencyTypeService = replicateManageAgencyTypeService;
    }

    @Override
    public void handle(CreateReplicateCommand command) {
        for (ObjectEnum object : command.getObjects()) {
            switch (object) {
                case MANAGE_INVOICE_TYPE -> {
                    for (ManageInvoiceTypeDto invoiceType : this.invoiceTypeService.findAllToReplicate()) {
                        this.replicateManageInvoiceTypeService.create(new ReplicateManageInvoiceTypeKafka(invoiceType.getId(), invoiceType.getCode(), invoiceType.getName()));
                    }
                }
                case MANAGE_ATTACHMENT_TYPE -> {
                    for (ManageAttachmentTypeDto attachmentTypeDto : this.attachmentTypeService.findAllToReplicate()) {
                        this.replicateManageAttachmentTypeService.create(new ReplicateManageAttachmentTypeKafka(attachmentTypeDto.getId(), attachmentTypeDto.getCode(), attachmentTypeDto.getName(), attachmentTypeDto.getStatus().toString(), attachmentTypeDto.getDefaults()));
                    }
                }
                case MANAGE_AGENCY_TYPE -> {
                    for (ManageAgencyTypeDto agencyTypeDto : this.manageAgencyTypeService.findAllToReplicate()) {
                        this.replicateManageAgencyTypeService.create(new ReplicateManageAgencyTypeKafka(agencyTypeDto.getId(), agencyTypeDto.getCode(), agencyTypeDto.getName(), agencyTypeDto.getStatus().toString()));
                    }
                }
                case MANAGE_AGENCY -> {//
                    for (ManageAgencyDto agencyDto : this.manageAgencyService.findAllToReplicate()) {
                        this.replicateManageAgencyService.create(new ReplicateManageAgencyKafka(
                                agencyDto.getId(), 
                                agencyDto.getCode(), 
                                agencyDto.getName(), 
                                agencyDto.getClient().getId(), 
                                agencyDto.getBookingCouponFormat(), 
                                agencyDto.getStatus().name(), 
                                agencyDto.getGenerationType().name(),
                                agencyDto.getAgencyType().getId()
                        ));
                    }
                }
                case MANAGE_BANK_ACCOUNT -> {//
                    for (ManageBankAccountDto bankAccountDto : this.manageBankAccountService.findAllToReplicate()) {
                        this.replicateManageBankAccount.create(new ReplicateManageBankAccountKafka(bankAccountDto.getId(), bankAccountDto.getAccountNumber(), bankAccountDto.getStatus().name(), bankAccountDto.getManageBank().getName(),bankAccountDto.getManageHotel().getId()));
                    }
                }
                case MANAGE_EMPLOYEE -> {//
                    for (ManageEmployeeDto employeeDto : this.manageEmployeeService.findAllToReplicate()) {
                        this.replicateManageEmployeeService.create(new ReplicateManageEmployeeKafka(employeeDto.getId(), employeeDto.getFirstName(), employeeDto.getLastName(), employeeDto.getEmail()));
                    }
                }
                case MANAGE_ATTACHMENT_STATUS -> {//
                    for (ManagePaymentAttachmentStatusDto paymentAttachmentStatusDto : this.managePaymentAttachmentStatusService.findAllToReplicate()) {
                        this.replicateManagePaymentAttachmentStatusService.create(new ReplicateManagePaymentAttachmentStatusKafka(paymentAttachmentStatusDto.getId(), paymentAttachmentStatusDto.getCode(), paymentAttachmentStatusDto.getName(), paymentAttachmentStatusDto.getStatus().name(), paymentAttachmentStatusDto.getDefaults()));
                    }
                }
                case MANEGE_CLIENT -> {//
                    for (ManageClientDto clientDto : this.managerClientService.findAllToReplicate()) {
                        this.replicateManageClientService.create(new ReplicateManageClientKafka(clientDto.getId(), clientDto.getCode(), clientDto.getName(), clientDto.getStatus().name(), clientDto.getIsNightType()));
                    }
                }
                case MANAGE_INVOICE_STATUS -> {
                    for (ManageInvoiceStatusDto invoiceStatusDto : this.invoiceStatusService.findAllToReplicate()) {
                        this.replicateManageInvoiceStatusService.create(new ReplicateManageInvoiceStatusKafka(invoiceStatusDto.getId(), invoiceStatusDto.getCode(), invoiceStatusDto.getName(), invoiceStatusDto.getShowClone()));
                    }
                }
                case MANAGE_INVOICE_TRANSACTION_TYPE -> {
                    for (ManageInvoiceTransactionTypeDto invoiceTransactionTypeDto : this.invoiceTransactionTypeService.findAllToReplicate()) {
                        this.replicateManageInvoiceTransactionTypeService.create(new ReplicateManageInvoiceTransactionTypeKafka(invoiceTransactionTypeDto.getId(), invoiceTransactionTypeDto.getCode(), invoiceTransactionTypeDto.getName()));
                    }
                }
                case MANAGE_PAYMENT_STATUS -> {
                    for (ManagerPaymentStatusDto paymentStatusDto : this.paymentStatusService.findAllToReplicate()) {
                        this.replicateManagePaymentStatusService.create(new ReplicateManagePaymentStatusKafka(paymentStatusDto.getId(), paymentStatusDto.getCode(), paymentStatusDto.getName(), paymentStatusDto.getStatus().name(), paymentStatusDto.getApplied()));
                    }
                }
                case MANAGE_PAYMENT_SOURCE -> {
                    for (ManagePaymentSourceDto paymentSourceDto : this.paymentSourceService.findAllToReplicate()) {
                        this.replicateManagePaymentSourceService.create(new ReplicateManagePaymentSourceKafka(paymentSourceDto.getId(), paymentSourceDto.getCode(), paymentSourceDto.getName(), paymentSourceDto.getStatus().name(), paymentSourceDto.getExpense()));
                    }
                }
                case MANAGE_PAYMENT_TRANSACTION_TYPE -> {
                    for (ManagePaymentTransactionTypeDto paymentTransactionTypeDto : this.paymentTransactionTypeService.findAllToReplicate()) {
                        this.replicateManagePaymentTransactionTypeService.create(new ReplicateManagePaymentTransactionTypeKafka(
                                paymentTransactionTypeDto.getId(), 
                                paymentTransactionTypeDto.getCode(), 
                                paymentTransactionTypeDto.getName(), 
                                paymentTransactionTypeDto.getStatus().name(), 
                                paymentTransactionTypeDto.getDeposit(), 
                                paymentTransactionTypeDto.getApplyDeposit(), 
                                paymentTransactionTypeDto.getCash(), 
                                paymentTransactionTypeDto.getRemarkRequired(), 
                                paymentTransactionTypeDto.getMinNumberOfCharacter(),
                                paymentTransactionTypeDto.getDefaultRemark(),
                                paymentTransactionTypeDto.getDefaults(),
                                paymentTransactionTypeDto.getPaymentInvoice()
                        ));
                    }
                }

                case MANAGE_HOTEL -> {//
                    for (ManageHotelDto hotelDto : this.manageHotelService.findAllToReplicate()) {
                        this.replicateManageHotelService.create(new ReplicateManageHotelKafka(
                                hotelDto.getId(), 
                                hotelDto.getCode(), 
                                hotelDto.getName(), 
                                hotelDto.getIsApplyByVCC(), 
                                hotelDto.getManageTradingCompanies() != null ? hotelDto.getManageTradingCompanies().getId() : null, 
                                hotelDto.getStatus().name(),
                                hotelDto.getRequiresFlatRate(),
                                hotelDto.getIsVirtual(),
                                hotelDto.getApplyByTradingCompany(),
                                hotelDto.getAutoApplyCredit()
                        ));
                    }
                }
                default -> System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
