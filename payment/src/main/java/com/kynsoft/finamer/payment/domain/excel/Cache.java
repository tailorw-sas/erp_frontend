package com.kynsoft.finamer.payment.domain.excel;

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
public class Cache {


    @Getter
    private Map<String, ManagePaymentTransactionTypeDto> managePaymentTransactionTypeMap;

    @Getter
    private Map<Long, ManageBookingDto> bookingsMap;

    @Getter
    private Map<Long, PaymentDto> paymentsMap;

    @Getter
    private Map<UUID, Map<Long, PaymentDetailDto>> paymentDetailsByPaymentsMap;

    @Getter
    private Map<String, List<ManageBookingDto>> bookingsByCouponMap;

    @Getter
    private Map<UUID, PaymentCloseOperationDto> closeOperationsByHotelMap;

    //Constructor de Cache para PaymentImportDetails
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

    public PaymentDto getPaymentByPaymentId(Long paymentId){
        if(Objects.isNull(this.paymentsMap) || this.paymentsMap.isEmpty()){
            //printLog("The paymentProjectionMap map is null or Empty");
            return null;
        }

        return this.paymentsMap.get(paymentId);
    }

    public ManageBookingDto getBooking(String bookingId){
        if(Objects.isNull(bookingId)){
            return null;
        }

        return this.bookingsMap.get(Long.parseLong(bookingId));
    }

    public PaymentDetailDto getPaymentDetailByPaymentId(UUID paymentId, Long paymentDetailGenId){
        Map<Long, PaymentDetailDto> paymentDetails = this.getPaymentDetailsByPaymentsMap().get(paymentId);
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
        if(Objects.isNull(this.getCloseOperationsByHotelMap()) || this.getCloseOperationsByHotelMap().isEmpty()){
            //printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        PaymentCloseOperationDto paymentCloseOperation = this.getCloseOperationsByHotelMap().get(hotel);
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

}
