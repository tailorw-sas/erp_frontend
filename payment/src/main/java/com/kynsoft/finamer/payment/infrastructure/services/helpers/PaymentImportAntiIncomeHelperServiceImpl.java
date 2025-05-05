package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.http.entity.income.ajustment.CreateIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.ajustment.NewIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.helper.DetailAndIncomeHelper;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentStatus;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageBookingWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentDetailWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.command.PaymentWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageBookingReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageInvoiceReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateAdjustmentHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateIncomeHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import com.kynsoft.finamer.payment.infrastructure.utils.PaymentUploadAttachmentUtil;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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

    private final IManageInvoiceStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    private final CreateIncomeHttpService createIncomeHttpService;
    private final CreateAdjustmentHttpService createAdjustmentHttpService;

    private final ManagePaymentDetailWriteDataJPARepository repositoryPaymentDetailsCommand;
    private final PaymentWriteDataJPARepository repositoryPaymentCommand;
    private final ManageInvoiceReadDataJPARepository repositoryInvoiceReadDataJPARepository;
    private final ManageBookingReadDataJPARepository repositoryBookingReadDataJPARepository;
    private final ManageBookingWriteDataJPARepository repositoryBookingWriteDataJPARepository;

    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManagePaymentStatusService paymentStatusService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final PaymentUploadAttachmentUtil paymentUploadAttachmentUtil;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    private List<PaymentDetail> details;
    private List<PaymentDetail> newDetails;
    private List<Payment> uniquePayments;
    private List<Booking> bookins;
    private String attachment;

    public PaymentImportAntiIncomeHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
            PaymentAntiValidatorFactory paymentAntiValidatorFactory,
            IPaymentDetailService paymentDetailService,
            IManagePaymentTransactionTypeService transactionTypeService,
            PaymentImportAntiErrorRepository antiErrorRepository,
            PaymentImportErrorRepository paymentImportErrorRepository,
            IManageInvoiceStatusService statusService,
            IManageEmployeeService manageEmployeeService,
            CreateIncomeHttpService createIncomeHttpService,
            CreateAdjustmentHttpService createAdjustmentHttpService,
            ManagePaymentDetailWriteDataJPARepository repositoryPaymentDetailsCommand,
            PaymentWriteDataJPARepository repositoryPaymentCommand,
            IPaymentCloseOperationService paymentCloseOperationService,
            ProducerUpdateBookingService producerUpdateBookingService,
            IManagePaymentStatusService paymentStatusService,
            ManageInvoiceReadDataJPARepository repositoryInvoiceReadDataJPARepository,
            ManageBookingReadDataJPARepository repositoryBookingReadDataJPARepository,
            ManageBookingWriteDataJPARepository repositoryBookingWriteDataJPARepository,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            PaymentUploadAttachmentUtil paymentUploadAttachmentUtil,
            ApplicationEventPublisher applicationEventPublisher,
            ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.details = new ArrayList<>();
        this.newDetails = new ArrayList<>();
        this.uniquePayments = new ArrayList<>();
        this.bookins = new ArrayList<>();
        this.attachment = "";
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentAntiValidatorFactory = paymentAntiValidatorFactory;
        this.paymentDetailService = paymentDetailService;
        this.transactionTypeService = transactionTypeService;
        this.antiErrorRepository = antiErrorRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
        this.createIncomeHttpService = createIncomeHttpService;
        this.createAdjustmentHttpService = createAdjustmentHttpService;
        this.repositoryPaymentDetailsCommand = repositoryPaymentDetailsCommand;
        this.repositoryPaymentCommand = repositoryPaymentCommand;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.paymentStatusService = paymentStatusService;
        this.repositoryInvoiceReadDataJPARepository = repositoryInvoiceReadDataJPARepository;
        this.repositoryBookingReadDataJPARepository = repositoryBookingReadDataJPARepository;
        this.repositoryBookingWriteDataJPARepository = repositoryBookingWriteDataJPARepository;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentUploadAttachmentUtil = paymentUploadAttachmentUtil;
        this.applicationEventPublisher = applicationEventPublisher;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        List<UUID> agencys = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(request.getEmployeeId()));
        List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(request.getEmployeeId()));

        paymentAntiValidatorFactory.createValidators();
        ExcelBeanReader<AntiToIncomeRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, AntiToIncomeRow.class);
        ExcelBean<AntiToIncomeRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (AntiToIncomeRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            row.setAgencys(agencys);
            row.setHotels(hotels);
            if (paymentAntiValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
                totalProcessRow++;
            }
        }
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
        Pageable pageable = PageRequest.of(0, 5000, Sort.by(Sort.Direction.ASC, "rowNumber"));
        Page<PaymentImportCache> cacheList;
        if (!paymentImportErrorRepository.existsPaymentImportErrorByImportProcessId(request.getImportProcessId())) {
            /*todo: subir el attachment y enviar esa misma info para los income a crear,
             * sustituir el envío del byte[] por esa info
             */
//            String attachment = "";
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

                List<Long> listaIds = cacheList.stream().map(obj -> Long.valueOf(obj.getTransactionId())).collect(Collectors.toList());
                this.details = this.paymentDetailService.findByPaymentDetailsIdIn(listaIds);
                this.uniquePayments = details.stream().map(PaymentDetail::getPayment).distinct().collect(Collectors.toList());

                System.err.println("Tamannno de la lista: " + details.size());
                String finalAttachment = attachment;
                cacheList.forEach(paymentImportCache -> {
                    Optional<PaymentDetail> foundDetail = details.stream().filter(detail -> detail.getPaymentDetailId().equals(Long.valueOf(paymentImportCache.getTransactionId()))).findFirst();
                    double amount = Double.parseDouble(paymentImportCache.getPaymentAmount());
                    if (foundDetail.get().getApplyDepositValue() - amount < 0) {
                        this.totalProcessRow--;
                        return;
                    }
                    String remark = paymentImportCache.getRemarks() == null ? transactionTypeDto.getDefaultRemark() : paymentImportCache.getRemarks();
                    this.createDetailsAndIncome(employeeDto, transactionTypeDto, foundDetail.get(), amount, remark, attachment);
                });
                pageable = pageable.next();
            } while (cacheList.hasNext());
            this.repositoryPaymentDetailsCommand.saveAll(newDetails);
            this.repositoryPaymentDetailsCommand.saveAll(details);
            this.repositoryPaymentCommand.saveAll(uniquePayments);

            //Se obtiene el invoice Status que se le dara al income
            ManageInvoiceStatusDto manageInvoiceStatusDto = statusService.findByCode(RELATE_INCOME_STATUS_CODE);
            System.err.println("Comenzando a crear los income: " + LocalTime.now());
            List<DetailAndIncomeHelper> incomes = new ArrayList<>();
            List<UUID> incoList = new ArrayList<>();
            for (PaymentDetail newDetail : this.newDetails) {
                CreateIncomeFromPaymentMessage msg = this.createIncomeHttpService.sendCreateIncomeRequest(sendToCreateRelatedIncome(newDetail, employeeDto, transactionTypeDto.getId(), manageInvoiceStatusDto.getId(), attachment));
                incomes.add(new DetailAndIncomeHelper(msg.getId(), newDetail.getId()));
                incoList.add(msg.getId());
                this.createAdjustmentHttpService.sendCreateIncomeRequest(this.createAdjustmentRequest(newDetail, employeeDto.getId(), UUID.fromString(request.getInvoiceTransactionTypeId()), msg.getId()));
            }
            System.err.println("Termina de crear los income: " + LocalTime.now());
            this.applyPayment(employeeDto, incomes, incoList);
        }
        System.err.println("Termina a leer la cache: " + LocalTime.now());
    }

    public void applyPayment(ManageEmployeeDto employeeDto, List<DetailAndIncomeHelper> incomes, List<UUID> incoList) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
        }
        List<Invoice> invoices = this.repositoryInvoiceReadDataJPARepository.findByIdIn(incoList);
        List<Long> bookingIds = invoices.stream().map(obj -> obj.getBookings().get(0).getBookingId()).collect(Collectors.toList());
        this.bookins = this.repositoryBookingReadDataJPARepository.findByBookingIdIn(bookingIds);

        for (DetailAndIncomeHelper income : incomes) {
            PaymentDetail detail = this.newDetails.stream().filter(d -> d.getId().equals(income.getPaymentDetailId())).findFirst().get();
            Booking booking = bookins.stream().filter(b -> b.getInvoice().getId().equals(income.getIncomeId())).findFirst().get();
            booking.setAmountBalance(booking.getAmountBalance() - detail.getAmount());
            this.updateBooking(booking);

            detail.setManageBooking(booking);
            detail.setApplyPayment(Boolean.TRUE);
            detail.setTransactionDate(transactionDate(detail.getPayment().getHotel().getId()));
            this.updatePaymentNewDetails(detail);

            Payment paymentUpdate = uniquePayments.stream().filter(payment -> payment.getId().equals(detail.getPayment().getId())).findFirst().get();
            if (paymentUpdate.getPaymentBalance() == 0 && paymentUpdate.getDepositBalance() == 0) {
                paymentUpdate.setPaymentStatus(new ManagePaymentStatus(this.paymentStatusService.findByAppliedCacheable()));
                this.createPaymentAttachmentStatusHistory(employeeDto, paymentUpdate.toAggregate());
            }
            paymentUpdate.setApplyPayment(true);
            this.updatePayment(paymentUpdate);
        }
        this.repositoryBookingWriteDataJPARepository.saveAll(bookins);
        this.repositoryPaymentDetailsCommand.saveAll(newDetails);
        this.repositoryPaymentCommand.saveAll(uniquePayments);
        System.err.println("Termina de aplicar Pago!!!! " + LocalTime.now());

        this.newDetails.stream()
                .forEach(detail -> {
                    try {
                        ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                                detail.getPayment().getId(),
                                detail.getPayment().getPaymentId(),
                                new ReplicatePaymentDetailsKafka(detail.getId(), detail.getPaymentDetailId())
                        );
                        ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(detail.getManageBooking().getId(),
                                detail.getManageBooking().getAmountBalance(),
                                paymentKafka,
                                false,
                                OffsetDateTime.now());
                        producerUpdateBookingService.update(
                                new UpdateBookingBalanceKafka(
                                        List.of(replicateBookingKafka)
                                )
                        );
                    } catch (Exception e) {
                        System.err.println("Error al enviar el evento de integracion: " + e.getMessage());
                    }
                });
        System.err.println("Culmina enviando a invoice: " + LocalTime.now());
    }
    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIdsCacheable(hotel);
        //PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    public void createDetailsAndIncome(ManageEmployeeDto employeeDto, ManagePaymentTransactionTypeDto paymentTransactionTypeDto,
            PaymentDetail paymentDetailDto, double amount, String remarks, String attachment) {

        Payment paymentUpdate = uniquePayments.stream().filter(payment -> payment.getId().equals(paymentDetailDto.getPayment().getId())).findFirst().get();

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - amount, updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setApplied, paymentUpdate.getApplied() + amount, updatePayment::setUpdate);
        //UpdateIfNotNull.updateDouble(paymentUpdate::setNotApplied, paymentUpdate.getNotApplied() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + amount, updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        OffsetDateTime transactionDate = OffsetDateTime.now(ZoneId.of("UTC"));

        //Se crea el Apply Deposit.
        PaymentDetail children = new PaymentDetail();
        children.setId(UUID.randomUUID());
        children.setStatus(Status.ACTIVE);
        children.setPayment(paymentUpdate);
        children.setTransactionType(new ManagePaymentTransactionType(paymentTransactionTypeDto));
        children.setAmount(amount);
        children.setRemark(remarks);
        children.setTransactionDate(transactionDate);

        //Se asigna el padre.
        children.setParentId(paymentDetailDto.getPaymentDetailId());

        //Se crea el Details.
        this.newDetails.add(children);

        //Agregando los Apply Deposit.
        List<PaymentDetail> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(paymentDetailDto.getPaymentDetails());
        updateChildrens.add(children);
        paymentDetailDto.setPaymentDetails(updateChildrens);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - amount);

        //Actualizando el Deposit
        this.updatePaymentDetails(paymentDetailDto);
        //paymentDetailService.update(paymentDetailDto);

        //Actualizando el Payment.
        this.updatePayment(paymentUpdate);
    }

    private void updatePaymentNewDetails(PaymentDetail update) {
        int index = this.newDetails.indexOf(update);
        if (index != -1) {
            this.newDetails.set(index, update);
        }
    }

    private void updatePaymentDetails(PaymentDetail update) {
        int index = this.details.indexOf(update);
        if (index != -1) {
            this.details.set(index, update);
        }
    }

    private void updatePayment(Payment update) {
        int index = this.uniquePayments.indexOf(update);
        if (index != -1) {
            this.uniquePayments.set(index, update);
        }
    }

    private void updateBooking(Booking update) {
        int index = this.bookins.indexOf(update);
        if (index != -1) {
            this.bookins.set(index, update);
        }
    }

    private CreateIncomeAdjustmentRequest createAdjustmentRequest(PaymentDetail paymentDetailDto, UUID employeeId, UUID transactionType, UUID income) {
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

    private CreateAntiToIncomeRequest sendToCreateRelatedIncome(PaymentDetail paymentDetailDto, ManageEmployeeDto employeeDto, UUID transactionType, UUID status, String attachment) {
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
