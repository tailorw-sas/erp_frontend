package com.kynsoft.finamer.settings.application.command.replicate.objects;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageBankAccountKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageClientKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageEmployeeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceStatusKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTransactionTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentAttachmentStatusKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentSourceKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentStatusKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentTransactionTypeKafka;
import static com.kynsoft.finamer.settings.application.command.replicate.objects.ObjectEnum.MANAGE_HOTEL;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import com.kynsoft.finamer.settings.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency.ProducerReplicateManageAgencyService;
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

    private final IManageAgencyService manageAgencyService;
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
                                         IManageInvoiceTransactionTypeService invoiceTransactionTypeService) {
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
                        this.replicateManageBankAccount.create(new ReplicateManageBankAccountKafka(bankAccountDto.getId(), bankAccountDto.getAccountNumber(), bankAccountDto.getStatus().name(), bankAccountDto.getManageBank().getName()));
                    }
                }
                case MANAGE_EMPLOYEE -> {//
                    for (ManageEmployeeDto employeeDto : this.manageEmployeeService.findAllToReplicate()) {
                        this.replicateManageEmployeeService.create(new ReplicateManageEmployeeKafka(employeeDto.getId(), employeeDto.getFirstName(), employeeDto.getLastName(), employeeDto.getEmail()));
                    }
                }
                case MANAGE_ATTACHMENT_STATUS -> {//
                    for (ManagePaymentAttachmentStatusDto paymentAttachmentStatusDto : this.managePaymentAttachmentStatusService.findAllToReplicate()) {
                        this.replicateManagePaymentAttachmentStatusService.create(new ReplicateManagePaymentAttachmentStatusKafka(paymentAttachmentStatusDto.getId(), paymentAttachmentStatusDto.getCode(), paymentAttachmentStatusDto.getName(), paymentAttachmentStatusDto.getStatus().name()));
                    }
                }
                case MANEGE_CLIENT -> {//
                    for (ManageClientDto clientDto : this.managerClientService.findAllToReplicate()) {
                        this.replicateManageClientService.create(new ReplicateManageClientKafka(clientDto.getId(), clientDto.getCode(), clientDto.getName(), clientDto.getStatus().name()));
                    }
                }
                case MANAGE_INVOICE_STATUS -> {
                    for (ManageInvoiceStatusDto invoiceStatusDto : this.invoiceStatusService.findAllToReplicate()) {
                        this.replicateManageInvoiceStatusService.create(new ReplicateManageInvoiceStatusKafka(invoiceStatusDto.getId(), invoiceStatusDto.getCode(), invoiceStatusDto.getName()));
                    }
                }
                case MANAGE_INVOICE_TRANSACTION_TYPE -> {
                    for (ManageInvoiceTransactionTypeDto invoiceTransactionTypeDto : this.invoiceTransactionTypeService.findAllToReplicate()) {
                        this.replicateManageInvoiceTransactionTypeService.create(new ReplicateManageInvoiceTransactionTypeKafka(invoiceTransactionTypeDto.getId(), invoiceTransactionTypeDto.getCode(), invoiceTransactionTypeDto.getName()));
                    }
                }
                case MANAGE_PAYMENT_STATUS -> {
                    for (ManagerPaymentStatusDto paymentStatusDto : this.paymentStatusService.findAllToReplicate()) {
                        this.replicateManagePaymentStatusService.create(new ReplicateManagePaymentStatusKafka(paymentStatusDto.getId(), paymentStatusDto.getCode(), paymentStatusDto.getName(), paymentStatusDto.getStatus().name()));
                    }
                }
                case MANAGE_PAYMENT_SOURCE -> {
                    for (ManagePaymentSourceDto paymentSourceDto : this.paymentSourceService.findAllToReplicate()) {
                        this.replicateManagePaymentSourceService.create(new ReplicateManagePaymentSourceKafka(paymentSourceDto.getId(), paymentSourceDto.getCode(), paymentSourceDto.getName(), paymentSourceDto.getStatus().name()));
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
                                paymentTransactionTypeDto.getDefaultRemark()
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
                                hotelDto.getIsVirtual()
                        ));
                    }
                }
                default -> System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
