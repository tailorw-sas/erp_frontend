package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.attachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.attachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create.CreateAttachmentStatusHistoryCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.application.command.paymentAttachmentStatusHistory.create.CreatePaymentAttachmentStatusHistoryCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPaymentToCredit.ApplyPaymentToCreditDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit.CreatePaymentDetailTypeDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit.CreatePaymentDetailTypeDepositMessage;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreatePaymentToCreditCommandHandler implements ICommandHandler<CreatePaymentToCreditCommand> {

    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IPaymentService paymentService;

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;

    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBankAccountService manageBankAccountService;

    public CreatePaymentToCreditCommandHandler(IManagePaymentSourceService sourceService,
            IManagePaymentStatusService statusService,
            IManageClientService clientService,
            IManageAgencyService agencyService,
            IPaymentService paymentService,
            IManageHotelService hotelService,
            IPaymentDetailService paymentDetailService,
            IManagePaymentAttachmentStatusService attachmentStatusService,
            IManageEmployeeService manageEmployeeService,
            IPaymentCloseOperationService paymentCloseOperationService,
            IManageBankAccountService manageBankAccountService) {
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.paymentService = paymentService;
        this.hotelService = hotelService;
        this.paymentDetailService = paymentDetailService;
        this.attachmentStatusService = attachmentStatusService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageBankAccountService = manageBankAccountService;
    }

    @Override
    public void handle(CreatePaymentToCreditCommand command) {

        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        List<ManageBankAccountDto> listBankAccountDtos = this.manageBankAccountService.findAllByHotel(hotelDto.getId());
        ManageBankAccountDto bankAccountDto = listBankAccountDtos.isEmpty() ? null : listBankAccountDtos.get(0);

        //Crear servicio para obtener el que esta marcado con expense
        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findByExpense();
        //Crear servicio para obtener el que esta marcado con applied
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findByApplied();
        ManageClientDto clientDto = this.clientService.findById(command.getClient());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        
        ManageEmployeeDto employee = null;
        try {
            employee = this.manageEmployeeService.findById(command.getEmployee());
        } catch (Exception e) {
        }

        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findBySupported();//El credit en su process debe de tener al menos attachemt de tipo support
        this.createPaymentToCreditNegative(hotelDto, bankAccountDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, attachmentStatusDto, command, employee);
        this.createPaymentToCreditPositive(hotelDto, bankAccountDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, attachmentStatusDto, command, employee);
    }

    //Payment creado con el deposit y apply deposit
    private void createPaymentToCreditPositive(ManageHotelDto hotelDto, ManageBankAccountDto bankAccountDto,
            ManagePaymentSourceDto paymentSourceDto,
            ManagePaymentStatusDto paymentStatusDto,
            ManageClientDto clientDto,
            ManageAgencyDto agencyDto,
            ManagePaymentAttachmentStatusDto attachmentStatusDto,
            CreatePaymentToCreditCommand command,
            ManageEmployeeDto employee) {

        if (!command.isAutoApplyCredit()) {
            paymentStatusDto = this.statusService.findByConfirmed();
        }

        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount() * -1;
        double applied = 0.0;
        double identified = 0.0;
        double notIdentified = paymentAmount;
        if (hotelDto.getAutoApplyCredit()) {// no aplica los apply deposit
            applied = 0.0;
            identified = 0.0;
            notIdentified = paymentAmount;
        } else {// aplica los apply deposit
            applied = paymentAmount;
            identified = paymentAmount;
            notIdentified = 0.0;
        }
        if (command.getInvoiceDto().getInvoiceType().name().equals(EInvoiceType.OLD_CREDIT.name())) {
            applied = 0.0;
            identified = 0.0;
            notIdentified = paymentAmount;
        }
        PaymentDto paymentDto = new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()),
                transactionDate(hotelDto.getId()),
                //LocalDate.now(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                //hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                paymentAmount,
                0.0,
                paymentAmount,
                0.0,
                0.0,
                /*
                *identified es cero y el notIdentified toma el valor del paymentAmount y su valor varia en paymentAmount - identified.
                *identified es la suma de todos los cash, que para este payment siempre va a ser cero.
                */
                identified,
                notIdentified,
                0.0, //Siempre es cero para este caso
                applied,
                "Created automatic to apply credit ( " + deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()) + ")",
                command.getInvoiceDto(),
                null,
                null,
                EAttachment.NONE,
                LocalTime.now()
        );

        paymentDto.setCreateByCredit(true);
        paymentDto.setImportType(ImportType.AUTOMATIC);
        paymentDto.setApplyPayment(true);
//        paymentDto.setHasAttachment(true);
//        paymentDto.setHasDetailTypeDeposit(true);
        PaymentDto paymentSave = this.paymentService.create(paymentDto);
        PaymentDetailDto parentDetailDto = this.createPaymentDetailsToCreditDeposit(paymentSave, command);
//        if (command.getInvoiceDto().getBookings() != null) {
        if (command.getInvoiceDto().getBookings() != null && command.isAutoApplyCredit()) {
            List<PaymentDetailDto> updateChildrens = new ArrayList<>();
            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
                updateChildrens.add(this.createPaymentDetailsToCreditApplyDeposit(paymentSave, booking.getParent(), parentDetailDto, command, booking.getInvoiceAmount()));
            }
            parentDetailDto.setChildren(updateChildrens);
        } else {
            paymentSave.setDepositBalance(paymentAmount);
            this.paymentService.update(paymentSave);
        }

//        ManageEmployeeDto employeeDto = null;
        if (command.getAttachments() != null) {
//            try {
//                employeeDto = this.manageEmployeeService.findById(command.getAttachments().get(0).getEmployee());
//            } catch (Exception e) {
//            }
            paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
            //this.createAttachmentStatusHistory(employeeDto, paymentDto, command);
            this.createAttachmentStatusHistory(employee, paymentDto, command);
        }

        //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto, command);
        this.createPaymentAttachmentStatusHistory(employee, paymentDto, command);

        this.paymentDetailService.update(parentDetailDto);

    }

    //Payment creado con el cash
    private void createPaymentToCreditNegative(ManageHotelDto hotelDto, ManageBankAccountDto bankAccountDto,
            ManagePaymentSourceDto paymentSourceDto,
            ManagePaymentStatusDto paymentStatusDto,
            ManageClientDto clientDto,
            ManageAgencyDto agencyDto,
            ManagePaymentAttachmentStatusDto attachmentStatusDto,
            CreatePaymentToCreditCommand command,
            ManageEmployeeDto employee) {

        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount();
        PaymentDto paymentDto = new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()),
                //LocalDate.now(),
                transactionDate(hotelDto.getId()),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                paymentAmount,
                0.0,
                0.0,
                0.0,
                0.0,
                /**
                 *Para este caso, el identified toma el valor del paymentAmount, dado que se crean cash por el valor total del payment Amount y
                 * entonces el notIdentified se hace cero.
                 **/
                paymentAmount,
                0.0,
                0.0,//Payment Amount - Deposit Balance - (Suma de trx tipo check Cash en el Manage Payment Transaction Type)
                paymentAmount,
                "Created automatic to apply credit ( " + deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()) + ")",
                command.getInvoiceDto(),
                null,
                null,
                EAttachment.NONE,
                LocalTime.now()
        );

        paymentDto.setCreateByCredit(true);
        paymentDto.setImportType(ImportType.AUTOMATIC);
//        paymentDto.setHasAttachment(true);
//        paymentDto.setHasDetailTypeDeposit(false);
        PaymentDto paymentSave = this.paymentService.create(paymentDto);
        if (command.getInvoiceDto().getBookings() != null) {
            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
                this.createPaymentDetailsToCreditCash(paymentSave, booking, command);
            }
        }

//        ManageEmployeeDto employeeDto = null;
        if (command.getAttachments() != null) {
//            try {
//                employeeDto = this.manageEmployeeService.findById(command.getAttachments().get(0).getEmployee());
//            } catch (Exception e) {
//            }
            paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
            //this.createAttachmentStatusHistory(employeeDto, paymentDto, command);
            this.createAttachmentStatusHistory(employee, paymentDto, command);
        }

        System.err.println("##################################################");
        System.err.println("##################################################");
        System.err.println("##################################################");
        System.err.println("Employeee: " + employee.getFirstName() + employee.getLastName());
        System.err.println("Employeee: " + employee.getId());
        System.err.println("##################################################");
        System.err.println("##################################################");
        System.err.println("##################################################");
        //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto, command);
        this.createPaymentAttachmentStatusHistory(employee, paymentDto, command);

    }

    private LocalDate transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDate.now();
        }
        return closeOperationDto.getEndDate();
    }

    private void createPaymentDetailsToCreditCash(PaymentDto paymentCash, ManageBookingDto booking, CreatePaymentToCreditCommand command) {
        CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentCash, booking.getId(), booking.getAmountBalance(), false, command.getInvoiceDto().getInvoiceDate(), true));
        //CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentCash, booking.getId(), booking.getAmountBalance() * -1, false, command.getInvoiceDto().getInvoiceDate(), true));
        //if (command.isAutoApplyCredit()) {
            command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), booking.getId(), null));//Este marcado o no el check AutoApplyCredit el cash que se crea siempre va aplicado al CREDIT.
        //}
    }

    private PaymentDetailDto createPaymentDetailsToCreditDeposit(PaymentDto payment, CreatePaymentToCreditCommand command) {
        CreatePaymentDetailTypeDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeDepositCommand(payment, true));
        return message.getNewDetailDto();
    }

    private PaymentDetailDto createPaymentDetailsToCreditApplyDeposit(PaymentDto payment, ManageBookingDto booking, PaymentDetailDto parentDetailDto, CreatePaymentToCreditCommand command, double amount) {
        CreatePaymentDetailTypeApplyDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeApplyDepositCommand(payment, /*booking.getAmountBalance()*/amount * -1, parentDetailDto, false, command.getInvoiceDto().getInvoiceDate(), true));
        command.getMediator().send(new ApplyPaymentToCreditDetailCommand(message.getNewDetailDto().getId(), booking.getId()));
        return message.getNewDetailDto();
    }

    private List<MasterPaymentAttachmentDto> createAttachment(List<CreateAttachmentRequest> attachments, PaymentDto paymentDto, CreatePaymentToCreditCommand command) {
          CreateAttachmentMessage message = command.getMediator().send(new CreateAttachmentCommand(attachments, paymentDto));
          return message.getRespose();
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, CreatePaymentToCreditCommand command) {
        command.getMediator().send(new CreateAttachmentStatusHistoryCommand(payment, employeeDto));
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, CreatePaymentToCreditCommand command) {
        command.getMediator().send(new CreatePaymentAttachmentStatusHistoryCommand(payment, employeeDto));
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
