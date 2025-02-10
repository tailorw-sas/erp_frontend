package com.kynsoft.finamer.invoicing.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateManageEmployeeCommandHandler implements ICommandHandler<CreateManageEmployeeCommand> {

    private final IManageEmployeeService service;
    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;

    public CreateManageEmployeeCommandHandler(IManageEmployeeService service,
                                              IManageHotelService hotelService,
                                              IManageAgencyService agencyService) {
        this.service = service;
        this.hotelService = hotelService;
        this.agencyService = agencyService;
    }

    @Override
    public void handle(CreateManageEmployeeCommand command) {
        List<ManageHotelDto> hotels = this.hotelService.findByIds(command.getManageHotelList());
        List<ManageAgencyDto> agencys = this.agencyService.findByIds(command.getManageAgencyList());

        service.create(new ManageEmployeeDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail(),
                command.getPhoneExtension(),
                agencys,
                hotels
        ));

    }
}
