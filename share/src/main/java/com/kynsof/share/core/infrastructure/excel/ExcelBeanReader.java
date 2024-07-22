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

        if (ExcelUtils.isSheetEmpty(sheetToRead)) {
            throw new EmptySheetException(DomainErrorMessage.EXCEL_SHEET_EMPTY_FORMAT_ERROR, new ErrorField("Active Sheet", "The active sheet is empty."));
        }
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
                        Cell cell = currentRow.getCell(cellInfo.getPosition());
                        ExcelUtils.readCell(cell, beanField, cellInfo, bean);
                    } else {
                        beanField.setFieldValue(currentRow.getRowNum(), bean);
                    }
                });
                rowCursor++;
                return bean;
            }
        }

        rowCursor = null;
        return null;
    }


}
