package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.Errors;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnisistErrors;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.bean.ImportInnsistGroupBy;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.importInnsist.response.ProducerResponseImportInnsistService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ImportInnsistServiceImpl {

    private final IManageAgencyService agencyService;

    private final IManageHotelService manageHotelService;

    private final IManageInvoiceService invoiceService;
    private final IManageRatePlanService ratePlanService;

    private final IManageRoomTypeService roomTypeService;

    private final IManageNightTypeService nightTypeService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IManageBookingService bookingService;
    private final ProducerResponseImportInnsistService producerResponseImportInnsistService;

    public ImportInnsistServiceImpl(IManageAgencyService agencyService,
            IManageHotelService manageHotelService,
            IManageInvoiceService invoiceService,
            IManageRatePlanService ratePlanService,
            IManageRoomTypeService roomTypeService, IManageNightTypeService nightTypeService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
            IManageInvoiceStatusService manageInvoiceStatusService,
            IManageInvoiceTypeService iManageInvoiceTypeService,
            IInvoiceStatusHistoryService invoiceStatusHistoryService,
            IManageBookingService bookingService,
            ProducerResponseImportInnsistService producerResponseImportInnsistService) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.roomTypeService = roomTypeService;
        this.nightTypeService = nightTypeService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.bookingService = bookingService;
        this.producerResponseImportInnsistService = producerResponseImportInnsistService;
    }

    public ImportInnisistErrors createInvoiceFromGroupedBooking(ImportInnsistKafka request) {
        List<Errors> errors = new ArrayList<>();
        List<ImportInnsistBookingKafka> importList = updateWithGenerationType(request, errors);

        if (!errors.isEmpty()) {
            return new ImportInnisistErrors(request.getImportInnsitProcessId(), errors);
        }
        this.createInvoiceGroupingByCoupon(request.getEmployee().toString(), importList, errors);
        this.createInvoiceGroupingByBooking(request.getEmployee().toString(), importList);

        ImportInnisistErrors innisistErrors = new ImportInnisistErrors(request.getImportInnsitProcessId(), errors);
        this.producerResponseImportInnsistService.create(innisistErrors);
        return innisistErrors;
    }

    private List<ImportInnsistBookingKafka> updateWithGenerationType(ImportInnsistKafka request, List<Errors> errors) {
        List<ImportInnsistBookingKafka> importList = new ArrayList<>();
        if (request.getImportList() == null || request.getImportList().isEmpty()) {
            errors.add(new Errors("", "La lista de booking recibida, esta vacia."));
            return importList;
        }

        for (ImportInnsistBookingKafka importInnsistBookingKafka : request.getImportList()) {
            try {
                ManageAgencyDto manageAgencyDto = agencyService.findByCode(importInnsistBookingKafka.getManageAgencyCode());
                importInnsistBookingKafka.setGenerationType(manageAgencyDto.getGenerationType().name());
                importList.add(importInnsistBookingKafka);
            } catch (Exception e) {
                errors.add(new Errors(importInnsistBookingKafka.getId().toString(), "La Agencia con codigo " + importInnsistBookingKafka.getManageAgencyCode() + " no fue encontrada."));
            }
            try {
                ManageHotelDto hotelDto = manageHotelService.findByCode(importInnsistBookingKafka.getManageHotelCode());
                if (this.bookingService.existsByExactLastChars(importInnsistBookingKafka.getHotelBookingNumber()
                                       .split("\\s+")[importInnsistBookingKafka.getHotelBookingNumber()
                                       .split("\\s+").length - 1], hotelDto.getId())) {
                    errors.add(new Errors(importInnsistBookingKafka.getId().toString(), "El Hotel Booking Number " + importInnsistBookingKafka.getHotelBookingNumber() + " ya existe."));
                }
            } catch (Exception e) {
                errors.add(new Errors(importInnsistBookingKafka.getId().toString(), "La Hotel con codigo " + importInnsistBookingKafka.getManageHotelCode() + " no fue encontrado."));
            }
        }
        return importList;
    }

    private void createInvoiceGroupingByCoupon(String employee, List<ImportInnsistBookingKafka> importList, List<Errors> errors) {
        Map<ImportInnsistGroupBy, List<ImportInnsistBookingKafka>> grouped;

        grouped = importList.stream().collect(Collectors.groupingBy(
                booking -> new ImportInnsistGroupBy(
                        booking.getManageAgencyCode(),
                        booking.getManageHotelCode(),
                        booking.getCouponNumber(),
                        EGenerationType.ByCoupon.name()
                )
        ));

        if (!grouped.isEmpty()) {
            grouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee);
            });
        }
    }

    private void createInvoiceGroupingByBooking(String employee, List<ImportInnsistBookingKafka> importList) {
        Predicate<ImportInnsistBookingKafka> byBooking = booking -> booking.getGenerationType().equals(EGenerationType.ByBooking.name());
        List<ImportInnsistBookingKafka> process = importList.stream().filter(byBooking).collect(Collectors.toList());
        process.forEach(innsist -> {
            ManageAgencyDto agency = agencyService.findByCode(innsist.getManageAgencyCode());
            ManageHotelDto hotel = manageHotelService.findByCode(innsist.getManageHotelCode());
            this.createInvoiceWithBooking(agency, hotel, List.of(innsist), employee);
        });
    }

    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<ImportInnsistBookingKafka> bookingRowList, String employee) {
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCECSED);
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(EInvoiceType.INVOICE);
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setManageInvoiceType(invoiceTypeDto);
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setManageInvoiceStatus(invoiceStatus);
        manageInvoiceDto.setIsManual(false);
        manageInvoiceDto.setInvoiceDate(getInvoiceDate(bookingRowList.get(0)));

        List<ManageBookingDto> bookingDtos = createBooking(bookingRowList);
        manageInvoiceDto.setBookings(bookingDtos);

        Double invoiceAmount = calculateInvoiceAmount(bookingDtos);
        manageInvoiceDto.setInvoiceAmount(invoiceAmount);
        manageInvoiceDto.setDueAmount(invoiceAmount);
        manageInvoiceDto.setOriginalAmount(invoiceAmount);

        if (hotel.isVirtual()) {
            manageInvoiceDto.setImportType(ImportType.BOOKING_FROM_FILE_VIRTUAL_HOTEL);
        } else {
            manageInvoiceDto.setImportType(ImportType.INVOICE_BOOKING_FROM_FILE);
        }

        String invoiceNumber = InvoiceType.getInvoiceTypeCode(EInvoiceType.INVOICE);
        if (hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotel.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotel.getCode();
        }
        manageInvoiceDto.setInvoiceNumber(invoiceNumber);
        invoiceService.update(manageInvoiceDto);
        this.createInvoiceHistory(manageInvoiceDto, employee);

        //TODO: aqui se envia a crear el invoice con sun booking en payment
        try {
            this.producerReplicateManageInvoiceService.create(manageInvoiceDto);
        } catch (Exception e) {
        }
    }

    private List<ManageBookingDto> createBooking(List<ImportInnsistBookingKafka> bookingRowList) {
        return bookingRowList.stream().map(bookingRow -> {
            ManageRatePlanDto ratePlanDto = Objects.nonNull(bookingRow.getRatePlanCode()) ? ratePlanService.findByCode(bookingRow.getRatePlanCode()) : null;
            ManageRoomTypeDto roomTypeDto = Objects.nonNull(bookingRow.getRoomTypeCode()) ? roomTypeService.findByCode(bookingRow.getRoomTypeCode()) : null;
            ManageNightTypeDto nightTypeDto = Objects.nonNull(bookingRow.getNightTypeCode()) ? nightTypeService.findByCode(bookingRow.getNightTypeCode()) : null;

            ManageBookingDto bookingDto = toAggregate(bookingRow);
            bookingDto.setId(bookingRow.getId());
            bookingDto.setRatePlan(ratePlanDto);
            bookingDto.setRoomType(roomTypeDto);
            bookingDto.setNightType(nightTypeDto);
            bookingDto.setHotelCreationDate(bookingRow.getHotelCreationDate());

            List<ManageRoomRateDto> rates = createRoomRateDto(bookingRow.getRoomRates());
            bookingDto.setRoomRates(rates);
            bookingDto.setInvoiceAmount(calculateBookingAmount(rates));

            return bookingDto;
        }).toList();
    }

    private List<ManageRoomRateDto> createRoomRateDto(List<ImportInnsistRoomRateKafka> roomRates) {
        List<ManageRoomRateDto> createRateDtos = new ArrayList<>();
        for (ImportInnsistRoomRateKafka roomRate : roomRates) {
            createRateDtos.add(toAggregateRoomRateDto(roomRate));
        }

        return createRateDtos;
    }

    private double calculateInvoiceAmount(List<ManageBookingDto> bookingDtos) {
        return bookingDtos.stream().mapToDouble(ManageBookingDto::getInvoiceAmount).sum();
    }

    private double calculateBookingAmount(List<ManageRoomRateDto> createRateDtos) {
        return createRateDtos.stream().mapToDouble(ManageRoomRateDto::getInvoiceAmount).sum();
    }

    private LocalDateTime getInvoiceDate(ImportInnsistBookingKafka bookingRow) {
        LocalDateTime excelDate = bookingRow.getBookingDate();
        LocalDateTime transactionDate = LocalDateTime.now();
        if (Objects.nonNull(bookingRow.getBookingDate())
                && Objects.nonNull(excelDate)
                && !LocalDate.now().isEqual(excelDate.toLocalDate())) {
            transactionDate = bookingRow.getBookingDate();
        }
        return transactionDate;
    }

    private ManageBookingDto toAggregate(ImportInnsistBookingKafka importInnsistBookingKafka) {
        ManageBookingDto manageBookingDto = new ManageBookingDto();
        manageBookingDto.setAdults(Objects.nonNull(importInnsistBookingKafka.getAdults()) ? importInnsistBookingKafka.getAdults() : 0);
        manageBookingDto.setChildren(Objects.nonNull(importInnsistBookingKafka.getChildren()) ? importInnsistBookingKafka.getChildren() : 0);
        manageBookingDto.setCheckIn(importInnsistBookingKafka.getCheckIn());
        manageBookingDto.setCheckOut(importInnsistBookingKafka.getCheckOut());
        manageBookingDto.setCouponNumber(Objects.nonNull(importInnsistBookingKafka.getCouponNumber()) ? importInnsistBookingKafka.getCouponNumber() : "");
        manageBookingDto.setHotelAmount(importInnsistBookingKafka.getHotelAmount());
        manageBookingDto.setFirstName(Objects.nonNull(importInnsistBookingKafka.getFirstName()) ? importInnsistBookingKafka.getFirstName() : "");
        manageBookingDto.setLastName(Objects.nonNull(importInnsistBookingKafka.getLastName()) ? importInnsistBookingKafka.getLastName() : "");
        manageBookingDto.setFullName(buildFullName(importInnsistBookingKafka));
        manageBookingDto.setHotelBookingNumber(Objects.nonNull(importInnsistBookingKafka.getHotelBookingNumber()) ? importInnsistBookingKafka.getHotelBookingNumber() : "");
        manageBookingDto.setHotelInvoiceNumber(Objects.nonNull(importInnsistBookingKafka.getHotelInvoiceNumber()) ? importInnsistBookingKafka.getHotelInvoiceNumber() : "");
        manageBookingDto.setDescription(Objects.nonNull(importInnsistBookingKafka.getDescription()) ? importInnsistBookingKafka.getDescription() : "");
        manageBookingDto.setRoomNumber(importInnsistBookingKafka.getRoomNumber());
        manageBookingDto.setBookingDate(importInnsistBookingKafka.getBookingDate());
        return manageBookingDto;
    }

    private ManageRoomRateDto toAggregateRoomRateDto(ImportInnsistRoomRateKafka importInnsistRoomRateKafka) {
        ManageRoomRateDto manageRoomRateDto = new ManageRoomRateDto();
        manageRoomRateDto.setId(UUID.randomUUID());
        manageRoomRateDto.setAdults(Objects.nonNull(importInnsistRoomRateKafka.getAdults()) ? importInnsistRoomRateKafka.getAdults() : 0);
        manageRoomRateDto.setChildren(Objects.nonNull(importInnsistRoomRateKafka.getChildren()) ? importInnsistRoomRateKafka.getChildren() : 0);
        manageRoomRateDto.setCheckIn(importInnsistRoomRateKafka.getCheckIn());
        manageRoomRateDto.setCheckOut(importInnsistRoomRateKafka.getCheckOut());
        manageRoomRateDto.setHotelAmount(importInnsistRoomRateKafka.getHotelAmount());
        manageRoomRateDto.setRemark(Objects.nonNull(importInnsistRoomRateKafka.getRemark()) ? importInnsistRoomRateKafka.getRemark() : "");
        manageRoomRateDto.setRoomNumber(importInnsistRoomRateKafka.getRoomNumber());
        manageRoomRateDto.setInvoiceAmount(importInnsistRoomRateKafka.getInvoiceAmount());
        manageRoomRateDto.setNights(importInnsistRoomRateKafka.getNights());
        return manageRoomRateDto;
    }

    private String buildFullName(ImportInnsistBookingKafka importInnsistBookingKafka) {
        if (Objects.nonNull(importInnsistBookingKafka.getFirstName()) && (Objects.nonNull(importInnsistBookingKafka.getLastName()))) {
            return importInnsistBookingKafka.getFirstName() + " " + importInnsistBookingKafka.getLastName();
        }
        if (Objects.nonNull(importInnsistBookingKafka.getFirstName())) {
            return importInnsistBookingKafka.getFirstName();
        }
        if (Objects.nonNull(importInnsistBookingKafka.getLastName())) {
            return importInnsistBookingKafka.getLastName();
        }
        return "";
    }

    private void createInvoiceHistory(ManageInvoiceDto manageInvoice, String employee) {
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        manageInvoice,
                        "The invoice data was inserted.",
                        LocalDateTime.now(),
                        employee,
                        EInvoiceStatus.PROCECSED
                )
        );
    }

}
