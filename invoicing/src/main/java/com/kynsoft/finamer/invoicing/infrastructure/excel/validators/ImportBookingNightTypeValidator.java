package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import java.util.List;
import java.util.Objects;

public class ImportBookingNightTypeValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageNightTypeService nightTypeService;
    private final IManageAgencyService agencyService;

    public ImportBookingNightTypeValidator(IManageNightTypeService nightTypeService,
                                           IManageAgencyService agencyService) {
        this.nightTypeService = nightTypeService;

        this.agencyService = agencyService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (isNightTypeMandatory(obj) && !this.validateIfNightTypeIsEmptyOrNull(obj, errorFieldList)) {
            return false;
        }
        if (isNightTypeMandatory(obj) && !this.validateIfNightTypeExist(obj, errorFieldList)) {
            return false;
        }
        return true;
    }

    private boolean isNightTypeMandatory(BookingRow obj) {
        if (Objects.nonNull(obj.getManageAgencyCode()) && !obj.getManageAgencyCode().isEmpty()
                && agencyService.existByCode(obj.getManageAgencyCode())) {
            ManageAgencyDto manageAgencyDto = agencyService.findByCode(obj.getManageAgencyCode());
            ManageClientDto clientDto = manageAgencyDto.getClient();
            if (Objects.nonNull(clientDto) &&
                    Objects.nonNull(clientDto.getIsNightType())
                    && clientDto.getIsNightType()
            ) {
                return true;
            }

        }
        return false;
    }

    private boolean validateIfNightTypeIsEmptyOrNull(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getNightType()) || obj.getNightType().isEmpty()) {
            errorFieldList.add(new ErrorField("Night Type", "Night Type can't be empty"));
            return false;
        }
        return true;
    }

    private boolean validateIfNightTypeExist(BookingRow obj, List<ErrorField> errorFieldList) {
        if (!nightTypeService.existNightTypeByCode(obj.getNightType())) {
            errorFieldList.add(new ErrorField("Night Type", "Night Type not exist"));
            return false;
        }
        return true;
    }
}
