package com.kynsoft.finamer.payment.domain.excel;

import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Cache implements ICache {


    private Map<String, ManagePaymentTransactionTypeDto> managePaymentTransactionTypeMap;
    private Map<Long, ManageBookingDto> bookingsMap;
    private Map<Long, PaymentDto> paymentsMap;
    private Map<UUID, Map<Long, PaymentDetailDto>> paymentDetailsByPaymentsMap;
    private Map<String, List<ManageBookingDto>> bookingsByCouponMap;
    private Map<UUID, PaymentCloseOperationDto> closeOperationsByHotelMap;
    private Map<Long, ManageInvoiceDto> invoicesByInvoiceIdMap;
    private Map<Long, PaymentDetailDto> paymentDetailsByPaymentDetailIdMap;

    //Constructor de Cache para PaymentImportDetails
    public Cache(List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList,
                 List<ManageBookingDto> bookings,
                 List<PaymentDto> paymentList,
                 List<PaymentDetailDto> paymentDetailList,
                 List<ManageBookingDto> bookingList,
                 List<PaymentCloseOperationDto> closeOperationList,
                 List<ManageInvoiceDto> invoiceList,
                 List<PaymentDetailDto> paymentDetailListById){
        this.setManagePaymentTransactionTypeMap(managePaymentTransactionTypeList);
        this.setBookingsMap(bookings);
        this.setPaymentsMap(paymentList);
        this.setPaymentDetailsByPaymentsMap(paymentDetailList);
        this.setBookingsByCouponMap(bookingList);
        this.setCloseOperationsByHotelMap(closeOperationList);
        this.setInvoicesByInvoiceIdMap(invoiceList);
        this.setPaymentDetailsByPaymentDetailIdMap(paymentDetailListById);
    }

    public Cache(List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList,
                 List<ManageBookingDto> bookings,
                 List<PaymentDto> paymentList,
                 List<PaymentDetailDto> paymentDetailList,
                 List<ManageBookingDto> bookingList,
                 List<PaymentCloseOperationDto> closeOperationList){
        this.setManagePaymentTransactionTypeMap(managePaymentTransactionTypeList);
        this.setBookingsMap(bookings);
        this.setPaymentsMap(paymentList);
        this.setPaymentDetailsByPaymentsMap(paymentDetailList);
        this.setBookingsByCouponMap(bookingList);
        this.setCloseOperationsByHotelMap(closeOperationList);
    }

    /// Setters de los mapas
    private void setManagePaymentTransactionTypeMap(List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList){
        this.managePaymentTransactionTypeMap = managePaymentTransactionTypeList.stream()
                .collect(Collectors.toMap(ManagePaymentTransactionTypeDto::getCode, managePaymentTransactionTypeDto -> managePaymentTransactionTypeDto));
    }

    private void setBookingsMap(List<ManageBookingDto> bookings){
        this.bookingsMap = bookings.stream()
                .collect(Collectors.toMap(ManageBookingDto::getBookingId, booking -> booking));
    }

    private void setPaymentsMap(List<PaymentDto> paymentList){
        this.paymentsMap = paymentList.stream()
                .collect(Collectors.toMap(PaymentDto::getPaymentId, payment -> payment));
    }

    private void setPaymentDetailsByPaymentsMap(List<PaymentDetailDto> paymentDetailList){
        this.paymentDetailsByPaymentsMap = paymentDetailList.stream()
                .collect(Collectors.groupingBy(paymentDetail -> paymentDetail.getPayment().getId(), Collectors.toMap(PaymentDetailDto::getPaymentDetailId, paymentDetail -> paymentDetail)));
    }

    private void setBookingsByCouponMap(List<ManageBookingDto> bookingList){
        this.bookingsByCouponMap = bookingList.stream()
                .collect(Collectors.groupingBy(ManageBookingDto::getCouponNumber));
    }

    private void setCloseOperationsByHotelMap(List<PaymentCloseOperationDto> closeOperationList){
        this.closeOperationsByHotelMap = closeOperationList.stream()
                .collect(Collectors.toMap(closeOperation -> closeOperation.getHotel().getId(), paymentCloseOperation -> paymentCloseOperation));
    }

    private void setInvoicesByInvoiceIdMap(List<ManageInvoiceDto> invoiceList){
        this.invoicesByInvoiceIdMap = invoiceList.stream()
                .collect(Collectors.toMap(ManageInvoiceDto::getInvoiceId, invoice -> invoice));
    }

    private void setPaymentDetailsByPaymentDetailIdMap(List<PaymentDetailDto> paymentDetailList){
        this.paymentDetailsByPaymentDetailIdMap = paymentDetailList.stream()
                .collect(Collectors.toMap(PaymentDetailDto::getPaymentDetailId, paymentDetail -> paymentDetail));
    }
    /// Metodos para consultas sobre los mapas

    public PaymentDto getPaymentByPaymentId(Long paymentId){
        if(Objects.isNull(this.paymentsMap) || this.paymentsMap.isEmpty()){
            //printLog("The paymentProjectionMap map is null or Empty");
            return null;
        }

        return this.paymentsMap.get(paymentId);
    }

    public ManageBookingDto getBooking(Long bookingId){
        if(Objects.isNull(bookingId)){
            return null;
        }

        return this.bookingsMap.get(bookingId);
    }

    public PaymentDetailDto getPaymentDetailByPaymentId(UUID paymentId, Long paymentDetailGenId){
        Map<Long, PaymentDetailDto> paymentDetails = this.paymentDetailsByPaymentsMap.get(paymentId);
        return paymentDetails.get(paymentDetailGenId);
    }

    public List<ManageBookingDto> getBookingsByCoupon(String coupon){
        if(Objects.isNull(this.bookingsByCouponMap) || this.bookingsByCouponMap.isEmpty()){
            //printLog("The bookingControlAmountBalanceByCouponMap map is null or Empty");
            return null;
        }

        return this.bookingsByCouponMap.get(coupon);
    }

    public ManagePaymentTransactionTypeDto getPaymentInvoiceTransactionType(){
        if(Objects.isNull(this.managePaymentTransactionTypeMap) || this.managePaymentTransactionTypeMap.isEmpty()){
            //printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return this.managePaymentTransactionTypeMap.values().stream()
                .filter(ManagePaymentTransactionTypeDto::getPaymentInvoice)
                .findFirst()
                .orElse(null);
    }

    public ManagePaymentTransactionTypeDto getDepositTransactionType(){
        if(Objects.isNull(this.managePaymentTransactionTypeMap) || this.managePaymentTransactionTypeMap.isEmpty()){
            //printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return this.managePaymentTransactionTypeMap.values().stream()
                .filter(ManagePaymentTransactionTypeDto::getDeposit)
                .findFirst()
                .orElse(null);
    }

    public OffsetDateTime getTransactionDateByHotelId(UUID hotel){
        if(Objects.isNull(this.closeOperationsByHotelMap) || this.closeOperationsByHotelMap.isEmpty()){
            //printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        PaymentCloseOperationDto paymentCloseOperation = this.closeOperationsByHotelMap.get(hotel);
        if (DateUtil.getDateForCloseOperation(paymentCloseOperation.getBeginDate(), paymentCloseOperation.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(paymentCloseOperation.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    public ManagePaymentTransactionTypeDto getManageTransactionTypeByCode(String code){
        if(Objects.isNull(this.managePaymentTransactionTypeMap) || this.managePaymentTransactionTypeMap.isEmpty()){
            //printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return this.managePaymentTransactionTypeMap.get(code);
    }

    public ManageInvoiceDto getInvoiceByInvoiceId(Long invoiceId){
        if(Objects.isNull(invoicesByInvoiceIdMap) || invoicesByInvoiceIdMap.isEmpty()){
            return null;
        }

        return invoicesByInvoiceIdMap.get(invoiceId);
    }

    public PaymentDetailDto getPaymentDetailByPaymentDetailId(Long paymentDetailId){
        return paymentDetailsByPaymentDetailIdMap.get(paymentDetailId);
    }

}
