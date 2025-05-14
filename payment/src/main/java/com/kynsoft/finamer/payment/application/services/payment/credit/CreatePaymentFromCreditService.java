package com.kynsoft.finamer.payment.application.services.payment.credit;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit.CreatePaymentToCreditCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashMessage;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.core.payment.ProcessCreatePayment;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
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
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IManagePaymentAttachmentStatusService managePaymentAttachmentStatusService;

    public CreatePaymentFromCreditService(IManageHotelService hotelService,
                                          IManageBankAccountService manageBankAccountService,
                                          IManagePaymentSourceService sourceService,
                                          IManagePaymentStatusService statusService,
                                          IManageClientService clientService,
                                          IManageAgencyService agencyService,
                                          IManageEmployeeService manageEmployeeService,
                                          IManagePaymentAttachmentStatusService attachmentStatusService,
                                          IPaymentCloseOperationService paymentCloseOperationService,
                                          IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                          IManagePaymentAttachmentStatusService managePaymentAttachmentStatusService){
        this.hotelService = hotelService;
        this.manageBankAccountService = manageBankAccountService;
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.managePaymentAttachmentStatusService = managePaymentAttachmentStatusService;
    }

    public PaymentDto createPayment(UUID hotelId,
                                    UUID clientId,
                                    UUID agencyId,
                                    UUID employeeId,
                                    ManageInvoiceDto invoiceDto){
        //TODO Validar en caso de excepcion como se manaja para que no llegue la exepcion a kafka y siga enviando el registro
        ManageHotelDto hotelDto = this.getHotelFromCredit(hotelId);
        ManageBankAccountDto bankAccountDto = this.getBankAccountFromHotel(hotelDto);
        ManagePaymentSourceDto paymentSourceDto = this.getExpensePaymentSource();
        ManagePaymentStatusDto paymentStatusDto = this.getAppliedPaymentStatus();
        ManageClientDto clientDto = this.getClient(clientId);
        ManageAgencyDto agencyDto = this.getAgency(agencyId);
        ManageEmployeeDto employee = this.getEmployee(employeeId);
        //El credit en su process debe de tener al menos attachemt de tipo support
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.getSupportedPaymentAttachementStatus();
        PaymentCloseOperationDto closeOperationDto = this.getCloseOperacion(hotelDto.getId());
        OffsetDateTime transactionDate = this.getTransactionDate(closeOperationDto);
        ManagePaymentTransactionTypeDto cashPaymentTransactionType = this.getCashPaymentTransactionType();
        List<PaymentStatusHistoryDto> paymentStatusHistoryList = new ArrayList<>();
        List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();

        this.createPaymentToCreditNegative(hotelDto,
                bankAccountDto,
                paymentSourceDto,
                paymentStatusDto,
                transactionDate,
                clientDto,
                agencyDto,
                attachmentStatusDto,
                invoiceDto,
                employee,
                cashPaymentTransactionType,
                paymentStatusHistoryList,
                paymentDetailsToCreate,
                closeOperationDto);

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
                                               ManageEmployeeDto employee,
                                               ManagePaymentTransactionTypeDto cashPaymentTransactionType,
                                               List<PaymentStatusHistoryDto> paymentStatusHistoryDtoList,
                                               List<PaymentDetailDto> paymentDetailDtoList,
                                               PaymentCloseOperationDto closeOperationDto) {

        Double paymentAmount = invoiceDto.getInvoiceAmount();
        String remark = this.getRemark(invoiceDto);
        String reference = "";
        List<CreateAttachment> createAttachmentList = new ArrayList<>();
        ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.managePaymentAttachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.managePaymentAttachmentStatusService.findByOtherSupported();
        //AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(attachment.getAttachmentType());
        //ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(attachment.getResourceType());

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
                reference,
                bankAccountDto == null,
                employee,
                closeOperationDto,
                createAttachmentList,
                attachmentStatusSupport,
                attachmentOtherSupport
                );
        PaymentDto paymentDto = processCreatePayment.create();
//this.masterPaymentAttachmentService.create(dtos);
        if (invoiceDto.getBookings() != null) {
            for (ManageBookingDto booking : invoiceDto.getBookings()) {
                PaymentDetailDto paymentDetailDto = this.createPaymentDetailsToCreditCash(paymentDto,
                        booking,
                        transactionDate,
                        employee,
                        cashPaymentTransactionType,
                        paymentStatusDto,
                        paymentStatusHistoryDtoList
                );
                paymentDetailDtoList.add(paymentDetailDto);
            }
        }

        //TODO Implementar la creación de los attachments
        //if (command.getAttachments() != null) {
            //paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
            //this.createAttachmentStatusHistory(employee, paymentDto, command);
        //}

    }

    private PaymentDetailDto createPaymentDetailsToCreditCash(PaymentDto paymentCash,
                                                  ManageBookingDto booking,
                                                  OffsetDateTime transactionDate,
                                                  ManageEmployeeDto employeeDto,
                                                  ManagePaymentTransactionTypeDto paymentTransactionType,
                                                  ManagePaymentStatusDto paymentStatusDto,
                                                  List<PaymentStatusHistoryDto> paymentStatusHistoryList) {
        ProcessCreatePaymentDetail processCreatePaymentDetail = new ProcessCreatePaymentDetail(paymentCash,
                booking.getInvoiceAmount(),
                transactionDate,
                employeeDto,
                null,
                paymentTransactionType,
                paymentStatusDto,
                null
                );
        processCreatePaymentDetail.process();
        PaymentDetailDto paymentDetailDto = processCreatePaymentDetail.getDetail();
        if(processCreatePaymentDetail.isPaymentApplied()){
            PaymentStatusHistoryDto paymentStatusHistoryDto = processCreatePaymentDetail.getPaymentStatusHistory();
            paymentStatusHistoryList.add(paymentStatusHistoryDto);
        }

        ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(paymentCash,
                paymentDetailDto,
                booking,
                transactionDate,
                booking.getInvoiceAmount());
        processApplyPaymentDetail.process();

        return paymentDetailDto;
    }

    //Payment creado con el deposit y apply deposit
//    private void createPaymentToCreditPositive(ManageHotelDto hotelDto, ManageBankAccountDto bankAccountDto, ManagePaymentSourceDto paymentSourceDto,
//            ManagePaymentStatusDto paymentStatusDto, ManageClientDto clientDto, ManageAgencyDto agencyDto,
//            ManagePaymentAttachmentStatusDto attachmentStatusDto, CreatePaymentToCreditCommand command, ManageEmployeeDto employee) {
//
//        if (!command.isAutoApplyCredit()) {
//            paymentStatusDto = this.statusService.findByConfirmed();
//        }
//
//        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount() * -1;
//        double applied = 0.0;
//        double identified = 0.0;
//        double notIdentified = paymentAmount;
//        if (hotelDto.getAutoApplyCredit()) {// no aplica los apply deposit
//            applied = 0.0;
//            identified = 0.0;
//            notIdentified = paymentAmount;
//        } else {// aplica los apply deposit
//            applied = paymentAmount;
//            identified = paymentAmount;
//            notIdentified = 0.0;
//        }
//        if (command.getInvoiceDto().getInvoiceType().name().equals(EInvoiceType.OLD_CREDIT.name())) {
//            applied = 0.0;
//            identified = 0.0;
//            notIdentified = paymentAmount;
//        }
//        PaymentDto paymentDto = new PaymentDto(
//                UUID.randomUUID(),
//                Long.MIN_VALUE,
//                Status.ACTIVE,
//                paymentSourceDto,
//                deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()),
//                transactionDate(hotelDto.getId()),
//                //LocalDate.now(),
//                paymentStatusDto,
//                clientDto,
//                agencyDto,
//                hotelDto,
//                //hotelDto,
//                bankAccountDto,
//                attachmentStatusDto,
//                paymentAmount,
//                0.0,
//                paymentAmount,
//                0.0,
//                0.0,
//                /*
//                *identified es cero y el notIdentified toma el valor del paymentAmount y su valor varia en paymentAmount - identified.
//                *identified es la suma de todos los cash, que para este payment siempre va a ser cero.
//                */
//                identified,
//                notIdentified,
//                0.0, //Siempre es cero para este caso
//                applied,
//                "Created automatic to apply credit ( " + deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()) + ")",
//                command.getInvoiceDto(),
//                null,
//                null,
//                EAttachment.NONE,
//                LocalTime.now()
//        );
//
//        paymentDto.setCreateByCredit(true);
//        paymentDto.setImportType(ImportType.AUTOMATIC);
//        paymentDto.setApplyPayment(true);
//        paymentDto.setHasAttachment(true);
//        paymentDto.setHasDetailTypeDeposit(true);
//        PaymentDto paymentSave = this.paymentService.create(paymentDto);
//        PaymentDetailDto parentDetailDto = this.createPaymentDetailsToCreditDeposit(paymentSave, command);
////        if (command.getInvoiceDto().getBookings() != null) {
//        if (command.getInvoiceDto().getBookings() != null && command.isAutoApplyCredit()) {
//            List<PaymentDetailDto> updateChildrens = new ArrayList<>();
//            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
//                updateChildrens.add(this.createPaymentDetailsToCreditApplyDeposit(paymentSave, booking.getParent(), parentDetailDto, command, booking.getInvoiceAmount()));
//            }
//            parentDetailDto.setPaymentDetails(updateChildrens);
//        } else {
//            paymentSave.setDepositBalance(paymentAmount);
//            this.paymentService.update(paymentSave);
//        }
//
////        ManageEmployeeDto employeeDto = null;
//        if (command.getAttachments() != null) {
    ////            try {
    ////                employeeDto = this.manageEmployeeService.findById(command.getAttachments().get(0).getEmployee());
    ////            } catch (Exception e) {
    ////            }
//            paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
//            //this.createAttachmentStatusHistory(employeeDto, paymentDto, command);
//            this.createAttachmentStatusHistory(employee, paymentDto, command);
//        }
//
//        //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto, command);
//        this.createPaymentAttachmentStatusHistory(employee, paymentDto, command);
//
//        this.paymentDetailService.update(parentDetailDto);
//
//    }

    private ManageHotelDto getHotelFromCredit(UUID hotelId){
        return this.hotelService.findById(hotelId);
    }

    private ManageBankAccountDto getBankAccountFromHotel(ManageHotelDto hotelDto){
        List<ManageBankAccountDto> listBankAccountDtos = this.manageBankAccountService.findAllByHotel(hotelDto.getId());
        return listBankAccountDtos.isEmpty() ? null : listBankAccountDtos.get(0);
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

    private OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperationDto) {
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

    private ManagePaymentTransactionTypeDto getCashPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByCash();
    }

    private ManagePaymentTransactionTypeDto getDepositPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByDeposit();
    }

    private PaymentCloseOperationDto getCloseOperacion(UUID hotelId){
        return this.paymentCloseOperationService.findByHotelId(hotelId);
    }
}
