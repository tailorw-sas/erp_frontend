package com.kynsoft.finamer.insis.application.query.roomRate.countByHotelInvoiceDate;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.roomRate.CountRoomRateByHotelAndInvoiceDateResponse;
import com.kynsoft.finamer.insis.domain.dto.CountRoomRateByHotelAndInvoiceDateDto;
import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetCountRoomRateByHotelInvoiceDateHandler implements IQueryHandler<GetCountRoomRateByHotelInvoiceDateQuery, PaginatedResponse> {

    private final IRoomRateService roomRateService;
    private final IManageEmployeeService employeeService;

    public GetCountRoomRateByHotelInvoiceDateHandler(IRoomRateService roomRateService,
                                                     IManageEmployeeService employeeService){
        this.roomRateService = roomRateService;
        this.employeeService = employeeService;
    }

    @Override
    public PaginatedResponse handle(GetCountRoomRateByHotelInvoiceDateQuery query) {

        ManageEmployeeDto employee = employeeService.findById(query.getEmployeeId());
        List<UUID> hotelIds = employee.getManageHotelList().stream().map(ManageHotelDto::getId).toList();

        List<CountRoomRateByHotelAndInvoiceDateDto> counts = roomRateService.countByHotelsAndInvoiceDate(hotelIds, query.getFromInvoiceDate(), query.getToInvoiceDate());
        List<CountRoomRateByHotelAndInvoiceDateResponse> responses = counts.stream().map(CountRoomRateByHotelAndInvoiceDateResponse::new).toList();

        return new PaginatedResponse(responses, 1, responses.size(), (long) responses.size(), responses.size(), 0);
    }
}
