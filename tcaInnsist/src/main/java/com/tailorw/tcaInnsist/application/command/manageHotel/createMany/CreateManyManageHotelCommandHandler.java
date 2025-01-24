package com.tailorw.tcaInnsist.application.command.manageHotel.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CreateManyManageHotelCommandHandler implements ICommandHandler<CreateManyManageHotelCommand> {

    private final IManageHotelService hotelService;
    private static final Logger log = LoggerFactory.getLogger(CreateManyManageHotelCommandHandler.class);

    @Override
    public void handle(CreateManyManageHotelCommand command) {
        List<ManageHotelDto> hotelDtos = command.getCreateManageHotelCommands()
                .stream()
                .map(createCommand -> {
                    return new ManageHotelDto(
                            createCommand.getId(),
                            createCommand.getCode(),
                            createCommand.getName(),
                            createCommand.getRoomType(),
                            createCommand.getTradingCompanyId()
                    );
                }).toList();
        hotelService.createMany(hotelDtos);
        log.info("Created many hotels");
    }
}
