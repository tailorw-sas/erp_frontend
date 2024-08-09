package com.kynsof.share.core.infrastructure.excel;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.CellInfo;
import com.kynsof.share.core.application.excel.ExcelUtils;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.reader.AbstractReader;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.BusinessRuleValidationException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.EmptySheetException;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.exception.ReadExcelException;
import com.kynsof.share.core.domain.response.ErrorField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;


public class ExcelBeanReader<T> extends AbstractReader<T> {

    public ExcelBeanReader(ReaderConfiguration readerConfiguration, Class<T> type) {
        super(readerConfiguration, type);
    }

    @Override
    public T readSingleLine() {
        T bean;
        if (!ExcelUtils.isEndOfContent(rowCursor, sheetToRead)) {
            try {
                bean = type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Map<CellInfo, BeanField> annotatedField = columPositionAnnotationProcessor.getAnnotatedFields();
            Row currentRow = sheetToRead.getRow(rowCursor);
            if (!ExcelUtils.isRowEmpty(currentRow)) {
                annotatedField.forEach((cellInfo, beanField) -> {
                    if (cellInfo.getPosition() != -1) {

                        Cell cell = currentRow.getCell(cellInfo.getPosition(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        ExcelUtils.readCell(cell, beanField, cellInfo, bean, workbook.createDataFormat());
                    } else {
                        beanField.setFieldValue(currentRow.getRowNum()+1, bean);
                    }
                });
                rowCursor++;
                return bean;
            }
        }

        rowCursor = null;
        return null;
    }

    @Override
    public void hasContent() {
        boolean empty=true;
        for(int i=readerConfiguration.isIgnoreHeaders()?1:0;i<=sheetToRead.getLastRowNum();i++){
            if(!ExcelUtils.isRowEmpty(sheetToRead.getRow(i))){
                empty= false;
                break;
            }
        }
        if (ExcelUtils.isSheetEmpty(sheetToRead) || empty) {
            throw new EmptySheetException(DomainErrorMessage.EXCEL_SHEET_EMPTY_FORMAT_ERROR, new ErrorField("Empty excel", "There is no data to import."));
        }
    }


}
