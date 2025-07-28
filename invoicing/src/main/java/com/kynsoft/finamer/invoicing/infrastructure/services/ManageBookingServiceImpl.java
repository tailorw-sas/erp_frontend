package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.DuplicateValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelBookingCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelInvoiceCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageBookingWriteDataJpaRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageBookingReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomRateReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ManageBookingServiceImpl implements IManageBookingService {

    private final ManageBookingWriteDataJpaRepository repositoryCommand;
    private final ManageRoomRateReadDataJPARepository manageRoomRateReadDataJPARepository;
    private final ManageBookingReadDataJPARepository repositoryQuery;
    private final IManageRoomRateService roomRateService;

    public ManageBookingServiceImpl(
            ManageBookingWriteDataJpaRepository repositoryCommand,ManageRoomRateReadDataJPARepository manageRoomRateReadDataJPARepository,
            ManageBookingReadDataJPARepository repositoryQuery,
            IManageRoomRateService roomRateService) {
        this.repositoryCommand = repositoryCommand;
        this.manageRoomRateReadDataJPARepository = manageRoomRateReadDataJPARepository;
        this.repositoryQuery = repositoryQuery;
        this.roomRateService = roomRateService;

    }

    @Override
    public void calculateInvoiceAmount(ManageBookingDto dto) {
        if (dto.getRoomRates() != null) {
            this.calculateInvoiceAmountWithRoomRates(dto);
        } else {
            Optional<Booking> optionalBooking = this.repositoryCommand.findById(dto.getId());

            if (optionalBooking.isEmpty()) {
                throw new IllegalArgumentException("Booking not found for ID: " + dto.getId());
            }

            Booking booking = optionalBooking.get();
            double invoiceAmount = 0.00;

            List<ManageRoomRate> roomRates = this.manageRoomRateReadDataJPARepository.findByBooking(booking);

            if (!roomRates.isEmpty()) {
                invoiceAmount = roomRates.stream()
                        .mapToDouble(ManageRoomRate::getInvoiceAmount)
                        .sum();
            }

            booking.setInvoiceAmount(invoiceAmount);
            booking.setDueAmount(invoiceAmount);
            this.repositoryCommand.save(booking);
        }
    }

    private void calculateInvoiceAmountWithRoomRates(ManageBookingDto dto) {
        Double InvoiceAmount = 0.00;

        if (dto.getRoomRates() != null) {
            for (int i = 0; i < dto.getRoomRates().size(); i++) {
                InvoiceAmount += dto.getRoomRates().get(i).getInvoiceAmount();
            }
            dto.setInvoiceAmount(InvoiceAmount);
            dto.setDueAmount(InvoiceAmount);

            this.update(dto);
        }
    }

    @Override
    public void calculateHotelAmount(ManageBookingDto dto) {
        Double HotelAmount = 0.00;

        if (dto.getRoomRates() != null) {

            for (int i = 0; i < dto.getRoomRates().size(); i++) {

                HotelAmount += dto.getRoomRates().get(i).getHotelAmount();

            }

            dto.setHotelAmount(HotelAmount);

            this.update(dto);
        }
    }

    @Override
    public UUID create(ManageBookingDto dto) {
        Booking entity = new Booking(dto);
        Booking newBooking = repositoryCommand.saveAndFlush(entity);
        dto.setId(newBooking.getId());
        dto.setReservationNumber(newBooking.getReservationNumber());
        dto.setBookingId(newBooking.getBookingId());
        return newBooking.getId();
    }

    @Override
    public UUID insert(ManageBookingDto dto) {
        Booking booking = new Booking(dto);
        this.insert(booking);

        dto.setId(booking.getId());
        dto.setReservationNumber(booking.getReservationNumber());
        dto.setBookingId(booking.getBookingId());

        if(Objects.nonNull(dto.getRoomRates()) && !dto.getRoomRates().isEmpty()){
            dto.setRoomRates(this.roomRateService.insertAll(dto.getRoomRates()));
        }

        return booking.getId();
    }

    @Override
    public List<ManageBookingDto> createAll(List<ManageBookingDto> bookingDtoList) {
        for(ManageBookingDto bookingDto : bookingDtoList){
            this.insert(bookingDto);
        }
        return bookingDtoList;
    }

    @Override
    public void update(ManageBookingDto dto) {
        Booking entity = new Booking(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Booking> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Booking> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public boolean existsByExactLastChars(String lastChars, UUID hotelId) {
        return this.repositoryQuery.existsByExactLastChars(lastChars, hotelId);
    }

    private PaginatedResponse getPaginatedResponse(Page<Booking> data) {
        List<ManageBookingResponse> responseList = new ArrayList<>();
        for (Booking entity : data.getContent()) {
            responseList.add(new ManageBookingResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void delete(ManageBookingDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageBookingDto findById(UUID id) {
        Optional<Booking> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND_,
                new ErrorField("id", DomainErrorMessage.BOOKING_NOT_FOUND_.getReasonPhrase())));
    }

    @Override
    public List<ManageBookingDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(Booking::toAggregate).toList();
    }

    @Override
    public List<ManageBookingDto> findBookingsWithRoomRatesByInvoiceIds(List<UUID> invoiceIds) {
        return repositoryQuery.findBookingsWithRoomRatesByInvoiceIds(invoiceIds).stream().map(Booking::toAggregateWithRates).toList();
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

            if ("dueAmount".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    filter.setValue(Double.valueOf(filter.getValue().toString()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

    @Override
    public void deleteInvoice(ManageBookingDto dto) {
        Booking entity = new Booking(dto);
        entity.setDeleteInvoice(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public List<ManageBookingDto> findAllToReplicate() {
        List<Booking> objects = this.repositoryQuery.findAll();
        List<ManageBookingDto> objectDtos = new ArrayList<>();

        for (Booking object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public boolean existsByHotelInvoiceNumber(String hotelInvoiceNumber, UUID hotelId) {
        return this.repositoryQuery.existsByHotelInvoiceNumber(hotelInvoiceNumber, hotelId);
    }

    @Override
    public ManageBookingDto findBookingId(Long bookingId) {
        Optional<Booking> optionalEntity = repositoryQuery.findByBookingId(bookingId);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND_,
                new ErrorField("id", DomainErrorMessage.BOOKING_NOT_FOUND_.getReasonPhrase())));
    }

    @Override
    public void updateAll(List<ManageBookingDto> bookingList) {
        if(Objects.nonNull(bookingList) && !bookingList.isEmpty()){
            List<Booking> bookings = bookingList.stream().map(Booking::new).collect(Collectors.toList());
            repositoryCommand.saveAll(bookings);
        }
    }

    public void insert(Booking booking){
        //this.repositoryCommand.insert(booking);
        Map<String, Object> results = this.repositoryCommand.insertBooking(booking.getId(),
                booking.getAdults(),
                booking.getBookingDate(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getChildren(),
                booking.getContract(),
                booking.getCouponNumber(),
                booking.isDeleteInvoice(),
                booking.getDeleted(),
                booking.getDeletedAt(),
                booking.getDescription(),
                booking.getDueAmount(),
                booking.getFirstName(),
                booking.getFolioNumber(),
                booking.getFullName(),
                booking.getHotelAmount(),
                booking.getHotelBookingNumber(),
                booking.getHotelCreationDate(),
                booking.getHotelInvoiceNumber(),
                booking.getInvoiceAmount(),
                booking.getLastName(),
                booking.getNights(),
                booking.getRateAdult(),
                booking.getRateChild(),
                booking.getRoomNumber(),
                booking.getUpdatedAt(),
                booking.getInvoice() != null ? booking.getInvoice().getId() : null,
                booking.getNightType() != null ? booking.getNightType().getId() : null,
                booking.getParent() != null ? booking.getParent().getId() : null,
                booking.getRatePlan() != null ? booking.getRatePlan().getId() : null,
                booking.getRoomCategory() != null ? booking.getRoomCategory().getId() : null,
                booking.getRoomType() != null ? booking.getRoomType().getId() : null);

        if(results != null){
            if(results.containsKey("o_id")){
                UUID bookingId = (UUID)results.get("o_id");
                booking.setId(bookingId);
            }
            if(results.containsKey("o_reservation_number")){
                Integer reservationNumber = (Integer) results.get("o_reservation_number");
                booking.setReservationNumber(reservationNumber.longValue());
            }
            if(results.containsKey("o_bookingid")){
                Integer bookingGenId = (Integer) results.get("o_bookingid");
                booking.setBookingId(bookingGenId.longValue());
            }
        }

        if(booking.getRoomRates() != null && !booking.getRoomRates().isEmpty()){
            this.roomRateService.createAll(booking.getRoomRates());
        }
    }

    @Override
    public void insertAll(List<Booking> bookins) {
        for (Booking booking : bookins){
            this.insert(booking);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateValidationResult validateHotelBookingCombinations(List<HotelBookingCombinationDto> combinations, String importType) {
        if (combinations == null || combinations.isEmpty()) {
            return DuplicateValidationResult.noDuplicates(0, importType);
        }

        long startTime = System.currentTimeMillis();

        try {
            // Preparar parámetros para la query bulk
            List<String> hotelCodes = combinations.stream()
                    .map(HotelBookingCombinationDto::getHotelCode)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> bookingNumbers = combinations.stream()
                    .map(HotelBookingCombinationDto::getBookingNumber)
                    .distinct()
                    .collect(Collectors.toList());

            // Ejecutar query bulk optimizada
            List<String> existingCombinationKeys = repositoryQuery.findExistingHotelBookingCombinations(hotelCodes, bookingNumbers);

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Hotel booking validation completed in {}ms. Checked: {}, Found: {}",
                    executionTime, combinations.size(), existingCombinationKeys.size());

            if (existingCombinationKeys.isEmpty()) {
                return DuplicateValidationResult.noDuplicates(combinations.size(), importType);
            }

            return DuplicateValidationResult.withDuplicates(
                    new HashSet<>(existingCombinationKeys),
                    combinations.size(),
                    importType
            );

        } catch (Exception e) {
            log.error("Error validating hotel booking combinations for importType: {}", importType, e);
            // En caso de error, asumir que no hay duplicados para no bloquear el proceso
            return DuplicateValidationResult.noDuplicates(combinations.size(), importType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateValidationResult validateHotelInvoiceCombinations(List<HotelInvoiceCombinationDto> combinations, String importType) {
        if (combinations == null || combinations.isEmpty()) {
            return DuplicateValidationResult.noDuplicates(0, importType);
        }

        long startTime = System.currentTimeMillis();

        try {
            // Preparar parámetros para la query bulk
            List<String> hotelCodes = combinations.stream()
                    .map(HotelInvoiceCombinationDto::getHotelCode)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> invoiceNumbers = combinations.stream()
                    .map(HotelInvoiceCombinationDto::getInvoiceNumber)
                    .distinct()
                    .collect(Collectors.toList());

            // Ejecutar query bulk optimizada (solo para hoteles virtuales)
            List<String> existingCombinationKeys = repositoryQuery.findExistingHotelInvoiceCombinations(hotelCodes, invoiceNumbers);

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Hotel invoice validation completed in {}ms. Checked: {}, Found: {}",
                    executionTime, combinations.size(), existingCombinationKeys.size());

            if (existingCombinationKeys.isEmpty()) {
                return DuplicateValidationResult.noDuplicates(combinations.size(), importType);
            }

            return DuplicateValidationResult.withDuplicates(
                    new HashSet<>(existingCombinationKeys),
                    combinations.size(),
                    importType
            );

        } catch (Exception e) {
            log.error("Error validating hotel invoice combinations for importType: {}", importType, e);
            // En caso de error, asumir que no hay duplicados para no bloquear el proceso
            return DuplicateValidationResult.noDuplicates(combinations.size(), importType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByHotelCodeAndBookingNumber(String hotelCode, String bookingNumber) {
        if (hotelCode == null || hotelCode.trim().isEmpty() ||
                bookingNumber == null || bookingNumber.trim().isEmpty()) {
            return false;
        }

        try {
            return repositoryQuery.existsByHotelCodeAndBookingNumber(
                    hotelCode.toUpperCase().trim(),
                    bookingNumber.replaceAll("\\s+", " ").trim()
            );
        } catch (Exception e) {
            log.error("Error checking existence for hotel {} and booking {}", hotelCode, bookingNumber, e);
            return false; // En caso de error, asumir que no existe para no bloquear
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByHotelCodeAndInvoiceNumber(String hotelCode, String invoiceNumber) {
        if (hotelCode == null || hotelCode.trim().isEmpty() ||
                invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            return false;
        }

        try {
            return repositoryQuery.existsByHotelCodeAndInvoiceNumber(
                    hotelCode.toUpperCase().trim(),
                    invoiceNumber.trim()
            );
        } catch (Exception e) {
            log.error("Error checking existence for hotel {} and invoice {}", hotelCode, invoiceNumber, e);
            return false; // En caso de error, asumir que no existe para no bloquear
        }
    }
}
