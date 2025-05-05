package com.kynsoft.finamer.payment.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CommonImportValidators {

    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;
    private final IPaymentCloseOperationService closeOperationService;
    private final SecurityImportValidators securityImportValidators;

    public CommonImportValidators(IManageHotelService hotelService,
                                  IManageAgencyService agencyService,
                                  IPaymentCloseOperationService closeOperationService,
                                  SecurityImportValidators securityImportValidators) {
        this.hotelService = hotelService;
        this.agencyService = agencyService;
        this.closeOperationService = closeOperationService;
        this.securityImportValidators = securityImportValidators;
    }

    public boolean validateAgency(String agencyCode, List<ErrorField> errorFieldList, List<UUID> agencyList) {
        if (Objects.isNull(agencyCode)){
            errorFieldList.add(new ErrorField("Agency", "Agency can't be empty"));
            return false;
        }
        boolean existAgency = agencyService.existByCode(agencyCode);
        if (existAgency) {
            ManageAgencyDto manageAgencyDto = agencyService.findByCode(agencyCode);
            this.securityImportValidators.validateAgency(agencyList, manageAgencyDto.getId(), errorFieldList);
            if (Status.INACTIVE.name().equals(manageAgencyDto.getStatus())) {
                errorFieldList.add(new ErrorField("Agency", "The agency is inactive"));
                return false;
            }
        } else {
            errorFieldList.add(new ErrorField("Agency", "The agency not exist"));
            return false;
        }
        return true;
    }

    public boolean validateHotel(String hotelCode, List<ErrorField> errorFieldList, List<UUID> hotelList) {
        if (Objects.isNull(hotelCode)){
            errorFieldList.add(new ErrorField("Hotel", "Hotel can't be empty"));
            return false;
        }
        boolean existHotel = hotelService.existByCode(hotelCode);
        if (existHotel) {
            ManageHotelDto manageHotelDto = hotelService.findByCode(hotelCode);
            this.securityImportValidators.validateHotel(hotelList, manageHotelDto.getId(), errorFieldList);
            if (Status.INACTIVE.name().equals(manageHotelDto.getStatus())) {
                errorFieldList.add(new ErrorField("Hotel", "The hotel is inactive"));
                return false;
            }
        } else {
            errorFieldList.add(new ErrorField("Hotel", "The hotel not exist"));
            return false;
        }
        return true;
    }

    public boolean validateTransactionDate(String transactionDate, String dateFormat, List<ErrorField> errorFieldList) {
        if (Objects.isNull(transactionDate)){
            errorFieldList.add(new ErrorField("Transaction Date", "Transaction Date can't be empty"));
            return false;
        }
        boolean valid = DateUtil.validateDateFormat(transactionDate, dateFormat);
        if (!valid) {
            errorFieldList.add(new ErrorField("Transaction Date", "Invalid date format"));
            return false;
        }
        LocalDate dateToValidate= DateUtil.parseDateToLocalDate(transactionDate,dateFormat);
        if (dateToValidate.isAfter(LocalDate.now())) {
            errorFieldList.add(new ErrorField("Transaction Date", "Transaction Date can't be future date"));
            return false;
        }
        return true;
    }

    public boolean validateRemarks(String remarks, List<ErrorField> errorFieldList) {
        if (Objects.isNull(remarks)){
            errorFieldList.add(new ErrorField("Remarks", "Remarks can't be empty"));
            return false;
        }
        boolean valid = remarks.length() < 8000;
        if (!valid) {
            errorFieldList.add(new ErrorField("Remarks", "Field length is to long"));
            return false;
        }
        return true;
    }

    public boolean validateCloseOperation(String transactionDateArg, String hotelCode, String dateFormat, List<ErrorField> errorFieldList,boolean isValidTransactionDate) {
        if (hotelService.existByCode(hotelCode) &&
              isValidTransactionDate ) {
            ManageHotelDto manageHotelDto = hotelService.findByCode(hotelCode);
            if (Status.ACTIVE.name().equals(manageHotelDto.getStatus())) {
                PaymentCloseOperationDto paymentCloseOperationDto = closeOperationService.findByHotelId(manageHotelDto.getId());
                LocalDate beginDate = paymentCloseOperationDto.getBeginDate();
                LocalDate endDate = paymentCloseOperationDto.getEndDate();
                LocalDate transactionDate = DateUtil.parseDateToLocalDate(transactionDateArg, dateFormat);
                boolean result = transactionDate.isBefore(beginDate) || transactionDate.isAfter(endDate);
                if (result) {
                    errorFieldList.add(new ErrorField("Transaction Date", "Transaction Date is out of close operation"));
                    return false;
                }
            }
        }
        return true;
    }

}
