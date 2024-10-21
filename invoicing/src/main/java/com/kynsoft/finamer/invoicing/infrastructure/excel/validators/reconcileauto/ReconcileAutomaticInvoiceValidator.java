package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.application.excel.ExcelUtils;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ReconcileAutomaticInvoiceValidator {
    private Workbook workbook;
    private Sheet sheet;


    public void loadWorkbook(byte[] file) throws IOException {
        this.workbook = WorkbookFactory.create(new ByteArrayInputStream(file));
        this.sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
    }

    public void closeWorkbook() throws IOException {
        if (Objects.nonNull(workbook)){
            workbook.close();
        }
    }

    public boolean hasContent() {
        boolean empty = true;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (!ExcelUtils.isRowEmpty(sheet.getRow(i))) {
                empty = false;
                break;
            }
        }
        if (ExcelUtils.isSheetEmpty(sheet) || empty) {
            throw new ExcelException("There is no data to import.");
        }
        return empty;
    }

    protected boolean isDateSeparator(Row currentRow) {
        for (int cellNum = 1; cellNum < currentRow.getLastCellNum(); cellNum++) {
            if (currentRow.getCell(cellNum) != null ||
                    currentRow.getCell(cellNum).getCellType() != CellType.BLANK ||
                    StringUtils.isNotBlank(currentRow.getCell(cellNum).toString())) {
                return false;
            }
        }
        return true;
    }

    public List<ErrorField> validateInvoice(ManageInvoiceDto manageInvoiceDto) {
        List<ErrorField> errorFieldList = new ArrayList<>();
        List<ManageBookingDto> bookingDtos = manageInvoiceDto.getBookings();
        for (ManageBookingDto booking : bookingDtos) {
            Optional<List<Row>> matchBookingInRow = searchBookingInTheFile(booking);
            List<Row> allRow = matchBookingInRow.get();
            if (allRow.isEmpty()) {
                errorFieldList.add(new ErrorField("Booking", "All bookings are not in the file"));
                return errorFieldList;
            } else {
                for (Row currentRow : allRow) {
                    errorFieldList.clear();
                    String couponNumber = currentRow.getCell(4).getStringCellValue() + currentRow.getCell(11).getStringCellValue();
                    String nightType = currentRow.getCell(13).getStringCellValue();
                    String price = currentRow.getCell(39).getStringCellValue();
                    String contract = currentRow.getCell(0).getStringCellValue();
                    if (validateCouponNumber(couponNumber, booking, errorFieldList
                    ) && validatePrice(price, booking, errorFieldList)
                            && validateNightType(nightType, booking, errorFieldList)) {
                        booking.setContract(contract);
                        errorFieldList.clear();
                        break;
                    }

                }

            }
        }
        return errorFieldList;
    }

    private Optional<List<Row>> searchBookingInTheFile(ManageBookingDto manageBookingDto) {
        List<Row> allMatch = new ArrayList<>();
        for (int i = 2; i < sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (ExcelUtils.isRowEmpty(currentRow) || this.isDateSeparator(currentRow)) {
                continue;
            }
            String reservationNumber = currentRow.getCell(22).getStringCellValue();
            if (reservationNumber.equals(manageBookingDto.getHotelBookingNumber())) {
                allMatch.add(currentRow);
            }
        }
        return Optional.of(allMatch);
    }


    private boolean validateCouponNumber(String couponNumber, ManageBookingDto manageBookingDto, List<ErrorField> errors) {
        if (!couponNumber.equals(manageBookingDto.getCouponNumber())) {
            errors.add(new ErrorField("CouponNumber", "The coupon number not match with the file"));
            return false;
        }
        return true;
    }

    private boolean validateNightType(String nightType, ManageBookingDto manageBookingDto, List<ErrorField> errors) {
        if (!nightType.equals(manageBookingDto.getNightType().getCode())) {
            errors.add(new ErrorField("Night Type", "The night type not match with the file"));
            return false;
        }
        return true;
    }

    private boolean validatePrice(String price, ManageBookingDto manageBookingDto, List<ErrorField> errors) {
        String[] priceSplit = price.split("Price:");
        Double priceAmount = Double.parseDouble(priceSplit[1]);
        if (!priceAmount.equals(manageBookingDto.getDueAmount())) {
            errors.add(new ErrorField("Night Type", "The night type not match with the file"));
            return false;
        }
        return true;
    }


}
