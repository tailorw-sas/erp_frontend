package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.mapper.PaymentBankRowMapper;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.bank.PaymentBankValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportBankErrorRepository;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentImportBankHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentImportBankErrorRepository paymentImportErrorRepository;
    private final PaymentBankValidatorFactory paymentBankValidatorFactory;
    private final IPaymentService paymentService;
    private final IManageAgencyService manageAgencyService;
    private final IManageHotelService manageHotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentSourceService paymentSourceService;
    private final IManagePaymentStatusService paymentStatusService;
    private final PaymentBankRowMapper paymentBankRowMapper;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    private final IManageEmployeeService employeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    @Value("${payment.source.bank.code}")
    private String PAYMENT_SOURCE_BANK_CODE;
    @Value("${payment.status.confirm.code}")
    private String PAYMENT_STATUS_CONF_CODE;
    @Value("${payment.default.attachment.status.code}")
    private String PAYMENT_ATTACHMENT_STATUS;

    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    public PaymentImportBankHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
            PaymentImportBankErrorRepository paymentImportErrorRepository,
            PaymentBankValidatorFactory paymentBankValidatorFactory,
            IPaymentService paymentService,
            IManageAgencyService manageAgencyService,
            IManageHotelService manageHotelService,
            IManageBankAccountService bankAccountService,
            IManagePaymentSourceService paymentSourceService,
            IManagePaymentStatusService paymentStatusService,
            PaymentBankRowMapper paymentBankRowMapper,
            IManagePaymentAttachmentStatusService attachmentStatusService,
            IPaymentStatusHistoryService paymentStatusHistoryService,
            IManageEmployeeService employeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.paymentBankValidatorFactory = paymentBankValidatorFactory;
        this.paymentService = paymentService;
        this.manageAgencyService = manageAgencyService;
        this.manageHotelService = manageHotelService;
        this.bankAccountService = bankAccountService;
        this.paymentSourceService = paymentSourceService;
        this.paymentStatusService = paymentStatusService;
        this.paymentBankRowMapper = paymentBankRowMapper;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.employeeService = employeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        this.totalProcessRow = 0;
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        List<UUID> agencys = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(request.getEmployeeId());
        List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(request.getEmployeeId());

        paymentBankValidatorFactory.createValidators();
        ExcelBeanReader<PaymentBankRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentBankRow.class);
        ExcelBean<PaymentBankRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentBankRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            row.setAgencys(agencys);
            row.setHotels(hotels);
            if (paymentBankValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
                this.totalProcessRow++;
            }
        }
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
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        if (!paymentImportErrorRepository.existsPaymentImportErrorByImportProcessId(request.getImportProcessId())) {
            Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "rowNumber"));
            Page<PaymentImportCache> cacheList;
            do {
                cacheList = paymentImportCacheRepository.findAllByImportProcessId(request.getImportProcessId(), pageable);
                List<PaymentDto> paymentDtoList = cacheList.map(cache -> {
                    PaymentDto paymentDto;
                    paymentDto = paymentBankRowMapper.toObject(cache).toAggregate();
                    //Optional<ManageBankAccountDto> manageBankAccountDtoOptional = Optional.ofNullable(bankAccountService.findByAccountNumber(cache.getBankAccount()));
                    ManageBankAccountDto manageBankAccountDtoOptional = bankAccountService.findManageBankAccountByCodeAndHotelCode(cache.getBankAccount(), cache.getHotel());
                    //manageBankAccountDtoOptional.ifPresent(paymentDto::setBankAccount);
                    paymentDto.setBankAccount(manageBankAccountDtoOptional);
                    Optional<ManagePaymentSourceDto> paymentSourceOptional = Optional.ofNullable(
                            paymentSourceService.findByCodeActive(PAYMENT_SOURCE_BANK_CODE));
                    paymentSourceOptional.ifPresent(paymentDto::setPaymentSource);
                    paymentDto.setAgency(manageAgencyService.findByCode(cache.getAgency()));
                    paymentDto.setHotel(manageHotelService.findByCode(cache.getHotel()));
                    Optional<ManageClientDto> clientDtoOptional = Optional.ofNullable(paymentDto.getAgency().getClient());
                    clientDtoOptional.ifPresent(paymentDto::setClient);
                    paymentDto.setStatus(Status.ACTIVE);
                    Optional<ManagePaymentStatusDto> paymentStatusOptional = Optional.ofNullable(
                            paymentStatusService.findByCode(PAYMENT_STATUS_CONF_CODE));
                    paymentStatusOptional.ifPresent(paymentDto::setPaymentStatus);
                    paymentDto.setId(UUID.randomUUID());
                    paymentDto.setAttachmentStatus(attachmentStatusService.findByCode(PAYMENT_ATTACHMENT_STATUS));
                    return paymentDto;
                }).toList();
                List<PaymentDto> createdPayment = paymentService.createBulk(paymentDtoList);
                ManageEmployeeDto employeeDto = employeeService.findById(request.getEmployeeId());
                createdPayment.forEach(paymentDto -> {
                    createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
                    createAttachmentStatusHistoryWithoutAttachmet(employeeDto, paymentDto);
                });
                pageable = pageable.next();
            } while (cacheList.hasNext());
        }
    }

    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentBankRowError> page = paymentImportErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(
                page.getContent().stream().sorted(Comparator.comparingInt(PaymentBankRowError::getRowNumber)).collect(Collectors.toList()),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber()
        );
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistoryWithoutAttachmet(ManageEmployeeDto employeeDto, PaymentDto payment) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating payment without attachment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus("NON-NONE");

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
