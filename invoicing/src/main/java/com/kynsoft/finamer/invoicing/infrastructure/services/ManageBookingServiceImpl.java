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
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageBookingWriteDataJpaRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageBookingReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomRateReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageBookingServiceImpl implements IManageBookingService {

    private final ManageBookingWriteDataJpaRepository repositoryCommand;
    private final ManageRoomRateReadDataJPARepository manageRoomRateReadDataJPARepository;
    private final ManageBookingReadDataJPARepository repositoryQuery;

    public ManageBookingServiceImpl(
            ManageBookingWriteDataJpaRepository repositoryCommand,ManageRoomRateReadDataJPARepository manageRoomRateReadDataJPARepository,
            ManageBookingReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.manageRoomRateReadDataJPARepository = manageRoomRateReadDataJPARepository;
        this.repositoryQuery = repositoryQuery;

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
        return repositoryCommand.saveAndFlush(entity).getId();
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

    @Override
    public Optional<ManageBookingDto> findManageBookingByBookingNumber(String bookingNumber) {
        Optional<Booking> manageBooking= this.repositoryQuery.findManageBookingByHotelBookingNumber(bookingNumber);
        if (manageBooking.isPresent()){
            return manageBooking.map(Booking::toAggregate);
        }
        return Optional.empty();
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
    public boolean existByBookingHotelNumber(String bookingHotelNumber) {
        return repositoryQuery.existsByHotelBookingNumber(bookingHotelNumber);
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
    public ManageBookingDto findByIdWithRates(UUID id) {
        Optional<Booking> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregateWithRates();
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
}
