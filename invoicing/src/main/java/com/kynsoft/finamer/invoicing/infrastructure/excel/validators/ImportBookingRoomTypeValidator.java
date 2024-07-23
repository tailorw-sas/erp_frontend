package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingRoomTypeValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageRoomTypeService roomTypeService;

    public ImportBookingRoomTypeValidator(ApplicationEventPublisher applicationEventPublisher, IManageRoomTypeService roomTypeService) {
        super(applicationEventPublisher);
        this.roomTypeService = roomTypeService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (!obj.getRoomType().isEmpty() &&
                !roomTypeService.existByCode(obj.getRoomType())) {
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Room Type", "Room Type not exist"),obj);
            return false;
        }
        return true;
    }


}
