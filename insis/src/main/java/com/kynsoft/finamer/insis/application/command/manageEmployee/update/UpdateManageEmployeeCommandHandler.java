package com.kynsoft.finamer.insis.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import com.kynsoft.finamer.insis.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateManageEmployeeCommandHandler implements ICommandHandler<UpdateManageEmployeeCommand> {

    private final IManageEmployeeService service;
    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;

    public UpdateManageEmployeeCommandHandler(IManageEmployeeService service,
                                              IManageHotelService hotelService,
                                              IManageAgencyService agencyService){
        this.service = service;
        this.hotelService = hotelService;
        this.agencyService = agencyService;
    }

    @Override
    public void handle(UpdateManageEmployeeCommand command) {

        List<ManageHotelDto> hotels = this.hotelService.findByIds(command.getHotels());
        List<ManageAgencyDto> agencys = this.agencyService.findByIds(command.getAgencies());

        ManageEmployeeDto dto = new ManageEmployeeDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail(),
                command.getUpdatedAt(),
                hotels,
                agencys
        );

        service.update(dto);
    }
}
