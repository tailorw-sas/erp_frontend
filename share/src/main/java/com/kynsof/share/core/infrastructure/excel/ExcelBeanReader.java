package com.kynsof.share.core.infrastructure.excel;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.CellInfo;
import com.kynsof.share.core.application.excel.ExcelUtils;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.reader.AbstractReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;


public class ExcelBeanReader<T> extends AbstractReader<T> {

    public ExcelBeanReader(ReaderConfiguration readerConfiguration, Class<T> type) {
        super(readerConfiguration, type);
    }

    @Override
    public T readSingleLine() {
        try {
            if (!isEndOfContent()) {
                T bean = type.getDeclaredConstructor().newInstance();
                Map<CellInfo, BeanField> annotatedField = columPositionAnnotationProcessor.getAnnotatedFields();
                Row currentRow = sheetToRead.getRow(rowCursor);
                annotatedField.forEach((cellInfo, beanField) -> {
                    if (cellInfo.getPosition() != -1) {
                        Cell element = currentRow.getCell(cellInfo.getPosition());
                        beanField.setFieldValue(ExcelUtils.getValueFromCell(cellInfo,element), bean);
                    } else {
                        beanField.setFieldValue(currentRow.getRowNum(), bean);
                    }
                });
                rowCursor++;

                return bean;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rowCursor = null;
        return null;
    }

    private boolean isEndOfContent() {
        return rowCursor > sheetToRead.getLastRowNum();
    }



}
