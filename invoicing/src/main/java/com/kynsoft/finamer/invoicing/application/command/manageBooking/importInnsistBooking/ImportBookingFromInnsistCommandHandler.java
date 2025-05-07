package com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.invoicing.domain.services.IImportInnsistService;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingFromInnsistCommandHandler implements ICommandHandler<ImportBookingFromInnsistCommand> {

    private final IImportInnsistService importInnsistService;

    public ImportBookingFromInnsistCommandHandler(IImportInnsistService importInnsistService){
        this.importInnsistService = importInnsistService;
    }

    @Override
    public void handle(ImportBookingFromInnsistCommand command) {
        //hacer validaciones
        ImportInnsistKafka importInnsistKafka = new ImportInnsistKafka(command.getImportInnsitProcessId(), command.getEmployee(),
                command.getImportList().stream()
                        .map(this::convertToImportInnsistBookingKafka)
                        .toList());
        importInnsistService.createInvoiceFromGroupedBooking(importInnsistKafka);
    }

    private ImportInnsistBookingKafka convertToImportInnsistBookingKafka(ImportInnsistBookingRequest bookingRequest){
        return new ImportInnsistBookingKafka(
                bookingRequest.getId(),
                bookingRequest.getHotelCreationDate(),
                bookingRequest.getInvoiceDate(),
                bookingRequest.getBookingDate(),
                bookingRequest.getCheckIn(),
                bookingRequest.getCheckOut(),
                bookingRequest.getHotelBookingNumber(),
                bookingRequest.getFirstName(),
                bookingRequest.getLastName(),
                bookingRequest.getRoomNumber(),
                bookingRequest.getAdults(),
                bookingRequest.getChildren(),
                bookingRequest.getNights(),
                bookingRequest.getRateAdult(),
                bookingRequest.getRateChild(),
                bookingRequest.getHotelInvoiceNumber(),
                bookingRequest.getFolioNumber(),
                bookingRequest.getHotelAmount(),
                bookingRequest.getDescription(),
                bookingRequest.getRatePlanCode(),
                bookingRequest.getNightTypeCode(),
                bookingRequest.getRoomTypeCode(),
                bookingRequest.getRoomCategoryCode(),
                bookingRequest.getManageHotelCode(),
                bookingRequest.getManageAgencyCode(),
                bookingRequest.getCouponNumber(),
                bookingRequest.getRoomRates().stream()
                        .map(this::convertToImportInnsistRoomRateKafka)
                        .toList()
        );
    }

    private ImportInnsistRoomRateKafka convertToImportInnsistRoomRateKafka(ImportInnsistRoomRateRequest roomRateRequest){
        return new ImportInnsistRoomRateKafka(
                roomRateRequest.getCheckIn(),
                roomRateRequest.getCheckOut(),
                roomRateRequest.getInvoiceAmount(),
                roomRateRequest.getRoomNumber(),
                roomRateRequest.getAdults(),
                roomRateRequest.getChildren(),
                roomRateRequest.getRateAdult(),
                roomRateRequest.getRateChild(),
                roomRateRequest.getHotelAmount(),
                roomRateRequest.getRemark(),
                roomRateRequest.getNights()
        );
    }
}
