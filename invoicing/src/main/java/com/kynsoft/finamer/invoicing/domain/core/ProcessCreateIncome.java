package com.kynsoft.finamer.invoicing.domain.core;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckAmountNotZeroRule;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ProcessCreateIncome {

    private final UUID id;
    private final InvoiceCloseOperationDto closeOperationDto;
    private final LocalDateTime invoiceDate;
    private final LocalDate dueDate;
    private final boolean manual;
    private final ManageAgencyDto agency;
    private final ManageHotelDto hotel;
    private final boolean reSend;
    private final LocalDate reSendDate;
    private final ManageInvoiceTypeDto invoiceType;
    private final ManageInvoiceStatusDto invoiceStatus;
    private final String employeeName;
    private final List<IncomeAdjustment> adjustmentRequests;

    public ProcessCreateIncome(UUID id,
                               InvoiceCloseOperationDto closeOperationDto,
                               LocalDateTime invoiceDate, LocalDate dueDate, boolean manual,
                               ManageAgencyDto agency, ManageHotelDto hotel, boolean reSend, LocalDate reSendDate,
                               ManageInvoiceTypeDto invoiceType, ManageInvoiceStatusDto invoiceStatus,
                               String employeeName,
                               List<IncomeAdjustment> adjustmentRequests){
        this.id = id;
        this.closeOperationDto = closeOperationDto;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.manual = manual;
        this.agency = agency;
        this.hotel = hotel;
        this.reSend = reSend;
        this.reSendDate = reSendDate;
        this.invoiceType = invoiceType;
        this.invoiceStatus = invoiceStatus;
        this.employeeName = employeeName;
        this.adjustmentRequests = adjustmentRequests;
    }


    public ManageInvoiceDto process(){
        ManageInvoiceDto income = this.generateNewManageInvoice(id,
                invoiceDate,
                dueDate,
                manual,
                hotel,
                agency,
                reSend,
                reSendDate,
                invoiceType,
                invoiceStatus);
        income.setOriginalAmount(0.0);

        this.createAdjustments(income, this.adjustmentRequests, this.employeeName, closeOperationDto);

        return income;
    }

    private ManageInvoiceDto generateNewManageInvoice(UUID id,
                                                      LocalDateTime invoiceDate,
                                                      LocalDate invoiceDueDate,
                                                      boolean manual,
                                                      ManageHotelDto hotelDto,
                                                      ManageAgencyDto agencyDto,
                                                      boolean isReSend,
                                                      LocalDate reSendDate,
                                                      ManageInvoiceTypeDto invoiceTypeDto,
                                                      ManageInvoiceStatusDto invoiceStatusDto){
        return  new ManageInvoiceDto(
                id,
                0L,
                0L,
                "",
                "",
                invoiceDate,
                invoiceDueDate,
                manual,
                0.0,
                0.0,
                hotelDto,
                agencyDto,
                EInvoiceType.INCOME,
                EInvoiceStatus.SENT,
                Boolean.FALSE,
                null,
                null,
                isReSend,
                reSendDate,
                invoiceTypeDto,
                invoiceStatusDto,
                null,
                false,
                null, 0.0,0
        );
    }

    private void createAdjustments(ManageInvoiceDto incomeDto,
                                   List<IncomeAdjustment> adjustmentRequests,
                                   String employeeFullName,
                                   InvoiceCloseOperationDto closeOperationDto){
        if(Objects.nonNull(adjustmentRequests) && !adjustmentRequests.isEmpty()){
            Double invoiceAmount = 0.0;
            List<ManageAdjustmentDto> adjustmentDtos = new ArrayList<>();

            for (IncomeAdjustment adjustment : adjustmentRequests) {
                // Puede ser + y -, pero no puede ser 0
                RulesChecker.checkRule(new CheckAmountNotZeroRule(adjustment.getAmount()));
                RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(adjustment.getDate()));

                adjustmentDtos.add(new ManageAdjustmentDto(
                        UUID.randomUUID(),
                        0L,
                        adjustment.getAmount(),
                        getInvoiceDate(closeOperationDto, adjustment.getDate().atStartOfDay()),
                        adjustment.getRemark(),
                        null,
                        adjustment.getTransactionType(),
                        null,
                        employeeFullName,
                        false
                ));
                invoiceAmount += adjustment.getAmount();
            }
            invoiceAmount = BankerRounding.round(invoiceAmount);

            ManageRoomRateDto roomRateDto = this.generateNewManageRoomRate();
            roomRateDto.setInvoiceAmount(invoiceAmount);
            roomRateDto.setAdjustments(adjustmentDtos);

            List<ManageRoomRateDto> roomRates = new ArrayList<>();
            roomRates.add(roomRateDto);

            ManageBookingDto bookingDto = this.generateNewManageBooking(incomeDto, invoiceAmount, roomRates);

            incomeDto.setInvoiceAmount(invoiceAmount);
            incomeDto.setDueAmount(invoiceAmount);
            incomeDto.setOriginalAmount(invoiceAmount);
            incomeDto.setBookings(List.of(bookingDto));
        }
    }

    private LocalDateTime getInvoiceDate(InvoiceCloseOperationDto closeOperationDto, LocalDateTime invoiceDate) {
        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate(), invoiceDate.toLocalDate())) {
            return invoiceDate;
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now());
    }

    private ManageRoomRateDto generateNewManageRoomRate(){
        return new ManageRoomRateDto(
                UUID.randomUUID(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0.0,
                null,
                null,
                null,
                null,
                null,
                0.00,
                "",
                null,
                null,
                null,
                false,
                null
        );
    }

    private ManageBookingDto generateNewManageBooking(ManageInvoiceDto incomeDto, Double invoiceAmount, List<ManageRoomRateDto> roomRates){
        return new ManageBookingDto(
                UUID.randomUUID(),
                0L,
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                invoiceAmount,
                invoiceAmount,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0.00,
                null,
                null,
                null,
                null,
                null,
                null,
                roomRates,
                null,
                null,
                null,
                false,
                null
        );
    }
}
