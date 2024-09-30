package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.PaymentExcelExporterCellColorEnum;
import com.kynsoft.finamer.payment.domain.dtoEnum.PaymentExcelExporterEnum;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Service
public class ExcelExporterService {

    private final IPaymentService paymentService;

    public ExcelExporterService(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public byte[] exportToExcel(Pageable pageable, List<FilterCriteria> filterCriteria, PaymentExcelExporterEnum eenum) throws Exception {
        List<PaymentDto> responses = this.paymentService.searchExcelExporter(pageable, filterCriteria).getData();

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
                for (PaymentDto entity : responses) {
                    Row dataRow = sheet.createRow(rowNum++);

                    CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.BLUE);
                    addPaymentData(entity, style, dataRow);
                }
            }

            default -> throw new IllegalArgumentException("No se ha especificado una opción válida en PaymentExcelExporterEnum");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    private void addPaymentData(PaymentDto entity, CellStyle style, Row dataRow) {
        dataRow.createCell(0).setCellValue(entity.getPaymentId());
        dataRow.getCell(0).setCellStyle(style);

        dataRow.createCell(1).setCellValue(entity.getPaymentSource().getName());
        dataRow.getCell(1).setCellStyle(style);

        dataRow.createCell(2).setCellValue(entity.getTransactionDate());
        dataRow.getCell(2).setCellStyle(style);

        dataRow.createCell(3).setCellValue(entity.getPaymentStatus().getName());
        dataRow.getCell(3).setCellStyle(style);

        dataRow.createCell(4).setCellValue(entity.getHotel().getName());
        dataRow.getCell(4).setCellStyle(style);

        dataRow.createCell(5).setCellValue(entity.getClient().getName());
        dataRow.getCell(5).setCellStyle(style);

        dataRow.createCell(6).setCellValue(entity.getAgency().getAgencyType().getName());
        dataRow.getCell(6).setCellStyle(style);

        dataRow.createCell(7).setCellValue(entity.getAgency().getName());
        dataRow.getCell(7).setCellStyle(style);

        dataRow.createCell(8).setCellValue("A.T");
        dataRow.getCell(8).setCellStyle(style);

        dataRow.createCell(9).setCellValue(entity.getBankAccount() != null ? entity.getBankAccount().getNameOfBank() : null);
        dataRow.getCell(9).setCellStyle(style);

        dataRow.createCell(10).setCellValue(entity.getPaymentAmount());
        dataRow.getCell(10).setCellStyle(style);

        dataRow.createCell(11).setCellValue(entity.getDepositBalance());
        dataRow.getCell(11).setCellStyle(style);

        dataRow.createCell(12).setCellValue(entity.getApplied());
        dataRow.getCell(12).setCellStyle(style);

        dataRow.createCell(13).setCellValue(entity.getNotApplied());
        dataRow.getCell(13).setCellStyle(style);

        dataRow.createCell(14).setCellValue(entity.getRemark());
        dataRow.getCell(14).setCellStyle(style);
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

        dataRow.createCell(8).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getAdults().toString() : "");
        dataRow.getCell(8).setCellStyle(style);

        dataRow.createCell(9).setCellValue(entity.getManageBooking() != null ? entity.getManageBooking().getChildren().toString() : "");
        dataRow.getCell(9).setCellStyle(style);

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

    private void createHeaderPayment(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = createFont(workbook, PaymentExcelExporterCellColorEnum.WHITE);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.getCell(0).setCellStyle(style);
        headerRow.createCell(1).setCellValue("P.S.");
        headerRow.getCell(1).setCellStyle(style);
        headerRow.createCell(2).setCellValue("Trans. Date");
        headerRow.getCell(2).setCellStyle(style);
        headerRow.createCell(3).setCellValue("Status");
        headerRow.getCell(3).setCellStyle(style);
        headerRow.createCell(4).setCellValue("Hotel");
        headerRow.getCell(4).setCellStyle(style);
        headerRow.createCell(5).setCellValue("Client");
        headerRow.getCell(5).setCellStyle(style);
        headerRow.createCell(6).setCellValue("Agency Cd");
        headerRow.getCell(6).setCellStyle(style);
        headerRow.createCell(7).setCellValue("Agency");
        headerRow.getCell(7).setCellStyle(style);
        headerRow.createCell(8).setCellValue("A.T.");
        headerRow.getCell(8).setCellStyle(style);
        headerRow.createCell(9).setCellValue("Bank Acc.");
        headerRow.getCell(9).setCellStyle(style);
        headerRow.createCell(10).setCellValue("T. Amount");
        headerRow.getCell(10).setCellStyle(style);
        headerRow.createCell(11).setCellValue("D. Balance");
        headerRow.getCell(11).setCellStyle(style);
        headerRow.createCell(12).setCellValue("Applied");
        headerRow.getCell(12).setCellStyle(style);
        headerRow.createCell(13).setCellValue("Not Applied");
        headerRow.getCell(13).setCellStyle(style);
        headerRow.createCell(14).setCellValue("Remark");
        headerRow.getCell(14).setCellStyle(style);
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

    private CellStyle createFont(Workbook workbook, PaymentExcelExporterCellColorEnum color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
//        if (color.BLUE.equals(PaymentExcelExporterCellColorEnum.BLUE)) {
//            font.setColor(IndexedColors.BLUE.index);
//        } else if (color.GREEN.equals(PaymentExcelExporterCellColorEnum.GREEN)){
//            font.setColor(IndexedColors.GREEN.index);
//        } else {
//            font.setColor(IndexedColors.WHITE.index);
//        }

        style.setFont(font);
        return style;
    }
}
