package com.kynsoft.finamer.insis.application.command.roomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class CreateRoomRateCommandHandler implements ICommandHandler<CreateRoomRateCommand> {

    private final IRoomRateService service;
    private final IManageHotelService hotelService;

    public CreateRoomRateCommandHandler(IRoomRateService service,
                                        IManageHotelService hotelService){
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateRoomRateCommand command) {
        ManageHotelDto hotelDto = hotelService.findByCode(command.getHotel());

        /*
        RoomRateDto dto = new RoomRateDto(
                command.getId(),
                command.getStatus(),
                hotelDto,
                command.getUpdatedAt(),
                command.getAgency(),
                command.getCheckInDate(),
                command.getCheckOutDate(),
                command.getStayDays(),
                command.getReservationCode(),
                command.getGuestName(),
                command.getFirstName(),
                command.getLastName(),
                command.getAmount(),
                command.getRoomType(),
                command.getCouponNumber(),
                command.getTotalNumberOfGuest(),
                command.getAdults(),
                command.getChildrens(),
                command.getRatePlan(),
                command.getInvoicingDate(),
                command.getHotelCreationDate(),
                command.getOriginalAmount(),
                command.getAmountPaymentApplied(),
                command.getRateByAdult(),
                command.getRateByChild(),
                command.getRemarks(),
                command.getRoomNumber(),
                command.getHotelInvoiceAmount(),
                command.getHotelInvoiceNumber(),
                command.getInvoiceFolioNumber(),
                command.getQuote(),
                command.getRenewalNumber(),
                command.getHash()
        );
*/

        //processRate(dto);
    }

    private void processRate(RoomRateDto dto){
        RoomRateDto existingRate = service.findByTcaId(dto);

        if(Objects.isNull(existingRate)){
            service.create(dto);
            return;
        }

        if(!dto.getHash().equals(existingRate.getHash())){
            service.create(dto);
            updateExistingRateIfPending(existingRate);
        }
    }

    private void updateExistingRateIfPending(RoomRateDto existingRate) {
        if (existingRate.getStatus() == RoomRateStatus.PENDING) {
            existingRate.setUpdatedAt(LocalDateTime.now());
            existingRate.setStatus(RoomRateStatus.DELETED);
            service.update(existingRate);
        }
    }
}
