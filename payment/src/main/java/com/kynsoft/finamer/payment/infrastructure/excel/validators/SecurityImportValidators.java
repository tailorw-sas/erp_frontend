package com.kynsoft.finamer.payment.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SecurityImportValidators {

    public boolean validateAgency(List<UUID> toValidate, UUID agencyId, List<ErrorField> errorFieldList) {
        if (!toValidate.contains(agencyId)) {
            errorFieldList.add(new ErrorField("Agency", "The employee does not have access to the agency."));
            return false;
        }
        return true;
    }

    public boolean validateHotel(List<UUID> toValidate, UUID hotelId, List<ErrorField> errorFieldList) {
        if (!toValidate.contains(hotelId)) {
            errorFieldList.add(new ErrorField("Hotel", "The employee does not have access to the hotel."));
            return false;
        }
        return true;
    }

}
