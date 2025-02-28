package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.PaymentExcelExporterCellColorEnum;
import com.kynsoft.finamer.payment.domain.dtoEnum.PaymentExcelExporterEnum;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class ExcelExporterService {

    private final IPaymentService paymentService;

    public ExcelExporterService(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public byte[] exportToExcel(Pageable pageable, List<FilterCriteria> filterCriteria, PaymentExcelExporterEnum eenum) throws Exception {
        PaginatedResponse responseAll = this.paymentService.searchExcelExporter(pageable, filterCriteria);
        List<PaymentDto> responses = responseAll.getData();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Paginated Results");
        createHeaderPayment(sheet, workbook);

        int rowNum = 1;
        switch (eenum) {
            case EXPORT_HIERARCHY -> {
                createHeaderPayment(sheet, workbook);
                for (PaymentDto entity : responses) {
                    Row dataRow = sheet.createRow(rowNum++);

                    CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.BLUE);
                    addPaymentData(entity, style, dataRow);
                    if (entity.getPaymentDetails() != null) {
                        createHeaderPaymentDetails(sheet, workbook, rowNum++);
                        for (PaymentDetailDto paymentDetail : entity.getPaymentDetails()) {
                            Row dataRowPaymentDetail = sheet.createRow(rowNum++);
                            addPaymentDetailsData(paymentDetail, style, dataRowPaymentDetail);
                        }
                    }

                }
            }

            case VISUAL_SETTING -> {
                createHeaderPayment(sheet, workbook);
                for (PaymentDto entity : responses) {
                    Row dataRow = sheet.createRow(rowNum++);

                    CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.BLUE);
                    addPaymentData(entity, style, dataRow);
                }
            }

            case EXPORT_SUMMARY -> {
                createHeaderPayment(sheet, workbook);
                Double pBalance = 0.0;
                Double dAmount = 0.0;
                Double applied = 0.0;
                Double notApplied = 0.0;

                //Status
                int appliedStatus = 0;
                int confirmedStatus = 0;
                int cancelledStatus = 0;
                int transitStatus = 0;

                //Importe por Status
                Double appliedImpStatus = 0.0;
                Double confirmedImpStatus = 0.0;
                Double cancelledImpStatus = 0.0;
                Double transitImpStatus = 0.0;
                for (PaymentDto entity : responses) {
                    Row dataRow = sheet.createRow(rowNum++);
                    pBalance = pBalance + entity.getPaymentAmount();
                    dAmount = dAmount + entity.getDepositBalance();
                    applied = applied + entity.getApplied();
                    notApplied = notApplied + entity.getNotApplied();

                    if (entity.getPaymentStatus().getApplied()) {
                        appliedStatus ++;
                        appliedImpStatus = appliedImpStatus + entity.getPaymentAmount();
                    }
                    if (entity.getPaymentStatus().isCancelled()) {
                        cancelledStatus ++;
                        cancelledImpStatus = cancelledImpStatus + entity.getPaymentAmount();
                    }
                    if (entity.getPaymentStatus().isConfirmed()) {
                        confirmedStatus ++;
                        confirmedImpStatus = confirmedImpStatus + entity.getPaymentAmount();
                    }
                    CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.BLUE);
                    addPaymentData(entity, style, dataRow);
                }
                createHeaderTotalPayment(sheet, workbook, rowNum, pBalance, dAmount, applied, notApplied, responseAll.getTotalElementsPage(), appliedStatus, confirmedStatus, 
                                         cancelledStatus, transitStatus, confirmedImpStatus, appliedImpStatus, cancelledImpStatus, transitImpStatus);
            }

            default -> throw new IllegalArgumentException("No se ha especificado una opción válida en PaymentExcelExporterEnum");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    private boolean attachemntSupport(List<MasterPaymentAttachmentDto> attachment) {
        if (attachment.isEmpty()) {
            return false;
        }
        for (MasterPaymentAttachmentDto masterPaymentAttachmentDto : attachment) {
            if (masterPaymentAttachmentDto.getAttachmentType().getDefaults()) {
                return true;
            }
        }
        return false;
    }

    private void addPaymentData(PaymentDto entity, CellStyle style, Row dataRow) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        dataRow.createCell(0).setCellValue(entity.getPaymentId());
        dataRow.getCell(0).setCellStyle(style);

        //dataRow.createCell(1).setCellValue(!entity.getAttachments().isEmpty());
        dataRow.createCell(1).setCellValue(attachemntSupport(entity.getAttachments()));
        dataRow.getCell(1).setCellStyle(style);

        dataRow.createCell(2).setCellValue(entity.getPaymentSource().getName());
        dataRow.getCell(2).setCellStyle(style);

        String time = entity.getTransactionDateTime() != null ? entity.getTransactionDateTime().toString() : "00:00:00";
        dataRow.createCell(3).setCellValue(entity.getTransactionDate().toString() + " " + time);
        dataRow.getCell(3).setCellStyle(style);

        dataRow.createCell(4).setCellValue(entity.getHotel().getCode() + "-" + entity.getHotel().getName());
        dataRow.getCell(4).setCellStyle(style);

        dataRow.createCell(5).setCellValue(entity.getClient().getCode() + "-" + entity.getClient().getName());
        dataRow.getCell(5).setCellStyle(style);

        dataRow.createCell(6).setCellValue(entity.getAgency().getCode() + "-" + entity.getAgency().getName());
        dataRow.getCell(6).setCellStyle(style);

        dataRow.createCell(7).setCellValue(entity.getAgency().getAgencyType().getCode() + "-" + entity.getAgency().getAgencyType().getName());
        dataRow.getCell(7).setCellStyle(style);

        dataRow.createCell(8).setCellValue(entity.getBankAccount() != null ? entity.getBankAccount().getAccountNumber() + "-" + entity.getBankAccount().getNameOfBank() : null);
        dataRow.getCell(8).setCellStyle(style);

        dataRow.createCell(9).setCellValue(decimalFormat.format(entity.getPaymentAmount()));
        dataRow.getCell(9).setCellStyle(style);

        dataRow.createCell(10).setCellValue(decimalFormat.format(entity.getDepositBalance()));
        dataRow.getCell(10).setCellStyle(style);

        dataRow.createCell(11).setCellValue(decimalFormat.format(entity.getApplied() != null ? entity.getApplied() : 0.00));
        dataRow.getCell(11).setCellStyle(style);

        dataRow.createCell(12).setCellValue(decimalFormat.format(entity.getNotApplied() != null ? entity.getNotApplied() : 0.00));
        dataRow.getCell(12).setCellStyle(style);

        dataRow.createCell(13).setCellValue(entity.getRemark());
        dataRow.getCell(13).setCellStyle(style);

        dataRow.createCell(14).setCellValue(entity.getPaymentStatus().getName());
        dataRow.getCell(14).setCellStyle(style);

    }

    private void createHeaderPayment(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.WHITE);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.getCell(0).setCellStyle(style);
        headerRow.createCell(1).setCellValue("Has Attachment");
        headerRow.getCell(1).setCellStyle(style);
        headerRow.createCell(2).setCellValue("P. Source");
        headerRow.getCell(2).setCellStyle(style);
        headerRow.createCell(3).setCellValue("Trans. Date");
        headerRow.getCell(3).setCellStyle(style);
        headerRow.createCell(4).setCellValue("Hotel");
        headerRow.getCell(4).setCellStyle(style);
        headerRow.createCell(5).setCellValue("Client");
        headerRow.getCell(5).setCellStyle(style);
        headerRow.createCell(6).setCellValue("Agency");
        headerRow.getCell(6).setCellStyle(style);
        headerRow.createCell(7).setCellValue("Agency Type");
        headerRow.getCell(7).setCellStyle(style);
        headerRow.createCell(8).setCellValue("Bank Acc.");
        headerRow.getCell(8).setCellStyle(style);
        headerRow.createCell(9).setCellValue("P. Amount");
        headerRow.getCell(9).setCellStyle(style);
        headerRow.createCell(10).setCellValue("D. Balance");
        headerRow.getCell(10).setCellStyle(style);
        headerRow.createCell(11).setCellValue("Applied");
        headerRow.getCell(11).setCellStyle(style);
        headerRow.createCell(12).setCellValue("Not Applied");
        headerRow.getCell(12).setCellStyle(style);
        headerRow.createCell(13).setCellValue("Remark");
        headerRow.getCell(13).setCellStyle(style);
        headerRow.createCell(14).setCellValue("Status");
        headerRow.getCell(14).setCellStyle(style);
    }

    private void createHeaderTotalPayment(Sheet sheet, Workbook workbook, int rowNum, Double pBalance, Double dAmount, Double applied, Double notApplied, int total,
                                          int appliedStatus, int confirmedStatus, int cancelledStatus, int transitStatus, Double confirmedImpStatus, Double appliedImpStatus,
                                          Double cancelledImpStatus, Double transitImpStatus) {
        Row headerRow = sheet.createRow(rowNum);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.WHITE);
        headerRow.createCell(0).setCellValue("Total #" + total);
        headerRow.getCell(0).setCellStyle(style);

        headerRow.createCell(3).setCellValue("Transit #" + transitStatus);
        headerRow.getCell(3).setCellStyle(style);

        headerRow.createCell(4).setCellValue("Can. #" + cancelledStatus);
        headerRow.getCell(4).setCellStyle(style);

        headerRow.createCell(5).setCellValue("Conf. #" + confirmedStatus);
        headerRow.getCell(5).setCellStyle(style);

        headerRow.createCell(6).setCellValue("Appl. #" + appliedStatus);
        headerRow.getCell(6).setCellStyle(style);

        Row headerRow1 = sheet.createRow(rowNum + 1);
        headerRow1.createCell(0).setCellValue("Total $"  + decimalFormat.format(pBalance));
        headerRow1.getCell(0).setCellStyle(style);

        headerRow1.createCell(3).setCellValue("Transit $" + decimalFormat.format(transitImpStatus));
        headerRow1.getCell(3).setCellStyle(style);

        headerRow1.createCell(4).setCellValue("Can. $" + decimalFormat.format(cancelledImpStatus));
        headerRow1.getCell(4).setCellStyle(style);

        headerRow1.createCell(5).setCellValue("Conf. $" + decimalFormat.format(confirmedImpStatus));
        headerRow1.getCell(5).setCellStyle(style);

        headerRow1.createCell(6).setCellValue("Applied. $" + decimalFormat.format(appliedImpStatus));
        headerRow1.getCell(6).setCellStyle(style);

        headerRow1.createCell(10).setCellValue("Dep. $" + decimalFormat.format(dAmount));
        headerRow1.getCell(10).setCellStyle(style);
        headerRow1.createCell(11).setCellValue("Appl. $" + decimalFormat.format(applied));
        headerRow1.getCell(11).setCellStyle(style);
        headerRow1.createCell(12).setCellValue("N. Appl. $" + decimalFormat.format(notApplied));
        headerRow1.getCell(12).setCellStyle(style);
    }

    private CellStyle createFont(Workbook workbook, PaymentExcelExporterCellColorEnum color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");

        font.setFontHeightInPoints((short)12);

        style.setFont(font);
        return style;
    }

    private void addPaymentDetailsData(PaymentDetailDto entity, CellStyle style, Row dataRow) {
        dataRow.createCell(0).setCellValue(entity.getPaymentDetailId());
        dataRow.getCell(0).setCellStyle(style);

        dataRow.createCell(1).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getBookingId().toString() : "");
        dataRow.getCell(1).setCellStyle(style);

        dataRow.createCell(2).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getInvoice().getInvoiceNo().toString() : "");
        dataRow.getCell(2).setCellStyle(style);

        dataRow.createCell(3).setCellValue(entity.getTransactionDate() != null ? entity.getTransactionDate().toString() : "");
        dataRow.getCell(3).setCellStyle(style);

        dataRow.createCell(4).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getFirstName() : "");
        dataRow.getCell(4).setCellStyle(style);

        dataRow.createCell(5).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getLastName() : "");
        dataRow.getCell(5).setCellStyle(style);

        dataRow.createCell(6).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getReservationNumber() : "");
        dataRow.getCell(6).setCellStyle(style);

        dataRow.createCell(7).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getCouponNumber() : "");
        dataRow.getCell(7).setCellStyle(style);

        if (entity.getManageBooking() != null) {
            dataRow.createCell(8).setCellValue(entity.getManageBooking().getAdults() != null ? entity.getManageBooking().getAdults().toString() : "");
            dataRow.getCell(8).setCellStyle(style);
        } else {
            dataRow.createCell(8).setCellValue("");
            dataRow.getCell(8).setCellStyle(style);
        }

        if (entity.getManageBooking() != null) {
            dataRow.createCell(9).setCellValue(entity.getManageBooking().getChildren() != null ? entity.getManageBooking().getChildren().toString() : "");
            dataRow.getCell(9).setCellStyle(style);
        } else {
            dataRow.createCell(9).setCellValue("");
            dataRow.getCell(9).setCellStyle(style);
        }

        dataRow.createCell(10).setCellValue("0.0");
        dataRow.getCell(10).setCellStyle(style);

        dataRow.createCell(11).setCellValue(entity.getAmount());
        dataRow.getCell(11).setCellStyle(style);

        dataRow.createCell(12).setCellValue(entity.getTransactionType().getName());
        dataRow.getCell(12).setCellStyle(style);

        dataRow.createCell(13).setCellValue(entity.getParentId() != null ? entity.getParentId().toString() : "");
        dataRow.getCell(13).setCellStyle(style);

        dataRow.createCell(14).setCellValue("Group Id");
        dataRow.getCell(14).setCellStyle(style);

        dataRow.createCell(15).setCellValue(entity.getRemark());
        dataRow.getCell(15).setCellStyle(style);
    }

    private void createHeaderPaymentDetails(Sheet sheet, Workbook workbook, int rowNum) {
        Row headerRow = sheet.createRow(rowNum);
        CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.WHITE);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.getCell(0).setCellStyle(style);
        headerRow.createCell(1).setCellValue("Booking Id");
        headerRow.getCell(1).setCellStyle(style);
        headerRow.createCell(2).setCellValue("Invoice No");
        headerRow.getCell(2).setCellStyle(style);
        headerRow.createCell(3).setCellValue("Transaction Date");
        headerRow.getCell(3).setCellStyle(style);
        headerRow.createCell(4).setCellValue("First Name");
        headerRow.getCell(4).setCellStyle(style);
        headerRow.createCell(5).setCellValue("Last Name");
        headerRow.getCell(5).setCellStyle(style);
        headerRow.createCell(6).setCellValue("Reservation");
        headerRow.getCell(6).setCellStyle(style);
        headerRow.createCell(7).setCellValue("Coupon No");
        headerRow.getCell(7).setCellStyle(style);
        headerRow.createCell(8).setCellValue("Adults");
        headerRow.getCell(8).setCellStyle(style);
        headerRow.createCell(9).setCellValue("Children");
        headerRow.getCell(9).setCellStyle(style);
        headerRow.createCell(10).setCellValue("Deposit");
        headerRow.getCell(10).setCellStyle(style);
        headerRow.createCell(11).setCellValue("Amount");
        headerRow.getCell(11).setCellStyle(style);
        headerRow.createCell(12).setCellValue("Transaction Type");
        headerRow.getCell(12).setCellStyle(style);
        headerRow.createCell(13).setCellValue("Parent Id");
        headerRow.getCell(13).setCellStyle(style);
        headerRow.createCell(14).setCellValue("Group Id");
        headerRow.getCell(14).setCellStyle(style);
        headerRow.createCell(15).setCellValue("Remark");
        headerRow.getCell(15).setCellStyle(style);
    }
}
