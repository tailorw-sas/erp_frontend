package com.kynsoft.finamer.payment.application.services.payment.credit;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.core.payment.ProcessCreatePayment;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;
    private final IPaymentService paymentService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageInvoiceService invoiceService;
    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;

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
                                          IManagePaymentAttachmentStatusService managePaymentAttachmentStatusService,
                                          IManageAttachmentTypeService manageAttachmentTypeService,
                                          IManageResourceTypeService manageResourceTypeService,
                                          IPaymentService paymentService,
                                          IPaymentStatusHistoryService paymentStatusHistoryService,
                                          IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                          IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                          IManageInvoiceService invoiceService,
                                          IPaymentDetailService paymentDetailService,
                                          IManageBookingService manageBookingService){
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
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.paymentService = paymentService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.invoiceService = invoiceService;
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
    }

    @Transactional
    public PaymentDto create(UUID hotelId,
                                    UUID clientId,
                                    UUID agencyId,
                                    UUID employeeId,
                                    ManageInvoiceDto invoiceDtoTypeCredit,
                                    List<CreateAttachmentRequest> createAttachmentRequests,
                             List<ManageBookingDto> bookinsToUpdate){
        //TODO Validar en caso de excepcion como se manaja para que no llegue la exepcion a kafka y siga enviando el registro
        ManageInvoiceDto invoiceDtoTypeInvoice = invoiceDtoTypeCredit.getParent();
        ManageHotelDto hotelDto = this.getHotelFromCredit(hotelId);
        ManageBankAccountDto bankAccountDto = this.getBankAccountFromHotel(hotelDto);
        ManagePaymentSourceDto paymentSourceDto = this.getExpensePaymentSource();
        ManagePaymentStatusDto appliedPaymentStatusDto = this.getAppliedPaymentStatus();
        ManagePaymentStatusDto confirmedPaymentStatusDto = this.getConfirmedPaymentStatus();
        ManageClientDto clientDto = this.getClient(clientId);
        ManageAgencyDto agencyDto = this.getAgency(agencyId);
        ManageEmployeeDto employee = this.getEmployee(employeeId);
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.getSupportedPaymentAttachementStatus();
        PaymentCloseOperationDto closeOperationDto = this.getCloseOperacion(hotelDto.getId());
        OffsetDateTime transactionDate = this.getTransactionDate(closeOperationDto);
        List<PaymentDto> paymentsToCreate = new ArrayList<>();
        List<PaymentStatusHistoryDto> paymentStatusHistoryList = new ArrayList<>();
        List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();
        List<PaymentDetailDto> paymentDetailsApplyDepositToCreate = new ArrayList<>();
        List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList = new ArrayList<>();
        List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList = new ArrayList<>();

        AttachmentTypeDto attachmentTypeSupport = this.manageAttachmentTypeService.getByDefault();
        AttachmentTypeDto attachmentTypeOtherSupport = this.manageAttachmentTypeService.getByAntiToIncomeImport();
        ResourceTypeDto resourceTypeDto = this.manageResourceTypeService.getByDefault();

        this.updateAttachmentTypes(createAttachmentRequests,
                resourceTypeDto,
                attachmentTypeSupport,
                attachmentTypeOtherSupport);

        this.createPaymentToCreditPositive(paymentSourceDto,
                confirmedPaymentStatusDto,
                appliedPaymentStatusDto,
                transactionDate,
                hotelDto,
                bankAccountDto,
                clientDto,
                agencyDto,
                attachmentStatusDto,
                invoiceDtoTypeInvoice,
                invoiceDtoTypeCredit,
                employee,
                paymentStatusHistoryList,
                paymentDetailsToCreate,
                closeOperationDto,
                createAttachmentRequests,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList,
                bookinsToUpdate,
                paymentDetailsApplyDepositToCreate,
                paymentsToCreate);

        this.createPaymentToCreditNegative(bankAccountDto,
                paymentSourceDto,
                confirmedPaymentStatusDto,
                appliedPaymentStatusDto,
                transactionDate,
                hotelDto,
                clientDto,
                agencyDto,
                attachmentStatusDto,
                invoiceDtoTypeCredit,
                employee,
                paymentStatusHistoryList,
                paymentDetailsToCreate,
                closeOperationDto,
                createAttachmentRequests,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList,
                bookinsToUpdate,
                paymentsToCreate);

        this.saveChanges(paymentsToCreate,
                paymentDetailsToCreate,
                paymentDetailsApplyDepositToCreate,
                masterPaymentAttachmentDtoList,
                attachmentStatusHistoryDtoList,
                paymentStatusHistoryList,
                bookinsToUpdate);
        return null;
    }

    private void updateAttachmentTypes(List<CreateAttachmentRequest> attachmentRequests,
                                       ResourceTypeDto defaultResourceType,
                                       AttachmentTypeDto attachmentTypeSupport,
                                       AttachmentTypeDto attachmentTypeOtherSupport){
        attachmentRequests.forEach(attachmentRequest -> {
            attachmentRequest.setResourceType(defaultResourceType.getId());
            if (attachmentRequest.isSupport() && this.countDefaultAttachments(attachmentRequests, attachmentTypeSupport.getId()) == 0) {
                attachmentRequest.setAttachmentType(attachmentTypeSupport.getId());
            } else {
                attachmentRequest.setAttachmentType(attachmentTypeOtherSupport.getId());
            }
        });
    }

    private long countDefaultAttachments(List<CreateAttachmentRequest> attachmentRequests, UUID attachmentTypeSupportId){
        return attachmentRequests.stream()
                .filter(createAttachmentRequest -> createAttachmentRequest.getAttachmentType() != null
                        && createAttachmentRequest.getAttachmentType().equals(attachmentTypeSupportId))
                .count();
    }

    //Payment creado con el cash
    private void createPaymentToCreditNegative(ManageBankAccountDto bankAccount,
                                                     ManagePaymentSourceDto paymentSource,
                                                     ManagePaymentStatusDto confirmedPaymentStatus,
                                                     ManagePaymentStatusDto appliedPaymentStatus,
                                                     OffsetDateTime transactionDate,
                                                     ManageHotelDto hotel,
                                                     ManageClientDto client,
                                                     ManageAgencyDto agency,
                                                     ManagePaymentAttachmentStatusDto attachmentStatus,
                                                     ManageInvoiceDto creditInvoice,
                                                     ManageEmployeeDto employee,
                                                     List<PaymentStatusHistoryDto> paymentStatusHistoryList,
                                                     List<PaymentDetailDto> paymentDetailList,
                                                     PaymentCloseOperationDto closeOperation,
                                                     List<CreateAttachmentRequest> createAttachmentRequests,
                                                     List<MasterPaymentAttachmentDto> masterPaymentAttachmentList,
                                                     List<AttachmentStatusHistoryDto> attachmentStatusHistoryList,
                                                     List<ManageBookingDto> bookingList,
                                               List<PaymentDto> paymentsToCreate) {
        ManagePaymentTransactionTypeDto cashPaymentTransactionType = this.getCashPaymentTransactionType();

        Double paymentAmount = creditInvoice.getInvoiceAmount();
        PaymentDto paymentDto = this.createPayment(paymentSource,
                confirmedPaymentStatus,
                transactionDate,
                hotel,
                client,
                agency,
                bankAccount,
                paymentAmount,
                attachmentStatus,
                creditInvoice,
                employee,
                closeOperation,
                createAttachmentRequests,
                masterPaymentAttachmentList,
                attachmentStatusHistoryList,
                paymentStatusHistoryList);

        if (creditInvoice.getBookings() != null) {
            for (ManageBookingDto booking : creditInvoice.getBookings()) {
                //Se crea tipo PAGO cuando viene del pago negativo
                PaymentDetailDto cashPaymentDetail = this.createPaymentDetail(paymentDto,
                        booking.getAmountBalance(),
                        transactionDate,
                        employee,
                        null,
                        cashPaymentTransactionType,
                        appliedPaymentStatus,
                        null,
                        paymentStatusHistoryList
                );
                paymentDetailList.add(cashPaymentDetail);

                //Se aplica el pago
                this.processApplyPaymentDetail(paymentDto,
                        cashPaymentDetail,
                        booking,
                        transactionDate,
                        booking.getAmountBalance());
                bookingList.add(booking);
            }
        }

        paymentsToCreate.add(paymentDto);
    }

    private void createPaymentToCreditPositive(ManagePaymentSourceDto paymentSource,
                                                     ManagePaymentStatusDto confirmedPaymentStatus,
                                                     ManagePaymentStatusDto appliedPaymentStatus,
                                                     OffsetDateTime transactionDate,
                                                     ManageHotelDto hotelDto,
                                                     ManageBankAccountDto bankAccount,
                                                     ManageClientDto client,
                                                     ManageAgencyDto agency,
                                                     ManagePaymentAttachmentStatusDto attachmentStatus,
                                                     ManageInvoiceDto invoiceDtoTypeInvoice,
                                                     ManageInvoiceDto invoiceDtoTypeCredit,
                                                     ManageEmployeeDto employee,
                                                     List<PaymentStatusHistoryDto> paymentStatusHistoryList,
                                                     List<PaymentDetailDto> paymentDetailList,
                                                     PaymentCloseOperationDto closeOperation,
                                                     List<CreateAttachmentRequest> createAttachmentRequests,
                                                     List<MasterPaymentAttachmentDto> masterPaymentAttachmentList,
                                                     List<AttachmentStatusHistoryDto> attachmentStatusHistoryList,
                                                     List<ManageBookingDto> bookingList,
                                                     List<PaymentDetailDto> paymentDetailApplyDepositList,
                                               List<PaymentDto> paymentsToCreate){
        Double paymentAmount = Math.abs(invoiceDtoTypeCredit.getInvoiceAmount());//  * -1;
        ManagePaymentTransactionTypeDto depositPaymentTransactionType = this.getDepositPaymentTransactionType();
        ManagePaymentTransactionTypeDto applyDepositPaymentTransactionType = this.getApplyDepositPaymentTransactionType();

        PaymentDto paymentDto = this.createPayment(paymentSource,
                confirmedPaymentStatus,
                transactionDate,
                hotelDto,
                client,
                agency,
                bankAccount,
                paymentAmount,
                attachmentStatus,
                invoiceDtoTypeCredit.getInvoiceType() == EInvoiceType.OLD_CREDIT ? invoiceDtoTypeCredit : invoiceDtoTypeInvoice,
                employee,
                closeOperation,
                createAttachmentRequests,
                masterPaymentAttachmentList,
                attachmentStatusHistoryList,
                paymentStatusHistoryList);

        //Se crea el deposito
        PaymentDetailDto depositPaymentDetail = this.createPaymentDetail(paymentDto,
                paymentAmount,
                transactionDate,
                employee,
                null,
                depositPaymentTransactionType,
                appliedPaymentStatus,
                null,
                paymentStatusHistoryList
        );
        paymentDetailList.add(depositPaymentDetail);

        if (!hotelDto.getNoAutoApplyCredit() && invoiceDtoTypeCredit.getInvoiceType() != EInvoiceType.OLD_CREDIT) {
            for (ManageBookingDto creditBooking : invoiceDtoTypeCredit.getBookings()) {
                if(creditBooking.getParent().getAmountBalance() > 0){
                    //Crear un AANT por cada booking para el deposito
                    Double amountToApply = Math.min(creditBooking.getParent().getAmountBalance(), Math.abs(creditBooking.getAmountBalance()));
                    PaymentDetailDto applyDepositPaymentDetail = this.createPaymentDetail(paymentDto,
                            amountToApply,
                            transactionDate,
                            employee,
                            null,
                            applyDepositPaymentTransactionType,
                            appliedPaymentStatus,
                            depositPaymentDetail,
                            paymentStatusHistoryList);
                    paymentDetailApplyDepositList.add(applyDepositPaymentDetail);

                    //Se aplica al AANT
                    this.processApplyPaymentDetail(paymentDto,
                            applyDepositPaymentDetail,
                            creditBooking.getParent(),
                            transactionDate,
                            amountToApply);
                    bookingList.add(creditBooking.getParent());
                }
            }
        }

        paymentsToCreate.add(paymentDto);
    }

    private PaymentDto createPayment(ManagePaymentSourceDto paymentSource,
                                     ManagePaymentStatusDto confirmedPaymentStatus,
                                     OffsetDateTime transactionDate,
                                     ManageHotelDto hotel,
                                     ManageClientDto client,
                                     ManageAgencyDto agency,
                                     ManageBankAccountDto bankAccount,
                                     Double paymentAmount,
                                     ManagePaymentAttachmentStatusDto attachmentStatus,
                                     ManageInvoiceDto invoice,
                                     ManageEmployeeDto employee,
                                     PaymentCloseOperationDto closeOperation,
                                     List<CreateAttachmentRequest> createAttachmentRequests,
                                     List<MasterPaymentAttachmentDto> masterPaymentAttachmentList,
                                     List<AttachmentStatusHistoryDto> attachmentStatusHistoryList,
                                     List<PaymentStatusHistoryDto> paymentStatusHistoryList){
        String remark = this.getRemark(invoice);
        String reference = invoice.getInvoiceNumber();
        List<CreateAttachment> createAttachmentList = this.getCreateAttachments(createAttachmentRequests);
        ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.managePaymentAttachmentStatusService.findBySupported();
        ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.managePaymentAttachmentStatusService.findByOtherSupported();
        PaymentStatusHistoryDto paymentStatusHistory = new PaymentStatusHistoryDto();

        ProcessCreatePayment processCreatePayment = new ProcessCreatePayment(paymentSource,
                confirmedPaymentStatus,
                transactionDate,
                client,
                agency,
                hotel,
                bankAccount,
                attachmentStatus,
                paymentAmount,
                remark,
                reference,
                bankAccount == null,
                employee,
                closeOperation,
                createAttachmentList,
                attachmentStatusSupport,
                attachmentOtherSupport,
                masterPaymentAttachmentList,
                attachmentStatusHistoryList,
                paymentStatusHistory,
                ImportType.AUTOMATIC,
                true
        );
        PaymentDto payment = processCreatePayment.create();
        paymentStatusHistoryList.add(paymentStatusHistory);

        return payment;
    }

    private PaymentDetailDto createPaymentDetail(PaymentDto payment,
                                                 Double amountToApply,
                                                 OffsetDateTime transactionDate,
                                                 ManageEmployeeDto employee,
                                                 String remark,
                                                 ManagePaymentTransactionTypeDto managePaymentTransactionType,
                                                 ManagePaymentStatusDto appliedPaymentStatus,
                                                 PaymentDetailDto parentPaymentDetail,
                                                 List<PaymentStatusHistoryDto> paymentStatusHistoryList){
        ProcessCreatePaymentDetail processCreatePaymentDetail = new ProcessCreatePaymentDetail(payment,
                amountToApply,
                transactionDate,
                employee,
                remark,
                managePaymentTransactionType,
                appliedPaymentStatus,
                parentPaymentDetail
        );
        processCreatePaymentDetail.process();
        PaymentDetailDto paymentDetail = processCreatePaymentDetail.getDetail();

        if(processCreatePaymentDetail.isPaymentApplied()){
            PaymentStatusHistoryDto paymentStatusHistoryDto = processCreatePaymentDetail.getPaymentStatusHistory();
            paymentStatusHistoryList.add(paymentStatusHistoryDto);
        }

        return paymentDetail;
    }

    private void processApplyPaymentDetail(PaymentDto payment,
                                           PaymentDetailDto paymentDetail,
                                           ManageBookingDto booking,
                                           OffsetDateTime transactionDate,
                                           Double amountToApply){
        ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                paymentDetail,
                booking,
                transactionDate,
                amountToApply);
        processApplyPaymentDetail.process();
    }

    private List<CreateAttachment> getCreateAttachments(List<CreateAttachmentRequest> createAttachmentRequests){
        Set<UUID> resourceTypeIds = new HashSet<>();
        Set<UUID> attachmentTypeIds = new HashSet<>();

        for(CreateAttachmentRequest attachmentRequest : createAttachmentRequests){
            resourceTypeIds.add(attachmentRequest.getResourceType());
            attachmentTypeIds.add(attachmentRequest.getAttachmentType());
        }

        Map<UUID, AttachmentTypeDto> attachmentTypeMap = this.getAttachmentTypeMap(new ArrayList<>(attachmentTypeIds));
        Map<UUID, ResourceTypeDto> resourceTypeMap = this.getResourceTypeMap(new ArrayList<>(resourceTypeIds));

        return createAttachmentRequests.stream()
                .map(attachmentRequest -> {
                    return this.convertCreateAttachementRequestToCreateAttachment(attachmentRequest,
                            attachmentTypeMap.get(attachmentRequest.getAttachmentType()),
                            resourceTypeMap.get(attachmentRequest.getResourceType()));
                })
                .collect(Collectors.toList());
    }

    private CreateAttachment convertCreateAttachementRequestToCreateAttachment(CreateAttachmentRequest request, AttachmentTypeDto attachmentType, ResourceTypeDto resourceType){
        return new CreateAttachment(attachmentType,
                resourceType,
                request.getFileName(),
                request.getFileWeight(),
                request.getPath(),
                request.getRemark(),
                request.isSupport());
    }

    private ManageHotelDto getHotelFromCredit(UUID hotelId){
        return this.hotelService.findById(hotelId);
    }

    private ManageBankAccountDto getBankAccountFromHotel(ManageHotelDto hotel){
        List<ManageBankAccountDto> bankAccounts = this.manageBankAccountService.findAllByHotel(hotel.getId());
        return bankAccounts.isEmpty() ? null : bankAccounts.get(0);
    }

    private ManagePaymentSourceDto getExpensePaymentSource(){
        return this.sourceService.findByExpense();
    }

    private ManagePaymentStatusDto getConfirmedPaymentStatus(){
        return this.statusService.findByConfirmed();
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

    private OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperation) {
        if (DateUtil.getDateForCloseOperation(closeOperation.getBeginDate(), closeOperation.getEndDate())) {
            return OffsetDateTime.now();
        }
        ZoneId localZone = ZoneId.systemDefault();
        return closeOperation.getEndDate().atTime(LocalTime.now()).atZone(localZone).toOffsetDateTime();

    }

    private String deleteHotelInfo(String input) {
        if(Objects.nonNull(input)){
            return input.replaceAll("-(.*?)-", "-");
        }
        return "";
    }

    private String getRemark(ManageInvoiceDto invoice){
        return "Created automatic to apply credit ( " + deleteHotelInfo(invoice.getInvoiceNumber()) + ")";
    }

    private ManagePaymentTransactionTypeDto getCashPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByCash();
    }

    private ManagePaymentTransactionTypeDto getDepositPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByDeposit();
    }

    private ManagePaymentTransactionTypeDto getApplyDepositPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByApplyDeposit();
    }

    private PaymentCloseOperationDto getCloseOperacion(UUID hotelId){
        return this.paymentCloseOperationService.findByHotelId(hotelId);
    }

    private Map<UUID, ResourceTypeDto> getResourceTypeMap(List<UUID> ids){
        return this.manageResourceTypeService.findAllByIds(ids).stream()
                .collect(Collectors.toMap(ResourceTypeDto::getId, resourceTypeDto -> resourceTypeDto));
    }

    private Map<UUID, AttachmentTypeDto> getAttachmentTypeMap(List<UUID> ids){
        return this.manageAttachmentTypeService.findAllById(ids).stream()
                .collect(Collectors.toMap(AttachmentTypeDto::getId, attachmentTypeDto -> attachmentTypeDto));
    }

    private void saveChanges(List<PaymentDto> payments,
                             List<PaymentDetailDto> paymentDetails,
                             List<PaymentDetailDto> paymentDetailsApplyDepositToCreate,
                             List<MasterPaymentAttachmentDto> masterPaymentAttachments,
                             List<AttachmentStatusHistoryDto> attachmentStatusHistoryList,
                             List<PaymentStatusHistoryDto> paymentStatusHistories,
                             List<ManageBookingDto> bookingList){
        this.paymentService.createBulk(payments);
        this.paymentDetailService.createAll(paymentDetailsApplyDepositToCreate);
        List<PaymentDetailDto> newDepositPaymentDetails = this.paymentDetailService.createAll(paymentDetails);
        this.setPaymentDetails(paymentDetailsApplyDepositToCreate, newDepositPaymentDetails);
        this.paymentDetailService.bulk(paymentDetailsApplyDepositToCreate);
        this.masterPaymentAttachmentService.create(masterPaymentAttachments);
        this.assignAttachmentIdsToAttachmentStatusHistory(masterPaymentAttachments, attachmentStatusHistoryList);
        this.attachmentStatusHistoryService.create(attachmentStatusHistoryList);
        this.paymentStatusHistoryService.createAll(paymentStatusHistories);
        this.manageBookingService.updateAllBooking(bookingList);
    }

    private void setPaymentDetails(List<PaymentDetailDto> applyDepositPaymentDetail, List<PaymentDetailDto> depositPaymentDetail){
        applyDepositPaymentDetail.forEach(applyDepositDetail ->  {
            PaymentDetailDto parentPaymentDetail = getChildPaymentDetail(depositPaymentDetail, applyDepositDetail.getId());
            if(Objects.nonNull(parentPaymentDetail)){
                applyDepositDetail.setParentId(parentPaymentDetail.getPaymentDetailId());
            }
        });
    }

    private PaymentDetailDto getChildPaymentDetail(List<PaymentDetailDto> depositPaymentDetail, UUID toFindPaymentDetail){
        for(PaymentDetailDto paymentDetail : depositPaymentDetail){
            if(paymentDetail.getPaymentDetails().stream().anyMatch(childDetail -> childDetail.getId().equals(toFindPaymentDetail))){
                return paymentDetail;
            }
        }

        return null;
    }

    private void assignAttachmentIdsToAttachmentStatusHistory(List<MasterPaymentAttachmentDto> masterPaymentAttachments, List<AttachmentStatusHistoryDto> attachmentStatusHistoryList){
        Map<String, Queue<Long>> attachmentIdQueueMap = masterPaymentAttachments.stream()
                .collect(Collectors.groupingBy(
                        MasterPaymentAttachmentDto::getFileName,
                        Collectors.mapping(MasterPaymentAttachmentDto::getAttachmentId,
                                Collectors.toCollection(LinkedList::new))
                ));

        attachmentStatusHistoryList.forEach(attachmentStatusHistoryDto -> {
            String fileName = getFileNameFromDescription(attachmentStatusHistoryDto.getDescription());
            Queue<Long> idQueue = attachmentIdQueueMap.get(fileName);

            if (idQueue != null && !idQueue.isEmpty()) {
                Long attachmentId = idQueue.poll(); // Saca y remueve el primero
                attachmentStatusHistoryDto.setAttachmentId(attachmentId);
            }
        });
    }

    private String getFileNameFromDescription(String description){
        String keyword = "The file name: ";
        int startIndex = description.indexOf(keyword) + keyword.length();
        return description.substring(startIndex);
    }
}
