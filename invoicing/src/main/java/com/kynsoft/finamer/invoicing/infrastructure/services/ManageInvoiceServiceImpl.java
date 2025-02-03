package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.writer.ExcelWriter;
import com.kynsof.share.core.application.excel.writer.WriterConfiguration;
import com.kynsof.share.core.domain.EWorkbookFormat;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanWriter;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.search.ManageInvoiceSearchResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceToPaymentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.excel.ExportInvoiceRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.ManageInvoiceSearchProjection;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.AgencyCouponFormatUtils;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import com.kynsoft.finamer.invoicing.infrastructure.event.update.sequence.UpdateSequenceEvent;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.importInnsist.response.undoImport.ProducerResponseUndoImportInnsistService;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class ManageInvoiceServiceImpl implements IManageInvoiceService {

    @Autowired
    private final ManageInvoiceWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceReadDataJPARepository repositoryQuery;

    @Autowired
    private final IInvoiceCloseOperationService closeOperationService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final IHotelInvoiceNumberSequenceService hotelInvoiceNumberSequenceService;
    private final ProducerResponseUndoImportInnsistService producerResponseUndoImportInnsistService;

    public ManageInvoiceServiceImpl(ManageInvoiceWriteDataJPARepository repositoryCommand,
            ManageInvoiceReadDataJPARepository repositoryQuery,
            IInvoiceCloseOperationService closeOperationService,
            ApplicationEventPublisher applicationEventPublisher,
            IHotelInvoiceNumberSequenceService hotelInvoiceNumberSequenceService,
            ProducerResponseUndoImportInnsistService producerResponseUndoImportInnsistService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.closeOperationService = closeOperationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.hotelInvoiceNumberSequenceService = hotelInvoiceNumberSequenceService;
        this.producerResponseUndoImportInnsistService = producerResponseUndoImportInnsistService;
    }

    public Long getInvoiceNumberSequence(String invoiceNumber) {
        Long lastInvoiceNo = this.repositoryQuery.findByInvoiceNumber(invoiceNumber);

        lastInvoiceNo += 1;
        return lastInvoiceNo;
    }

    @Override
    public void calculateInvoiceAmount(ManageInvoiceDto dto) {
        Double InvoiceAmount = 0.00;

        if (dto.getBookings() != null) {

            for (int i = 0; i < dto.getBookings().size(); i++) {

                InvoiceAmount += dto.getBookings().get(i).getInvoiceAmount();

            }

            dto.setInvoiceAmount(InvoiceAmount);
            dto.setDueAmount(InvoiceAmount);

            this.update(dto);
        }
    }

    @Override
    public ManageInvoiceDto create(ManageInvoiceDto dto) {
        InvoiceUtils.establishDueDate(dto);
        InvoiceUtils.calculateInvoiceAging(dto);//TODO Eliminar esto
        Invoice entity = new Invoice(dto);
        entity.setInvoiceDate(LocalDateTime.of(dto.getInvoiceDate().toLocalDate(), LocalTime.now()));
        if (dto.getHotel().isVirtual() && dto.getInvoiceType().equals(EInvoiceType.INVOICE)) {
            String invoiceNumber = dto.getInvoiceNumber() + "-" + dto.getHotelInvoiceNumber();
            entity.setInvoiceNumber(invoiceNumber);
            entity.setInvoiceNo(dto.getHotelInvoiceNumber());
            dto.setInvoiceNo(dto.getHotelInvoiceNumber());
            String invoicePrefix = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType()) + "-" + dto.getHotelInvoiceNumber();
            entity.setInvoiceNumberPrefix(invoicePrefix);
        } else {
            String invoiceNumber = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType());
            Long lastInvoiceNo = 0L;

            if (dto.getHotel().getManageTradingCompanies() != null && dto.getHotel().getApplyByTradingCompany()) {
                EInvoiceType invoiceType = dto.getInvoiceType().name().equals(EInvoiceType.OLD_CREDIT.name()) ? EInvoiceType.CREDIT : dto.getInvoiceType();
                HotelInvoiceNumberSequenceDto sequence = this.hotelInvoiceNumberSequenceService.getByTradingCompanyCodeAndInvoiceType(dto.getHotel().getManageTradingCompanies().getCode(), invoiceType);
                lastInvoiceNo = sequence.getInvoiceNo() + 1;
                this.applicationEventPublisher.publishEvent(new UpdateSequenceEvent(this, sequence));
                invoiceNumber += "-" + dto.getHotel().getCode() + "-" + lastInvoiceNo;
            } else {
                EInvoiceType invoiceType = dto.getInvoiceType().name().equals(EInvoiceType.OLD_CREDIT.name()) ? EInvoiceType.CREDIT : dto.getInvoiceType();
                HotelInvoiceNumberSequenceDto sequence = this.hotelInvoiceNumberSequenceService.getByHotelCodeAndInvoiceType(dto.getHotel().getCode(), invoiceType);
                lastInvoiceNo = sequence.getInvoiceNo() + 1;
                this.applicationEventPublisher.publishEvent(new UpdateSequenceEvent(this, sequence));
                invoiceNumber += "-" + dto.getHotel().getCode() + "-" + lastInvoiceNo;
            }
            entity.setInvoiceNo(lastInvoiceNo);
            entity.setInvoiceNumber(invoiceNumber);
//            Long lastInvoiceNo = this.getInvoiceNumberSequence(dto.getInvoiceNumber());
//            String invoiceNumber = dto.getInvoiceNumber() + "-" + lastInvoiceNo;
//            entity.setInvoiceNumber(invoiceNumber);
//            entity.setInvoiceNo(lastInvoiceNo);
            dto.setInvoiceNo(lastInvoiceNo);
            String invoicePrefix = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType()) + "-" + lastInvoiceNo;
            entity.setInvoiceNumberPrefix(invoicePrefix);
        }

        Invoice invoice = this.repositoryCommand.saveAndFlush(entity);
        return invoice.toAggregate();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        Specification<Invoice> specifications = new GenericSpecificationsBuilder<Invoice>(filterCriteria).build();
        if (pageable.getSort().isSorted()) {
            boolean hasCreatedAt = pageable.getSort().stream()
                    .anyMatch(order -> order.getProperty().equals("createdAt"));
            if (hasCreatedAt) {
                Sort sort = Sort.by(Sort.Order.desc("createdAt"));
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            }
        }

        Page<ManageInvoiceSearchProjection> data1 = repositoryQuery.findAllProjected(specifications, pageable);
        //   Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponseProjection(data1);
        //  return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponseProjection(Page<ManageInvoiceSearchProjection> data) {
        List<ManageInvoiceSearchResponse> responseList = new ArrayList<>();
        for (ManageInvoiceSearchProjection entity : data.getContent()) {
            try {
//                Boolean isCloseOperation = entity.getHotel().getCloseOperation() != null
//                        && !(entity.getInvoiceDate().toLocalDate().isBefore(entity.getHotel().getCloseOperation().getBeginDate())
//                        || entity.getInvoiceDate().toLocalDate().isAfter(entity.getHotel().getCloseOperation().getEndDate()));
                ManageInvoiceSearchResponse response = new ManageInvoiceSearchResponse(entity);
                responseList.add(response);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public PaginatedResponse sendList(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedSendListResponse(data);
    }

    private PaginatedResponse searchToExporter(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceSearchProjection> data = repositoryQuery.findAllProjected(specifications, pageable);

        return getPaginatedResponseProjection(data);
    }

    @Override
    public PaginatedResponse searchToPayment(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponseToPayment(data);
    }

    @Override
    public Page<ManageInvoiceDto> getInvoiceForSummary(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);
        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        return repositoryQuery.findAll(specifications, pageable).map(Invoice::toAggregate);
    }

    @Override
    public void exportInvoiceList(Pageable pageable, List<FilterCriteria> filterCriteria, ByteArrayOutputStream outputStream) {
        // Obtener datos
        List<ManageInvoiceSearchResponse> data = this.searchToExporter(pageable, filterCriteria).getData();

        // Inicialización de listas y encabezado
        List<ExportInvoiceRow> rows = new ArrayList<>();
        rows.add(createHeaderRow());

        // Inicialización de contadores y totales
        double totalCount = 0.00;
        double totalAmount = 0.00;

        Map<String, Double> statusCounts = new HashMap<>();
        Map<String, Double> statusAmounts = new HashMap<>();
        initializeStatusMaps(statusCounts, statusAmounts);

        // Formatos
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Locale locale = Locale.US;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", locale);

        // Procesar filas
        for (ManageInvoiceSearchResponse invoice : data) {
            totalCount++;
            totalAmount += invoice.getInvoiceAmount();

            String status = processInvoiceStatus(invoice, statusCounts, statusAmounts);

            rows.add(createDataRow(invoice, status, formatter, decimalFormat));
        }

        // Agregar totales
        rows.add(createTotalRow(statusCounts, totalCount, "Totals (#)"));
        rows.add(createTotalRow(statusAmounts, totalAmount, "Totals ($)", decimalFormat));

        // Configuración y escritura
        WriterConfiguration<ExportInvoiceRow> config = new WriterConfiguration.WriterConfigurationBuilder<ExportInvoiceRow>()
                .withType(ExportInvoiceRow.class)
                .withWorkbookFormat(EWorkbookFormat.XLSX)
                .withSheetNames(Collections.singletonList("Invoice List"))
                .withRows(rows)
                .build();

        new ExcelBeanWriter<>(config).write(outputStream);
    }

// Métodos auxiliares

    private ExportInvoiceRow createHeaderRow() {
        return new ExportInvoiceRow(
                0, "Has Attachment", "Id", "Type", "Hotel", "Agency Cd", "Agency", "Inv. No",
                "Gen. Date", "Status", "Manual", "Invoice Amount", "Invoice Balance", "Auto Rec", null
        );
    }

    private void initializeStatusMaps(Map<String, Double> counts, Map<String, Double> amounts) {
        for (String status : List.of("PROCESSED", "RECONCILED", "SENT", "CANCELED", "WAITING")) {
            counts.put(status, 0.00);
            amounts.put(status, 0.00);
        }
    }

    private String processInvoiceStatus(ManageInvoiceSearchResponse invoice, Map<String, Double> counts, Map<String, Double> amounts) {
        String status = switch (invoice.getStatus()) {
            case PROCESSED -> "PROCESSED";
            case RECONCILED -> "RECONCILED";
            case SENT -> "SENT";
            case CANCELED -> "CANCELED";
            default -> "WAITING";
        };

        counts.compute(status, (k, v) -> v + 1);
        amounts.compute(status, (k, v) -> v + invoice.getInvoiceAmount());
        return status;
    }

    private ExportInvoiceRow createDataRow(ManageInvoiceSearchResponse invoice, String status, DateTimeFormatter formatter, DecimalFormat decimalFormat) {
        return new ExportInvoiceRow(
                0,
                invoice.getHasAttachments() != null ? invoice.getHasAttachments().toString() : "false",
                Optional.ofNullable(invoice.getInvoiceId()).map(Object::toString).orElse(""),
                Optional.ofNullable(invoice.getInvoiceType()).map(type -> InvoiceType.getInvoiceTypeCode(type) + "-" + type).orElse(""),
                Optional.ofNullable(invoice.getHotel()).map(hotel -> hotel.getCode() + "-" + hotel.getName()).orElse(""),
                Optional.ofNullable(invoice.getAgency()).map(agency -> agency.getCode()).orElse(""),
                Optional.ofNullable(invoice.getAgency()).map(agency -> agency.getName()).orElse(""),
                invoice.getInvoiceNumber(),
                Optional.ofNullable(invoice.getInvoiceDate()).map(formatter::format).orElse(""),
                status,
                invoice.getIsManual() ? "1" : "0",
                decimalFormat.format(Optional.ofNullable(invoice.getInvoiceAmount()).orElse(0.00)),
                decimalFormat.format(Optional.ofNullable(invoice.getDueAmount()).orElse(0.00)),
                invoice.getAutoRec() ? "1" : "0",
                null
        );
    }

    private ExportInvoiceRow createTotalRow(Map<String, Double> map, double overallValue, String label) {
        return new ExportInvoiceRow(
                0, "", label,
                "#" + overallValue,
                "", "",
                "Pro #" + map.get("PROCESSED"),
                "Rec #" + map.get("RECONCILED"),
                "Sen #" + map.get("SENT"),
                "Can #" + map.get("CANCELED"),
                "",
                "", "", "", null
        );
    }

    private ExportInvoiceRow createTotalRow(Map<String, Double> map, double overallValue, String label, DecimalFormat decimalFormat) {
        return new ExportInvoiceRow(
                0, "", label,
                "$" + decimalFormat.format(overallValue),
                "", "",
                "Pro $" + decimalFormat.format(map.get("PROCESSED")),
                "Rec $" + decimalFormat.format(map.get("RECONCILED")),
                "Sen $" + decimalFormat.format(map.get("SENT")),
                "Can $" + decimalFormat.format(map.get("CANCELED")),
                "",
                "", "", "", null
        );
    }

    private PaginatedResponse getPaginatedResponse(Page<Invoice> data) {
        List<ManageInvoiceSearchResponse> responseList = new ArrayList<>();
        for (Invoice entity : data.getContent()) {
            try {
                Boolean isCloseOperation = entity.getHotel().getCloseOperation() != null
                        && !(entity.getInvoiceDate().toLocalDate().isBefore(entity.getHotel().getCloseOperation().getBeginDate())
                        || entity.getInvoiceDate().toLocalDate().isAfter(entity.getHotel().getCloseOperation().getEndDate()));
                ManageInvoiceSearchResponse response = new ManageInvoiceSearchResponse(entity.toAggregateSearch(),
                        entity.getHasAttachments(), isCloseOperation);
                responseList.add(response);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private PaginatedResponse getPaginatedSendListResponse(Page<Invoice> data) {
        // Filtramos el contenido de acuerdo a las condiciones
        List<ManageInvoiceSearchResponse> filteredResponseList = data.getContent().stream()
                .filter(entity -> {
                    ManageAgency agency = entity.getAgency();
                    List<Booking> bookings = entity.getBookings();
                    return (agency != null && !agency.getValidateCheckout()) || (bookings != null && !hasPastDueBooking(bookings));
                })
                .map(entity -> new ManageInvoiceSearchResponse(entity.toAggregateSearch(), null, null))
                .collect(Collectors.toList());

        // Calculamos el total de elementos después del filtrado
        int totalFilteredElements = filteredResponseList.size();

        // Calculamos el total de páginas según el tamaño original de la página
        int pageSize = data.getSize();
        int totalPages = (int) Math.ceil((double) totalFilteredElements / pageSize);

        // Devolvemos la respuesta paginada con los datos filtrados y calculados
        return new PaginatedResponse(
                filteredResponseList, // Lista de datos filtrados
                totalPages, // Total de páginas basado en los datos filtrados
                totalFilteredElements, // Total de elementos después del filtrado
                (long) totalFilteredElements, // Total de elementos
                pageSize, // Tamaño de la página original
                data.getNumber() // Número de la página actual (0-indexado)
        );
    }

    public boolean hasPastDueBooking(List<Booking> bookings) {
        LocalDate currentDate = LocalDate.now(); // Obtener la fecha actual (sin hora)
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking booking : bookings) {
                if (booking.getCheckOut() != null
                        && (booking.getCheckOut().toLocalDate().isBefore(currentDate)
                        || booking.getCheckOut().toLocalDate().isEqual(currentDate))) {
                    return true; // Si checkOut es antes o igual a currentDate, devolver true
                }
            }
        }

        return false; // Si no se encontró ningún booking con checkOut anterior o igual, devolver false
    }

    private PaginatedResponse getPaginatedResponseToPayment(Page<Invoice> data) {
        List<ManageInvoiceToPaymentResponse> responseList = new ArrayList<>();

        for (Invoice entity : data.getContent()) {
            ManageInvoiceToPaymentResponse response = new ManageInvoiceToPaymentResponse(entity.toAggregate());
            responseList.add(response);
        }

        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ManageInvoiceDto update(ManageInvoiceDto dto) {
        Invoice entity = new Invoice(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        return repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void deleteInvoice(ManageInvoiceDto dto) {
        Invoice entity = new Invoice(dto);
        entity.setDeleteInvoice(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
        if (entity.getImportType().equals(ImportType.INSIST)) {
            this.producerResponseUndoImportInnsistService.create(entity.getId());
        }
    }

    @Override
    public void delete(ManageInvoiceDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageInvoiceDto findById(UUID id) {
        Optional<Invoice> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INVOICE_NOT_FOUND_,
                new ErrorField("id", "The invoice not found.")));

    }

    @Override
    public List<ManageInvoiceDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(Invoice::toAggregate).toList();
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

    @Override
    public List<ManageInvoiceDto> findAllToReplicate() {
        List<Invoice> objects = this.repositoryQuery.findAll();
        List<ManageInvoiceDto> objectDtos = new ArrayList<>();

        for (Invoice object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public Double findSumOfAmountByParentId(UUID parentId) {
        return this.repositoryQuery.findSumOfAmountByParentId(parentId).orElse(0.0);
    }

    @Override
    public ManageInvoiceDto findByInvoiceId(long id) {
        return repositoryQuery.findByInvoiceId(id)
                .map(Invoice::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INVOICE_NOT_FOUND_,
                new ErrorField("invoiceId", "The invoice not found."))));
    }

    @Override
    public boolean existManageInvoiceByInvoiceId(long invoiceId) {
        return repositoryQuery.existsByInvoiceId(invoiceId);
    }

    @Override
    public ManageInvoiceDto changeInvoiceStatus(ManageInvoiceDto dto, ManageInvoiceStatusDto status) {
        if (status.isReconciledStatus()) {
            StringBuilder errorList = new StringBuilder("The following Hotel Booking Numbers do not match: ");
            String couponFormat = dto.getAgency().getBookingCouponFormat();
            if (!AgencyCouponFormatUtils.agencyCouponFormatIsValid(couponFormat)) {
                throw new BusinessNotFoundException(
                        new GlobalBusinessException(
                                DomainErrorMessage.BOOKING_COUPON_FORMAT_NOT_VALID,
                                new ErrorField("bookingCouponFormat", DomainErrorMessage.BOOKING_COUPON_FORMAT_NOT_VALID.getReasonPhrase())));
            }

            boolean valid = true;
            List<ManageBookingDto> bookings = dto.getBookings();
            for (ManageBookingDto bookingDto : bookings) {
                if (!AgencyCouponFormatUtils.validateCode(bookingDto.getHotelBookingNumber(), couponFormat)) {
                    valid = false;
                    errorList.append(" " + bookingDto.getHotelBookingNumber() + ",");
                }
            }
            errorList.replace(errorList.length() - 1, errorList.length(), ".");
            if (!valid) {
                throw new BusinessNotFoundException(
                        new GlobalBusinessException(
                                DomainErrorMessage.HOTEL_BOOKING_NUMBER_NOT_VALID,
                                new ErrorField("hotelBookingNumber", errorList.toString())));
            }
            dto.setStatus(RECONCILED);
            dto.setManageInvoiceStatus(status);
        }
        return dto;
    }

}
