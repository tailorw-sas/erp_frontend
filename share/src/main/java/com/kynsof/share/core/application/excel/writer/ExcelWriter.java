package com.kynsof.share.core.application.excel.writer;

import lombok.Getter;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.OutputStream;
@Getter
public abstract class ExcelWriter<T> {
    private final WriterConfiguration<T> writerConfiguration;


    protected ExcelWriter(WriterConfiguration<T> writerConfiguration) {
        this.writerConfiguration = writerConfiguration;
    }

    public abstract void write(OutputStream outputStream);
}
