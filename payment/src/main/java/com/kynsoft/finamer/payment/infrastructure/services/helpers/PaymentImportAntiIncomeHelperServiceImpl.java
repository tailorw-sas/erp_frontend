package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.http.entity.income.ajustment.CreateIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.ajustment.NewIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.applyDeposit.CreatePaymentDetailApplyDepositFromFileCommand;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.paymentDetails.PaymentDetailSimple;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateAdjustmentHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateIncomeHttpService;
import com.kynsoft.finamer.payment.infrastructure.utils.PaymentUploadAttachmentUtil;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class PaymentImportAntiIncomeHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentAntiValidatorFactory paymentAntiValidatorFactory;
    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    private final PaymentImportAntiErrorRepository antiErrorRepository;
    private final PaymentImportErrorRepository paymentImportErrorRepository;

    private final IPaymentService paymentService;
    private final IManageInvoiceStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    private final CreateIncomeHttpService createIncomeHttpService;
    private final CreateAdjustmentHttpService createAdjustmentHttpService;
    private final IManageInvoiceService manageInvoiceService;

    private final ServiceLocator<IMediator> serviceLocator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PaymentUploadAttachmentUtil paymentUploadAttachmentUtil;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    public PaymentImportAntiIncomeHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
            PaymentAntiValidatorFactory paymentAntiValidatorFactory,
            RedisTemplate<String, String> redisTemplate,
            IPaymentDetailService paymentDetailService,
            IManagePaymentTransactionTypeService transactionTypeService,
            PaymentImportAntiErrorRepository antiErrorRepository,
            PaymentImportErrorRepository paymentImportErrorRepository,
            IPaymentService paymentService,
            IManageInvoiceStatusService statusService,
            IManageEmployeeService manageEmployeeService,
            CreateIncomeHttpService createIncomeHttpService,
            CreateAdjustmentHttpService createAdjustmentHttpService,
            IManageInvoiceService manageInvoiceService,
            ServiceLocator<IMediator> serviceLocator,
            ApplicationEventPublisher applicationEventPublisher,
            PaymentUploadAttachmentUtil paymentUploadAttachmentUtil) {
        super(redisTemplate);
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentAntiValidatorFactory = paymentAntiValidatorFactory;
        this.paymentDetailService = paymentDetailService;
        this.transactionTypeService = transactionTypeService;
        this.antiErrorRepository = antiErrorRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.paymentService = paymentService;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
        this.createIncomeHttpService = createIncomeHttpService;
        this.createAdjustmentHttpService = createAdjustmentHttpService;
        this.manageInvoiceService = manageInvoiceService;
        this.serviceLocator = serviceLocator;
        this.applicationEventPublisher = applicationEventPublisher;
        this.paymentUploadAttachmentUtil = paymentUploadAttachmentUtil;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        System.err.println("|||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("|||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("Comienza a leer el excel: " + LocalTime.now());
        System.err.println("|||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("|||||||||||||||||||||||||||||||||||||||||||||||||||");
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        paymentAntiValidatorFactory.createValidators();
        ExcelBeanReader<AntiToIncomeRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, AntiToIncomeRow.class);
        ExcelBean<AntiToIncomeRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (AntiToIncomeRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            if (paymentAntiValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
                totalProcessRow++;
            }
        }
        System.err.println("Termina de leer el excel: " + LocalTime.now());
    }

    public void createAttachment(PaymentImportDetailRequest request) {
        UUID paymentsId = this.getPaymentIdStoreFromCache(request.getImportProcessId());
        if (Objects.nonNull(paymentsId) && Objects.nonNull(request.getAttachment())) {
            this.sentToCreateAttachment(paymentsId, request);
        }
    }

    private void sentToCreateAttachment(UUID paymentsId, PaymentImportDetailRequest request) {
        CreateAttachmentEvent createAttachmentEvent = new CreateAttachmentEvent(this, paymentsId, request.getAttachment(),
                UUID.fromString(request.getEmployeeId()),
                request.getAttachmentFileName(),
                String.valueOf(request.getFile().length));
        applicationEventPublisher.publishEvent(createAttachmentEvent);
    }

    @Override
    public void cachingPaymentImport(Row paymentRow) {
        PaymentImportCache paymentImportCache = PaymentCacheFactory.getPaymentImportCache(paymentRow);
        paymentImportCacheRepository.save(paymentImportCache);
    }

    @Override
    public void clearPaymentImportCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList;
        do {
            cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
            paymentImportCacheRepository.deleteAll(cacheList.getContent());
            pageable = pageable.next();
        } while (cacheList.hasNext());
    }

    @Override
    public void readPaymentCacheAndSave(Object rawRequest) {
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "rowNumber"));
        Page<PaymentImportCache> cacheList;
        if (!paymentImportErrorRepository.existsPaymentImportErrorByImportProcessId(request.getImportProcessId())) {
            /*todo: subir el attachment y enviar esa misma info para los income a crear,
             * sustituir el envío del byte[] por esa info
             */
            String attachment = "";
            try {
                LinkedHashMap<String, String> response = paymentUploadAttachmentUtil.uploadAttachmentContent("detail.pdf", request.getAttachment());
                attachment = response.get("url");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(UUID.fromString(request.getEmployeeId()));
            ManagePaymentTransactionTypeDto transactionTypeDto = this.transactionTypeService.findByApplyDepositAndDefaults();
            do {
                cacheList = paymentImportCacheRepository.findAllByImportProcessId(request.getImportProcessId(), pageable);
                String finalAttachment = attachment;
                cacheList.forEach(paymentImportCache -> {
                    //PaymentDetailDto parentPaymentDetail = paymentDetailService.findByGenId(Integer.parseInt(paymentImportCache.getTransactionId()));
                    //PaymentDetailSimpleDto paymentDetailDto = this.paymentDetailService.findSimpleDetailByGenId(Integer.parseInt(paymentImportCache.getTransactionId()));

                    PaymentDetailSimple paymentDetailDto = this.paymentDetailService.findPaymentDetailsSimpleCacheableByGenId(Integer.parseInt(paymentImportCache.getTransactionId()));

                    String remark = paymentImportCache.getRemarks() == null ? transactionTypeDto.getDefaultRemark() : paymentImportCache.getRemarks();
                    this.sendToCreateApplyDeposit(paymentDetailDto.getId(),
                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                            UUID.fromString(request.getEmployeeId()),
                            transactionTypeDto.getId(),
                            //getDefaultApplyDepositTransactionTypeId(),
                            UUID.fromString(request.getInvoiceTransactionTypeId()),
                            //paymentImportCache.getRemarks(), 
                            remark,
                            finalAttachment,
                            employeeDto,
                            transactionTypeDto
                    );

                });
                pageable = pageable.next();
            } while (cacheList.hasNext());
        }
        System.err.println("Termina a leer la cache: " + LocalTime.now());
    }

    private void sendToCreateApplyDeposit(UUID paymentDetail, double amount, UUID employee, UUID transactionType,
            UUID transactionTypeIdForAdjustment, String remarks, String attachment, ManageEmployeeDto employeeDto,
            ManagePaymentTransactionTypeDto transactionTypeDto) {
//        if (remarks == null || remarks.isEmpty()) {
//            remarks = this.transactionTypeService.findByApplyDeposit().getDefaultRemark();
//        }
        CreatePaymentDetailApplyDepositFromFileCommand createPaymentDetailApplyDepositCommand
                = new CreatePaymentDetailApplyDepositFromFileCommand(Status.ACTIVE,
                        paymentDetail,
                        transactionType,
                        amount,
                        remarks,
                        employee,
                        transactionTypeIdForAdjustment);
        createPaymentDetailApplyDepositCommand.setAttachment(attachment);
        this.createDetailsAndIncome(createPaymentDetailApplyDepositCommand, employeeDto, transactionTypeDto);
//        ApplyDepositEvent applyDepositEvent = new ApplyDepositEvent(createPaymentDetailApplyDepositCommand, true);
//        applicationEventPublisher.publishEvent(applyDepositEvent);

    }

    public void createDetailsAndIncome(CreatePaymentDetailApplyDepositFromFileCommand command, ManageEmployeeDto employeeDto, ManagePaymentTransactionTypeDto paymentTransactionTypeDto) {

//        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());
//        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.transactionTypeService.findById(command.getTransactionType());

        //Se obtiene el details tipo DEPOSIT al que se le creara un apply deposit
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setApplied, paymentUpdate.getApplied() + command.getAmount(), updatePayment::setUpdate);
        //UpdateIfNotNull.updateDouble(paymentUpdate::setNotApplied, paymentUpdate.getNotApplied() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        OffsetDateTime transactionDate = OffsetDateTime.now(ZoneId.of("UTC"));

        //Se crea el Apply Deposit.
        PaymentDetailDto children = new PaymentDetailDto();
        children.setId(command.getId());
        children.setStatus(command.getStatus());
        children.setPayment(paymentUpdate);
        children.setTransactionType(paymentTransactionTypeDto);
        children.setAmount(command.getAmount());
        children.setRemark(command.getRemark());
        children.setTransactionDate(transactionDate);

        //Se asigna el padre.
        children.setParentId(paymentDetailDto.getPaymentDetailId());

        //Se crea el Details.
        this.paymentDetailService.create(children);

        //Agregando los Apply Deposit.
        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(paymentDetailDto.getChildren());
        updateChildrens.add(children);
        paymentDetailDto.setChildren(updateChildrens);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());

        //Actualizando el Deposit
        paymentDetailService.update(paymentDetailDto);

        //Actualizando el Payment.
        this.paymentService.update(paymentUpdate);

        //Se obtiene el invoice Status que se le dara al income
        ManageInvoiceStatusDto manageInvoiceStatusDto = statusService.findByCode(RELATE_INCOME_STATUS_CODE);

        //Se envia a crear el income.
        CreateIncomeFromPaymentMessage msg = this.createIncomeHttpService.sendCreateIncomeRequest(sendToCreateRelatedIncome(children, employeeDto, command.getTransactionTypeForAdjustment(), manageInvoiceStatusDto.getId(), command.getAttachment()));

        //Se crea el ajuste para el income anterior.
        String response = this.createAdjustmentHttpService.sendCreateIncomeRequest(this.createAdjustmentRequest(children, employeeDto.getId(), command.getTransactionTypeForAdjustment(), msg.getId()));

        //Como la BD no se ha actualizado, se espera 1 segundo.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
        }

        //Se realiza la aplicacion de pago.
        ManageInvoiceDto invoice = manageInvoiceService.findById(msg.getId());
        ApplyPaymentDetailCommand applyPaymentDetailCommand = new ApplyPaymentDetailCommand(children.getId(),
                invoice.getBookings().get(0).getId(), employeeDto.getId());
        serviceLocator.getBean(IMediator.class).send(applyPaymentDetailCommand);
//        this.sendToCreateRelatedIncome(children,employeeDto.getId(),employeeDto.getFirstName(), command.getTransactionTypeForAdjustment(),manageInvoiceStatusDto.getId(), command.getAttachment());
    }

    private CreateIncomeAdjustmentRequest createAdjustmentRequest(PaymentDetailDto paymentDetailDto, UUID employeeId, UUID transactionType, UUID income) {
        CreateIncomeAdjustmentRequest request = new CreateIncomeAdjustmentRequest();
        request.setEmployee(employeeId.toString());
        request.setIncome(income);
        request.setStatus("ACTIVE");

        NewIncomeAdjustmentRequest newIncomeAdjustmentRequest = new NewIncomeAdjustmentRequest();
        newIncomeAdjustmentRequest.setTransactionType(transactionType);
        newIncomeAdjustmentRequest.setDate(LocalDate.now().toString());
        newIncomeAdjustmentRequest.setRemark(paymentDetailDto.getRemark());
        newIncomeAdjustmentRequest.setAmount(paymentDetailDto.getAmount());

        request.setAdjustments(List.of(newIncomeAdjustmentRequest));

        return request;
    }

    private CreateAntiToIncomeRequest sendToCreateRelatedIncome(PaymentDetailDto paymentDetailDto, ManageEmployeeDto employeeDto, UUID transactionType, UUID status, String attachment) {
        CreateAntiToIncomeRequest income = new CreateAntiToIncomeRequest();
        income.setInvoiceDate(LocalDateTime.now().toString());
        income.setManual(Boolean.FALSE);
        income.setAgency(paymentDetailDto.getPayment().getAgency().getId());
        income.setHotel(paymentDetailDto.getPayment().getHotel().getId());
        income.setInvoiceType(UUID.randomUUID());
        income.setInvoiceStatus(status);
        income.setIncomeAmount(paymentDetailDto.getAmount());
        income.setStatus("ACTIVE");
        income.setInvoiceNumber(0L);
        income.setDueDate(LocalDate.now().toString());
        income.setReSend(Boolean.FALSE);
        income.setReSendDate(LocalDate.now().toString());
        income.setEmployee(employeeDto.getId().toString());
        income.setAttachments(List.of(this.attachment(attachment, employeeDto)));
        return income;
    }

    private CreateAntiToIncomeAttachmentRequest attachment(String attachment, ManageEmployeeDto employeeDto) {
        return new CreateAntiToIncomeAttachmentRequest(attachment, "" + employeeDto.getFirstName() + " " + employeeDto.getLastName(), employeeDto.getId());
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentAntiRowError> page = antiErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(
                page.getContent().stream().sorted(Comparator.comparingInt(PaymentAntiRowError::getRowNumber)).collect(Collectors.toList()),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber()
        );
    }

    public UUID getPaymentIdStoreFromCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
        Optional<PaymentImportCache> paymentImportCache = cacheList.stream().findFirst();
        return paymentImportCache.map(importCache -> {
//            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(Integer.parseInt(importCache.getTransactionId()));
//            return paymentDetailDto.getPayment().getId();
            PaymentDetailSimpleDto paymentDetailDto = paymentDetailService.findSimpleDetailByGenId(Integer.parseInt(importCache.getTransactionId()));
            return paymentDetailDto.getPaymentId();
        }).orElse(null);

    }

    private UUID getDefaultApplyDepositTransactionTypeId() {
        FilterCriteria markAsDefault = new FilterCriteria();
        markAsDefault.setKey("defaults");
        markAsDefault.setValue(true);
        markAsDefault.setOperator(SearchOperation.EQUALS);
        markAsDefault.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria applyDeposit = new FilterCriteria();
        applyDeposit.setKey("applyDeposit");
        applyDeposit.setValue(true);
        applyDeposit.setOperator(SearchOperation.EQUALS);
        applyDeposit.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);
        PaginatedResponse response = transactionTypeService.search(Pageable.unpaged(), List.of(markAsDefault, applyDeposit, statusActive));
        Assert.notEmpty(response.getData(), "There is not  default apply deposit transaction type");
        ManagePaymentTransactionTypeResponse response1 = (ManagePaymentTransactionTypeResponse) response.getData().get(0);
        return response1.getId();
    }

}
