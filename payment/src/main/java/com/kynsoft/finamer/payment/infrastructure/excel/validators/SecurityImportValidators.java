package com.kynsoft.finamer.payment.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import org.springframework.stereotype.Service;

@Service
public class SecurityImportValidators {

    private Cache cache;

    public SecurityImportValidators(){}

    public SecurityImportValidators(Cache cache){
        this.cache = cache;
    }

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

    public boolean validateAgency(Long paymentDetailId, List<ErrorField> errorFieldList) {
        PaymentDetailDto paymentDetailDto = this.cache.getPaymentDetailByPaymentDetailId(paymentDetailId);
        if(Objects.isNull(paymentDetailDto)){
            errorFieldList.add(new ErrorField("Transaction ID", "Payment Details not found." + paymentDetailId));
            return false;
        }

        ManageEmployeeDto employeeDto = this.cache.getEmployeeDto();
        if(Objects.isNull(employeeDto)){
            errorFieldList.add(new ErrorField("Employee ID", "Employee not found."));
            return false;
        }

        if (!this.getAgencyIds(employeeDto.getManageAgencyList()).contains(paymentDetailDto.getPayment().getAgency().getId())) {
            errorFieldList.add(new ErrorField("Agency", "The employee does not have access to the agency."));
            return false;
        }

        return true;
    }

    public boolean validateHotel(Long paymentDetailId, List<ErrorField> errorFieldList) {
        PaymentDetailDto paymentDetailDto = this.cache.getPaymentDetailByPaymentDetailId(paymentDetailId);
        if(Objects.isNull(paymentDetailDto)){
            errorFieldList.add(new ErrorField("Transaction ID", "Payment Details not found." + paymentDetailId));
            return false;
        }

        ManageEmployeeDto employeeDto = this.cache.getEmployeeDto();
        if(Objects.isNull(employeeDto)){
            errorFieldList.add(new ErrorField("Employee ID", "Employee not found."));
            return false;
        }

        if (!this.getHotelIds(employeeDto.getManageHotelList()).contains(paymentDetailDto.getPayment().getHotel().getId())) {
            errorFieldList.add(new ErrorField("Hotel", "The employee does not have access to the hotel."));
            return false;
        }

        return true;
    }

    private List<UUID> getAgencyIds(List<ManageAgencyDto> agencies){
        return agencies.stream()
                .map(ManageAgencyDto::getId)
                .toList();
    }

    private List<UUID> getHotelIds(List<ManageHotelDto> hotels){
        return hotels.stream()
                .map(ManageHotelDto::getId)
                .toList();
    }

}
