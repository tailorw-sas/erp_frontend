package com.kynsoft.finamer.payment.application.services.payment.credit;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit.CreatePaymentToCreditCommand;
import com.kynsoft.finamer.payment.domain.core.payment.ProcessCreatePayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.*;

import java.time.*;
import java.util.List;
import java.util.UUID;

public class CreatePaymentFromCreditService {

    private final IManageHotelService hotelService;
    private final IManageBankAccountService manageBankAccountService;
    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageEmployeeService manageEmployeeService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreatePaymentFromCreditService(IManageHotelService hotelService,
                                          IManageBankAccountService manageBankAccountService,
                                          IManagePaymentSourceService sourceService,
                                          IManagePaymentStatusService statusService,
                                          IManageClientService clientService,
                                          IManageAgencyService agencyService,
                                          IManageEmployeeService manageEmployeeService,
                                          IManagePaymentAttachmentStatusService attachmentStatusService,
                                          IPaymentCloseOperationService paymentCloseOperationService){
        this.hotelService = hotelService;
        this.manageBankAccountService = manageBankAccountService;
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    public PaymentDto createPayment(UUID hotelId,
                                    UUID clientId,
                                    UUID agencyId,
                                    UUID employeeId){
        //TODO Validar en caso de exepcion como se manaja para que no llegue la exepcion a kafka y siga enviando el registro
        ManageHotelDto hotelDto = this.getHotelFromCredit(hotelId);
        ManageBankAccountDto bankAccountDto = this.getBankAccountFromHotel(hotelDto);
        ManagePaymentSourceDto paymentSourceDto = this.getExpensePaymentSource();
        ManagePaymentStatusDto paymentStatusDto = this.getAppliedPaymentStatus();
        ManageClientDto clientDto = this.getClient(clientId);
        ManageAgencyDto agencyDto = this.getAgency(agencyId);
        ManageEmployeeDto employee = this.getEmployee(employeeId);
        //El credit en su process debe de tener al menos attachemt de tipo support
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.getSupportedPaymentAttachementStatus();
        OffsetDateTime transactionDate = this.getTransactionDate(hotelDto.getId());

        //this.createPaymentToCreditNegative(hotelDto, bankAccountDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, attachmentStatusDto, command, employee);
        //this.createPaymentToCreditPositive(hotelDto, bankAccountDto, paymentSourceDto, paymentStatusDto, clientDto, agencyDto, attachmentStatusDto, command, employee);

        return null;
    }

    //Payment creado con el cash
    private void createPaymentToCreditNegative(ManageHotelDto hotelDto,
                                               ManageBankAccountDto bankAccountDto,
                                               ManagePaymentSourceDto paymentSourceDto,
                                               ManagePaymentStatusDto paymentStatusDto,
                                               OffsetDateTime transactionDate,
                                               ManageClientDto clientDto,
                                               ManageAgencyDto agencyDto,
                                               ManagePaymentAttachmentStatusDto attachmentStatusDto,
                                               ManageInvoiceDto invoiceDto,
                                               CreatePaymentToCreditCommand command,
                                               ManageEmployeeDto employee) {

        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount();
        String remark = this.getRemark(invoiceDto);

        ProcessCreatePayment processCreatePayment = new ProcessCreatePayment(paymentSourceDto,
                paymentStatusDto,
                transactionDate,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                paymentAmount,
                remark,
                invoiceDto
                );
        PaymentDto paymentDto = processCreatePayment.create();

        if (command.getInvoiceDto().getBookings() != null) {
            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
                //this.createPaymentDetailsToCreditCash(paymentSave, booking, command);
            }
        }

        if (command.getAttachments() != null) {
            //paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
            //this.createAttachmentStatusHistory(employee, paymentDto, command);
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
        //this.createPaymentAttachmentStatusHistory(employee, paymentDto, command);

    }

    private ManageHotelDto getHotelFromCredit(UUID hotelId){
        return this.hotelService.findById(hotelId);
    }

    private ManageBankAccountDto getBankAccountFromHotel(ManageHotelDto hotelDto){
        List<ManageBankAccountDto> listBankAccountDtos = this.manageBankAccountService.findAllByHotel(hotelDto.getId());
        ManageBankAccountDto bankAccountDto = listBankAccountDtos.isEmpty() ? null : listBankAccountDtos.get(0);

        return bankAccountDto;
    }

    private ManagePaymentSourceDto getExpensePaymentSource(){
        return this.sourceService.findByExpense();
    }

    private ManagePaymentStatusDto getAppliedPaymentStatus(){
        return this.statusService.findByApplied();
    }

    private ManageClientDto getClient(UUID clientId){
        return this.clientService.findById(clientId);
    }

    private ManageAgencyDto getAgency(UUID agencyId){
        return this.agencyService.findById(agencyId);
    }

    private ManageEmployeeDto getEmployee(UUID employeeId){
        return this.manageEmployeeService.findById(employeeId);
    }

    private ManagePaymentAttachmentStatusDto getSupportedPaymentAttachementStatus(){
        return this.attachmentStatusService.findBySupported();
    }

    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

    private String getRemark(ManageInvoiceDto invoiceDto){
        return "Created automatic to apply credit ( " + deleteHotelInfo(invoiceDto.getInvoiceNumber()) + ")";
    }
}
