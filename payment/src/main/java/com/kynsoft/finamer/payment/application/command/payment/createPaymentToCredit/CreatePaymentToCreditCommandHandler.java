package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    private final IManageInvoiceService invoiceService;

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    public CreatePaymentToCreditCommandHandler(IManagePaymentSourceService sourceService,
                                                IManagePaymentStatusService statusService,
                                                IManageClientService clientService,
                                                IManageAgencyService agencyService,
                                                IPaymentService paymentService,
                                                IManageHotelService hotelService,
                                                IPaymentDetailService paymentDetailService,
                                                IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                IManageInvoiceService invoiceService) {
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.paymentService = paymentService;
        this.hotelService = hotelService;
        this.invoiceService = invoiceService;
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }

    @Override
    public void handle(CreatePaymentToCreditCommand command) {

//        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());
//        RulesChecker.checkRule(new CheckIfDateIsBeforeCurrentDateRule(command.getTransactionDate()));
//        RulesChecker.checkRule(new CheckPaymentAmountGreaterThanZeroRule(command.getPaymentAmount()));
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
//        PaymentCloseOperationDto closeOperationDto = this.closeOperationService.findByHotelIds(hotelDto.getId());
//        RulesChecker.checkRule(new CheckIfTransactionDateIsWithInRangeCloseOperationRule(command.getTransactionDate(), closeOperationDto.getBeginDate(), closeOperationDto.getEndDate()));

        //Crear servicio para obtener el que esta marcado con expense
        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findByExpense();
        //Crear servicio para obtener el que esta marcado con applied
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findByApplied();
        ManageClientDto clientDto = this.clientService.findById(command.getClient());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

//        ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(command.getBankAccount());
//        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findById(command.getAttachmentStatus());
        this.createPaymentToCreditNegative(hotelDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, command);
        this.createPaymentToCreditPositive(hotelDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, command);
    }

    //Payment creado con el deposit y apply deposit
    private void createPaymentToCreditPositive(ManageHotelDto hotelDto,
            ManagePaymentSourceDto paymentSourceDto,
            ManagePaymentStatusDto paymentStatusDto,
            ManageClientDto clientDto,
            ManageAgencyDto agencyDto,
            CreatePaymentToCreditCommand command) {

        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount() * -1;
        PaymentDto paymentDto = new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                command.getInvoiceDto().getInvoiceNumber(),
                LocalDate.now(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                null,
                null,
                paymentAmount,
                0.0,
                paymentAmount,
                0.0,
                0.0,
                paymentAmount,
                0.0,
                0.0,//Payment Amount - Deposit Balance - (Suma de trx tipo check Cash en el Manage Payment Transaction Type)
                paymentAmount,
                "Created automatic to apply credit to the reservation number ( " + command.getInvoiceDto().getInvoiceNumber() + ")",
                command.getInvoiceDto(),
                null,
                null
        );

        PaymentDto paymentSave = this.paymentService.create(paymentDto);
        PaymentDetailDto parentDetailDto = this.createPaymentDetailsToCreditDeposit(paymentSave);
        if (command.getInvoiceDto().getBookings() != null) {
            List<PaymentDetailDto> updateChildrens = new ArrayList<>();
            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
                updateChildrens.add(this.createPaymentDetailsToCreditApplyDeposit(paymentSave, booking, parentDetailDto));
            }
            parentDetailDto.setChildren(updateChildrens);
        }
        this.paymentDetailService.update(parentDetailDto);

    }

    //Payment creado con el cash
    private void createPaymentToCreditNegative(ManageHotelDto hotelDto,
            ManagePaymentSourceDto paymentSourceDto,
            ManagePaymentStatusDto paymentStatusDto,
            ManageClientDto clientDto,
            ManageAgencyDto agencyDto,
            CreatePaymentToCreditCommand command) {

        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount();
        PaymentDto paymentDto = new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                command.getInvoiceDto().getInvoiceNumber(),
                LocalDate.now(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                null,
                null,
                paymentAmount,
                0.0,
                0.0,
                0.0,
                0.0,
                paymentAmount,
                0.0,
                0.0,//Payment Amount - Deposit Balance - (Suma de trx tipo check Cash en el Manage Payment Transaction Type)
                paymentAmount,
                "Created automatic to apply credit to the reservation number ( " + command.getInvoiceDto().getInvoiceNumber() + ")",
                command.getInvoiceDto(),
                null,
                null
        );

        PaymentDto paymentSave = this.paymentService.create(paymentDto);
        if (command.getInvoiceDto().getBookings() != null) {
            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
                this.createPaymentDetailsToCreditCash(paymentSave, booking);
            }
        }
    }

    private void createPaymentDetailsToCreditCash(PaymentDto paymentCash, ManageBookingDto booking) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                paymentCash,
                this.paymentTransactionTypeService.findByPaymentInvoice(),
                booking.getInvoiceAmount(),
                paymentCash.getRemark(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        this.paymentDetailService.create(newDetailDto);
    }

    private PaymentDetailDto createPaymentDetailsToCreditDeposit(PaymentDto payment) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                this.paymentTransactionTypeService.findByDeposit(),
                payment.getPaymentAmount(),
                payment.getRemark(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        this.paymentDetailService.create(newDetailDto);
        return newDetailDto;
    }

    private PaymentDetailDto createPaymentDetailsToCreditApplyDeposit(PaymentDto payment, ManageBookingDto booking, PaymentDetailDto parentDetailDto) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                this.paymentTransactionTypeService.findByApplyDeposit(),
                booking.getInvoiceAmount(),
                payment.getRemark(),
                null,
                null,
                null,
                OffsetDateTime.now(ZoneId.of("UTC")),
                null,
                null,
                null,
                null,
                null,
                null
        );
        newDetailDto.setParentId(parentDetailDto.getPaymentDetailId());
        this.paymentDetailService.create(newDetailDto);
        return newDetailDto;
    }
}
