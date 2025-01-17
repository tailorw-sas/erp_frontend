package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;

import java.util.List;
import java.util.Objects;

public class ImportBookingRoomTypeValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageRoomTypeService roomTypeService;

    public ImportBookingRoomTypeValidator( IManageRoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.nonNull(obj.getRoomType()) && !roomTypeService.existByCode(obj.getRoomType())) {
            errorFieldList.add(new ErrorField("Room Type", "Room Type not exist."));
            return false;
        }
        try {
            if (Objects.nonNull(obj.getRoomType())) {
                this.roomTypeService.findManageRoomTypenByCodeAndHotelCode(obj.getRoomType(), obj.getManageHotelCode());
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("Room Type", "The selected room type does not belong to the hotel."));
            return false;
        }
        return true;
    }


}
